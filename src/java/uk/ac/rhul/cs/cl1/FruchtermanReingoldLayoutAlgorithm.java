package uk.ac.rhul.cs.cl1;

import java.util.List;

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
			dx = 0;
			dy = 0;
		}
		
		public void addDxDy(double dx0, double dy0) {
			this.dx += dx0;
			this.dy += dy0;
		}
		
		public double getVelocity() {
			return Math.sqrt(dx * dx + dy * dy);
		}
		
		public void scaleDxDy(double factor) {
			dx *= factor; dy *= factor;
		}
		
		public void move() {
			x += dx; y += dy;
			dx = 0; dy = 0;
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
		int numberOfNodes = graph.getNodeCount();
		double maxDelta = numberOfNodes;
		double repulseRadius = iterationCount * maxDelta;
		double area = maxDelta * maxDelta;
		double frk = Math.sqrt(area / numberOfNodes);
		
		int i, j;
		double xd, yd, ded, force;
		
		if (numberOfNodes == 0)
			return new Layout(graph);
		
		// Initialize node data
		NodeData[] nodeDataArray = new NodeData[numberOfNodes];
		for (i = 0; i < numberOfNodes; i++) {
			nodeDataArray[i] = new NodeData();
		}
		
		// Convert the graph to a list of edges and normalize weights
		List<Edge> edges = graph.getEdgeList();
		xd = 0;
		for (Edge edge: edges) {
			if (edge.weight < 0)
				edge.weight = 1;
			if (edge.weight > xd)
				xd = edge.weight;
		}
		for (Edge edge: edges) {
			edge.weight /= xd;
		}
		
		for (int iter = 0; iter < iterationCount; iter++) {
			// Set the temperature
			double t = maxDelta * Math.pow(1 - (iter / (double)iterationCount), coolingExponent);
			
			// Calculate repulsive forces for each undirected vertex pair
			for (i = 0; i < numberOfNodes; i++) {
				NodeData firstNode = nodeDataArray[i];
				for (j = i+1; j < numberOfNodes; j++) {
					NodeData secondNode = nodeDataArray[j];
					
					xd = firstNode.x - secondNode.x;
					yd = firstNode.y - secondNode.y;
					ded = Math.sqrt(xd*xd + yd*yd);
					
					if (ded == 0)
						continue;
					
					xd /= ded; yd /= ded;
					force = frk * frk * (1.0 / ded - ded * ded / repulseRadius);
					xd *= force; yd *= force;
					
					firstNode.addDxDy(xd, yd);
					secondNode.addDxDy(-xd, -yd);
				}
			}
			
			// Calculate attraction forces for each edge
			for (Edge edge: edges) {
				NodeData firstNode = nodeDataArray[edge.source];
				NodeData secondNode = nodeDataArray[edge.target];
				
				xd = firstNode.x - secondNode.x;
				yd = firstNode.y - secondNode.y;
				ded = Math.sqrt(xd*xd + yd*yd);
				
				if (ded == 0)
					continue;
				
				force = -ded * ded / frk * edge.weight;
				xd *= force; yd *= force;
				firstNode.addDxDy(xd, yd);
				secondNode.addDxDy(-xd, -yd);
			}
			
			// Dampen motion and move the points
			for (NodeData nodeData: nodeDataArray) {
				ded = nodeData.getVelocity();
				if (ded > t) {
					// Dampen to t
					nodeData.scaleDxDy(t/ded);
				}
				nodeData.move();
			}
		}
		
		// Copy the results into the layout
		Layout layout = new Layout(graph);
		for (i = 0; i < numberOfNodes; i++) {
			layout.setCoordinates(i, nodeDataArray[i].x, nodeDataArray[i].y);
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
