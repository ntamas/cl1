package uk.ac.rhul.cs.cl1;

import com.sosnoski.util.hashset.IntHashSet;

/**
 * A subset of the nodes of a given graph.
 * 
 * A subset on the given graph classifies the nodes of the graph into two groups:
 * internal nodes (those that are within the subset) and external nodes (those that are
 * outside the subset). External nodes that are adjacent to at least one internal node
 * are called external boundary nodes; similarly, internal nodes that are adjacent to
 * at least one external node are called internal boundary nodes.
 * 
 * The edge classification is similar, but there are four different kinds of edges
 * depending on where the endpoints are. If both endpoints are internal, then the edge
 * is called internal as well. If at least one endpoint is external, the edge is called
 * external. Edges with <i>exactly</i> one external endpoint are called boundary
 * edges.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class NodeSet {
	/**
	 * The graph associated with this node set
	 */
	protected Graph graph = null;
	
	/**
	 * The list of node indices in the set.
	 * 
	 * This list is always sorted in ascending order.
	 */
	protected int[] nodes = null;
	
	/**
	 * Total weight of the internal edges
	 */
	protected double totalInternalEdgeWeight = 0.0;
	
	/**
	 * Total weight of the boundary edges
	 */
	protected double totalBoundaryEdgeWeight = 0.0;
	
	/**
	 * Constructs a new, empty nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public NodeSet(Graph graph) {
		this(graph, null);
	}
	
	/**
	 * Constructs a new nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public NodeSet(Graph graph, int[] members) {
		super();
		this.graph = graph;
		if (members != null)
			this.setMembers(members);
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	protected void setMembers(int[] members) {
		IntHashSet set = new IntHashSet();
		int i, j;
		
		for (i = 0; i < members.length; i++)
			set.add(i);
		
		this.totalBoundaryEdgeWeight = 0.0;
		this.totalInternalEdgeWeight = 0.0;
		
		for (i = 0; i < members.length; i++) {
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(members[i], Directedness.ALL);
			for (j = 0; j < edgeIdxs.length; j++) {
				double weight = this.graph.getEdgeWeight(edgeIdxs[j]);
				int endpoint = this.graph.getEdgeEndpoint(edgeIdxs[j], i);
				if (set.contains(endpoint)) {
					/* This is an internal edge */
					this.totalInternalEdgeWeight += weight;
				} else {
					/* This is a boundary edge */
					this.totalBoundaryEdgeWeight += weight;
				}
			}
		}
		
		/* Internal edges were found twice, divide the result by two */
		this.totalInternalEdgeWeight /= 2.0;
	}
	
	/**
	 * Returns the total internal edge weight in this nodeset
	 */
	public double getTotalInternalEdgeWeight() {
		return this.totalInternalEdgeWeight;
	}
	
	/**
	 * Returns the total boundary edge weight in this nodeset
	 */
	public double getTotalBoundaryEdgeWeight() {
		return this.totalBoundaryEdgeWeight;
	}
}
