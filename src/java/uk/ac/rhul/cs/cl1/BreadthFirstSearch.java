package uk.ac.rhul.cs.cl1;

import com.sosnoski.util.array.IntArray;

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
	public BreadthFirstSearchIterator iterator() {
		return new BreadthFirstSearchIterator(graph, seedNode, subgraph);
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
	 * Restricts the BFS iterator to the given subgraph
	 */
	public void restrictToSubgraph(int[] subgraph) {
		this.subgraph = subgraph;
	}
	
	/**
	 * Restricts the BFS iterator to the given subgraph
	 */
	public void restrictToSubgraph(Integer[] subgraph) {
		this.subgraph = new int[subgraph.length];
		for (int i = 0; i < this.subgraph.length; i++)
			this.subgraph[i] = subgraph[i];
	}
}
