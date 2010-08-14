package uk.ac.rhul.cs.cl1.filters;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

/**
 * Filters {@link MutableNodeSet} instances by size.
 * 
 * @author ntamas
 */
public class SizeFilter implements NodeSetFilter {
	/**
	 * The lower size threshold used by the filter
	 */
	protected int minSize;
	
	/**
	 * The upper size threshold used by the filter
	 */
	protected int maxSize;
	
	/**
	 * Constructs a filter with no size limits
	 */
	public SizeFilter() {
		this(0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower size limit
	 * 
	 * @param  minSize  the lower size limit to be used
	 */
	public SizeFilter(int minSize) {
		this(minSize, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a filter with the given lower and upper size limits
	 * 
	 * @param  minSize  the lower size limit to be used
	 * @param  maxSize  the upper size limit to be used
	 */
	public SizeFilter(int minSize, int maxSize) {
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	/**
	 * Returns whether the given {@link MutableNodeSet} has the required size.
	 * 
	 * @return  true if the given {@link MutableNodeSet} contains at least
	 *               minSize members and at most maxSize members.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		return nodeSet.size() >= minSize && nodeSet.size() <= maxSize;
	}

	/**
	 * Gets the lower bound of the size filter.
	 * 
	 * @return the minimum size of the filter
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * Gets the upper bound of the size filter.
	 * 
	 * @return the maximum size of the filter
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Sets the lower bound of the size filter.
	 * 
	 * @param minSize the minimum size to set
	 */
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	/**
	 * Sets the upper bound of the size filter.
	 * 
	 * @param maxSize the maximum size to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
