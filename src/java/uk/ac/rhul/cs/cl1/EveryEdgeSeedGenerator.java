package uk.ac.rhul.cs.cl1;

import java.util.Iterator;

/**
 * Seed generator class where every single edge of a graph will be generated as
 * a seed nodeset.
 * 
 * @author tamas
 */
public class EveryEdgeSeedGenerator extends SeedGenerator {
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	protected class IteratorImpl extends SeedIterator {
		/** Iterator over the edges of the graph */
		Iterator<Edge> edgeIt;
		
		/** Number of edges that will be generated */
		private double totalSteps;
		
		/** Number of edges generated so far */
		private int steps;
		
		/** Constructs the iterator */
		IteratorImpl() {
			edgeIt = graph.iterator();
			steps = 0;
			totalSteps = graph.getEdgeCount();
		}

		/**
		 * Returns the percentage of edges processed so far.
		 */
		@Override
		public double getPercentCompleted() {
			return 100.0 * steps / totalSteps;
		}
		
		public boolean hasNext() {
			return edgeIt.hasNext();
		}

		public MutableNodeSet next() {
			MutableNodeSet result = new MutableNodeSet(graph);
			Edge edge = edgeIt.next();
			result.add(edge.source);
			result.add(edge.target);
			steps++;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/** Constructs a seed generator that is not associated to any graph yet */
	public EveryEdgeSeedGenerator() {
		super();
	}
	
	/** Constructs a seed generator for the given graph that considers every edge as a seed nodeset */
	public EveryEdgeSeedGenerator(Graph graph) {
		super(graph);
	}

	public SeedIterator iterator() {
		return new IteratorImpl();
	}
	
	/**
	 * Returns the number of edges in the graph.
	 */
	public int size() {
		return this.graph.getEdgeCount();
	}
}
