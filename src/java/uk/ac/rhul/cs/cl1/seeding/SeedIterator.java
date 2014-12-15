package uk.ac.rhul.cs.cl1.seeding;

import java.util.Iterator;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Iterator that iterates over {@link Seed}s.
 * 
 * This class provides an extra feature compared to "plain" iterators:
 * the clustering algorithm will report back to the iterator whenever a
 * cluster is found, and the iterator has the chance to adapt its
 * behaviour if needed. This is used to implement {@link UnusedNodesSeedGenerator}
 * where nodes that are found in a cluster are not generated by the iterator
 * later on.
 * 
 * @author tamas
 */
public abstract class SeedIterator implements Iterator<Seed> {
	/**
	 * Returns the percentage of seeds that have already been generated.
	 * 
	 * This may also be an estimate. If the number of seeds cannot be known in
	 * advance and cannot be estimated in any reasonable way, -1 will be returned.
	 * 
	 * @return   the percentage of seeds that have already been generated.
	 */
	public double getPercentCompleted() {
		return -1;
	}
	
	/**
	 * Method that will be called whenever a cluster is found.
	 * 
	 * This method does nothing by default.
	 */
	public void processFoundCluster(NodeSet cluster) {
	}
	
	/**
	 * Removals are not supported in seed iterators by default.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
