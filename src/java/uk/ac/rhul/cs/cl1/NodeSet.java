package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.ac.rhul.cs.graph.BreadthFirstSearch;
import uk.ac.rhul.cs.graph.Directedness;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.stats.independentsamples.MannWhitneyTest;
import uk.ac.rhul.cs.stats.tests.H1;
import uk.ac.rhul.cs.utils.StringUtils;
import uk.ac.rhul.cs.utils.UniqueIDGenerator;

import com.sosnoski.util.array.IntArray;
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
public class NodeSet implements Iterable<Integer>, Intersectable<NodeSet>, Sized {
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
	 * Significance of the nodeset
	 */
	protected Double significance = null;
	
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
	 * Compares a nodeset with another (lexicographical order).
	 */
	public int compareTo(NodeSet other) {
		if (this.members == null && other.members == null)
			return 0;
		if (this.members == null)
			return -1;
		if (other.members == null)
			return 1;
		
		Iterator<Integer> it1 = this.members.iterator();
		Iterator<Integer> it2 = other.members.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			Integer i1 = it1.next(), i2 = it2.next();
			if (i1 < i2)
				return -1;
			if (i1 > i2)
				return 1;
		}
		if (it1.hasNext())
			return 1;
		if (it2.hasNext())
			return -1;
		return 0;
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
	 * @param    idxs   indexes of the node being tested
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
	 * @param    idxs   indexes of the node being tested
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
	 * Returns the commitment of a node to this nodeset
	 * 
	 * The commitment of a node is defined as the total weight of edges leading from
	 * this node to other members of this nodeset, divided by the total weight of edges
	 * adjacent to the node.
	 * 
	 * @param  nodeIndex    the index of the node
	 * @return the commitment of the node
	 */
	public double getCommitment(int nodeIndex) {
		IntHashSet memberHashSet = this.getMemberHashSet();
		double in = 0.0, out = 0.0;
		int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(nodeIndex, Directedness.ALL);
		for (int edgeIdx: edgeIdxs) {
			double weight = this.graph.getEdgeWeight(edgeIdx);
			int endpoint = this.graph.getEdgeEndpoint(edgeIdx, nodeIndex);
			if (memberHashSet.contains(endpoint)) {
				/* This is an internal edge */
				in += weight;
			} else {
				out += weight;
			}
		}
		
		if (in + out == 0)
			return 0.0;
		
		return in / (in + out);
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
	 * Returns the names of the members of this nodeset
	 * @return the names of the members
	 */
	public String[] getMemberNames() {
		String[] result = new String[this.members.size()];
		int i = 0;
		
		for (Integer member: this.members) {
			result[i] = this.graph.getNodeName(member);
			i++;
		}
		
		return result;
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
	 * Checks whether the nodeset is connected in the graph
	 */
	public boolean isConnected() {
		if (this.members.size() < 2)
			return true;
		
		BreadthFirstSearch bfs = new BreadthFirstSearch(this.graph, this.members.first());
		Integer[] dummy = {};
		bfs.restrictToSubgraph(this.members.toArray(dummy));
		
		return bfs.toArray().length == this.members.size();
	}
	
	/**
	 * Checks whether the given node is a cut vertex of the nodeset.
	 * 
	 * A vertex is a cut vertex of the nodeset if its removal would make the
	 * nodeset disconnected. 
	 */
	public boolean isCutVertex(int index) {
		if (this.members.isEmpty())
			return false;
		
		IntArray newMembers = new IntArray();
		for (int member: this.members)
			if (member != index)
				newMembers.add(member);
		
		if (newMembers.size() == 0)
			return false;
		
		BreadthFirstSearch bfs = new BreadthFirstSearch(this.graph, newMembers.get(0));
		bfs.restrictToSubgraph(newMembers.toArray());
		
		return bfs.toArray().length != newMembers.size();
	}
	
	/**
	 * Returns whether the nodeset is empty or not
	 */
	public boolean isEmpty() {
		return this.members.isEmpty();
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
	protected void setMembers(Iterable<Integer> members) {
		this.members = new TreeSet<Integer>();
		if (members == null)
			return;
		
		for (Integer member: members)
			this.members.add(member);
		
		recalculate();
	}
	
	/**
	 * Sets the members of this nodeset
	 */
	protected void setMembers(int[] members) {
		this.members = new TreeSet<Integer>();
		if (members == null)
			return;
		
		for (int member: members)
			this.members.add(member);
		
		recalculate();
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
	 * Returns the internal weight of a given node
	 */
	public double getInternalWeight(int nodeIndex) {
		IntHashSet memberHashSet = this.getMemberHashSet();
		double result = 0.0;
		int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(nodeIndex, Directedness.ALL);
		for (int edgeIdx: edgeIdxs) {
			double weight = this.graph.getEdgeWeight(edgeIdx);
			int endpoint = this.graph.getEdgeEndpoint(edgeIdx, nodeIndex);
			if (memberHashSet.contains(endpoint)) {
				/* This is an internal edge */
				result += weight;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the intersection of this nodeset with another
	 */
	public NodeSet getIntersectionWith(NodeSet other) {
		Set<Integer> smaller;
		IntHashSet larger;
		IntArray intersection = new IntArray();
		
		if (this.size() < other.size()) {
			smaller = this.members;
			larger = other.getMemberHashSet();
		} else {
			smaller = other.members;
			larger = this.getMemberHashSet();
		}
		
		for (int member: smaller)
			if (larger.contains(member))
				intersection.add(member);
		
		return new NodeSet(this.getGraph(), intersection.toArray());
	}
	
	/**
	 * Returns the size of the intersection between this nodeset and another
	 */
	public int getIntersectionSizeWith(NodeSet other) {
		int isectSize = 0;
		Set<Integer> smaller;
		IntHashSet larger;
		
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
	 * Returns the statistical significance of the nodeset
	 * 
	 * The statistical significance of the nodeset is the p-value of a one-sided
	 * Mann-Whitney U test on the in-weights and the out-weights. It tells us
	 * whether the mean in-weight is significantly larger than the mean
	 * out-weight; in other words, it roughly tells us what is the probability of
	 * the community satisfying the weak criterion of Radicchi et al purely
	 * by chance.
	 */
	public double getSignificance() {
		if (significance == null)
			significance = this.getSignificanceReal();
		return significance;
	}
	
	protected double getSignificanceReal() {
		double[] inWeights = new double[this.size()];
		double[] outWeights = new double[this.size()];
		IntHashSet memberHashSet = this.getMemberHashSet();
		int j;
		
		Arrays.fill(inWeights, 0.0);
		Arrays.fill(outWeights, 0.0);
		
		j = 0;
		for (int i: members) {
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(i, Directedness.ALL);
			for (int edgeIdx: edgeIdxs) {
				double weight = this.graph.getEdgeWeight(edgeIdx);
				int endpoint = this.graph.getEdgeEndpoint(edgeIdx, i);
				if (memberHashSet.contains(endpoint)) {
					/* This is an internal edge */
					inWeights[j] += weight;
				} else {
					/* This is a boundary edge */
					outWeights[j] += weight;
				}
			}
			j++;
		}
		
		/* Internal edges were found twice, divide the result by two */
		MannWhitneyTest test = new MannWhitneyTest(inWeights, outWeights, H1.GREATER_THAN);
		return test.getSP();
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
	public Set<Integer> getExternalBoundaryNodes() {
		IntHashSet seen = new IntHashSet(this.getMemberHashSet());
		Set<Integer> result = new TreeSet<Integer>();

		for (int i: members) {
			int[] edgeIdxs = this.graph.getAdjacentEdgeIndicesArray(i, Directedness.ALL);
			for (int edgeIdx: edgeIdxs) {
				int endpoint = this.graph.getEdgeEndpoint(edgeIdx, i);
				if (!seen.contains(endpoint)) {
					/* This is an external boundary node that we haven't seen yet */
					seen.add(endpoint);
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
		return toString(" ");
	}
	
	/**
	 * Prints the nodes in this set to a string using a given separator
	 */
	public String toString(String separator) {
		return StringUtils.join(getMemberNames(), separator);
	}
}
