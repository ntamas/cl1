package uk.ac.rhul.cs.cl1;

import java.util.PriorityQueue;

import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.Multiset;
import uk.ac.rhul.cs.utils.Pair;
import uk.ac.rhul.cs.utils.TreeMultimap;
import uk.ac.rhul.cs.utils.TreeMultiset;

/**
 * Merges highly overlapping node sets in multiple passes, recalculating
 * similarities after each pass.
 * 
 * The algorithm first finds all the overlapping node set pairs that are
 * more similar to each other than a given threshold and puts them in a
 * priority queue. In each step, the pair at the front of the queue (i.e.
 * the one with the highest similarity) is taken and merged, the
 * similarities are re-calculated and those still above the threshold
 * are put back in the queue. The process continues until the queue becomes
 * empty.
 * 
 * @author tamas
 *
 */
public class MultiPassNodeSetMerger extends AbstractNodeSetMerger {
	class NodeSetPair extends Pair<NodeSet, NodeSet> implements Comparable<NodeSetPair> {
		double similarity;
		
		public NodeSetPair(final NodeSet left, final NodeSet right, double similarity) {
			super(left, right);
			this.similarity = similarity;
		}

		public int compareTo(NodeSetPair other) {
			if (this.equals(other))
				return 0;
			
			if (this.similarity < other.similarity)
				return 1;
			if (this.similarity > other.similarity)
				return -1;
			return 0;
		}
	}
	
	public ValuedNodeSetList mergeOverlapping(ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc, double threshold) {
		int i, j, n = nodeSets.size();
		long stepsTotal = n * (n-1) / 2, stepsTaken = 0;
		double similarity;
		ValuedNodeSetList result = new ValuedNodeSetList();
		
		if (n == 0)
			return result;
		
		Graph graph = nodeSets.get(0).getGraph();
		
		// Stage 1: find overlapping pairs and index them
		PriorityQueue<NodeSetPair> pairs = new PriorityQueue<NodeSetPair>();
		TreeMultimap<NodeSet, NodeSetPair> nodesetsToPairs = new TreeMultimap<NodeSet, NodeSetPair>();
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Finding highly overlapping clusters...");
		}
		
		for (i = 0; i < n; i++) {
			ValuedNodeSet v1 = nodeSets.get(i);
			for (j = i+1; j < n; j++) {
				NodeSet v2 = nodeSets.get(j);
				similarity = similarityFunc.getSimilarity(v1, v2);
				if (similarity >= threshold) {
					NodeSetPair pair = new NodeSetPair(v1, v2, similarity);
					pairs.add(pair);
					nodesetsToPairs.put(v1, pair);
					nodesetsToPairs.put(v2, pair);
				}
			}
			if (nodesetsToPairs.containsKey(v1)) {
				// No other node set is similar to v1, so add it to the result
				result.add(v1);
			}
			
			stepsTaken += (n - i - 1);
			if (stepsTaken > stepsTotal)
				stepsTaken = stepsTotal;
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int)(100 * (((float)stepsTaken) / stepsTotal)));
			}
		}
		
		// Stage 2: merge overlapping pairs one by one
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Merging highly overlapping clusters...");
		}
		
		stepsTotal = pairs.size();
		stepsTaken = 0;
		
		while (!pairs.isEmpty()) {
			NodeSetPair pair = pairs.poll();
			NodeSet v1 = pair.getLeft();
			NodeSet v2 = pair.getRight();
			
			// If v1 was already merged into another nodeset, continue
			if (!nodesetsToPairs.containsKey(v1))
				continue;
			
			// If v2 was already merged into another nodeset, continue
			if (!nodesetsToPairs.containsKey(v2))
				continue;
			
			// Remove the v1-v2 pair from the nodeset to pair mappings
			nodesetsToPairs.remove(v1, pair);
			nodesetsToPairs.remove(v2, pair);
			
			// Merge v1 and v2
			Multiset<Integer> unionMembers = new TreeMultiset<Integer>();
			unionMembers.addAll(v1.getMembers());
			unionMembers.addAll(v2.getMembers());
			
			ValuedNodeSet unionNodeSet = new ValuedNodeSet(graph, unionMembers.elementSet());
			for (Multiset.Entry<Integer> entry: unionMembers.entrySet())
				unionNodeSet.setValue(entry.getElement(), entry.getCount());
			
			// Update the NodeSetPairs related to either v1 or v2
			for (NodeSetPair oldPair: nodesetsToPairs.get(v1)) {
				NodeSet v3 = oldPair.getLeft();
				if (v3 == v1)
					v3 = oldPair.getRight();
				similarity = similarityFunc.getSimilarity(unionNodeSet, v3);
				if (similarity >= threshold) {
					NodeSetPair newPair = new NodeSetPair(unionNodeSet, v3, similarity);
					pairs.add(newPair);
					nodesetsToPairs.put(unionNodeSet, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
			}
			for (NodeSetPair oldPair: nodesetsToPairs.get(v2)) {
				NodeSet v3 = oldPair.getLeft();
				if (v3 == v2)
					v3 = oldPair.getRight();
				similarity = similarityFunc.getSimilarity(unionNodeSet, v3);
				if (similarity >= threshold) {
					NodeSetPair newPair = new NodeSetPair(unionNodeSet, v3, similarity);
					pairs.add(newPair);
					nodesetsToPairs.put(unionNodeSet, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
			}
			if (!nodesetsToPairs.containsKey(unionNodeSet)) {
				// Nothing will be merged to unionNodeSet later, so
				// add it to the result
				result.add(unionNodeSet);
			}
			
			// Remove v1 and v2 from the mapping from nodesets to pairs since
			// they were already merged into another nodeset
			nodesetsToPairs.removeAll(v1);
			nodesetsToPairs.removeAll(v2);
			
			stepsTaken += 1;
			if (stepsTaken > stepsTotal)
				stepsTotal = stepsTaken;
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int)(100 * (((float)stepsTaken) / stepsTotal)));
			}
		}
		
		return null;
	}
}
