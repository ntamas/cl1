package uk.ac.rhul.cs.graph;

/**
 * Abstract superclass for different kinds of graph layout algorithms.
 * 
 * @author ntamas
 */
public abstract class GraphLayoutAlgorithm extends GraphAlgorithm {
	public GraphLayoutAlgorithm() {
		super();
	}
	
	public GraphLayoutAlgorithm(Graph graph) {
		super(graph);
	}
	
	/**
	 * Returns the calculated layout
	 */
	public abstract Layout getResults();
}
