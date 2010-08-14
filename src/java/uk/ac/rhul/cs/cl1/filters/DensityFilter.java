package uk.ac.rhul.cs.cl1.filters;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

public class DensityFilter implements NodeSetFilter {
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
	public DensityFilter() {
		this(0.0, Double.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower density limit
	 * 
	 * @param  minDensity  the lower density limit to be used
	 */
	public DensityFilter(double minDensity) {
		this(minDensity, Double.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower and upper density limits
	 * 
	 * @param  minDensity  the lower density limit to be used
	 * @param  maxDensity  the upper density limit to be used
	 */
	public DensityFilter(double minDensity, double maxDensity) {
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

	/**
	 * Returns the lower density limit of the filter.
	 * 
	 * @return the lower density limit
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Returns the upper density limit of the filter.
	 * 
	 * @return the upper density limit
	 */
	public double getMaxDensity() {
		return maxDensity;
	}

	/**
	 * Sets the lower density limit of the filter.
	 * 
	 * @return the new lower density limit
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = minDensity;
	}

	/**
	 * Sets the upper density limit of the filter.
	 * 
	 * @return the new upper density limit
	 */
	public void setMaxDensity(double maxDensity) {
		this.maxDensity = maxDensity;
	}
}
