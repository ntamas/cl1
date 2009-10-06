package uk.ac.rhul.cs.cl1;

import java.util.HashSet;
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
	protected List<NodeSet> result = null;
	
	/** Minimum size of the clusters that will be returned */
	protected int minSize = 1;
	
	/** Minimum density of the clusters that will be returned */
	protected double minDensity = 0.2;
	
	/** Overlap threshold value: no pair of complexes will have an overlap larger than this in the result */
	protected double overlapThreshold = 0.8;
	
	/**
	 * Returns the minimum density of clusters
	 * @return the minimum density of clusters
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Sets the minimum density of clusters that can be considered acceptable.
	 * @param minDensity the minDensity to set
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = Math.max(0, minDensity);
	}

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
	 * Returns the overlap threshold of the algorithm.
	 * 
	 * The overlap threshold controls whether two given complexes will be merged in the final
	 * result set. The complexes will be merged if their meet/min coefficient is larger than
	 * this ratio.
	 * 
	 * @return the overlapThreshold
	 */
	public double getOverlapThreshold() {
		return overlapThreshold;
	}

	/**
	 * Sets the overlap threshold of the algorithm.
	 * 
	 * @param overlapThreshold the new overlap threshold
	 * @see getOverlapThreshold()
	 */
	public void setOverlapThreshold(double overlapThreshold) {
		this.overlapThreshold = Math.max(0, overlapThreshold);
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
		HashSet<NodeSet> addedNodeSets = new HashSet<NodeSet>();
		
		/* For each node, start growing a cluster */
		for (int i = 0; i < n; i++) {
			MutableNodeSet cluster = new MutableNodeSet(graph);
			
			ClusterGrowthProcess growthProcess = new GreedyClusterGrowthProcess(cluster, 0.2);
			cluster.add(i);
			while (growthProcess.step());
			
			/* Check the size of the cluster -- if too small, skip it */
			if (cluster.size() < minSize)
				continue;
			
			/* Check the density of the cluster -- if too sparse, skip it */
			if (cluster.getDensity() < minDensity)
				continue;
			
			/* Freeze the cluster so it becomes hashable */
			NodeSet frozenCluster = cluster.freeze();
			cluster = null;
			
			/* If the cluster was already detected from another seed node, continue */
			if (addedNodeSets.contains(frozenCluster))
				continue;
			
			/* Add the cluster to the result list */
			result.add(frozenCluster);
			addedNodeSets.add(frozenCluster);
		}
		
		/* Throw away the addedNodeSets hash, we don't need it anymore */
		addedNodeSets.clear();
		addedNodeSets = null;
		
		/* Merge highly overlapping clusters */
		result = result.mergeOverlapping(overlapThreshold);
		
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
