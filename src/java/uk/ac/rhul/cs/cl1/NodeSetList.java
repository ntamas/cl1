package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;

/**
 * A list of nodesets that is typically used as a result object in Cluster ONE.
 * 
 * This object is practically an ArrayList of NodeSet objects with some extra
 * methods that allow Cluster ONE to clean up the list by merging highly overlapping
 * nodesets.
 * 
 * @author ntamas
 */
public class NodeSetList extends ArrayList<NodeSet> {
	/**
	 * Merges highly overlapping nodesets and returns a new nodeset list.
	 * 
	 * The algorithm progresses by creating a graph where each node
	 * refers to one of the nodesets. Any two nodes in the graph will be
	 * connected if the corresponding clusters overlap by at least the
	 * given threshold. The connected components of the graph will be
	 * used to derive the new nodesets in the result.
	 * 
	 * Cluster overlaps are measured by the meet/min coefficient.
	 * 
	 * @param  threshold  the overlap threshold. Nodesets will be merged
	 *                    if their overlap is at least as large as the
	 *                    given threshold.
	 *
	 * @return  a new nodeset list where no two nodesets have an overlap
	 *          larger than or eqal to the given threshold
	 *
	 * @see NodeSet.getMeetMinCoefficientWith()
	 */
	public NodeSetList mergeOverlapping(double threshold) {
		int i, n = this.size();
		NodeSetList result = new NodeSetList();
		
		if (n == 0)
			return result;
		
		boolean[] visited = new boolean[n];
		Graph graph = this.get(0).getGraph();
		Graph overlapGraph = new Graph();
		
		Arrays.fill(visited, false);		
		overlapGraph.createNodes(n);
		
		for (i = 0; i < n; i++) {
			NodeSet v1 = this.get(i);
			for (int j = i+1; j < n; j++) {
				if (v1.getMeetMinCoefficientWith(this.get(j)) >= threshold)
					overlapGraph.createEdge(i, j);
			}
		}
		
		i = 0;
		while (i < n) {
			while (i < n && visited[i]) {
				i++;
			}
			if (i == n)
				break;
			
			BreadthFirstSearch bfs = new BreadthFirstSearch(overlapGraph, i);
			NodeSet nodeSet = this.get(i);
			SortedSet<Integer> members = nodeSet.getMembers();
			for (int j: bfs) {
				members.addAll(this.get(j).getMembers());
				this.set(j, null);
				visited[j] = true;
			}
			
			result.add(new NodeSet(graph, members));
			i++;
		}
		
		return result;
	}
}
