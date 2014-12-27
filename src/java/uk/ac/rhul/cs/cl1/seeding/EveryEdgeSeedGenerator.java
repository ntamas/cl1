package uk.ac.rhul.cs.cl1.seeding;

import java.util.Iterator;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.graph.Edge;
import uk.ac.rhul.cs.graph.Graph;

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
		private int totalSteps;

		/** Constructs the iterator */
		IteratorImpl() {
			edgeIt = graph.iterator();
			totalSteps = graph.getEdgeCount();
		}

		public int getEstimatedLength() {
			return totalSteps;
		}

		public boolean hasNext() {
			return edgeIt.hasNext();
		}

		public Seed next() {
			Edge edge = edgeIt.next();
			Seed result = new Seed(graph, edge.source, edge.target);
			return result;
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
