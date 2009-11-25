package uk.ac.rhul.cs.cl1;

/**
 * Algorithm that places the vertices of a graph according to the
 * Fruchterman-Reingold layout algorithm.
 * 
 * @author ntamas
 */
public class FruchtermanReingoldLayoutAlgorithm extends GraphLayoutAlgorithm {
	/**
	 * Number of iterations to perform
	 */
	private int iterationCount = 500;
	
	/**
	 * Cooling exponent
	 */
	private double coolingExponent = 0.99;
	
	/**
	 * Class holding local data associated to a graph node in the FR layout algorithm
	 */
	private class NodeData {
		double x;
		double y;
		double dx;
		double dy;
		
		public NodeData() {
			x = Math.random();
			y = Math.random();
			resetDxDy();
		}
		
		public void resetDxDy() {
			dx = 0; dy = 0;
		}
		
		public void addDxDy(double dx0, double dy0) {
			this.dx += dx0;
			this.dy += dy0;
		}
		
		public void move() {
			x += dx; y += dy;
		}
	}
	
	/**
	 * Constructs an FR-layout instance not associated to any graph
	 */
	public FruchtermanReingoldLayoutAlgorithm() {
		super();
	}
	
	/**
	 * Constructs an FR-layout instance associated to the given graph
	 */
	public FruchtermanReingoldLayoutAlgorithm(Graph graph) {
		super(graph);
	}
	
	/**
	 * Returns the number of FR iterations to perform in the layout algorithm
	 */
	public int getIterationCount() {
		return iterationCount;
	}
	
	/**
	 * Returns the calculated layout
	 */
	public Layout getResults() {
		double maxDelta = graph.getNodeCount();
		
		Layout layout = new Layout(graph);
		NodeData[] nodeData = new NodeData[graph.getNodeCount()];
		
		for (int i = 0; i < iterationCount; i++) {
			double t = maxDelta * Math.pow(i / (double)iterationCount, coolingExponent);
		}
		
		return layout;
	}

	/**
	 * Sets the number of FR iterations to perform in the layout algorithm
	 * 
	 * @param iterationCount   the number of iterations to perform
	 */
	public void setIterationCount(int iterationCount) {
		if (iterationCount >= 1)
			this.iterationCount = iterationCount;
	}
}
