package uk.ac.rhul.cs.cl1;

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
		this.graph = graph;
		q.add(seedNode); q.add(0);
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
		this(graph, seedNode);
		if (subset != null)
			restrictToSubgraph(subset);
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
		Integer result = q.remove();
		distance = q.remove();
		
		int[] neighbors = graph.getAdjacentNodeIndicesArray(result, Directedness.OUT);
		
		/* Add the current node to the set of visited nodes */
		visited.add(result);
		
		/* Check all the neighbors and add the nodes not visited to the queue */
		for (int neighbor: neighbors) {
			if (!this.visited.contains(neighbor)) {
				q.add(neighbor);
				q.add(distance+1);
				this.visited.add(neighbor);
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
	 * It is achieved by assuming that all the node indices NOT in the given
	 * subset have already been visited.
	 * 
	 * @param  subset    an array of node indices to which we restrict
	 *                   the traversal
	 */
	public void restrictToSubgraph(int[] subset) {
		IntHashSet s = new IntHashSet();
		int i, n = graph.getNodeCount();
			
		for (i = 0; i < subset.length; i++)
			s.add(subset[i]);
		
		visited.clear();
		for (i = 0; i < n; i++)
			if (!s.contains(i))
				visited.add(i);
	}
	
}
