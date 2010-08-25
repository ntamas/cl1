package uk.ac.rhul.cs.graph;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Class representing a layout of a graph
 * 
 * @author tamas
 */
public class Layout {
	/** Internal array of coordinates */
	public double[] coordinates = null;
	
	/**
	 * Creates an empty layout for the given graph
	 */
	public Layout(Graph graph) {
		this(graph.getNodeCount());
	}
	
	/**
	 * Creates an empty layout for the given number of nodes
	 */
	public Layout(int numberOfNodes) {
		coordinates = new double[2 * numberOfNodes];
		Arrays.fill(coordinates, 0);
	}
	
	/**
	 * Fits the layout into the given rectangle by rescaling and translating coordinates appropriately
	 * 
	 * @param  rectangle   the rectangle in which we want to fit the layout
	 */
	public void fitToRectangle(Rectangle2D rectangle) {
		Rectangle2D currentBoundingRectangle = this.getBoundingRectangle();
		double sx = rectangle.getWidth() / currentBoundingRectangle.getWidth();
		double sy = rectangle.getHeight() / currentBoundingRectangle.getHeight();
		
		/* First, translate the current rectangle so that the lower left corner is in
		 * the origin */
		AffineTransform trans = AffineTransform.getTranslateInstance(
				-currentBoundingRectangle.getMinX() * sx,
				-currentBoundingRectangle.getMinY() * sy
		);
		
		/* Next, scale it up for the desired size */
		trans.scale(sx, sy);
		
		/* Finally, translate the lower left corner to where it should be */
		trans.translate(rectangle.getMinX() / sx, rectangle.getMinY() / sy);
		
		/* Now, apply the transformation to all the coordinates */
		trans.transform(coordinates, 0, coordinates, 0, coordinates.length / 2);
	}

	/**
	 * Returns the bounding rectangle of the layout
	 */
	public Rectangle2D getBoundingRectangle() {
		if (coordinates.length == 0)
			return null;
		
		double minX = coordinates[0], minY = coordinates[1];
		double maxX = coordinates[0], maxY = coordinates[1];
		int i, n = coordinates.length;
		
		for (i = 2; i < n; i += 2) {
			double x = coordinates[i], y = coordinates[i+1];
			if (minX > x)
				minX = x;
			if (minY > y)
				minY = y;
			if (maxX < x)
				maxX = x;
			if (maxY < y)
				maxY = y;
		}
			
		return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
	}
	
	/**
	 * Sets the coordinates of the node with the given index
	 * 
	 * @param i   the index of the node
	 * @param x   the X coordinate
	 * @param y   the Y coordinate
	 */
	public void setCoordinates(int i, double x, double y) {
		coordinates[i*2] = x;
		coordinates[i*2+1] = y;
	}

	/**
	 * Returns the number of points in this layout
	 */
	public int size() {
		return this.coordinates.length / 2;
	}

	/**
	 * Returns the coordinates of the given point in this layout
	 * 
	 * @param i   the index of the node
	 */
	public Point2D getCoordinates(int i) {
		return new Point2D.Double(coordinates[i*2], coordinates[i*2+1]);
	}
}
