package uk.ac.rhul.cs.utils;

import java.util.HashMap;
import java.util.List;

/**
 * Implementation of a multimap backed by a {@link HashMap}.
 * 
 * @author tamas
 *
 * @param <K> the type for the keys
 * @param <V> the type for the values
 */
public class HashMultimap<K, V> extends MultimapBase<K, V> {
	protected void initializeStorage() {
		data = new HashMap<K, List<V>>();
	}

}
