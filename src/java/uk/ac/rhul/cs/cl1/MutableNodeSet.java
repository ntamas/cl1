package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

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
	 * A hash of node indices in the set for quick membership checks
	 */
	protected IntHashSet memberHashSet = new IntHashSet();
	
	/**
	 * Auxiliary array used when adding/removing nodes
	 * 
	 * For nodes within the set, this array stores the total weight of internal
	 * edges adjacent to the node. For nodes outside the set, this array stores
	 * the total weight of boundary edges adjacent to the node.
	 */
	protected double[] inWeights = null;
	
	/**
	 * Auxiliary array used when adding/removing nodes
	 * 
	 * For nodes within the set, this array stores the total weight of boundary
	 * edges adjacent to the node. For nodes outside the set, this array stores
	 * the total weight of external edges adjacent to the node.
	 */
	protected double[] outWeights = null;
	
	/**
	 * Constructs a new, empty mutable nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public MutableNodeSet(Graph graph) {
		super(graph);
		this.members = new TreeSet<Integer>();
		initializeInOutWeights();
	}
	
	/**
	 * Constructs a new nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  a collection containing the member IDs
	 */
	public MutableNodeSet(Graph graph, Collection<Integer> members) {
		this(graph);
		this.setMembers(members);
	}
	
	/**
	 * Constructs a new nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  an array containing the member IDs
	 */
	public MutableNodeSet(Graph graph, int[] members) {
		this(graph);
		this.setMembers(members);
	}
	
	protected void initializeInOutWeights() {
		totalInternalEdgeWeight = 0.0;
		totalBoundaryEdgeWeight = 0.0;
		
		if (inWeights == null)
			inWeights = new double[graph.getNodeCount()];
		if (outWeights == null)
			outWeights = new double[graph.getNodeCount()];
		
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
		if (memberHashSet.contains(node))
			return false;
		
		/* First, increase the internal and the boundary weights with the
		 * appropriate amounts */
		totalInternalEdgeWeight += inWeights[node];
		totalBoundaryEdgeWeight += outWeights[node] - inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments to inWeights and outWeights */
		for (int adjEdge: graph.getAdjacentEdgeIndicesArray(node, Directedness.ALL)) {
			int adjNode = graph.getEdgeEndpoint(adjEdge, node);
			if (adjNode == node)
				continue;
			
			double weight = graph.getEdgeWeight(adjEdge);
			inWeights[adjNode]  += weight;
			outWeights[adjNode] -= weight;
		}
		
		/* Add the node to the nodeset */
		memberHashSet.add(node);
		members.add(node);
		
		return true;
	}
	
	/**
	 * Clears the nodeset
	 */
	public void clear() {
		this.members.clear();
		this.memberHashSet.clear();
		initializeInOutWeights();
	}
	
	/**
	 * Freezes the nodeset (i.e. converts it to a non-mutable NodeSet)
	 */
	public NodeSet freeze() {
		NodeSet result = new NodeSet(this.graph);
		result.members = this.members;
		result.totalInternalEdgeWeight = this.totalInternalEdgeWeight;
		result.totalBoundaryEdgeWeight = this.totalBoundaryEdgeWeight;
		return result;
	}
	
	/**
	 * Returns the addition affinity of a node to this nodeset
	 * 
	 * The addition affinity of a node is defined as the value of the quality function
	 * when the nodeset is augmented by the given node.
	 * 
	 * @param   nodeIndex   the index of the node
	 * @precondition   the node is not in the set
	 */
	public double getAdditionAffinity(int nodeIndex) {
		double num, den;
		
		num = this.totalInternalEdgeWeight + this.inWeights[nodeIndex];
		den = this.totalInternalEdgeWeight + this.totalBoundaryEdgeWeight + this.outWeights[nodeIndex];
		
		return num/den;
	}
	
	/**
	 * Returns the total weight of edges that are adjacent to the given node and another internal node.
	 * 
	 * The query node can either be internal or external. For internal nodes, the returned weight is
	 * equal to the amount with which the total internal edge weight of the node set would decrease
	 * if the node is removed from the cluster. For external nodes, the returned weight is equal to
	 * the amount with which the total internal edge weight of the node set would increase if the
	 * node is added to the cluster.
	 * 
	 * @param   nodeIndex   the index of the node
	 */
	public double getTotalAdjacentInternalWeight(int nodeIndex) {
		return this.inWeights[nodeIndex];
	}
	
	/**
	 * Returns the removal affinity of a node to this nodeset
	 * 
	 * The affinity of a node is defined as the value of the quality function when the node is
	 * removed from the nodeset.
	 * 
	 * @param   nodeIndex   the index of the node
	 * @precondition    the node is already in the set
	 */
	public double getRemovalAffinity(int nodeIndex) {
		double num, den;
		
		num = this.totalInternalEdgeWeight - this.inWeights[nodeIndex];
		den = this.totalInternalEdgeWeight + this.totalBoundaryEdgeWeight - this.outWeights[nodeIndex];
		
		return num/den;
	}
	
	/**
	 * Returns an IntHashSet for efficient repeated membership checks
	 * 
	 * MutableNodeSet maintains memberHashSet in parallel with the ordinary members
	 * variable, so we just return it here.
	 */
	protected IntHashSet getMemberHashSet() {
		return memberHashSet;
	}
	
	/**
	 * Removes a node from this nodeset
	 * 
	 * @param   node   the index of the node being added
	 * @return  true if the node was removed, false if the node was not a member
	 */
	public boolean remove(int node) {
		if (!memberHashSet.contains(node))
			return false;
		
		/* First, decrease the internal and the boundary weights with the
		 * appropriate amounts */
		totalInternalEdgeWeight -= inWeights[node];
		totalBoundaryEdgeWeight -= outWeights[node] - inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments to inWeights and outWeights */
		for (int adjEdge: graph.getAdjacentEdgeIndicesArray(node, Directedness.ALL)) {
			int adjNode = graph.getEdgeEndpoint(adjEdge, node);
			if (adjNode == node)
				continue;
			
			double weight = graph.getEdgeWeight(adjEdge);
			inWeights[adjNode]  -= weight;
			outWeights[adjNode] += weight;
		}
		
		/* Remove the node from the nodeset */
		memberHashSet.remove(node);
		members.remove(node);
		
		return true;
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	public void setMembers(Collection<Integer> members) {
		this.clear();
		for (int member: members)
			this.add(member);
	}
}
