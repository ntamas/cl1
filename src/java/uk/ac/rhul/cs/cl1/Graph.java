package uk.ac.rhul.cs.cl1;

import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphPerspective;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sosnoski.util.array.IntArray;
import com.sosnoski.util.hashmap.StringIntHashMap;

/**
 *  Graph class used in Cluster ONE
 *  
 *  This class uses a simple adjacency list representation in the background.
 */
public class Graph implements giny.model.RootGraph {
	/**
	 * Map from node names to node IDs
	 */
	StringIntHashMap nodeIndexMap = new StringIntHashMap(1000, 0.8, 0, StringIntHashMap.STANDARD_HASH);
	/**
	 * Map from edge names to edge IDs
	 */
	StringIntHashMap edgeIndexMap = new StringIntHashMap(2000, 0.8, 0, StringIntHashMap.STANDARD_HASH);
	
	/**
	 * The list of nodes in this graph
	 */
	ArrayList<Node> nodes = new ArrayList<Node>();
	/**
	 * The list of edges in this graph
	 */
	ArrayList<Edge> edges = new ArrayList<Edge>();
	
	/**
	 * The list of out-neighbors for each node
	 */
	ArrayList<IntArray> outAdjacencyLists = new ArrayList<IntArray>();
	
	/**
	 * The list of in-neighbors for each node
	 */
	ArrayList<IntArray> inAdjacencyLists = new ArrayList<IntArray>();
	
	public boolean addEdgeMetaChild(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean addMetaChild(Node arg0, Node arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean addMetaChild(Node arg0, Edge arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean addNodeMetaChild(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	/**
	 * Returns true iff the given edge is in this graph
	 */
	public boolean containsEdge(Edge edge) {
		return edgeIndexMap.containsKey(edge.getIdentifier());
	}

	/**
	 * Returns true iff the given node is in this graph
	 */
	public boolean containsNode(Node node) {
		return nodeIndexMap.containsKey(node.getIdentifier());
	}

	/**
	 * Creates a new edge between the given nodes
	 * 
	 * The edge will be directed iff src != dest, undirected otherwise.
	 * 
	 * @param  src  the source node
	 * @param  dest the target node
	 * 
	 * @return the index of the new edge or zero if either node is not in this graph.
	 */
	public int createEdge(Node src, Node dest) {
		return createEdge(nodeIndexMap.get(src.getIdentifier()), nodeIndexMap.get(dest.getIdentifier()),
				src != dest);
	}

	/**
	 * Creates a new edge between nodes with the given indices.
	 * 
	 * The edge will be directed iff src != dest, undirected otherwise.
	 * 
	 * @param  src  the source node
	 * @param  dest the target node
	 * 
	 * @return the index of the new edge or zero if either node is not in this graph.
	 */
	public int createEdge(int src, int dest) {
		return createEdge(src, dest, src != dest);
	}

	/**
	 * Creates a new edge between the given nodes
	 * 
	 * @param  src  the source node
	 * @param  dest the target node
	 * @param  directed  whether the edge should be directed
	 * 
	 * @return the index of the new edge or zero if either node is not in this graph.
	 */
	public int createEdge(Node src, Node dest, boolean directed) {
		return createEdge(nodeIndexMap.get(src.getIdentifier()), nodeIndexMap.get(dest.getIdentifier()),
				directed);
	}

	/**
	 * Creates a new edge between the given nodes
	 * 
	 * @param  src  the source node
	 * @param  dest the target node
	 * @param  directed  whether the edge should be directed
	 * 
	 * @return the index of the new edge or zero if either node is not in this graph.
	 */
	public int createEdge(int src, int dest, boolean directed) {
		if (src == 0 || dest == 0)
			return 0;
		
		edges.add(new EdgeImpl(this, src, dest));
		int result = edges.size();
		outAdjacencyLists.get(src).add(result);
		inAdjacencyLists.get(dest).add(result);
		return edges.size();
	}

	/**
	 * Creates new edges between the given nodes
	 * 
	 * @param  srcs  the source nodes
	 * @param  dests the target nodes
	 * @param  directed  whether the edges should be directed
	 * 
	 * @return the indices of the new edges or zero if either node is not in this graph.
	 */
	public int[] createEdges(int[] srcs, int[] dests, boolean directed) {
		int[] result = new int[srcs.length];
		
		for (int i = 0; i < srcs.length; i++)
			result[i] = createEdge(srcs[i], dests[i], directed);
		
		return result;
	}

	public GraphPerspective createGraphPerspective(Node[] arg0, Edge[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphPerspective createGraphPerspective(int[] arg0, int[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Creates a new node and returns its index
	 */
	public int createNode() {
		nodes.add(new NodeImpl(this));
		outAdjacencyLists.add(new IntArray());
		inAdjacencyLists.add(new IntArray());
		return nodes.size();
	}

	public int createNode(GraphPerspective arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int createNode(Node[] nodes, Edge[] edges) {
		throw new AssertionError("meta-objects not supported");
	}

	public int createNode(int[] node_indices, int[] edge_indices) {
		throw new AssertionError("meta-objects not supported");
	}

	/**
	 * Create some new nodes in the graph.
	 * 
	 * @return an array of length new_node_count containing the indices of the newly created nodes
	 * @deprecated Use createNode() instead
	 */
	public int[] createNodes(int new_node_count) {
		int[] result = new int[new_node_count];
		int n = nodes.size();
		for (int i = 1; i <= new_node_count; i++) {
			nodes.add(new NodeImpl(this));
			result[i] = n + i;
		}
		return result;
	}
	
	/**
	 * Returns whether there's an edge from a given node to another.
	 * 
	 * Undirected edges are considered to go in both directions.
	 * 
	 * @param  from  the source node
	 * @param  to    the target node
	 * 
	 * @return whether there's an edge or not
	 */
	public boolean edgeExists(Node from, Node to) {
		return edgeExists(getIndex(from), getIndex(to));
	}

	public boolean edgeExists(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	public List edgeMetaChildrenList(Node arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List edgeMetaChildrenList(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List edgeMetaParentsList(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns an iterator over all edges in this graph.
	 */
	public Iterator edgesIterator() {
		return edges.iterator();
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns the list of all edges in this graph.
	 * 
	 * The list should not be modified by the caller in any way.
	 * 
	 * @deprecated
	 */
	public List edgesList() {
		return edges;
	}

	@SuppressWarnings("unchecked")
	public List edgesList(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List edgesList(int arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void ensureCapacity(int arg0, int arg1) {
		System.err.println("Nobody expects the Spanish Inquisition!");
	}

	public int[] getAdjacentEdgeIndicesArray(int arg0, boolean arg1,
			boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getChildlessMetaDescendants(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int[] getConnectingEdgeIndicesArray(int[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getConnectingNodeIndicesArray(int[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns the number of distinct edges incident on the given node
	 * 
	 * @return  the degree or -1 if the node does not belong to this graph
	 */
	public int getDegree(Node arg0) {
		return this.getDegree(this.getIndex(arg0));
	}

	/**
	 * Returns the number of distinct edges incident on the node with the given index
	 * 
	 * @return  the degree or -1 if the node does not belong to this graph
	 */
	public int getDegree(int index) {
		try {
			return this.outAdjacencyLists.get(index).size() + this.inAdjacencyLists.get(index).size();
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}
	}

	/**
	 * Returns the edge with the given index
	 */
	public Edge getEdge(int index) {
		return this.edges.get(index);
	}
	
	/**
	 * Returns the number of edges in this graph
	 */
	public int getEdgeCount() {
		return this.edges.size();
	}

	public int getEdgeCount(Node arg0, Node arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getEdgeCount(int arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getEdgeIndicesArray() {
		int n = this.getEdgeCount();
		int[] result = new int[n];
		for (int i = 0; i < n; i++)
			result[i] = i+1;
		return result;
	}

	public int[] getEdgeIndicesArray(int arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getEdgeIndicesArray(int arg0, int arg1, boolean arg2,
			boolean arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getEdgeMetaChildIndicesArray(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int[] getEdgeMetaParentIndicesArray(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int getEdgeSourceIndex(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getEdgeTargetIndex(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInDegree(Node arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInDegree(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInDegree(Node arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInDegree(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIndex(Node arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIndex(Edge arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Node getNode(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNodeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] getNodeIndicesArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getNodeMetaChildIndicesArray(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int[] getNodeMetaChildIndicesArray(int arg0, boolean arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public int[] getNodeMetaParentIndicesArray(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	public int getOutDegree(Node arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getOutDegree(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getOutDegree(Node arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getOutDegree(int arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEdgeDirected(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEdgeMetaChild(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isEdgeMetaParent(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isMetaChild(Node arg0, Node arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isMetaChild(Node arg0, Edge arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isMetaParent(Node arg0, Node arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isMetaParent(Edge arg0, Node arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isNeighbor(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNeighbor(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNodeMetaChild(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isNodeMetaChild(int arg0, int arg1, boolean arg2) {
		throw new AssertionError("meta-objects not supported");
	}

	public boolean isNodeMetaParent(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List metaParentsList(Node arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List metaParentsList(Edge arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List neighborsList(Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List nodeMetaChildrenList(Node arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List nodeMetaChildrenList(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List nodeMetaParentsList(int arg0) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public Iterator nodesIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List nodesList() {
		// TODO Auto-generated method stub
		return null;
	}

	public Edge removeEdge(Edge arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int removeEdge(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean removeEdgeMetaChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	public List removeEdges(List arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] removeEdges(int[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Node removeNode(Node arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int removeNode(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean removeNodeMetaChild(int arg0, int arg1) {
		throw new AssertionError("meta-objects not supported");
	}

	@SuppressWarnings("unchecked")
	public List removeNodes(List arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] removeNodes(int[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
