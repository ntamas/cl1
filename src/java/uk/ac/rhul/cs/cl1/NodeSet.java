package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.ac.rhul.cs.utils.StringUtils;

import com.sosnoski.util.array.StringArray;
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
public class NodeSet implements Iterable<Integer> {
	/**
	 * The graph associated with this node set
	 */
	protected Graph graph = null;
	
	/**
	 * The set of node indices in the set.
	 * 
	 * This list is always sorted in ascending order.
	 */
	protected SortedSet<Integer> members = null;
	
	/**
	 * Total weight of the internal edges
	 */
	protected double totalInternalEdgeWeight = 0.0;
	
	/**
	 * Total weight of the boundary edges
	 */
	protected double totalBoundaryEdgeWeight = 0.0;
	
	/**
	 * Quality of the nodeset according to the standard Cluster ONE quality function
	 */
	protected Double quality = null;
	
	/**
	 * Constructs a new, empty nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public NodeSet(Graph graph) {
		this.graph = graph;
	}
	
	/**
	 * Constructs a new nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  a collection containing the member IDs
	 */
	public NodeSet(Graph graph, Collection<Integer> members) {
		this(graph);
		this.setMembers(members);
	}
	
	/**
	 * Constructs a new nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  an array containing the member IDs
	 */
	public NodeSet(Graph graph, int[] members) {
		this(graph);
		this.setMembers(members);
	}

	/**
	 * Checks whether a node is a member of the nodeset or not
	 * @param    idx   index of the node being tested
	 * @return   true if the node is a member of the set, false otherwise
	 */
	public boolean contains(int idx) {
		return members.contains(idx);
	}
	
	/**
	 * Checks whether any of the given nodes is a member of the nodeset or not
	 * @param    nodeIndex   index of the node being tested
	 * @return   true if any node is a member of the set, false otherwise
	 */
	public boolean containsAny(Collection<Integer> idxs) {
		for (Integer i: idxs)
			if (this.members.contains(i))
				return true;
		return false;
	}
	
	/**
	 * Checks whether all of the given nodes are a member of the nodeset or not
	 * @param    nodeIndex   index of the node being tested
	 * @return   true if all the nodes are a member of the set, false otherwise
	 */
	public boolean containsAll(Collection<Integer> idxs) {
		return this.members.containsAll(idxs);
	}
	
	/**
	 * Checks whether two nodesets are equal.
	 * 
	 * Two nodesets are equal if they are the same reference or if they
	 * belong to the same graph and have the same members
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof NodeSet))
			return false;

		NodeSet other = (NodeSet)o;
		return other.graph.equals(this.graph) && other.members.equals(this.members);
	}
	
	/**
	 * Returns the graph this nodeset is associated to
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Returns an IntHashSet for efficient repeated membership checks
	 */
	protected IntHashSet getMemberHashSet() {
		// We use an IntHashSet for membership checks, it's more efficient
		IntHashSet memberSet = new IntHashSet();
		for (int i: members)
			memberSet.add(i);
		return memberSet;
	}
	
	/**
	 * Returns the members of this nodeset
	 * @return the members
	 */
	public SortedSet<Integer> getMembers() {
		return new TreeSet<Integer>(members);
	}

	/**
	 * Returns the hash code of this nodeset
	 * 
	 * This class is overridden to ensure that equal nodesets have equal hash codes
	 */
	public int hashCode() {
		return graph.hashCode() + members.hashCode();
	}
	
	/**
	 * Returns the number of nodes in this nodeset
	 */
	public int size() {
		return this.members.size();
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	protected void setMembers(Collection<Integer> members) {
		if (members == null) {
			this.members = new TreeSet<Integer>();
			return;
		}
		this.members = new TreeSet<Integer>(members);
		recalculate();
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	protected void setMembers(int[] members) {
		if (members == null) {
			this.members = new TreeSet<Integer>();
			return;
		}
		
		List<Integer> list = new ArrayList<Integer>();
		for (int member: members)
			list.add(member);
		this.setMembers(list);
		
		return;
	}
	
	/**
	 * Recalculate some internal variables when the member set changes
	 */
	protected void recalculate() {
		IntHashSet memberHashSet = this.getMemberHashSet();
		
		this.totalBoundaryEdgeWeight = 0.0;
		this.totalInternalEdgeWeight = 0.0;
		
		for (int i: members) {
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(i, Directedness.ALL);
			for (int edgeIdx: edgeIdxs) {
				double weight = this.graph.getEdgeWeight(edgeIdx);
				int endpoint = this.graph.getEdgeEndpoint(edgeIdx, i);
				if (memberHashSet.contains(endpoint)) {
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
	 * Returns the density of this nodeset
	 */
	public double getDensity() {
		if (this.size() < 2)
			return 0.0;

		return 2.0 * this.totalInternalEdgeWeight / (this.size() * (this.size() - 1));
	}
	
	/**
	 * Returns the size of the intersection between this nodeset and another
	 */
	public int getIntersectionSizeWith(NodeSet other) {
		int isectSize = 0;
		Set<Integer> smaller = null;
		IntHashSet larger = null;
		
		if (this.size() < other.size()) {
			smaller = this.members;
			larger = other.getMemberHashSet();
		} else {
			smaller = other.members;
			larger = this.getMemberHashSet();
		}
		
		for (int member: smaller)
			if (larger.contains(member))
				isectSize++;
		
		return isectSize;
	}
	
	/**
	 * Returns the matching ratio of this nodeset and another
	 * 
	 * The matching ratio is the size of the intersection of the two nodesets
	 * squared, divided by the product of the sizes of the two nodesets.
	 * 
	 * @return   the meet/min coefficient
	 * @precondition   the two nodesets must belong to the same graph and they
	 *                 must not be empty
	 */
	public double getMatchingRatioWith(NodeSet other) {
		double isect = this.getIntersectionSizeWith(other);
		return isect * isect / (this.size() * other.size());
	}
	
	/**
	 * Returns the meet/min coefficient of this nodeset with another
	 * 
	 * The meet/min coefficient is the size of the intersection of the two nodesets,
	 * divided by the minimum of the sizes of the two nodesets.
	 * 
	 * @return   the meet/min coefficient
	 * @precondition   the two nodesets must belong to the same graph and they
	 *                 must not be empty
	 */
	public double getMeetMinCoefficientWith(NodeSet other) {
		return (double)(this.getIntersectionSizeWith(other)) / Math.min(this.size(), other.size()); 
	}
	
	/**
	 * Returns the value of the quality function for this nodeset
	 */
	public double getQuality() {
		if (quality == null)
			quality = this.totalInternalEdgeWeight / (this.totalInternalEdgeWeight + this.totalBoundaryEdgeWeight);
		return quality;
	}
	
	/**
	 * Extracts the subgraph spanned by the nodeset and returns it as a new {@link Graph} object
	 * 
	 * @return   the subgraph as a new {@link Graph}
	 */
	public Graph getSubgraph() {
		boolean directed = this.getGraph().isDirected();
		Graph result = new Graph(directed);
		IntHashSet memberSet = this.getMemberHashSet();
		UniqueIDGenerator<Integer> idGen = new UniqueIDGenerator<Integer>(result);

		for (int i: members) {
			int srcId = idGen.get(i);
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(i, Directedness.OUT);
			for (int edgeIdx: edgeIdxs) {
				int endpoint = this.graph.getEdgeEndpoint(edgeIdx, i);
				/* If not an internal edge, continue */
				if (!memberSet.contains(endpoint))
					continue;
				/* Avoid creating each edge twice in undirected graphs */
				if (!directed && i > endpoint)
					continue;
				/* Add the edge */
				result.createEdge(srcId, idGen.get(endpoint), this.graph.getEdgeWeight(edgeIdx));
			}
		}
		
		return result;
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
	
	/**
	 * Returns a set of all the external boundary nodes of this set
	 */
	public Set<Integer> getExternalBoundaryNodeIterator() {
		IntHashSet memberSet = this.getMemberHashSet();
		Set<Integer> result = new TreeSet<Integer>();
		
		for (int i: members) {
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(i, Directedness.ALL);
			for (int edgeIdx: edgeIdxs) {
				int endpoint = this.graph.getEdgeEndpoint(edgeIdx, i);
				if (!memberSet.contains(endpoint)) {
					/* This is an external boundary node */
					result.add(endpoint);
				}
			}
		}
		
		return result;
	}

	/**
	 * Iterates over the members of this nodeset
	 */
	public Iterator<Integer> iterator() {
		return this.members.iterator();
	}
	
	/**
	 * Prints the nodes in this set to a string
	 */
	public String toString() {
		StringArray names = new StringArray();
		
		for (Integer member: this.members) {
			names.add(this.graph.getNodeName(member));
		}
		
		return StringUtils.join(names.iterator(), ' ');
	}
}
