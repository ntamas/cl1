package uk.ac.rhul.cs.cl1;

/**
 * Calculates a circular layout for a graph
 * 
 * @author ntamas
 */
public class CircularLayoutAlgorithm extends GraphLayoutAlgorithm {
	public double theta;
	
	public CircularLayoutAlgorithm() {
	}
	
	/**
	 * Constructs a circular layout algorithm instance associated to the given graph
	 * 
	 * @param graph
	 */
	public CircularLayoutAlgorithm(Graph graph) {
		super(graph);
	}
	
	/**
	 * Sets the graph associated to the layout algorithm instance
	 */
	public void setGraph(Graph graph) {
		super.setGraph(graph);
		theta = Math.PI * 2 / graph.getNodeCount();
	}
	
	/**
	 * Returns the calculated layout
	 */
	public Layout getResults() {
		Layout result = new Layout(this.graph);
		int i, n = this.graph.getNodeCount();
		
		for (i = 0; i < n; i++)
			result.setCoordinates(i, Math.cos(theta * i), Math.sin(theta * i));
		
		return result;
	}
}
