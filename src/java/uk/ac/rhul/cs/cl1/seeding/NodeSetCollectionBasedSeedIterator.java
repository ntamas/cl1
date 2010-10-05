package uk.ac.rhul.cs.cl1.seeding;

import java.util.Collection;
import java.util.Iterator;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Seed iterator class that iterates over seeds in a collection.
 * 
 * @author tamas
 */
public class NodeSetCollectionBasedSeedIterator extends SeedIterator {
	/** Iterator that will be used to iterate over the originally supplied NodeSet instances */
	private Iterator<NodeSet> nodeSetIterator = null;
	
	/** Number of seeds generated so far */
	private int processed = 0;
	
	/** Total number of seeds that will be generated */
	private int size = 0;
	
	public NodeSetCollectionBasedSeedIterator(Collection<NodeSet> nodeSets) {
		processed = 0;
		size = nodeSets.size();
		nodeSetIterator = nodeSets.iterator();
	}
	
	public double getPercentCompleted() {
		return 100.0 * processed / size;
	}
	
	public boolean hasNext() {
		return nodeSetIterator.hasNext();
	}

	public MutableNodeSet next() {
		processed++;
		return new MutableNodeSet(nodeSetIterator.next());
	}
}
