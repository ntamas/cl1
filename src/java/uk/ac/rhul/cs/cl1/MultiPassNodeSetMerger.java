package uk.ac.rhul.cs.cl1;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;

import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.HashMultimap;
import uk.ac.rhul.cs.utils.Multiset;
import uk.ac.rhul.cs.utils.TreeMultiset;
import uk.ac.rhul.cs.utils.UnorderedPair;

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
	/**
	 * Returns whether the node set merger is in debug mode.
	 */
	protected boolean debugging = false;
	
	class NodeSetPair extends UnorderedPair<ValuedNodeSet> implements Comparable<NodeSetPair> {
		double similarity;
		
		public NodeSetPair(final ValuedNodeSet left, final ValuedNodeSet right, double similarity) {
			super(left, right);
			this.similarity = similarity;
		}
		
		public ValuedNodeSet getOtherThan(ValuedNodeSet item) {
			if (this.getLeft() == item)
				return this.getRight();
			return this.getLeft();
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
		
		public String toString() {
			return "{" + this.getLeft().toString() + "} - {" + this.getRight().toString() + "}: " + similarity;
		}
	}
	
	/**
	 * Returns whether the node set merger is in debug mode or not.
	 */
	public boolean isDebugging() {
		return debugging;
	}

	public ValuedNodeSetList mergeOverlapping(ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc, double threshold) {
		int i, j, n = nodeSets.size();
		long stepsTotal = n * (n-1) / 2, stepsTaken = 0;
		double similarity;
		ValuedNodeSetList result = new ValuedNodeSetList();
		HashSet<ValuedNodeSet> activeNodesets = new HashSet<ValuedNodeSet>();
		
		if (n == 0)
			return result;
		
		Graph graph = nodeSets.get(0).getGraph();
		
		// Stage 1: find overlapping pairs and index them
		PriorityQueue<NodeSetPair> pairs = new PriorityQueue<NodeSetPair>();
		HashMultimap<ValuedNodeSet, NodeSetPair> nodesetsToPairs = new HashMultimap<ValuedNodeSet, NodeSetPair>();
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Finding highly overlapping clusters...");
		}
		
		for (i = 0; i < n; i++) {
			ValuedNodeSet v1 = nodeSets.get(i);
			for (j = i+1; j < n; j++) {
				ValuedNodeSet v2 = nodeSets.get(j);
				similarity = similarityFunc.getSimilarity(v1, v2);
				if (similarity >= threshold) {
					NodeSetPair pair = new NodeSetPair(v1, v2, similarity);
					pairs.add(pair);
					nodesetsToPairs.put(v1, pair);
					nodesetsToPairs.put(v2, pair);
				}
			}
			if (!nodesetsToPairs.containsKey(v1)) {
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
		
		// Store which nodesets are still active (i.e. unmerged)
		activeNodesets.addAll(nodesetsToPairs.keySet());
		
		if (debugging) {
			System.err.println("Nodesets with no similar pairs:");
			System.err.println(result);
			System.err.println("Overlapping pairs to consider:");
			System.err.println(pairs);
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
			ValuedNodeSet v1 = pair.getLeft();
			ValuedNodeSet v2 = pair.getRight();
			
			debug("Merging pair: " + pair);
			debug("  Active nodesets: " + activeNodesets);
			
			// If v1 was already merged into another nodeset, continue
			if (!activeNodesets.contains(v1)) {
				debug("  " + v1 + " already absorbed in another nodeset, skipping.");
				nodesetsToPairs.remove(v2, pair);
				continue;
			}
			
			// If v2 was already merged into another nodeset, continue
			if (!activeNodesets.contains(v2)) {
				debug("  " + v2 + " already absorbed in another nodeset, skipping.");
				nodesetsToPairs.remove(v1, pair);
				continue;
			}
			
			// Remove the v1-v2 pair from the nodeset to pair mappings
			nodesetsToPairs.remove(v1, pair);
			nodesetsToPairs.remove(v2, pair);
			
			// Merge v1 and v2
			Multiset<Integer> unionMembers = new TreeMultiset<Integer>();
			unionMembers.addAll(v1.getMembers());
			unionMembers.addAll(v2.getMembers());
			
			ValuedNodeSet unionNodeset = new ValuedNodeSet(graph, unionMembers.elementSet());
			for (Multiset.Entry<Integer> entry: unionMembers.entrySet())
				unionNodeset.setValue(entry.getElement(), entry.getCount());
			
			// Update the NodeSetPairs related to either v1 or v2
			boolean v1SubsetOfv2 = unionNodeset.equals(v2);
			boolean v2SubsetOfv1 = unionNodeset.equals(v1);
			
			if (!v1SubsetOfv2 && !v2SubsetOfv1) {
				debug("v1 and v2 are not subsets of each other.");
				for (NodeSetPair oldPair: nodesetsToPairs.get(v1)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v1);
					similarity = similarityFunc.getSimilarity(unionNodeset, v3);
					nodesetsToPairs.remove(v3, oldPair);
					
					if (similarity < threshold)
						continue;
					
					NodeSetPair newPair = new NodeSetPair(unionNodeset, v3, similarity);
					debug("  (1) updating pair: " + oldPair + " --> " + newPair);
					
					pairs.add(newPair);
					nodesetsToPairs.put(unionNodeset, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
				for (NodeSetPair oldPair: nodesetsToPairs.get(v2)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v2);
					similarity = similarityFunc.getSimilarity(unionNodeset, v3);
					nodesetsToPairs.remove(v3, oldPair);
					
					if (similarity < threshold)
						continue;
					
					NodeSetPair newPair = new NodeSetPair(unionNodeset, v3, similarity);
					debug("  (2) updating pair: " + oldPair + " --> " + newPair);
					
					pairs.add(newPair);
					nodesetsToPairs.put(unionNodeset, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
				
				// Remove v1 and v2 from the mapping from nodesets to pairs since
				// they were already merged into another nodeset
				nodesetsToPairs.removeAll(v1);
				nodesetsToPairs.removeAll(v2);		
				activeNodesets.remove(v1);
				activeNodesets.remove(v2);

				activeNodesets.add(unionNodeset);
			} else if (v1SubsetOfv2) {
				debug("  v1 is subset of v2.");
				// v1 is subset of v2; unionNodeSet is then equal to v2.
				// Pairs pertaining to v2 will stay as they are.
				// Pairs pertaining to v1 have to be updated
				Collection<NodeSetPair> v2Pairs = nodesetsToPairs.get(v2);
				for (NodeSetPair oldPair: nodesetsToPairs.get(v1)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v1);
					similarity = similarityFunc.getSimilarity(v2, v3);
					nodesetsToPairs.remove(v3, oldPair);
					
					debug("  Similarity of {" + v2 + "} and {" + v3 + "} is " + similarity);
					if (similarity < threshold)
						continue;
					
					NodeSetPair newPair = new NodeSetPair(v2, v3, similarity);
					if (v2Pairs.contains(newPair)) {
						debug("  This pair is already among v2's pairs, skipping.");
						continue;
					}
					
					debug("  (3) updating pair: " + oldPair + " --> " + newPair);
					
					pairs.add(newPair);
					nodesetsToPairs.put(v2, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
				
				// Remove v1 from the mapping from nodesets to pairs
				nodesetsToPairs.removeAll(v1);
				activeNodesets.remove(v1);
			} else if (v2SubsetOfv1) {
				debug("  v2 is subset of v1.");
				
				// v2 is subset of v1; unionNodeSet is then equal to v1.
				// Pairs pertaining to v1 will stay as they are.
				// Pairs pertaining to v2 have to be updated
				Collection<NodeSetPair> v1Pairs = nodesetsToPairs.get(v1);
				for (NodeSetPair oldPair: nodesetsToPairs.get(v2)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v2);
					similarity = similarityFunc.getSimilarity(v1, v3);
					nodesetsToPairs.remove(v3, oldPair);
					
					if (similarity < threshold)
						continue;
					
					NodeSetPair newPair = new NodeSetPair(v1, v3, similarity);
					if (v1Pairs.contains(newPair))
						continue;
					
					debug("  (4) updating pair: " + oldPair + " --> " + newPair);
					
					pairs.add(newPair);
					nodesetsToPairs.put(v1, newPair);
					nodesetsToPairs.put(v3, newPair);
				}
				
				// Remove v2 from the mapping from nodesets to pairs
				nodesetsToPairs.removeAll(v2);
				activeNodesets.remove(v2);
			}
			
			debug("  Active nodesets: " + activeNodesets);
			debug("  Queue is now: " + pairs);
			
			stepsTaken += 1;
			if (stepsTaken > stepsTotal)
				stepsTotal = stepsTaken;
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted((int)(100 * (((float)stepsTaken) / stepsTotal)));
			}
		}
		
		// Add the nodesets that are still active
		result.addAll(activeNodesets);
		
		return result;
	}

	/**
	 * Turns the debugging mode on or off.
	 */
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	private void debug(String message) {
		if (!this.debugging)
			return;
		System.err.println(message);
	}
}
