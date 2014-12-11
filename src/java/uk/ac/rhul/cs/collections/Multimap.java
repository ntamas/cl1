package uk.ac.rhul.cs.collections;

import java.util.Collection;
import java.util.Set;

/**
 * A collection similar to {@link Map} which may associate multiple
 * values with a single key.
 * 
 * This class is largely based upon the eponymous class in the Google
 * Collections library.
 * 
 * @author tamas
 */
public interface Multimap<K, V> {
	/**
	 * Removes all key-value pairs from the multimap.
	 */
	public void clear();
	
	/**
	 * Returns true if the multimap contains any values for the specified key.
	 */
	public boolean containsKey(Object key);
	
	/**
	 * Returns a collection view of all values associated with a key.
	 * 
	 * If no pairs in the multimap have the provided key, an empty collection
	 * is returned.
	 */
	public Collection<V> get(K key);
	
	/**
	 * Returns true if the multimap contains no key-value pairs.
	 */
	public boolean isEmpty();
	
	/**
	 * Returns the set of all keys, each appearing once in the returned
	 * set.
	 */
	Set<K> keySet();
	
	/**
	 * Adds the given key-value pair to the multimap.
	 * 
	 * @param key    the key to be added
	 * @param value  the value to be added
	 * @return true if the operation increased the size of the multimap,
	 *              false otherwise.
	 */
	public boolean put(K key, V value);
	
	/**
	 * Removes the given key-value pair from the multimap.
	 * 
	 * @param key    the key to be removed
	 * @param value  the value to be removed
	 * @return true if the multimap changed.
	 */
	public boolean remove(Object key, Object value);
	
	/**
	 * Removes all the values associated to the given key from the multimap.
	 * 
	 * @param key    the key to be removed
	 * @return  the collection of removed values, or an empty collection if no
	 *          values were associated with the provided key.
	 */
	public Collection<V> removeAll(Object key);
}
