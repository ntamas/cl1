package uk.ac.rhul.cs.cl1.filters;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

/**
 * Common interface for classes that filter {@link MutableNodeSet} instances.
 * 
 * These classes take a {@link MutableNodeSet}, perform some operation on
 * it and return either true or false depending on whether the nodeset
 * was accepted or not.
 * 
 * @author ntamas
 */
public interface NodeSetFilter {
	/**
	 * Returns whether the given {@link MutableNodeSet} is accepted by this filter
	 * or not.
	 * 
	 * The filter may also modify the {@link MutableNodeSet} in-place.
	 * 
	 * @param  nodeSet  the {@link MutableNodeSet} to be filtered
	 * @return whether the filter accepted the {@link MutableNodeSet} or not.
	 */
	public boolean filter(MutableNodeSet nodeSet);
}