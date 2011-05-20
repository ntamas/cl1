package uk.ac.rhul.cs.cl1;

import java.util.PriorityQueue;

import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.Pair;

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
		PriorityQueue<NodeSetPair> pairs = new PriorityQueue<NodeSetPair>();
		ValuedNodeSetList result = new ValuedNodeSetList();
		
		if (n == 0)
			return result;
		
		Graph graph = nodeSets.get(0).getGraph();
		
		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Finding highly overlapping clusters...");
		}
		
		for (i = 0; i < n; i++) {
			NodeSet v1 = nodeSets.get(i);
			for (j = i+1; j < n; j++) {
				NodeSet v2 = nodeSets.get(j);
				double similarity = similarityFunc.getSimilarity(v1, v2);
				if (similarity >= threshold)
					pairs.add(new NodeSetPair(v1, v2, similarity));
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
		
		// TODO
		
		return null;
	}
}
