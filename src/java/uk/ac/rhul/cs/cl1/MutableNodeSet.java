package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import uk.ac.rhul.cs.graph.Directedness;
import uk.ac.rhul.cs.graph.Edge;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.stats.independentsamples.MannWhitneyTest;
import uk.ac.rhul.cs.stats.tests.H1;

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
	 * edges incident on the node. For nodes outside the set, this array stores
	 * the total weight of boundary edges incident on the node.
	 */
	protected double[] inWeights = null;
	
	/**
	 * Stores the total weight of each node, i.e. the sum of all the weights
	 * incident on the given node. This array is useful because it holds that:
	 *
	 * <ul>
	 *     <li>For nodes within the set, this array stores the total weight
	 *     of <em>boundary</em> edges incident on the node.</li>
	 *     <li>For nodes outside the set, this array stores the total weight
	 *     of <em>external</em> edges incident on the node.</li>
	 * </ul>
	 */
	protected double[] totalWeights = null;
	
	/**
	 * Constructs a new, empty mutable nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public MutableNodeSet(Graph graph) {
		super(graph);
		this.members = new TreeSet<Integer>();
		initializeInAndTotalWeights();
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
	 * Constructs a new mutable nodeset by cloning an existing one.
	 *
	 * @param  nodeSet  the nodeset to clone
	 */
	private MutableNodeSet(MutableNodeSet nodeSet) {
		super(nodeSet.graph);

		this.members = new TreeSet<Integer>(nodeSet.members);
		for (int member: members) {
			this.memberHashSet.add(member);
		}

		totalInternalEdgeWeight = nodeSet.totalInternalEdgeWeight;
		totalBoundaryEdgeWeight = nodeSet.totalBoundaryEdgeWeight;

		inWeights = nodeSet.inWeights.clone();
		totalWeights = nodeSet.totalWeights; // .clone();
	}

	/**
	 * Constructs a new mutable nodeset from the given non-mutable nodeset
	 * 
	 * @param nodeSet  the original, non-mutable nodeset
	 */
	public MutableNodeSet(NodeSet nodeSet) {
		this(nodeSet.getGraph(), nodeSet.getMembers());
	}
	
	protected void initializeInAndTotalWeights() {
		totalInternalEdgeWeight = 0.0;
		totalBoundaryEdgeWeight = 0.0;
		
		if (inWeights == null) {
			inWeights = new double[graph.getNodeCount()];
		} else {
			Arrays.fill(inWeights, 0.0);
		}

		if (totalWeights == null) {
			totalWeights = new double[graph.getNodeCount()];
			for (Edge e: graph) {
				totalWeights[e.source] += e.weight;
				totalWeights[e.target] += e.weight;
			}
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
		 * appropriate amounts. Here we are actually increasing totalBoundaryEdgeWeight
		 * by outWeights[node] - inWeights[node] but make use of the fact that
		 * outWeights[node] = totalWeights[node] - inWeights[node] */
		totalInternalEdgeWeight += inWeights[node];
		totalBoundaryEdgeWeight += totalWeights[node] - 2 * inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments to inWeights */
		for (int adjEdge: graph.getAdjacentEdgeIndicesArray(node, Directedness.ALL)) {
			int adjNode = graph.getEdgeEndpoint(adjEdge, node);
			if (adjNode == node)
				continue;
			
			inWeights[adjNode] += graph.getEdgeWeight(adjEdge);
		}
		
		/* Add the node to the nodeset */
		memberHashSet.add(node);
		members.add(node);
		
		return true;
	}
	
	/**
	 * Adds more nodes to this nodeset
	 * 
	 * @param   nodes    a collection of the nodes being added
	 * @return  the number of nodes that were not members originally
	 */
	public int add(int[] nodes) {
		int result = 0;
		
		for (int i: nodes)
			if (this.add(i))
				result++;
		
		return result;
	}
	
	/**
	 * Clears the nodeset
	 */
	public void clear() {
		/* Things will change, invalidate the cached values */
		invalidateCache();
		
		this.members.clear();
		this.memberHashSet.clear();
		initializeInAndTotalWeights();
	}

	/**
	 * Creates a semantically equivalent copy of this MutableNodeSet.
	 */
	public MutableNodeSet clone() {
		return new MutableNodeSet(this);
	}

	/**
	 * Freezes the nodeset (i.e. converts it to a non-mutable NodeSet)
	 */
	public NodeSet freeze() {
		return new NodeSet(this.graph, this.members);
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
		double den = this.totalWeights[nodeIndex];
		return den == 0 ? 0 : (this.inWeights[nodeIndex] / den);
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
	 * Returns an IntHashSet for efficient repeated membership checks
	 * 
	 * MutableNodeSet maintains memberHashSet in parallel with the ordinary members
	 * variable, so we just return it here.
	 */
	protected IntHashSet getMemberHashSet() {
		return memberHashSet;
	}
	
	protected double getSignificanceReal() {
		int i, n = members.size();
		double[] memberInWeights = new double[n];
		double[] memberOutWeights = new double[n];

		i = 0;
		for (int member: members) {
			memberInWeights[i] = inWeights[member];
			memberOutWeights[i] = totalWeights[member] - memberInWeights[i];
			i++;
		}

		MannWhitneyTest test = new MannWhitneyTest(memberInWeights, memberOutWeights, H1.GREATER_THAN);
		return test.getSP();
	}
	
	/**
	 * Invalidates the cached member variables when the nodeset changes
	 */
	private void invalidateCache() {
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
		 * appropriate amounts. Here we are actually decreasing totalBoundaryEdgeWeight
		 * by outWeights[node] - inWeights[node] but make use of the fact that
		 * outWeights[node] = totalWeights[node] - inWeights[node] */
		totalInternalEdgeWeight -= inWeights[node];
		totalBoundaryEdgeWeight -= totalWeights[node] - 2 * inWeights[node];
		
		/* For each edge adjacent to the given node, make some adjustments to inWeights */
		for (int adjEdge: graph.getAdjacentEdgeIndicesArray(node, Directedness.ALL)) {
			int adjNode = graph.getEdgeEndpoint(adjEdge, node);
			if (adjNode == node)
				continue;
			
			double weight = graph.getEdgeWeight(adjEdge);
			inWeights[adjNode]  -= weight;
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
