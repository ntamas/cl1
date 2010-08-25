package uk.ac.rhul.cs.graph;

/**
 * Calculates a circular layout for a graph
 * 
 * @author ntamas
 */
public class CircularLayoutAlgorithm extends GraphLayoutAlgorithm {
	/**
	 * Constructs a circular layout algorithm not associated to any graph
	 */
	public CircularLayoutAlgorithm() {
		super();
	}
	
	/**
	 * Constructs a circular layout algorithm instance associated to the given graph
	 */
	public CircularLayoutAlgorithm(Graph graph) {
		super(graph);
	}
	
	/**
	 * Returns the calculated layout
	 */
	public Layout getResults() {
		Layout result = new Layout(this.graph);
		int i, n = this.graph.getNodeCount();
		double theta = Math.PI * 2 / n;
		
		for (i = 0; i < n; i++)
			result.setCoordinates(i, Math.cos(theta * i), Math.sin(theta * i));
		
		return result;
	}
}
