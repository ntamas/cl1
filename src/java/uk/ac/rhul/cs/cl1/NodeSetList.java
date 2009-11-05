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
	 * See {@link NodeSet.mergeOverlapping(double, TaskMonitor)} for more
	 * details. This version is different only in one respect: it does
	 * not report its progress to a {@link TaskMonitor}.
	 * 
	 * @param mergingMethod  Determines which method to use to calculate the
	 *                    size of overlap between two nodesets. <tt>match</tt>
	 *                    means the matching coefficient, <tt>meet/min</tt>
	 *                    means the meet/min coefficient.
	 * 
	 * @param  threshold  the overlap threshold. Nodesets will be merged
	 *                    if their overlap is at least as large as the
	 *                    given threshold.
	 *
	 * @param  minDensity minimal density constraint that is enforced after
	 *                    merging the nodesets. Nodesets having a lower density
	 *                    than this after merging are discarded.
	 *
	 * @return  a new nodeset list where no two nodesets have an overlap
	 *          larger than or eqal to the given threshold
	 *
	 * @see NodeSet.mergeOverlapping(double, TaskMonitor)
	 */
	public NodeSetList mergeOverlapping(String mergingMethod, double threshold,
			double minDensity) {
		return mergeOverlapping(mergingMethod, threshold, minDensity, null);
	}

	/**
	 * Merges highly overlapping nodesets and returns a new nodeset list.
	 * 
	 * The algorithm progresses by creating a graph where each node
	 * refers to one of the nodesets. Any two nodes in the graph will be
	 * connected if the corresponding clusters overlap by at least the
	 * given threshold. The connected components of the graph will be
	 * used to derive the new nodesets in the result.
	 * 
	 * @param mergingMethod  Determines which method to use to calculate the
	 *                    size of overlap between two nodesets. <tt>match</tt>
	 *                    means the matching coefficient, <tt>meet/min</tt>
	 *                    means the meet/min coefficient.
	 * 
	 * @param  threshold  the overlap threshold. Nodesets will be merged
	 *                    if their overlap is at least as large as the
	 *                    given threshold.
	 *
	 * @param  minDensity minimal density constraint that is enforced after
	 *                    merging the nodesets. Nodesets having a lower density
	 *                    than this after merging are discarded.
	 *
	 * @return  a new nodeset list where no two nodesets have an overlap
	 *          larger than or equal to the given threshold, and no nodeset
	 *          has a density smaller than minDensity
	 *
	 * @see NodeSet.getMeetMinCoefficientWith()
	 */
	public NodeSetList mergeOverlapping(String mergingMethod, double threshold,
			double minDensity, TaskMonitor monitor) {
		int i, n = this.size();
		long stepsTotal = n * (n-1) / 2, stepsTaken = 0;
		NodeSetList result = new NodeSetList();
		
		if (n == 0)
			return result;
		
		boolean[] visited = new boolean[n];
		Graph graph = this.get(0).getGraph();
		Graph overlapGraph = new Graph();
		
		Arrays.fill(visited, false);		
		overlapGraph.createNodes(n);
		
		if (monitor != null) {
			monitor.setPercentCompleted(0);
			monitor.setStatus("Finding highly overlapping clusters...");
		}
		
		for (i = 0; i < n; i++) {
			NodeSet v1 = this.get(i);
			
			if (mergingMethod.equals("match")) {
				for (int j = i+1; j < n; j++) {
					if (v1.getMatchingRatioWith(this.get(j)) >= threshold)
						overlapGraph.createEdge(i, j);
				}
			} else if (mergingMethod.equals("meet/min")) {
				for (int j = i+1; j < n; j++) {
					if (v1.getMeetMinCoefficientWith(this.get(j)) >= threshold)
						overlapGraph.createEdge(i, j);
				}
			}
			
			stepsTaken += (n - i - 1);
			if (stepsTaken > stepsTotal)
				stepsTaken = stepsTotal;
			if (monitor != null) {
				monitor.setPercentCompleted((int)(100 * (((float)stepsTaken) / stepsTotal)));
			}
		}
		
		if (monitor != null) {
			monitor.setPercentCompleted(0);
			monitor.setStatus("Merging highly overlapping clusters...");
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
			nodeSet = new NodeSet(graph, members);
			if (nodeSet.getDensity() >= minDensity)
				result.add(nodeSet);
			i++;
			
			if (monitor != null)
				monitor.setPercentCompleted(100 * i / n);
		}
		
		if (monitor != null) {
			monitor.setPercentCompleted(100);
		}
		
		return result;
	}
}
