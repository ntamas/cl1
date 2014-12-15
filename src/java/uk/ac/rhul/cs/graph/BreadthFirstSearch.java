package uk.ac.rhul.cs.graph;

/**
 * Breadth first search algorithm on a graph.
 * 
 * @author ntamas
 */
public class BreadthFirstSearch extends GraphTraversalAlgorithm {
	/**
	 * Constructs a BFS algorithm instance that will run on the given graph
	 * 
	 * @param graph     the graph on which we are running the BFS algorithm
	 * @param seedNode  the starting point of the traversal
	 */
	public BreadthFirstSearch(Graph graph, int seedNode) {
		super(graph, seedNode);
	}
	
	/**
	 * Returns an iterator that will iterate over the nodes visited during the traversal
	 */
	public BreadthFirstSearchIterator iterator() {
		return new BreadthFirstSearchIterator(graph, seedNode, subgraph);
	}
}
