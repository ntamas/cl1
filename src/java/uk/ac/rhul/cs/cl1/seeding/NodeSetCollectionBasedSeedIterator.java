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
	
	/** Total number of seeds that will be generated */
	private int size = 0;
	
	public NodeSetCollectionBasedSeedIterator(Collection<NodeSet> nodeSets) {
		size = nodeSets.size();
		nodeSetIterator = nodeSets.iterator();
	}
	
	public int getEstimatedLength() {
		return size;
	}
	
	public boolean hasNext() {
		return nodeSetIterator.hasNext();
	}

	public Seed next() {
		return new Seed(nodeSetIterator.next());
	}
}
