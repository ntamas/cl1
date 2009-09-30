package uk.ac.rhul.cs.cl1;

import java.lang.ref.WeakReference;

import giny.model.Node;
import giny.model.RootGraph;

/// Implementation of a GINY edge
public class EdgeImpl implements giny.model.Edge {
	/// Weak reference to the root graph which owns this edge
	WeakReference<RootGraph> graph;
	/// The index of the source node of this edge
	int source = 0;
	/// The index of the target node of this edge
	int target = 0;
	/// Whether the edge is directed
	boolean directed = false;
	/// String identifier of this edge (should be unique)
	String identifier;
	
	/** 
	 * Constructs a new edge.
	 * 
	 * @param   graph   the graph where this edge belongs to
	 * @param   src     the index of the source node of the edge
	 * @param   dest    the index of the target node of the edge
	 */
	EdgeImpl(RootGraph graph, int src, int dest) {
		this.graph  = new WeakReference<RootGraph>(graph);
		this.source = src;
		this.target = dest;
	}
	
	/// Returns the source node of this edge
	public Node getSource() {
		return this.graph.get().getNode(this.source);
	}

	/// Returns the target node of this edge
	public Node getTarget() {
		return this.graph.get().getNode(this.target);
	}
	
	/// Returns whether the edge is directed
	public boolean isDirected() {
		return this.directed;
	}
	
	/// Returns the identifier of this edge
	public String getIdentifier() {
		return identifier;
	}
	
	/// Returns the root graph which owns this edge
	public RootGraph getRootGraph() {
		return graph.get();
	}
	
	/// Returns the index of this edge in its root graph
	public int getRootGraphIndex() {
		return graph.get().getIndex(this);
	}

	/**
	 * Sets a new identifier for this edge
	 * 
	 * @param  arg  the new identifier for this edge
	 */
	public boolean setIdentifier(String arg) {
		this.identifier = arg;
		return true;
	}

}
