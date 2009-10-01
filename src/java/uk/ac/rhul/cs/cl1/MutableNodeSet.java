package uk.ac.rhul.cs.cl1;

import java.util.Arrays;

import com.sosnoski.util.hashset.IntHashSet;

/**
 * A mutable subset of the nodes of a given graph.
 * 
 * This class is used to grow cohesive subgroups on a given graph, starting from a seed
 * node. The node set gives facilities to query the total weight of internal and boundary
 * edges or to iterate over the set of external boundary nodes.
 * 
 * @see NodeSet
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class MutableNodeSet extends NodeSet {
	/**
	 * The list of node indices in the set
	 */
	protected IntHashSet nodeSet = new IntHashSet();
	
	/**
	 * Auxiliary array used when adding/removing nodes
	 * 
	 * For nodes within the set, this array stores the total weight of internal
	 * edges adjacent to the node. For nodes outside the set, this array stores
	 * the total weight of boundary edges adjacent to the node.
	 */
	protected int[] inWeights = null;
	
	/**
	 * Auxiliary array used when adding/removing nodes
	 * 
	 * For nodes within the set, this array stores the total weight of boundary
	 * edges adjacent to the node. For nodes outside the set, this array stores
	 * the total weight of external edges adjacent to the node.
	 */
	protected int[] outWeights = null;
	
	/**
	 * Constructs a new, empty mutable nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public MutableNodeSet(Graph graph) {
		super(graph);
		
		inWeights = new int[graph.getNodeCount()];
		outWeights = new int[graph.getNodeCount()];
		Arrays.fill(inWeights, 0);
		Arrays.fill(outWeights, 0);
		
		for (Edge e: graph) {
			outWeights[e.source] += e.weight;
			outWeights[e.target] += e.weight;
		}
	}
	
	/**
	 * Adds a node to this nodeset
	 * 
	 * @param   node   the index of the node being added
	 * @return  true if the node was added, false if the node was already a member
	 */
	public boolean add(int node) {
		if (nodeSet.contains(node))
			return false;
		
		/* First, increase the internal and the boundary weights with the
		 * appropriate amounts */
		totalInternalEdgeWeight += inWeights[node];
		totalBoundaryEdgeWeight += outWeights[node] - inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments */
		// TODO
		
		/* Add the node to the nodeset */
		nodeSet.add(node);
		return true;
	}
	
	/**
	 * Removes a node from this nodeset
	 * 
	 * @param   node   the index of the node being added
	 * @return  true if the node was removed, false if the node was not a member
	 */
	public boolean remove(int node) {
		if (!nodeSet.contains(node))
			return false;
		
		/* First, decrease the internal and the boundary weights with the
		 * appropriate amounts */
		totalInternalEdgeWeight -= inWeights[node];
		totalBoundaryEdgeWeight -= outWeights[node] - inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments */
		// TODO
		
		/* Remove the node from the nodeset */
		nodeSet.remove(node);
		return true;
	}
}
