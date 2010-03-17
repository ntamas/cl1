package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import uk.ac.rhul.cs.stats.H1;
import uk.ac.rhul.cs.stats.MannWhitneyTest;

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
	
	/**
	 * Constructs a new mutable nodeset from the given non-mutable nodeset
	 * 
	 * @param nodeSet  the original, non-mutable nodeset
	 */
	public MutableNodeSet(NodeSet nodeSet) {
		this(nodeSet.getGraph(), nodeSet.getMembers());
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
		
		/* Things will change, invalidate the cached values */
		invalidateCache();
		
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
		/* Things will change, invalidate the cached values */
		invalidateCache();
		
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
	 * Performs a haircut operation on the nodeset
	 * 
	 * The haircut operation tries to eliminate vertices that connect only loosely
	 * to the rest of the nodeset. This is achieved by removing vertices whose
	 * internal weight is less than some percentage (e.g., 20%) of the average
	 * internal weight of the cluster.
	 */
	public void haircut(double threshold) {
		while (!this.members.isEmpty()) {
			int minIdx = this.members.first();
			double minInWeight = this.inWeights[minIdx];
			double limit = 2 * this.totalInternalEdgeWeight / this.size() * threshold;
			
			for (int i: this.members) {
				if (this.inWeights[i] < minInWeight) {
					minInWeight = this.inWeights[i];
					minIdx = i;
				}
			}
			if (minInWeight < limit)
				this.remove(minIdx);
			else
				break;
		}
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
	 * Returns the commitment of a node to this nodeset
	 * 
	 * The commitment of a node is defined as the total weight of edges leading from
	 * this node to other members of this nodeset, divided by the total weight of edges
	 * adjacent to the node.
	 * 
	 * @param  nodeIndex    the index of the node
	 * @return the commitment of the node
	 */
	@Override
	public double getCommitment(int nodeIndex) {
		double den = this.inWeights[nodeIndex] + this.outWeights[nodeIndex];
		if (den == 0)
			return 0;
		return this.inWeights[nodeIndex] / den;
	}
	
	/**
	 * Returns the internal weight of a given node
	 */
	@Override
	public double getInternalWeight(int nodeIndex) {
		return this.inWeights[nodeIndex];
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
	
	protected double getSignificanceReal() {
		MannWhitneyTest test = new MannWhitneyTest(this.inWeights, this.outWeights, H1.GREATER_THAN);
		return test.getSP();
	}
	
	/**
	 * Invalidates the cached quality value when the nodeset changes
	 */
	private void invalidateCache() {
		this.quality = null;
	}

	/**
	 * Removes a node from this nodeset
	 * 
	 * @param   node   the index of the node being removed
	 * @return  true if the node was removed, false if the node was not a member
	 */
	public boolean remove(int node) {
		if (!memberHashSet.contains(node))
			return false;
		
		/* Things will change, invalidate the cached values */
		invalidateCache();
		
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
	 * Removes more nodes from this nodeset
	 * 
	 * @param   nodes    a collection of the nodes being removed
	 */
	public void remove(int[] nodes) {
		for (int i: nodes)
			this.remove(i);
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	@Override
	protected void setMembers(int[] members) {
		this.clear();
		for (int member: members)
			this.add(member);
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	public void setMembers(Iterable<Integer> members) {
		this.clear();
		for (int member: members)
			this.add(member);
	}
}
