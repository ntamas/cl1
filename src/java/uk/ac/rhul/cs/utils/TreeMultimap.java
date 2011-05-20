package uk.ac.rhul.cs.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tree-based implementation of a multimap.
 * 
 * This object uses a {@link TreeMap} from keys to lists of values to
 * implement a multimap data structure.
 * 
 * @author tamas
 */
public class TreeMultimap<K, V> implements Multimap<K, V> {
	/**
	 * Internal storage for the key-value pairs.
	 */
	protected TreeMap<K, List<V>> data;
	
	public void clear() {
		data.clear();
	}

	public boolean containsKey(Object key) {
		List<V> values = data.get(key);
		return values != null && values.size() > 0;
	}

	public Collection<V> get(K key) {
		Collection<V> values = data.get(key);
		if (values == null)
			return Collections.emptyList();
		return values;
	}

	public boolean isEmpty() {
		for (Map.Entry<K, List<V>> pair: data.entrySet())
			if (!pair.getValue().isEmpty())
				return false;
		return true;
	}
	
	public boolean put(K key, V value) {
		List<V> values = data.get(key);
		if (values == null) {
			values = new ArrayList<V>();
			data.put(key, values);
		}
		values.add(value);
		return true;
	}
	
	public boolean remove(Object key, Object value) {
		List<V> values = data.get(key);
		if (values == null)
			return false;
		return values.remove(value);
	}
	
	public Collection<V> removeAll(Object key) {
		List<V> values = data.remove(key);
		if (values == null)
			return Collections.emptyList();
		return values;
	}
}
