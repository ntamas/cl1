package uk.ac.rhul.cs.utils;

/**
 * Simple class template storing an immutable unordered pair of
 * objects of the same type.
 * 
 * @author tamas
 */
public class UnorderedPair<T> extends Pair<T, T> {
	/** Constructs a pair from the given values */
	public UnorderedPair(final T left, final T right) {
		super(left, right);
	}
	
	/** Equality testing for pairs */
	public final boolean equals(Object o) {
		if (!(o instanceof UnorderedPair))
			return false;
		
		final UnorderedPair<?> other = (UnorderedPair<?>)o;
		if (equal(getLeft(), other.getLeft()) && equal(getRight(), other.getRight()))
			return true;
		if (equal(getRight(), other.getLeft()) && equal(getLeft(), other.getRight()))
			return true;
		return false;
	}
	
	/** Returns a hash code for this pair */
	public int hashCode() {
		int hLeft = getLeft() == null ? 0 : getLeft().hashCode();
		int hRight = getRight() == null ? 0 : getRight().hashCode();
		
		if (hLeft > hRight)
			return hRight + (37 * hLeft);
		return hLeft + (37 * hRight);
	}
}
