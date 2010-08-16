package uk.ac.rhul.cs.cl1.filters;

import java.util.ArrayList;
import java.util.List;

import uk.ac.rhul.cs.cl1.MutableNodeSet;

/**
 * A filter that accepts a nodeset if all its subfilters accept it.
 * 
 * This can be used to chain filters in a predefined order such that the
 * initial {@link MutableNodeSet} is passed each of the subfilters one
 * by one, and rejected if any one of the subfilters reject the nodeset.
 * When a filter performs some transformation on the nodeset, the
 * transformed nodeset will be passed on to the next filter.
 * 
 * @author tamas
 */
public class FilterChain implements NodeSetFilter {
	private List<NodeSetFilter> filters = new ArrayList<NodeSetFilter>();
	
	/**
	 * Constructs a new, empty filter chain that accepts every nodeset.
	 */
	public FilterChain() {
	}
	
	/**
	 * Adds the given filter to the end of the filter chain.
	 * 
	 * @param  filter  the filter to be added
	 */
	public boolean add(NodeSetFilter filter) {
		return filters.add(filter);
	}
	
	/**
	 * Removes all the filters from the filter chain
	 */
	public void clear() {
		filters.clear();
	}
	
	/**
	 * Checks whether the filter chain contains the given filter
	 */
	public boolean contains(NodeSetFilter filter) {
		return filters.contains(filter);
	}
	
	/**
	 * Returns whether all the subfilters in the chain accept the given
	 * {@link MutableNodeSet}.
	 * 
	 * @return  true if the given {@link MutableNodeSet} is accepted by
	 *               all the subfilters in the chain.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		for (NodeSetFilter f: filters) {
			if (!f.filter(nodeSet))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns whether the filter chain is empty.
	 */
	public boolean isEmpty() {
		return filters.isEmpty();
	}
	
	/**
	 * Returns the number of subfilters in the filter chain.
	 */
	public int size() {
		return filters.size();
	}
}
