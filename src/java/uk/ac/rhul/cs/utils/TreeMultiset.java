package uk.ac.rhul.cs.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Tree-based multiset class
 * @author tamas
 *
 * @param <E>  the types of elements to be stored in this multiset
 */
public class TreeMultiset<E> implements Multiset<E> {
	public class Entry implements Multiset.Entry<E> {
		E object;
		int count;
		
		private Entry(Map.Entry<E, Integer> entry) {
			this.object = entry.getKey();
			this.count = entry.getValue();
		}
		
		public int getCount() { return count; }
		public E getElement() { return object; }
		
		public String toString() {
			if (count == 1)
				return object.toString();
			
			StringBuilder sb = new StringBuilder();
			sb.append(object.toString());
			sb.append(" x ");
			sb.append(count);
			return sb.toString();
		}
	}
	
	/**
	 * Internal storage area
	 */
	protected TreeMap<E, Integer> data;
	
	public TreeMultiset() {
		data = new TreeMap<E, Integer>();
	}
	
	public int add(E element, int occurrences) {
		if (occurrences < 0)
			throw new IllegalArgumentException("occurrences must not be negative");
		
		int count = this.count(element);
		if (occurrences > 0)
			data.put(element, count + occurrences);
		
		return count;
	}

	public int count(Object element) {
		Integer count = data.get(element);
		if (count == null)
			return 0;
		return count;
	}

	public Set<E> elementSet() {
		return data.keySet();
	}

	public Set<Multiset.Entry<E>> entrySet() {
		Set<Multiset.Entry<E>> result = new HashSet<Multiset.Entry<E>>();
		
		for (Map.Entry<E, Integer> entry: data.entrySet())
			result.add(new Entry(entry));
		
		return result;
	}

	public int remove(E element, int occurrences) {
		if (occurrences < 0)
			throw new IllegalArgumentException("occurrences must not be negative");
		
		int count = this.count(element);
		if (occurrences > 0) {
			if (count <= occurrences)
				data.remove(element);
			else
				data.put(element, count - occurrences);
		}
		
		return count;
	}

	public int setCount(E element, int count) {
		if (count < 0)
			throw new IllegalArgumentException("count must not be negative");
		
		int oldCount = this.count(element);
		if (count == 0)
			data.remove(element);
		else
			data.put(element, count);
		
		return oldCount;
	}

	public boolean add(E element) {
		int count = this.count(element);
		data.put(element, count + 1);
		
		return count > 0;
	}

	public boolean addAll(Collection<? extends E> elements) {
		boolean result = false;
		
		for (E element: elements)
			result = result | this.add(element);
		
		return result;
	}

	public void clear() {
		data.clear();
	}

	public boolean contains(Object key) {
		return data.containsKey(key);
	}

	public boolean containsAll(Collection<?> keys) {
		for (Object o: keys)
			if (!data.containsKey(o))
				return false;
		return true;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Iterator<E> iterator() {
		return data.keySet().iterator();
	}
	
	@SuppressWarnings("unchecked")
	public boolean remove(Object element) {
		int count = this.count(element);
		if (count == 0)
			return false;
		if (count == 1) {
			data.remove(element);
			return true;
		}
		data.put((E) element, count - 1);
		
		return true;
	}

	public boolean removeAll(Collection<?> elements) {
		boolean result = false;
		
		for (Object element: elements)
			result = result | this.remove(element);
		
		return result;
	}

	public boolean retainAll(Collection<?> elements) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		int totalSize = 0;
		for (Map.Entry<E, Integer> entry: data.entrySet())
			totalSize += entry.getValue();
		return totalSize;
	}

	public Object[] toArray() {
		Object[] result = new Object[this.size()];
		int i = 0;
		
		for (Map.Entry<E, Integer> entry: data.entrySet()) {
			E obj = entry.getKey();
			int n = entry.getValue();
			for (int j = 0; j < n; j++, i++)
				result[i] = obj;
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] dummy) {
		return (T[])(this.toArray());
	}
	
	public String toString() {
		return "[" + StringUtils.join(this.entrySet().iterator(), ", ") + "]";
	}
}
