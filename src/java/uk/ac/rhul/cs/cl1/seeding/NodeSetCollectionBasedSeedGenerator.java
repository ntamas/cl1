package uk.ac.rhul.cs.cl1.seeding;

import java.util.Arrays;
import java.util.Collection;
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
	
	/**
	 * Constructs a seed generator backed by the given {@link NodeSet} collection
	 */
	public NodeSetCollectionBasedSeedGenerator(Collection<NodeSet> nodeSets) {
		super();
		this.nodeSets = nodeSets;
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
		return nodeSets.size();
	}
	
	/**
	 * Returns an iterator that iterates over the nodesets given at construction time
	 */
	public SeedIterator iterator() {
		return new NodeSetCollectionBasedSeedIterator(nodeSets);
	}
}
