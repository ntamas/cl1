package uk.ac.rhul.cs.graph;

import uk.ac.rhul.cs.cl1.NullTaskMonitor;
import uk.ac.rhul.cs.cl1.TaskMonitor;
import uk.ac.rhul.cs.cl1.TaskMonitorSupport;

/**
 * Calculates the transitivity (i.e. clustering coefficient) of a given graph
 * or a given set of nodes in a graph.
 * 
 * @author tamas
 */
public class TransitivityCalculator extends GraphAlgorithm implements TaskMonitorSupport {
	public TransitivityCalculator() {
		super();
	}

	public TransitivityCalculator(Graph graph) {
		super(graph);
	}

	/** A task monitor where the algorithm will report its progress */
	protected TaskMonitor monitor = new NullTaskMonitor();

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

		monitor.setPercentCompleted(0);

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

			// TODO: this is not entirely exact here because each node should be
			// weighted by the square of its degree
			monitor.setPercentCompleted((int)(i * 100.0 / nodeCount));
		}

		monitor.setPercentCompleted(100);

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

	/**
	 * Sets the task monitor where the algorithm will report its progress
	 *
	 * @param monitor    the task monitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}
}
