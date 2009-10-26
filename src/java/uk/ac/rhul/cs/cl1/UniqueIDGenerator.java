package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that is used in graph readers to generate unique numeric IDs for string IDs
 * (or any other hashable objects).
 * 
 * This class implements a single public method named get() which returns the numeric
 * ID for the given node name. Internally, the generator uses a map to keep track of
 * the name-ID assignments. Whenever an ID is requested for a name that is not in the
 * map yet, a new ID will be generated. The names used must be immutable objects.
 */
public class UniqueIDGenerator<K> {
	/**
	 * Internal storage for name-ID assignments
	 */
	protected Map<K, Integer> map = new HashMap<K, Integer>();
	
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
	 * A new ID will be created for the given node name if it was not
	 * seen before.
	 * 
	 * @param name   the name we are looking for
	 * @return    the corresponding ID
	 */
	public int get(K name) {
		Integer result = map.get(name);
		if (result == null) {
			result = graph.createNode(name.toString());
			map.put(name, result);
		}
		return result;
	}
	
	/**
	 * Returns the ID for the given node name (strict variant)
	 * 
	 * This one differs from {@link get(K)} by requiring that the given
	 * name already has an associated ID.
	 * 
	 * @throw  IllegalStateException   if the given key does not have
	 *                                 an associated ID
	 */
	public int getStrict(K name) {
		Integer result = map.get(name);
		if (result == null)
			throw new IllegalStateException("key not found: "+name.toString());
		return result;
	}
	
	/**
	 * Returns a copy of the internal map
	 * 
	 * @return   a map mapping node names to integer node IDs according to the present
	 *           state of the ID generator
	 */
	public Map<K, Integer> toMap() {
		Map<K, Integer> result = new TreeMap<K, Integer>(map);
		return result;
	}
	
	/**
	 * Returns a reverse mapping constructed from the internal map
	 * 
	 * @return   a map mapping integer node IDs to node names according to the present
	 *           state of the ID generator
	 */
	public Map<Integer, K> getReversedMap() {
		Map<Integer, K> result = new TreeMap<Integer, K>();
		
		for (Map.Entry<K, Integer> entry: map.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}
		return result;
	}
	
	/**
	 * Returns a reverse mapping list constructed from the internal map
	 * 
	 * @return  a list mapping integer node IDs to node names according to the present
	 *          state of the ID generator
	 */
	public List<K> getReversedList() {
		List<K> result = new ArrayList<K>(map.size());
		for (int i = 0; i < map.size(); i++)
			result.add(null);
		for (Map.Entry<K, Integer> entry: map.entrySet()) {
			result.set(entry.getValue(), entry.getKey());
		}
		return result;
	}
}
