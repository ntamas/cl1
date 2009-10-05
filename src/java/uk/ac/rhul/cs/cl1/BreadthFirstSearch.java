package uk.ac.rhul.cs.cl1;

import java.util.Iterator;

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

	/**
	 * Returns the current seed node of the algoritm
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
		
	}
}
