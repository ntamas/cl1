package uk.ac.rhul.cs.cl1.merging;

import java.util.Arrays;
import java.util.SortedSet;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.similarity.SimilarityFunction;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;
import uk.ac.rhul.cs.cl1.ValuedNodeSetList;
import uk.ac.rhul.cs.graph.BreadthFirstSearch;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.collections.Multiset;
import uk.ac.rhul.cs.collections.TreeMultiset;

/**
 * Merges highly overlapping node sets in a node set list in a single
 * pass.
 * 
 * The algorithm first finds all the overlapping node set pairs that are
 * more similar to each other than a given threshold and creates a graph out
 * of these pairs. The connected components of this graph will then become
 * the new nodesets.
 * 
 * This was the only node set merging algorithm up to ClusterONE 0.91.
 * 
 * @author tamas
 *
 */
public class SinglePassNodeSetMerger extends AbstractNodeSetMerger {
	/**
	 * Merges highly overlapping nodesets and returns a new nodeset list.
	 * 
	 * The algorithm progresses by creating a graph where each node
	 * refers to one of the nodesets. Any two nodes in the graph will be
	 * connected if the corresponding clusters overlap by at least the
	 * given threshold. The connected components of the graph will be
	 * used to derive the new nodesets in the result.
	 * 
	 * @param  similarityFunc  specifies the similarity function to use
	 * @param  threshold  the overlap threshold. Nodesets will be merged
	 *                    if their overlap is at least as large as the
	 *                    given threshold.
	 * @param  monitor    a {@link uk.ac.rhul.cs.cl1.TaskMonitor} to report our progress to
	 * 
	 * @return  a new nodeset list where no two nodesets have an overlap
	 *          larger than or equal to the given threshold, and no nodeset
	 *          has a density smaller than minDensity
	 */
	public ValuedNodeSetList mergeOverlapping(
			ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc,
			double threshold) {
		int i, n = nodeSets.size();
		long stepsTotal = n * (n-1) / 2, stepsTaken = 0;
		ValuedNodeSetList result = new ValuedNodeSetList();
		
		if (n == 0)
			return result;
		
		Graph graph = nodeSets.get(0).getGraph();
		Graph overlapGraph = new Graph();
		
		overlapGraph.createNodes(n);
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Finding highly overlapping clusters...");
		}
		
		for (i = 0; i < n; i++) {
			NodeSet v1 = nodeSets.get(i);
			for (int j = i+1; j < n; j++) {
				if (similarityFunc.getSimilarity(v1, nodeSets.get(j)) >= threshold)
					overlapGraph.createEdge(i, j);
			}
			
			stepsTaken += (n - i - 1);
			if (stepsTaken > stepsTotal)
				stepsTaken = stepsTotal;
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int)(100 * (((float)stepsTaken) / stepsTotal)));
			}
		}
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Merging highly overlapping clusters...");
		}
		
		boolean[] visited = new boolean[n];
		Arrays.fill(visited, false);
		
		i = 0;
		while (i < n) {
			while (i < n && visited[i]) {
				i++;
			}
			if (i == n)
				break;
			
			if (overlapGraph.getDegree(i) == 0) {
				result.add(nodeSets.get(i));
				visited[i] = true;
			} else {
				BreadthFirstSearch bfs = new BreadthFirstSearch(overlapGraph, i);
				Multiset<Integer> members = new TreeMultiset<Integer>();
				for (int j: bfs) {
					SortedSet<Integer> newMembers = nodeSets.get(j).getMembers();
					members.addAll(newMembers);
					nodeSets.set(j, null);
					visited[j] = true;
				}
				ValuedNodeSet newNodeSet = new ValuedNodeSet(graph, members.elementSet());
				for (Multiset.Entry<Integer> entry: members.entrySet())
					newNodeSet.setValue(entry.getElement(), entry.getCount());
				result.add(newNodeSet);
			}
			
			i++;
			
			if (taskMonitor != null)
				taskMonitor.setPercentCompleted(100 * i / n);
		}
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}
		
		return result;
	}
}
