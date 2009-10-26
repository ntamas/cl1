package uk.ac.rhul.cs.cl1;

/**
 * Simple class template storing an immutable pair of objects.
 * 
 * @author tamas
 */
public class Pair<L, R> {
	/** Left value */
	private final L left;

	/** Right value */
	private final R right;
	
	/** Returns the left value */
	public L getLeft() {
		return left;
	}
	
	/** Returns the right value */
	public R getRight() {
		return right;
	}
	
	/** Constructs a pair from the given values */
	public Pair(final L left, final R right) {
		this.left = left;
		this.right = right;
	}
	
	/** Factory method that saves some typing in most cases */
	public static <A, B> Pair<A, B> create(A left, B right) {
		return new Pair<A, B>(left, right);
	}
	
	/** Equality testing for pairs */
	@SuppressWarnings("unchecked")
	public final boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		
		final Pair<?, ?> other = (Pair)o;
		return equal(getLeft(), other.getLeft()) && equal(getRight(), other.getRight());
	}
	
	private static final boolean equal(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}
	
	/** Returns a hash code for this pair */
	public int hashCode() {
		int hLeft = getLeft() == null ? 0 : getLeft().hashCode();
		int hRight = getRight() == null ? 0 : getRight().hashCode();
		
		return hLeft + (37 * hRight);
	}
}
