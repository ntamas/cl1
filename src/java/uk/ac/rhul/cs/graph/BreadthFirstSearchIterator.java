package uk.ac.rhul.cs.graph;

import java.util.Iterator;

import com.sosnoski.util.hashset.IntHashSet;
import com.sosnoski.util.queue.IntQueue;

/**
 * Iterator that traverses the vertices of a graph in breadth first order.
 * 
 * @author tamas
 *
 */
public class BreadthFirstSearchIterator implements Iterator<Integer> {
	/** Graph which the iterator will traverse */
	protected Graph graph = null;

	/**
	 * When the BFS is restricted to a subgraph, this node contains the set of nodes that
	 * are allowed during the BFS. Otherwise it is null.
	 */
	protected IntHashSet allowedNodes = null;

	/** Queue that holds the nodes that are to be visited and their distances */
	protected IntQueue q = new IntQueue();
	/** Set that holds the nodes that have already been visited */
	protected IntHashSet visited = new IntHashSet();
	
	/** Distance of the last returned node from the seed */
	protected Integer distance = null;

	/**
	 * Constructs a new BFS iterator.
	 * 
	 * @param  graph     the graph to be traversed
	 * @param  seedNode  the index of the seed node
	 */
	public BreadthFirstSearchIterator(Graph graph, int seedNode) {
		this(graph, seedNode, null);
	}
	
	/**
	 * Constructs a new BFS iterator restricted to a set of nodes.
	 * 
	 * @param  graph     the graph to be traversed
	 * @param  seedNode  the index of the seed node
	 * @param  subset    an array of node indices which must be traversed.
	 *                   Nodes not in this nodeset are assumed to have been
	 *                   already visited by the iterator. Can also be null,
	 *                   which is equivalent to an empty array.
	 */
	public BreadthFirstSearchIterator(Graph graph, int seedNode, int[] subset) {
		this.graph = graph;
		if (subset != null) {
			restrictToSubgraph(subset);
		}
		pushNode(seedNode, 0);
		if (!q.isEmpty()) {
			visited.add(seedNode);
		}
	}
	
	/**
	 * Returns the distance of the last returned node from the seed.
	 * 
	 * @return  the distance of the last node from the seed or null if the
	 *          traversal has not yet started.
	 */
	public Integer getDistance() {
		return distance;
	}
	
	/**
	 * Returns whether there are more nodes left in the traversal.
	 */
	public boolean hasNext() {
		return !q.isEmpty();
	}
	
	/**
	 * Returns the index of the next visited node
	 */
	public Integer next() {
		int result = q.remove();
		distance = q.remove();
		
		int[] neighbors = graph.getAdjacentNodeIndicesArray(result, Directedness.OUT);
		
		/* Check all the neighbors and add the nodes not visited to the queue */
		for (int neighbor: neighbors) {
			if (!visited.contains(neighbor)) {
				pushNode(neighbor, distance + 1);
				visited.add(neighbor);
			}
		}
		
		return result;
	}

	/**
	 * Removal is not supported.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Restricts the BFS traversal to the given subset.
	 * 
	 * @param  subset    an array of node indices to which we restrict
	 *                   the traversal
	 */
	public void restrictToSubgraph(int[] subset) {
		allowedNodes = new IntHashSet();
		for (int node: subset) {
			allowedNodes.add(node);
		}
	}

	/**
	 * Pushes the given node and the given distance into the queue if we are allowed
	 * to visit the node.
	 *
	 * @param  node      the node to push to the queue
	 * @param  distance  the distance of the node from the start point
	 */
	public void pushNode(int node, int distance) {
		if (allowedNodes != null && !allowedNodes.contains(node))
			return;

		q.add(node);
		q.add(distance);
	}
}
