package uk.ac.rhul.cs.cl1;

import java.util.Iterator;

/**
 * Iterator that iterates over all the edges in a graph
 * 
 * Make sure you don't modify the graph while iterating over its edges,
 * otherwise the results are unspecified.
 * 
 * @author tamas
 */
public class EdgeIterator implements Iterator<Edge> {
	/**
	 * The graph over which we are iterating
	 */
	protected Graph graph = null;
	
	/**
	 * Internal index pointer
	 */
	public int edgeIndex = 0;
	
	/**
	 * Constructs an edge iterator
	 * 
	 * @param  graph   the graph over which we are iterating
	 */
	public EdgeIterator(Graph graph) {
		this.graph = graph;
		this.edgeIndex = 0;
	}
	
	@Override
	public boolean hasNext() {
		return this.edgeIndex < this.graph.getEdgeCount();
	}

	@Override
	public Edge next() {
		Edge result = new Edge(this.graph, this.edgeIndex);
		this.edgeIndex++;
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
