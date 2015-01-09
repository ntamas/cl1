package uk.ac.rhul.cs.cl1.merging;

import java.util.*;

import com.sosnoski.util.array.IntArray;
import com.sosnoski.util.queue.IntQueue;
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
	 *
	 * @return  a new nodeset list where no two nodesets have an overlap
	 *          larger than or equal to the given threshold, and no nodeset
	 *          has a density smaller than minDensity
	 */
	public ValuedNodeSetList mergeOverlapping(
			ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc,
			double threshold) {
		return mergeOverlappingNew(nodeSets, similarityFunc, threshold);
	}

	public ValuedNodeSetList mergeOverlappingOld(
			ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc,
			double threshold) {
		int i, n = nodeSets.size();
		ValuedNodeSetList result = new ValuedNodeSetList();

		// The step counting is a bit tricky; instead of storing the actual number of
		// node set pairs that we have to check in stepsTotal, we divide it by n and
		// store that to avoid overflows in an integer when n is large.
		double stepsTotal = (n-1) / 2.0, stepsTaken = 0.0;

		if (n == 0)
			return result;
		
		Graph graph = nodeSets.get(0).getGraph();
		Graph overlapGraph = new Graph();
		
		overlapGraph.createNodes(n);
		
		if (taskMonitor != null) {
			taskMonitor.setStatus("Finding highly overlapping clusters...");
			taskMonitor.setPercentCompleted(0);
		}
		
		for (i = 0; i < n; i++) {
			NodeSet v1 = nodeSets.get(i);
			for (int j = i+1; j < n; j++) {
				if (similarityFunc.getSimilarity(v1, nodeSets.get(j)) >= threshold)
					overlapGraph.createEdge(i, j);
			}

			stepsTaken += (n - i - 1) / (double)(n);
			if (stepsTaken > stepsTotal) {
				stepsTaken = stepsTotal;
			}

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int)(100 * stepsTaken / stepsTotal));
			}
		}

		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}

		if (taskMonitor != null) {
			taskMonitor.setStatus("Merging highly overlapping clusters...");
			taskMonitor.setPercentCompleted(0);
		}

		BitSet visited = new BitSet(n);
		for (i = visited.nextClearBit(0); i < n; i = visited.nextClearBit(i+1)) {
			if (overlapGraph.getDegree(i) == 0) {
				result.add(nodeSets.get(i));
				visited.set(i);
			} else {
				BreadthFirstSearch bfs = new BreadthFirstSearch(overlapGraph, i);
				Multiset<Integer> members = new TreeMultiset<Integer>();
				for (int j: bfs) {
					SortedSet<Integer> newMembers = nodeSets.get(j).getMembers();
					members.addAll(newMembers);
					nodeSets.set(j, null);
					visited.set(j);
				}
				ValuedNodeSet newNodeSet = new ValuedNodeSet(graph, members.elementSet());
				for (Multiset.Entry<Integer> entry: members.entrySet())
					newNodeSet.setValue(entry.getElement(), entry.getCount());
				result.add(newNodeSet);
			}
			
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int) (100.0 * i / n));
			}
		}
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}
		
		return result;
	}

	public ValuedNodeSetList mergeOverlappingNew(
			ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc,
			double threshold) {
		int i, j, n;
		int numNodes;
		int numNodeSets = nodeSets.size();
		ValuedNodeSetList result = new ValuedNodeSetList();
		IntArray[] nodesToNodeSetIndexes;
		double stepsTotal;
		int stepsTaken;

		if (numNodeSets == 0)
			return result;

		Graph graph = nodeSets.get(0).getGraph();
		numNodes = graph.getNodeCount();

		// Create an index that maps nodes to all the nodesets that contain the node.
		// This is used to figure out easily what other nodesets could intersect the
		// nodeset we will be considering later on during a BFS.

		if (taskMonitor != null) {
			taskMonitor.setStatus("Indexing clusters...");
			taskMonitor.setPercentCompleted(0);
		}

		nodesToNodeSetIndexes = new IntArray[numNodes];
		stepsTaken = 0;
		stepsTotal = nodeSets.size();
		for (NodeSet nodeSet: nodeSets) {
			for (int member: nodeSet) {
				if (nodesToNodeSetIndexes[member] == null)
				{
					nodesToNodeSetIndexes[member] = new IntArray();
				}
				nodesToNodeSetIndexes[member].add(stepsTaken);
			}
			stepsTaken++;

			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int) (100.0 * stepsTaken / stepsTotal));
			}
		}

		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}

		// Okay, indexing done. Now we will start a BFS on a graph where the vertices
		// are nodesets and two nodesets are connected if their similarity is larger than
		// or equal to the threshold -- but we do this without constructing the graph.
		// Here we assume that the similarity function is symmetric so the graph is
		// essentially undirected.

		if (taskMonitor != null) {
			taskMonitor.setStatus("Merging highly overlapping clusters...");
			taskMonitor.setPercentCompleted(0);
		}

		BitSet visited = new BitSet(numNodeSets);
		IntQueue q = new IntQueue();
		Multiset<Integer> members = new TreeMultiset<Integer>();
		// TODO: if we used a Multiset for potentialNeighbors, we could get the intersection
		// sizes for "free"
		Set<Integer> potentialNeighbors = new HashSet<Integer>();

		for (i = visited.nextClearBit(0); i < numNodeSets; i = visited.nextClearBit(i+1)) {
			// Okay, start a BFS from nodeSet i
			q.clear();
			q.add(i);
			members.clear();

			// Mark the initial nodeset as visited (we are marking nodesets as visited as soon as
			// they are added to the queue so they are not added twice)
			visited.set(i);

			while (!q.isEmpty()) {
				// Get the current nodeset from the queue
				int nodeSetIndex = q.remove();
				NodeSet currentNodeSet = nodeSets.get(nodeSetIndex);

				// Merge the current nodeset into 'members'
				members.addAll(currentNodeSet.getMembers());

				// Look at the index and find the potential neighbors of the nodeset in the
				// similarity graph by looking up each of its nodes.
				potentialNeighbors.clear();
				for (int node: currentNodeSet) {
					IntArray array = nodesToNodeSetIndexes[node];
					n = array.size();
					for (j = 0; j < n; j++) {
						potentialNeighbors.add(array.get(j));
					}
				}

				// Check each potential neighbor to see whether its similarity to the
				// currentNodeSet is high enough
				for (Integer neighborNodeSetIndex: potentialNeighbors) {
					if (visited.get(neighborNodeSetIndex))
						continue;

					NodeSet neighborNodeSet = nodeSets.get(neighborNodeSetIndex);

					// If neighborNodeSet is null, it means that we have processed it already when
					// it was part of the queue earlier. It also means that we have checked the
					// (currentNodeSet, neighborNodeSet) pair already so there is nothing to do here.
					// This is a shortcut that we mark by (*) so we can refer to it in later comments.
					if (neighborNodeSet == null)
						continue;

					if (similarityFunc.getSimilarity(currentNodeSet, neighborNodeSet) >= threshold) {
						// Add neighborNodeSet to the queue and mark it as visited
						q.add(neighborNodeSetIndex);
						visited.set(neighborNodeSetIndex);
					}
				}

				// We can throw away the nodeset now because it has been merged into 'members'.
				// It also makes it possible to make a shortcut (marked by (*)) above in the for loop.
				nodeSets.set(nodeSetIndex, null);
			}

			// Construct a new ValuedNodeSet from 'members' and store it in the result
			ValuedNodeSet newNodeSet = new ValuedNodeSet(graph, members.elementSet());
			for (Multiset.Entry<Integer> entry: members.entrySet()) {
				newNodeSet.setValue(entry.getElement(), entry.getCount());
			}
			result.add(newNodeSet);

			// Update the progress bar
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int) (100.0 * i / numNodeSets));
			}
		}

		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}

		return result;
	}
}
