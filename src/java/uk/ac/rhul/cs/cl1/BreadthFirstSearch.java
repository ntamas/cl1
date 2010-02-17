package uk.ac.rhul.cs.cl1;

import java.util.Iterator;

import com.sosnoski.util.array.IntArray;
import com.sosnoski.util.hashset.IntHashSet;
import com.sosnoski.util.queue.IntQueue;

/**
 * Breadth first search algorithm on a graph.
 * 
 * @author ntamas
 */
public class BreadthFirstSearch extends GraphAlgorithm implements Iterable<Integer> {
	/** Seed node from where the traversal will start */
	protected int seedNode;
	
	/** Subgraph on which we run the BFS algorithm or null if we run it on the whole graph */
	protected int[] subgraph = null;
	
	/**
	 * Returns the current seed node of the algorithm
	 * @return the seed node
	 */
	public int getSeedNode() {
		return seedNode;
	}

	/**
	 * Sets the current seed node of the algorithm
	 * @param seedNode the seed node to set
	 */
	public void setSeedNode(int seedNode) {
		this.seedNode = seedNode;
	}

	/**
	 * Constructs a BFS algorithm instance that will run on the given graph
	 * 
	 * @param graph  the graph on which we are running the BFS algorithm
	 */
	public BreadthFirstSearch(Graph graph, int seedNode) {
		super(graph);
		this.seedNode = seedNode;
	}
	
	/**
	 * Returns an iterator that will iterate over the nodes visited during the traversal
	 */
	public Iterator<Integer> iterator() {
		return new BFSIterator();
	}
	
	/**
	 * Returns the BFS-order traversal in an array
	 */
	public int[] toArray() {
		IntArray result = new IntArray();
		for (int i: this)
			result.add(i);
		return result.toArray();
	}
	
	/**
	 * Restricts the BFS iterator the given subgraph
	 */
	public void restrictToSubgraph(int[] subgraph) {
		this.subgraph = subgraph;
	}
	
	/**
	 * Restricts the BFS iterator the given subgraph
	 */
	public void restrictToSubgraph(Integer[] subgraph) {
		this.subgraph = new int[subgraph.length];
		for (int i = 0; i < this.subgraph.length; i++)
			this.subgraph[i] = subgraph[i];
	}
	
	/**
	 * Internal iterator class
	 */
	class BFSIterator implements Iterator<Integer> {
		/** Queue that holds the nodes that are to be visited */
		IntQueue q = new IntQueue();
		/** Set that holds the nodes that have already been visited */
		IntHashSet visited = new IntHashSet();
		
		/** Constructs a BFS iterator with the current seed node */
		public BFSIterator() {
			q.add(seedNode);
			
			if (subgraph != null) {
				IntHashSet s = new IntHashSet();
				int i, n = graph.getNodeCount();
				
				for (i = 0; i < subgraph.length; i++)
					s.add(subgraph[i]);
				
				for (i = 0; i < n; i++)
					if (!s.contains(i))
						visited.add(i);
			}
		}
		
		public boolean hasNext() {
			return !q.isEmpty();
		}
		
		/** Returns the index of the next visited node */
		public Integer next() {
			Integer result = q.remove();
			int[] neighbors = graph.getAdjacentNodeIndicesArray(result, Directedness.OUT);
			
			/* Add the current node to the set of visited nodes */
			visited.add(result);
			
			/* Check all the neighbors and add the nodes not visited to the queue */
			for (int neighbor: neighbors) {
				if (!this.visited.contains(neighbor)) {
					q.add(neighbor);
					this.visited.add(neighbor);
				}
			}
			
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Restricts the iterator by assuming that some of the nodes were visited already.
		 * 
		 * This is useful when you want to check that a subgraph of a given graph is connected
		 * by itself or not -- you just have to restrict the iterator by assuming that all
		 * vertices not in the subgraph were visited already.
		 */
		public void assumeVisited(int[] vertices) {
			for (int i: vertices)
				visited.add(i);
		}
	}
}
