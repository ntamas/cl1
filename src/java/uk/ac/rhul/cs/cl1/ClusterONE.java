package uk.ac.rhul.cs.cl1;

/**
 * Main class for the Cluster ONE algorithm.
 * 
 * This class represents an instance of the algorithm along with all its
 * necessary parameters. The main entry point of the algorithm is the
 * run() method which executes the clustering algorithm on the graph
 * set earlier using the setGraph() method. The algorithm can also be
 * run in a separate thread as it implements the Runnable interface.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class ClusterONE implements Runnable {
	/**
	 * The name of the application that will appear on the user interface
	 */
	public static final String applicationName = "Cluster ONE";
	
	/**
	 * The version number of the application
	 */
	public static final String version = "0.1";
	
	/**
	 * The graph that will be clustered by the algorithm
	 */
	public Graph graph = null;
	
	/**
	 * Executes the algorithm on the graph set earlier by setGraph()
	 */
	public void run() {
		int n = graph.getNodeCount();
		// TODO Auto-generated method stub
	}
	
	/**
	 * Executes the algorithm on the given graph.
	 * 
	 * This is a shortcut method that can be used whenever we don't want to
	 * spawn a separate thread for the algorithm (e.g., when running from the
	 * command line)
	 * 
	 * @param   graph    the graph being clustered
	 */
	public void runOnGraph(Graph graph) {
		setGraph(graph);
		run();
	}

	/**
	 * Set the graph that will be clustered by the algorithm
	 * 
	 * @param   graph    the graph being clustered
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}
