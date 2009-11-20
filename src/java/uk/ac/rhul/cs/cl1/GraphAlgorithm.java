package uk.ac.rhul.cs.cl1;

/**
 * Abstract class for an algorithm that will be run on a graph.
 * 
 * @author ntamas
 */
public abstract class GraphAlgorithm {
	/** The graph on which we are running the algorithm */
	protected Graph graph = null;

	/** Marks whether the algorithm should be stopped at the earliest possible occasion */
	protected boolean shouldStop = false;
	
	/**
	 * Creates an algorithm with no associated graph
	 */
	public GraphAlgorithm() {}
	
	/**
	 * Creates an algorithm that will run on the given graph.
	 */
	public GraphAlgorithm(Graph graph) {
		this.setGraph(graph);
	}

	/**
	 * Returns the graph on which we are running the algorithm.
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Non-blocking request to halt the algorithm when it is safe to do so.
	 */
	public void halt() {
		shouldStop = true;
	}
	
	/**
	 * Sets the graph on which we are running the algorithm.
	 * @param graph the graph to set
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}
