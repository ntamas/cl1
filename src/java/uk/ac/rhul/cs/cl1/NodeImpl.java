package uk.ac.rhul.cs.cl1;

import java.lang.ref.WeakReference;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

/// Implementation of a GINY node
public class NodeImpl implements Node {
	/// Weak reference to the root graph which owns this edge
	WeakReference<RootGraph> graph;
	/// String identifier of this node (should be unique)
	String identifier;

	/** 
	 * Constructs a new node.
	 * 
	 * @param   graph   the graph where this edge belongs to
	 */
	NodeImpl(RootGraph graph) {
		this.graph  = new WeakReference<RootGraph>(graph);
	}
	
	/**
	 * @deprecated
	 */
	public GraphPerspective getGraphPerspective() {
		throw new AssertionError("graph perspective handling not implemented");
	}

	/**
	 * @deprecated
	 */
	public boolean setGraphPerspective(GraphPerspective perspective) {
		throw new AssertionError("graph perspective handling not implemented");
	}
	
	/// Returns the unique identifier of this node
	public String getIdentifier() {
		return this.identifier;
	}

	/// Returns the root graph which owns this node
	public RootGraph getRootGraph() {
		return this.graph.get();
	}

	/// Returns the index of this node in its root graph
	public int getRootGraphIndex() {
		return this.graph.get().getIndex(this);
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
