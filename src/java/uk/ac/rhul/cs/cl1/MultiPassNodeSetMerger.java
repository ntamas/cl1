package uk.ac.rhul.cs.cl1;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

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
	 * Auxiliary data structure for verification mode; stores how many times
	 * a given node appeared in the input data.
	 */
	private TreeMap<Integer, Integer> counts = new TreeMap<Integer, Integer>();
	
	/**
	 * Returns whether the node set merger is in debug mode.
	 */
	protected boolean debugging = false;
	
	/**
	 * Returns whether the node set merger is in verification mode.
	 * 
	 * In verification mode, the merger takes note of each node in the
	 * incoming node set and how many times they appear there. At the
	 * end of the merging process, the following should hold:
	 * 
	 *   - The sum of values of a given node in the resulting list of
	 *     {@link ValuedNodeSet}s must be equal to the sum of values of
	 *     the same node in the incoming {@link ValuedNodeSet}.
	 *     
	 *   - The similarity score between any pair of nodesets must be
	 *     smaller than the threshold in the result.
	 *     
	 * Verification mode turns on these checks. When the resulting list of
	 * {@link ValuedNodeSet} instances fail these checks, this means that
	 * there is a bug in the merging algorithm.
	 */
	protected boolean verificationMode = true;
	
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
			return this.getLeft().compareTo(this.getRight());
		}
		
		public int hashCode() {
			return super.hashCode() + (37 * new Double(similarity).hashCode());
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

	/**
	 * Returns whether the node set merger is in verification mode.
	 */
	public boolean isVerificationMode() {
		return verificationMode;
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
		
		if (verificationMode) {
			prepareForVerification(nodeSets);
		}
		
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
				if (similarity > 0) {
					NodeSetPair pair = new NodeSetPair(v1, v2, similarity);
					pairs.add(pair);
					debug("  Adding " + pair + " to pairs of " + v1);
					nodesetsToPairs.put(v1, pair);
					// debug("  Pairs of " + v1 + " are now " + nodesetsToPairs.get(v1));
					debug("  Adding " + pair + " to pairs of " + v2);
					nodesetsToPairs.put(v2, pair);
					// debug("  Pairs of " + v2 + " are now " + nodesetsToPairs.get(v2));
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
		
		// Checkpoint
		if (isVerificationMode()) {
			ValuedNodeSetList tmpResult = new ValuedNodeSetList();
			tmpResult.addAll(result);
			tmpResult.addAll(activeNodesets);
			verifyResult(tmpResult, similarityFunc, -1);
		}
		
		// Stage 2: merge overlapping pairs one by one
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Merging highly overlapping clusters...");
		}
		
		stepsTotal = pairs.size();
		stepsTaken = 0;
		
		while (!pairs.isEmpty()) {
			NodeSetPair pair = pairs.poll();
			ValuedNodeSet v1 = pair.getLeft();
			ValuedNodeSet v2 = pair.getRight();
			
			if (pair.similarity < threshold)
				break;
			
			debug("Merging pair: " + pair);
			// debug("  Active nodesets: " + activeNodesets);
			
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
			for (Multiset.Entry<Integer> entry: unionMembers.entrySet()) {
				Integer elt = entry.getElement();
				int count = v1.getValue(elt, 0) + v2.getValue(elt, 0);
				unionNodeset.setValue(elt, count);
			}
			
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
				debug("  v2: " + nodesetsToPairs.get(v1));
				for (NodeSetPair oldPair: nodesetsToPairs.get(v2)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v2);
					if (unionNodeset == v3) {
						// This happens when there is a triangle in the similarity
						// graph, i.e. v1 -- v2 -- v3 -- v1, and v1 is being merged
						// with v2. unionNodeset was then already added as a neighbor
						// to v3 in the previous for loop, so we can skip it here.
						continue;
					}
					
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
			} else if (v1SubsetOfv2 && !v2SubsetOfv1) {
				debug("  v1 is a real subset of v2.");
				for (int member: v1)
					v2.setValue(member, v1.getValue(member) + v2.getValue(member));
				
				// v1 is subset of v2; unionNodeSet is then equal to v2.
				// Pairs pertaining to v2 will stay as they are.
				// Pairs pertaining to v1 have to be updated.
				// Note that v2 will appear among the pairs related to v1.
				Collection<NodeSetPair> v2Pairs = nodesetsToPairs.get(v2);
				for (NodeSetPair oldPair: nodesetsToPairs.get(v1)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v1);
					if (v3 == v2)
						continue;
					
					similarity = similarityFunc.getSimilarity(v2, v3);
					nodesetsToPairs.remove(v3, oldPair);
					debug("  Similarity of {" + v2 + "} and {" + v3 + "} is " + similarity);
					if (similarity == 0)
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
			} else if (v2SubsetOfv1 && !v1SubsetOfv2) {
				debug("  v2 is a real subset of v1.");
				for (int member: v2)
					v1.setValue(member, v2.getValue(member) + v1.getValue(member));
				
				// v2 is subset of v1; unionNodeSet is then equal to v1.
				// Pairs pertaining to v1 will stay as they are.
				// Pairs pertaining to v2 have to be updated
				// Note that v1 will appear among the pairs related to v2.
				Collection<NodeSetPair> v1Pairs = nodesetsToPairs.get(v1);
				for (NodeSetPair oldPair: nodesetsToPairs.get(v2)) {
					ValuedNodeSet v3 = oldPair.getOtherThan(v2);
					if (v3 == v1)
						continue;
					
					similarity = similarityFunc.getSimilarity(v1, v3);
					nodesetsToPairs.remove(v3, oldPair);
					
					if (similarity == 0)
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
			} else {
				// v1 and v2 are equal. This can happen if they were joined via two
				// independent join paths. We remove v2 and keep v1
				debug("  v1 and v2 are identical.");
				for (int member: v2)
					v1.setValue(member, v2.getValue(member) + v1.getValue(member));
				nodesetsToPairs.removeAll(v2);
				activeNodesets.remove(v2);
			}
			
			// debug("  Active nodesets: " + activeNodesets);
			// debug("  Queue is now: " + pairs);
			
			// Checkpoint
			if (isVerificationMode()) {
				ValuedNodeSetList tmpResult = new ValuedNodeSetList();
				tmpResult.addAll(result);
				tmpResult.addAll(activeNodesets);
				try {
					verifyResult(tmpResult, similarityFunc, -1);
				} catch (RuntimeException ex) {
					System.err.println("Step " + stepsTaken + "\n" +
							"Verification failed after merging:\n" + v1 + "\nand:\n" + v2);
					throw ex;
				}
			}
			
			stepsTaken++;
		}
		
		// Add the nodesets that are still active
		result.addAll(activeNodesets);
		
		if (verificationMode) {
			verifyResult(result, similarityFunc, threshold);
		}
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
		}
		
		return result;
	}
	
	/**
	 * Prepares the input nodeset for verification later on.
	 */
	private void prepareForVerification(ValuedNodeSetList input) {
		counts.clear();
		for (ValuedNodeSet nodeSet: input) {
			for (int member: nodeSet) {
				if (counts.containsKey(member))
					counts.put(member, counts.get(member) + 1);
				else
					counts.put(member, 1);
			}
		}
	}
	
	/**
	 * Verifies the result.
	 * 
	 * @see verificationMode for more details.
	 * @throws RuntimeException if the verification failed
	 */
	private void verifyResult(ValuedNodeSetList result,
			SimilarityFunction<NodeSet> similarityFunc, double threshold) {
		TreeMap<Integer, Integer> newCounts = new TreeMap<Integer, Integer>();
		
		newCounts.clear();
		for (ValuedNodeSet nodeSet: result) {
			for (int member: nodeSet) {
				if (newCounts.containsKey(member))
					newCounts.put(member, newCounts.get(member) + 1);
				else
					newCounts.put(member, 1);
			}
		}
		
		if (!newCounts.keySet().equals(counts.keySet())) {
			Graph graph = result.get(0).getGraph();
			
			Set<Integer> ks = counts.keySet();
			StringBuilder sb = new StringBuilder("Nodes only in counts:");
			ks.removeAll(newCounts.keySet());
			for (int k: ks) {
				sb.append(" " + graph.getNodeName(k));
			}
			sb.append("\n");
			
			ks = newCounts.keySet();
			sb.append("Nodes only in newCounts:");
			ks.removeAll(counts.keySet());
			for (int k: ks) {
				sb.append(" " + graph.getNodeName(k));
			}
			
			throw new RuntimeException("newCounts and counts is different!\n" + sb.toString());
		}
		
		if (threshold < 0)
			return;
		
		for (ValuedNodeSet nodeSet1: result) {
			for (ValuedNodeSet nodeSet2: result) {
				if (nodeSet1 == nodeSet2)
					continue;
				
				double sim = similarityFunc.getSimilarity(nodeSet1, nodeSet2);
				if (sim >= threshold)
					throw new RuntimeException("similarity of " + nodeSet1 +
							" and " + nodeSet2 + " is " + sim +
							", while the threshold is " + threshold);
			}
		}
	}
	
	/**
	 * Turns the debugging mode on or off.
	 */
	public void setDebugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	/**
	 * Turns the verification mode on or off.
	 */
	public void setVerificationMode(boolean verificationMode) {
		this.verificationMode = verificationMode;
	}

	private void debug(String message) {
		if (!this.debugging)
			return;
		System.err.println(message);
	}
}
