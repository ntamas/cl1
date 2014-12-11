package uk.ac.rhul.cs.collections;

import java.util.Collection;
import java.util.Set;

/**
 * A collection that supports order-independent equality, like {@link Set},
 * but may have duplicate elements. A multiset is also sometimes called a
 * <i>bag</i>.
 * 
 * This class is largely based upon the eponymous class in the Google
 * Collections library.
 * 
 * @author tamas
 */
public interface Multiset<E> extends Collection<E> {
	/**
	 * Returns the number of occurrences of an element in this multiset
	 * 
	 * @param  element  the element to count the occurrences of
	 * @return the number of occurrences of the element in this multiset
	 */
	int count(Object element);
	
	/**
	 * Adds a number of occurrences of an element to this multiset.
	 * 
	 * @param  element      the element to add occurrences of
	 * @param  occurrences  the number of occurrences of the element to add
	 * @return the count of the element before the operation
	 * 
	 * @throws IllegalArgumentException  if {@code occurrences} is negative
	 */
	int add(E element, int occurrences);
	
	/**
	 * Removes a number of occurrences of an element from this multiset.
	 * 
	 * If the multiset contains fewer than this number of occurrences, all
	 * occurrences will be removed.
	 * 
	 * @param  element      the element to remove occurrences of
	 * @param  occurrences  the number of occurrences of the element to remove
	 * @return the count of the element before the operation
	 * 
	 * @throws IllegalArgumentException  if {@code occurrences} is negative
	 */
	int remove(E element, int occurrences);
	
	/**
	 * Adds of removes the necessary occurrences of an element such that the
	 * element attains the desired count.
	 * 
	 * @param  element  the element to add/remove occurrences of
	 * @param  count    the desired count of the element
	 * @return the count of the element before the operation
	 * 
	 * @throws IllegalArgumentException  if {@code count} is negative
	 */
	int setCount(E element, int count);
	
	/**
	 * Returns the set of distinct elements in this multiset.
	 */
	Set<E> elementSet();
	
	/**
	 * Returns a view of the contents of this multiset, grouped into
	 * {@code Multiset.Entry} instances, each providing an element and the
	 * corresponding count.
	 */
	Set<Entry<E>> entrySet();
	
	/**
	 * An unmodifiable element-count pair for a multiset.
	 */
	interface Entry<E> {
		/**
		 * Returns the element corresponding to this entry.
		 */
		E getElement();
		
		/**
		 * Returns the count corresponding to this entry.
		 */
		int getCount();
		
		/**
		 * Returns the canonical string representation of this entry,
		 * defined as follows. If the count for this entry is one, this
		 * is simply the string representation of the corresponding
		 * element. Otherwise, it is the string representation of the element,
		 * followed by {@code " x "}, followed by the count.
		 */
		String toString();
	}
}
