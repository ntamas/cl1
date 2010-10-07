package uk.ac.rhul.cs.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * Adapts a BlockingQueue to conform to the {@link Collection} interface with
 * a different behaviour.
 * 
 * By default, {@link BlockingQueue.add} adds an item to the queue only if
 * is not full and throws an exception otherwise. This class modifies the
 * default behaviour: calling {@link BlockingQueueAdapter.add} will forward
 * the call to {@link BlockingQueue.put} instead, which blocks the current
 * thread until more space becomes available in the queue.
 * 
 * @author ntamas
 *
 */
public class BlockingQueueAdapter<E> implements Collection<E> {
	private BlockingQueue<E> queue;
	
	/**
	 * Creates an adapter that adapts the given blocking queue.
	 * 
	 * @param queue  the queue to be adapted
	 */
	public BlockingQueueAdapter(BlockingQueue<E> queue) {
		this.queue = queue;
	}
	
	/**
	 * Returns the number of elements in the queue.
	 */
	public int size() {
		return this.queue.size();
	}

	/**
	 * Returns whether the queue is empty
	 */
	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	/**
	 * Checks whether the queue contains the given object
	 */
	public boolean contains(Object o) {
		return this.queue.contains(o);
	}
	
	/**
	 * Returns an iterator over the queue
	 */
	public Iterator<E> iterator() {
		return this.queue.iterator();
	}
	
	/**
	 * Converts the contents of the queue to an array
	 */
	public Object[] toArray() {
		return this.queue.toArray();
	}

	/**
	 * Converts the contents of the queue to an array
	 */
	public <T> T[] toArray(T[] a) {
		return this.queue.toArray(a);
	}
	
	/**
	 * Adds the given element to the queue, blocking the current thread if
	 * there is not enough space available.
	 * 
	 * @return  true if the element was added successfully, false if the thread
	 *          has been interrupted while waiting for space to become available.
	 */
	public boolean add(E e) {
		try {
			this.queue.put(e);
		} catch (InterruptedException ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * Removes the given object from the queue.
	 */
	public boolean remove(Object o) {
		return this.queue.remove(o);
	}
	
	/**
	 * Returns true if the queue contains all the elements of the given collection
	 */
	public boolean containsAll(Collection<?> c) {
		return this.queue.containsAll(c);
	}
	
	/**
	 * Adds all the elements in the given collection to the queue, blocking the
	 * current thread if there is not enough space available.
	 * 
	 * @return  true if the element was added successfully, false if the thread
	 *          has been interrupted while waiting for space to become available.
	 */
	public boolean addAll(Collection<? extends E> c) {
		for (E elem: c)
			if (!this.queue.add(elem))
				return false;
		return true;
	}

	/**
	 * Removes all the elements in the given collection from the queue.
	 */
	public boolean removeAll(Collection<?> c) {
		return this.queue.removeAll(c);
	}

	/**
	 * Removes all but the elements in the given collection from the queue.
	 */
	public boolean retainAll(Collection<?> c) {
		return this.queue.retainAll(c);
	}
	
	/**
	 * Clears the queue.
	 */
	public void clear() {
		this.queue.clear();
	}
}
