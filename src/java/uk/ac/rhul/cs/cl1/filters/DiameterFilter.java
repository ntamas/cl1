package uk.ac.rhul.cs.cl1.filters;

import uk.ac.rhul.cs.cl1.BreadthFirstSearch;
import uk.ac.rhul.cs.cl1.BreadthFirstSearchIterator;
import uk.ac.rhul.cs.cl1.MutableNodeSet;

/**
 * Filters node sets based on their diameter.
 * 
 * This filter rejects node sets whose diameter is not between two given
 * extremes.
 * 
 * @author tamas
 *
 */
public class DiameterFilter implements NodeSetFilter {
	/**
	 * The lower diameter threshold used by the filter
	 */
	protected int minDiameter;
	
	/**
	 * The upper diameter threshold used by the filter
	 */
	protected int maxDiameter;
	
	/**
	 * Constructs a diameter filter with no limits.
	 */
	public DiameterFilter() {
		this(0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a diameter filter with the given upper limit.
	 * 
	 * @param  maxDiameter  the upper diameter limit to be used
	 */
	public DiameterFilter(int maxDiameter) {
		this(0, maxDiameter);
	}
	
	/**
	 * Constructs a filter with the given lower and upper limits
	 * 
	 * @param  minSize  the lower diameter limit to be used
	 * @param  maxSize  the upper diameter limit to be used
	 */
	public DiameterFilter(int minDiameter, int maxDiameter) {
		this.minDiameter = minDiameter;
		this.maxDiameter = maxDiameter;
	}
	
	/**
	 * Accepts a node set if and only if its diameter is within the given limits. 
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		Integer[] members = nodeSet.getMembers().toArray(new Integer[0]);
		BreadthFirstSearch bfs = new BreadthFirstSearch(nodeSet.getGraph(), 0);
		int diameter = 0;
		bfs.restrictToSubgraph(members);
		
		for (int node: nodeSet) {
			bfs.setSeedNode(node);
			BreadthFirstSearchIterator iter = bfs.iterator();
			while (iter.hasNext()) {
				iter.next();
			}
			diameter = Math.max(diameter, iter.getDistance());
			if (diameter > maxDiameter)
				return false;
		}
		
		return (diameter >= minDiameter);
	}
}
