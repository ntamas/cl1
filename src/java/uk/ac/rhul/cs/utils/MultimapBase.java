package uk.ac.rhul.cs.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base implementation of a multimap.
 * 
 * This object contains implementation for most of the methods required
 * by the {@link Multimap} interface without knowing what type of map
 * is used to back the multimap.
 * 
 * @author tamas
 */
public abstract class MultimapBase<K, V> implements Multimap<K, V> {
	/**
	 * Internal storage for the key-value pairs.
	 */
	protected Map<K, List<V>> data;
	
	public MultimapBase() {
		initializeStorage();
	}
	
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
	
	public Set<K> keySet() {
		return data.keySet();
	}
	
	protected abstract void initializeStorage();
	
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
		
		boolean result = values.remove(value);
		if (result && values.isEmpty()) {
			data.remove(key);
		}
		return result;
	}
	
	public Collection<V> removeAll(Object key) {
		List<V> values = data.remove(key);
		if (values == null)
			return Collections.emptyList();
		return values;
	}
}
