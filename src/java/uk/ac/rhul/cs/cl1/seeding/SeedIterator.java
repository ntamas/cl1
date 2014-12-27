package uk.ac.rhul.cs.cl1.seeding;

import java.util.Iterator;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Iterator that iterates over {@link Seed}s.
 * 
 * This class provides an extra feature compared to "plain" iterators:
 * it can estimate its own length (i.e. the total number of items that the
 * iterator will yield) or declare that a reasonable estimate cannot be
 * provided. This is used to implement support for progress bars during
 * the course of the ClusterONE algorithm.
 *
 * @author tamas
 */
public abstract class SeedIterator implements Iterator<Seed> {
	/**
	 * Returns the number of seeds that the seed iterator will generate.
	 *
	 * This may also be an estimate. If the number of seeds cannot be known in
	 * advance and cannot be estimated in any reasonable way, -1 will be returned.
	 *
	 * It is allowed for the iterator to return different values from this
	 * method as the iteration progresses.
	 *
t	 * @return   the number of seeds that the iterator will generate
	 */
	public int getEstimatedLength() {
		return -1;
	}
	
	/**
	 * Removals are not supported in seed iterators by default.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
