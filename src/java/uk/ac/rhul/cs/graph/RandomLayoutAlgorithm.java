package uk.ac.rhul.cs.graph;

/**
 * Layout algorithm that places the graph nodes randomly in the unit square
 * 
 * @author ntamas
 */
public class RandomLayoutAlgorithm extends GraphLayoutAlgorithm {
	/**
	 * Constructs a random layout algorithm not associated to any graph
	 */
	public RandomLayoutAlgorithm() {
		super();
	}
	
	/**
	 * Constructs a random layout algorithm instance associated to the given graph
	 * 
	 * @param graph
	 */
	public RandomLayoutAlgorithm(Graph graph) {
		super(graph);
	}
	
	/**
	 * Returns the calculated layout
	 */
	public Layout getResults() {
		Layout result = new Layout(this.graph);
		int i, n = this.graph.getNodeCount();
		
		for (i = 0; i < n; i++)
			result.setCoordinates(i, Math.random(), Math.random());
		
		return result;
	}

}
