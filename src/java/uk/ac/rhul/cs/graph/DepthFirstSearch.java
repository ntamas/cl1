package uk.ac.rhul.cs.graph;

/**
 * Depth first search algorithm on a graph.
 *
 * @author ntamas
 */
public class DepthFirstSearch extends GraphTraversalAlgorithm {
    /**
     * Constructs a DFS algorithm instance that will run on the given graph
     *
     * @param graph     the graph on which we are running the DFS algorithm
     * @param seedNode  the starting point of the traversal
     */
    public DepthFirstSearch(Graph graph, int seedNode) {
        super(graph, seedNode);
    }

    /**
     * Returns an iterator that will iterate over the nodes visited
     * during the traversal.
     */
    public DepthFirstSearchIterator iterator() {
        return new DepthFirstSearchIterator(graph, seedNode, subgraph);
    }
}
