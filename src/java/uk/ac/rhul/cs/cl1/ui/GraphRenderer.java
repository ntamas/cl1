package uk.ac.rhul.cs.cl1.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import uk.ac.rhul.cs.cl1.Edge;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.GraphLayoutAlgorithm;
import uk.ac.rhul.cs.cl1.Layout;

/**
 * Class that is responsible for rendering a graph with a given layout to
 * a given drawing context.
 * 
 * @author tamas
 */
public class GraphRenderer implements Callable<Icon> {
	/** Local instance of the graph layout algorithm used in the rendering process */
	GraphLayoutAlgorithm algorithm = null;
	
	/** Calculated layout of the graph according to the layout algorithm */
	Layout layout = null;
	
	/**
	 * Constructs a renderer that will render the given graph using the given
	 * layout algorithm
	 */
	public GraphRenderer(Graph graph, GraphLayoutAlgorithm algorithm) {
		this.algorithm = algorithm;
		this.algorithm.setGraph(graph);
	}
	
	/**
	 * Calculates the layout of the graph
	 */
	private void calculateLayout() {
		if (this.layout == null)
			this.layout = this.algorithm.getResults();
	}
	
	/**
	 * Renders the graph to the given graphics context into the given box
	 */
	public void render(Graphics2D g, Rectangle2D rect) {
		calculateLayout();
		this.layout.fitToRectangle(rect);
		
		Graph graph = this.algorithm.getGraph();
		int n = this.layout.size();
		
		if (graph.getNodeCount() != n) {
			System.out.println("hmmm, wtf?");
			return;
		}
		
		/* Draw the edges */
		g.setColor(Color.BLACK);
		for (Edge edge: graph) {
			Point2D from = this.layout.getCoordinates(edge.source);
			Point2D to = this.layout.getCoordinates(edge.target);
			g.drawLine((int)from.getX(), (int)from.getY(), (int)to.getX(), (int)to.getY());
		}
		
		/* Draw the nodes */
		g.setColor(Color.RED);
		for (int i = 0; i < n; i++) {
			Point2D point = this.layout.getCoordinates(i);
			g.fillOval((int)point.getX()-2, (int)point.getY()-2, 5, 5);
		}
	}

	/**
	 * Renders the graph to the given image, covering the whole area of the image
	 */
	public void render(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.setComposite(AlphaComposite.Src);
		g2d.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Rectangle2D rect = new Rectangle2D.Double(3, 3, image.getWidth() - 6, image.getHeight() - 6);
		this.render(g2d, rect);
	}
	
	public Icon call() throws Exception {
		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
		this.render(image);
		return new ImageIcon(image);
	}
}
