package uk.ac.rhul.cs.cl1.filters;

import com.sosnoski.util.array.IntArray;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

/**
 * Accepts all nodesets but performs a haircut operation on them.
 * 
 * The haircut operation removes some nodes from a nodeset based on the total
 * weight of their connections to the rest of the nodeset. First, the total
 * weight of the nodeset and the average weight per node is calculated.
 * After that, we remove nodes where the total weight of adjacent edges with
 * the other endpoint in the nodeset is less than the average weight times
 * a multiplier factor (e.g., 0.2).
 * 
 * There are two variants to the haircut operation. The non-iterative variant
 * finds all the nodes whose internal weight is less than the threshold (the
 * average weight times the multiplier factor), removes them, and then returns.
 * The iterative variant finds the node whose internal weight is the smallest,
 * checks if it is below the threshold, and if so, removes it. If there was a
 * removal, the iterative variant re-calculates the threshold based on the
 * new average weight and then continues until no removal is possible.
 * 
 * @author tamas
 */
public class HaircutFilter implements NodeSetFilter {
	/**
	 * The multiplier factor used in the haircut process.
	 */
	private double threshold = 0.2;
	
	/**
	 * Whether we are using the iterative variant of the filter
	 */
	private boolean iterative = true;
	
	/**
	 * Constructs a new haircut filter with the given threshold
	 * 
	 * @param threshold  the threshold to be used for the filter.
	 * @param iterative  whether we want an iterative filter or not
	 */
	public HaircutFilter(double threshold, boolean iterative) {
		this.setThreshold(threshold);
		this.setIterative(iterative);
	}
	
	/**
	 * Performs the haircut operation on the nodeset and accepts it.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		if (threshold <= 0.0)
			return true;
		
		if (this.isIterative())
			return filterIterative(nodeSet);
		else
			return filterNonIterative(nodeSet);
	}
	
	/**
	 * Iterative variant of the haircut operation
	 */
	protected boolean filterIterative(MutableNodeSet nodeSet) {
		while (!nodeSet.isEmpty()) {
			int minIdx = -1;
			double minInWeight = Double.MAX_VALUE;
			double limit = 2 * nodeSet.getTotalInternalEdgeWeight() / nodeSet.size() * threshold;
			
			for (int i: nodeSet) {
				double inWeight = nodeSet.getInternalWeight(i);
				
				if (inWeight < minInWeight) {
					minInWeight = inWeight;
					minIdx = i;
				}
			}
			
			if (minInWeight < limit)
				nodeSet.remove(minIdx);
			else
				break;
		}
		
		return true;
	}

	/**
	 * Non-iterative variant of the haircut operation
	 */
	protected boolean filterNonIterative(MutableNodeSet nodeSet) {
		IntArray nodesToRemove = new IntArray();
		
		double limit = 2 * nodeSet.getTotalInternalEdgeWeight() / nodeSet.size() * threshold;
			
		for (int i: nodeSet) {
			if (nodeSet.getInternalWeight(i) < limit)
				nodesToRemove.add(i);
		}
		
		nodeSet.remove(nodesToRemove.toArray());
		
		return true;
	}
	
	/**
	 * Returns the multiplier factor used during the haircut operation.
	 * 
	 * @return the multiplier factor
	 */
	public double getThreshold() {
		return threshold;
	}
	
	/**
	 * Returns whether the filter is an iterative haircut filter
	 * 
	 * @return  whether the filter is an iterative haircut filter
	 */
	public boolean isIterative() {
		return iterative;
	}
	
	/**
	 * Sets whether the filter is an iterative haircut filter or not
	 * 
	 * @param  iterative  whether the filter is an iterative haircut filter
	 */
	public void setIterative(boolean iterative) {
		this.iterative = iterative;
	}
	
	/**
	 * Sets the multiplier factor used during the haircut operation.
	 * 
	 * @param threshold  the multiplier factor
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
