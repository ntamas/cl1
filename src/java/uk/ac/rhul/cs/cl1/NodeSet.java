package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
		return other.graph == this.graph && other.members == this.members;
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
	 * Returns the value of the quality function for this nodeset
	 */
	public double getQuality() {
		return this.totalInternalEdgeWeight  / (this.totalInternalEdgeWeight + this.totalBoundaryEdgeWeight);
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

	@Override
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
