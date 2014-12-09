package uk.ac.rhul.cs.utils;

import java.util.*;

import com.sosnoski.util.hashmap.ObjectIntHashMap;
import uk.ac.rhul.cs.graph.Graph;

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
	protected ObjectIntHashMap map = new ObjectIntHashMap();
	
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
		int result = map.get(name);
		if (result == ObjectIntHashMap.DEFAULT_NOT_FOUND) {
			result = graph.createNode(name.toString());
			map.add(name, result);
		}
		return result;
	}
	
	/**
	 * Returns the ID for the given node name (strict variant)
	 * 
	 * This one differs from {@link #get(K)} by requiring that the given
	 * name already has an associated ID.
	 * 
	 * @throw  IllegalStateException   if the given key does not have
	 *                                 an associated ID
	 */
	public int getStrict(K name) {
		int result = map.get(name);
		if (result == ObjectIntHashMap.DEFAULT_NOT_FOUND) {
			throw new IllegalStateException("key not found: " + name.toString());
		}
		return result;
	}
	
	/**
	 * Returns a copy of the internal map
	 * 
	 * @return   a map mapping node names to integer node IDs according to the present
	 *           state of the ID generator
	 */
	public Map<K, Integer> toMap() {
		Map<K, Integer> result = new TreeMap<K, Integer>();
		Iterator<?> it = map.iterator();
		while (it.hasNext())
		{
			K key = (K)it.next();
			result.put(key, map.get(key));
		}
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
		Iterator<?> it = map.iterator();
		while (it.hasNext())
		{
			K key = (K)it.next();
			result.put(map.get(key), key);
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
		for (int i = 0; i < map.size(); i++) {
			result.add(null);
		}

		Iterator<?> it = map.iterator();
		while (it.hasNext())
		{
			K key = (K)it.next();
			result.set(map.get(key), key);
		}

		return result;
	}
}
