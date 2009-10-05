package uk.ac.rhul.cs.cl1;

import java.util.List;

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
public class ClusterONE extends GraphAlgorithm implements Runnable {
	/** The name of the application that will appear on the user interface */
	public static final String applicationName = "Cluster ONE";
	
	/** The version number of the application */
	public static final String version = "0.1";
	
	/** The clustering result as a list of NodeSets */
	public List<NodeSet> result = null;
	
	/** Minimum size of the clusters that will be returned */
	public int minSize = 1;
	
	/**
	 * Returns the minimum size of the clusters that will be returned
	 * @return the minimum size
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * Sets the minimum size of the clusters that will be returned
	 * @param minSize the minimum size
	 */
	public void setMinSize(int minSize) {
		this.minSize = Math.max(1, minSize);
	}

	/**
	 * Returns the clustering results or null if there was no clustering executed so far
	 */
	public List<NodeSet> getResults() {
		return result;
	}
	
	/**
	 * Executes the algorithm on the graph set earlier by setGraph()
	 */
	public void run() {
		int n = graph.getNodeCount();
		NodeSetList result = new NodeSetList();
		
		/* For each node, start growing a cluster */
		for (int i = 0; i < n; i++) {
			MutableNodeSet cluster = new MutableNodeSet(graph);
			
			ClusterGrowthProcess growthProcess = new GreedyClusterGrowthProcess(cluster);
			cluster.add(i);
			while (growthProcess.step());
			
			/* Check the size of the cluster -- if too small, skip it */
			if (cluster.size() < minSize)
				continue;
			
			/* TODO: check if the cluster is already there */
			result.add(cluster.freeze());
		}
		
		/* Merge highly overlapping clusters */
		result = result.mergeOverlapping(0.9);
		
		/* Return the result effectively */
		this.result = result;
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
}
