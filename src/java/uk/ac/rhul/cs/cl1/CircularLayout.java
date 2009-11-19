package uk.ac.rhul.cs.cl1;

import java.awt.geom.Point2D;

/**
 * Calculates a circular layout for a graph
 * 
 * @author ntamas
 */
public class CircularLayout extends GraphLayoutAlgorithm {
	public double theta;
	
	public CircularLayout() {
	}
	
	/**
	 * Constructs a circular layout algorithm instance associated to the given graph
	 * 
	 * @param graph
	 */
	public CircularLayout(Graph graph) {
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
	 * Returns the coordinates of the given node
	 * @param  nodeIndex   the index of the given node
	 */
	public Point2D.Double getCoordinates(int nodeIndex) {
		return new Point2D.Double(Math.cos(theta * nodeIndex), Math.sin(theta * nodeIndex));
	}
}
