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
	
	/** Algorithm settings for this instance */
	protected ClusterONEAlgorithmParameters params = null;

	/** A task monitor where the algorithm will report its progress */
	protected TaskMonitor monitor = null;
	
	/**
	 * Constructs an instance of the algorithm using the default algorithm parameters.
	 */
	public ClusterONE() {
		this(null);
	}

	/**
	 * Constructs an instance of the algorithm using the given algorithm parameters.
	 * 
	 * @param algorithmParameters   a {@link ClusterONEAlgorithmParameters} instance that
	 *                              controls the algorithms. If null, the defaults
	 *                              will be used.
	 */
	public ClusterONE(ClusterONEAlgorithmParameters algorithmParameters) {
		if (algorithmParameters == null)
			this.params = new ClusterONEAlgorithmParameters();
		else
			this.params = algorithmParameters;
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
		double minSize = params.getMinSize();
		double minDensity = params.getMinDensity();
		
		NodeSetList result = new NodeSetList();
		HashSet<NodeSet> addedNodeSets = new HashSet<NodeSet>();
		SeedGenerator seedGenerator = null;
		
		try {
			seedGenerator = SeedGenerator.fromString(params.getSeedGenerator(), graph);
		} catch (InstantiationException ex) {
			monitor.setException(ex, "Invalid seed generator method: "+params.getSeedGenerator());
		}
		
		/* For each seed, start growing a cluster */
		int step = 0;
		int numExpectedSeeds = seedGenerator.size();
		monitor.setStatus("Growing clusters from seeds...");
		monitor.setPercentCompleted(0);
		for (MutableNodeSet cluster: seedGenerator) {
			ClusterGrowthProcess growthProcess = new GreedyClusterGrowthProcess(cluster, 0.2);
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
			
			/* Add the cluster if we haven't found it before */
			if (!addedNodeSets.contains(frozenCluster)) {
				result.add(frozenCluster);
				addedNodeSets.add(frozenCluster);
			}
			
			/* Increase counter, report progress */
			step++;
			if (step > numExpectedSeeds) {
				monitor.setPercentCompleted(-1);
			} else {
				monitor.setPercentCompleted(100 * step / numExpectedSeeds);
			}
		}
		monitor.setPercentCompleted(100);
		
		/* Throw away the addedNodeSets hash, we don't need it anymore */
		addedNodeSets.clear();
		addedNodeSets = null;
		
		/* Merge highly overlapping clusters */
		monitor.setPercentCompleted(0);
		monitor.setStatus("Merging highly overlapping clusters...");
		result = result.mergeOverlapping(params.getOverlapThreshold(), monitor);
		monitor.setPercentCompleted(100);
		
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

	/**
	 * Sets the task monitor where the algorithm will report its progress
	 * @param monitor    the task monitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}
}
