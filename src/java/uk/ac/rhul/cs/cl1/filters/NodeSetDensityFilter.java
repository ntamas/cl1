package uk.ac.rhul.cs.cl1.filters;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

public class NodeSetDensityFilter implements NodeSetFilter {
	/**
	 * The lower density threshold used by the filter
	 */
	protected double minDensity;
	
	/**
	 * The upper density threshold used by the filter
	 */
	protected double maxDensity;
	
	/**
	 * Constructs a filter with no density limits
	 */
	public NodeSetDensityFilter() {
		this(0.0, Double.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower density limit
	 * 
	 * @param  minDensity  the lower density limit to be used
	 */
	public NodeSetDensityFilter(double minDensity) {
		this(minDensity, Double.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower and upper density limits
	 * 
	 * @param  minDensity  the lower density limit to be used
	 * @param  maxDensity  the upper density limit to be used
	 */
	public NodeSetDensityFilter(double minDensity, double maxDensity) {
		this.minDensity = minDensity;
		this.maxDensity = maxDensity;
	}
	
	/**
	 * Returns whether the given {@link MutableNodeSet} has the required density.
	 * 
	 * @return  true if the density of the given {@link MutableNodeSet} is between
	 *               minDensity and maxDensity, inclusive.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		double density = nodeSet.getDensity();
		return density >= this.minDensity && density <= this.maxDensity;
	}
}
