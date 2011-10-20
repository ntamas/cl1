package uk.ac.rhul.cs.graph;

/**
 * Calculates the transitivity (i.e. clustering coefficient) of a given graph
 * or a given set of nodes in a graph.
 * 
 * @author tamas
 */
public class TransitivityCalculator extends GraphAlgorithm {
	public TransitivityCalculator() {
		super();
	}

	public TransitivityCalculator(Graph graph) {
		super(graph);
	}

	/**
	 * Returns the average local transitivity of the graph.
	 * 
	 * @todo  not implemented yet
	 */
	public Double getAverageLocalTransitivity() {
		// TODO
		throw new RuntimeException("average local transitivity not implemented yet");
	}
	
	/**
	 * Returns the global transitivity of the graph.
	 * 
	 * Global transitivity is defined as three times the number of triangles (or,
	 * simply the number of closed triplets) divided by the number of connected
	 * triplets.
	 * 
	 * @return  the transitivity or null if the calculation was interrupted
	 */
	public Double getGlobalTransitivity() {
		long triangles = 0;
		long triplets = 0;
		int nodeCount = graph.getNodeCount();
		int i;
		
		shouldStop = false;
		
		for (i = 0; i < nodeCount; i++) {
			if (shouldStop)
				return null;
			
			int[] neis = graph.getAdjacentNodeIndicesArray(i, Directedness.ALL);
			for (int j: neis) {
				if (j <= i)
					continue;
				
				for (int k: neis) {
					if (j < k && graph.areConnected(j, k))
						triangles++;
				}
			}
			triplets += (neis.length * (neis.length - 1)) / 2;
		}
		
		return (triplets == 0) ? 0 : (3.0 * triangles / triplets);
	}
	
	/**
	 * Sets the graph that the calculation will run on.
	 * 
	 * This method throws an <code>UnsupportedOperationException</code> for
	 * directed graphs.
	 */
	@Override
	public void setGraph(Graph graph) {
		if (graph.isDirected())
			throw new UnsupportedOperationException(
					"transitivity calculation works for undirected graphs only"
			);
		
		super.setGraph(graph);
	}
}
