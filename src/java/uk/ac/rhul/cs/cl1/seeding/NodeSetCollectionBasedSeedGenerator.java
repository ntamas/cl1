package uk.ac.rhul.cs.cl1.seeding;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Seed generator class that takes a collection of NodeSet objects and
 * returns each one as a seed.
 * 
 * @author tamas
 */
public class NodeSetCollectionBasedSeedGenerator extends SeedGenerator {
	/** NodeSet backing this generator */
	private Collection<NodeSet> nodeSets = null;
	
	/** Expected number of nodesets that will be returned */
	private int size = 0;
	
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	protected class IteratorImpl extends SeedIterator {
		/** Iterator that will be used to iterate over the originally supplied NodeSet instances */
		private Iterator<NodeSet> nodeSetIterator = null;
		
		/** Number of seeds generated so far */
		private int processed = 0;
		
		public IteratorImpl() {
			processed = 0;
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

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Constructs a seed generator backed by the given {@link NodeSet} collection
	 */
	public NodeSetCollectionBasedSeedGenerator(Collection<NodeSet> nodeSets) {
		super();
		this.nodeSets = nodeSets;
		size = nodeSets.size();
	}
	
	/**
	 * Constructs a seed generator backed by a single {@link NodeSet}
	 */
	public NodeSetCollectionBasedSeedGenerator(NodeSet nodeSet) {
		this(Arrays.asList(nodeSet));
	}
	
	/**
	 * Returns the expected number of seeds in this generator
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Returns an iterator that iterates over the nodesets given at construction time
	 */
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
}
