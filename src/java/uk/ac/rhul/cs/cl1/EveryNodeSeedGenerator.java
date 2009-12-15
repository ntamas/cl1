package uk.ac.rhul.cs.cl1;

/**
 * Seed generator class where every single node of a graph will be generated as
 * a seed nodeset.
 * 
 * @author tamas
 */
public class EveryNodeSeedGenerator extends SeedGenerator {
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	private class IteratorImpl extends SeedIterator {
		/** Node counter */
		private int i;
		/** Maximum node count */
		private int n;
		
		/** Constructs the iterator */
		IteratorImpl() {
			n = graph.getNodeCount();
			i = 0;
		}
		
		public boolean hasNext() {
			return i < n;
		}

		public MutableNodeSet next() {
			MutableNodeSet result = new MutableNodeSet(graph);
			result.add(i);
			i++;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/** Constructs a seed generator that is not associated to any graph yet */
	public EveryNodeSeedGenerator() {
		super();
	}
	
	/** Constructs a seed generator for the given graph that considers every node as a seed nodeset. */
	public EveryNodeSeedGenerator(Graph graph) {
		super(graph);
	}

	/**
	 * Iterates over each node of the graph as a MutableNodeSet.
	 * 
	 * The node count of the graph must stay the same while generating
	 * seed nodesets.
	 */
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
	
	/**
	 * Returns the number of nodes in the graph.
	 */
	public int size() {
		return this.graph.getNodeCount();
	}
}