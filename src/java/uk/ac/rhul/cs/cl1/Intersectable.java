package uk.ac.rhul.cs.cl1;

/**
 * Abstract interface for objects that can be intersected.
 * 
 * @author tamas
 */
public interface Intersectable<T> {
	/**
	 * Returns the size of the intersection of this object with another.
	 * 
	 * @param   other  the other object
	 * @return  the number of elements in the intersection or -1 if it does
	 *          not make sense.
	 */
	public int getIntersectionSizeWith(T other);
	
	/**
	 * Returns the intersection of this object with another.
	 * 
	 * @param   other  the other object
	 * @return  the intersection of this object and the other one.
	 */
	public T getIntersectionWith(T other);
}
