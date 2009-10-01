package uk.ac.rhul.cs.cl1;

import com.sosnoski.util.hashmap.StringIntHashMap;

/**
 * Class that is used in graph readers to generate unique numeric IDs for string IDs.
 * 
 * This class implements a single public method named get() which returns the numeric
 * ID for the given node name. Internally, the generator uses a map to keep track of
 * the name-ID assignments. Whenever an ID is requested for a name that is not in the
 * map yet, a new ID will be generated. The names used must be immutable objects.
 */
public class UniqueIDGenerator {
	/**
	 * Internal storage for name-ID assignments
	 */
	protected StringIntHashMap map = new StringIntHashMap();
	
	/**
	 * The graph this generator is associated with
	 */
	protected Graph graph = null;
	
	/**
	 * Constructs a generator associated to the given graph
	 */
	public UniqueIDGenerator(Graph graph) {
		this.graph = graph;
	}
	
	/**
	 * Returns the ID for the given node name
	 * 
	 * @param name   the name we are looking for
	 * @return    the corresponding ID
	 */
	public int get(String name) {
		int result = map.get(name);
		if (result == StringIntHashMap.DEFAULT_NOT_FOUND) {
			result = graph.createNode(name);
			map.add(name, result);
		}
		return result;
	}
}
