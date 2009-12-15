package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Seed generator class that takes a collection of NodeSet objects and
 * returns each one as a seed.
 * 
 * @author tamas
 */
public class NodeSetCollectionBasedSeedGenerator extends SeedGenerator {
	/** Iterator that will be used to iterate over the originally supplied NodeSet instances */
	private Iterator<NodeSet> nodeSetIterator = null;
	
	/** Expected number of nodesets that will be returned */
	private int size = 0;
	
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	protected class IteratorImpl extends SeedIterator {
		public boolean hasNext() {
			return nodeSetIterator.hasNext();
		}

		public MutableNodeSet next() {
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
		nodeSetIterator = nodeSets.iterator();
		size = nodeSets.size();
	}
	
	/**
	 * Constructs a seed generator backed by a single {@link NodeSet}
	 */
	public NodeSetCollectionBasedSeedGenerator(NodeSet nodeSet) {
		super(nodeSet.getGraph());
		
		nodeSetIterator = Arrays.asList(nodeSet).iterator();
		size = 1;
	}
	
	/**
	 * Constructs a seed generator backed by a few given nodes of the given graph
	 */
	public NodeSetCollectionBasedSeedGenerator(Graph graph, int... members) {
		super(graph);
		
		nodeSetIterator = Arrays.asList(new NodeSet(graph, members)).iterator();
		size = members.length;
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
