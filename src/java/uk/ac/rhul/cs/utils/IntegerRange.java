package uk.ac.rhul.cs.utils;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A finite range of integers.
 * 
 * @author tamas
 */
public class IntegerRange extends AbstractList<Integer> {
	/**
	 * The start of the range, inclusive.
	 */
	private int start;
	
	/**
	 * The step size of the range.
	 */
	private int step;
	
	/**
	 * The number of unique elements in the range.
	 */
	private int length;
	
	/**
	 * Constructs an integer range between zero and the given element.
	 * 
	 * @param  end    the end of the range, exclusive
	 */
	public IntegerRange(int end) {
		this(0, end, 1);
	}
	
	/**
	 * Constructs an integer range between the two given elements.
	 * 
	 * @param  start  the start of the range, inclusive
	 * @param  end    the end of the range, exclusive
	 */
	public IntegerRange(int start, int end) {
		this(start, end, 1);
	}
	
	/**
	 * Constructs an integer range between the two given elements with the
	 * given step size.
	 * 
	 * @param  start  the start of the range, inclusive
	 * @param  end    the end of the range, exclusive
	 * @param  step   the step size
	 */
	public IntegerRange(int start, int end, int step) {
		this.start = start;
		this.step = step;
		this.length = (int)Math.ceil((end-start) / (double)step);
		if (this.length < 0)
			this.length = 0;
	}
	
	/**
	 * Returns the given element from the range.
	 */
	public Integer get(int index) {
		if (index < 0 || index >= this.length)
			throw new IndexOutOfBoundsException();
		return this.start + this.step * index;
	}
	
	/**
	 * Returns an iterator over the elements in this list.
	 */
	public Iterator<Integer> iterator() {
		return new IteratorImpl(0);
	}
	
	/**
	 * Returns a list iterator over the elements in this list.
	 */
	public ListIterator<Integer> listIterator() {
		return new IteratorImpl(0);
	}
	
	/**
	 * Returns a list iterator over the elements in this list from the given index.
	 */
	public ListIterator<Integer> listIterator(int index) {
		return new IteratorImpl(index);
	}
	
	/**
	 * Returns the number of unique elements in the range.
	 */
	public int size() {
		return this.length;
	}
	
	/**
	 * Implementation of an iterator over the range
	 */
	class IteratorImpl implements ListIterator<Integer> {
		int index;
		
		public IteratorImpl(int index) {
			this.index = index;
			if (index < 0 || index >= length)
				throw new IndexOutOfBoundsException("List index out of bounds: " + index);
		}
		
		public void add(Integer arg0) {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return index < length;
		}

		public boolean hasPrevious() {
			return index > 0;
		}

		public Integer next() {
			int result = start + index * step;
			if (index >= length)
				throw new NoSuchElementException();
			index++;
			return result;
		}

		public int nextIndex() {
			return index;
		}

		public Integer previous() {
			index--;
			int result = start + index * step;
			if (index < 0)
				throw new NoSuchElementException();
			return result;
		}

		@Override
		public int previousIndex() {
			return index-1;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void set(Integer arg0) {
			throw new UnsupportedOperationException();
		}
	}
}
