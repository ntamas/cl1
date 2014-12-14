package uk.ac.rhul.cs.graph;

import com.sosnoski.util.array.IntArray;

/**
 * Superclass for graph traversal algorithms such as breadth and depth first
 * search, random walk etc.
 */
public abstract class GraphTraversalAlgorithm extends GraphAlgorithm implements Iterable<Integer> {
    /** Seed node from where the traversal will start */
    protected int seedNode;

    /** Subgraph on which we run the traversal algorithm or null if we run it on the whole graph */
    protected int[] subgraph = null;

    /**
     * Constructs a traversal algorithm instance that will run on the given graph
     *
     * @param graph  the graph on which we are running the traversal
     */
    public GraphTraversalAlgorithm(Graph graph, int seedNode) {
        super(graph);
        this.seedNode = seedNode;
    }

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
     * Restricts the traversal to the given subgraph if supported.
     *
     * @throws  java.lang.UnsupportedOperationException  if the traversal does not support
     *          restrictions to subgraphs.
     */
    public void restrictToSubgraph(int[] subgraph) {
        this.subgraph = subgraph;
    }

    /**
     * Restricts the traversal to the given subgraph if supported.
     * Do not override this method; override {@link #restrictToSubgraph(int[])} instead.
     */
    public final void restrictToSubgraph(Integer[] subgraph) {
        this.subgraph = new int[subgraph.length];
        for (int i = 0; i < this.subgraph.length; i++)
            this.subgraph[i] = subgraph[i];
    }

    /**
     * Returns the nodes that the traversal visited in an array.
     */
    public int[] toArray() {
        IntArray result = new IntArray();
        for (int i: this)
            result.add(i);
        return result.toArray();
    }

}
