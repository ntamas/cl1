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
	protected class IteratorImpl implements Iterator<MutableNodeSet> {
		/** Iterator over the edges of the graph */
		Iterator<Edge> edgeIt;
		
		/** Constructs the iterator */
		IteratorImpl() {
			edgeIt = graph.iterator();
		}
		
		public boolean hasNext() {
			return edgeIt.hasNext();
		}

		public MutableNodeSet next() {
			MutableNodeSet result = new MutableNodeSet(graph);
			Edge edge = edgeIt.next();
			result.add(edge.source);
			result.add(edge.target);
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/** Constructs a seed generator for the given graph that considers every edge as a seed nodeset */
	public EveryEdgeSeedGenerator(Graph graph) {
		super(graph);
	}

	public Iterator<MutableNodeSet> iterator() {
		return new IteratorImpl();
	}
	
	/**
	 * Returns the number of edges in the graph.
	 */
	public int size() {
		return this.graph.getEdgeCount();
	}
}
