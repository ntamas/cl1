package uk.ac.rhul.cs.cl1;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.ac.rhul.cs.utils.ArrayUtils;

/**
 * Main class for the Cluster ONE algorithm.
 * 
 * This class represents an instance of the algorithm along with all its
 * necessary parameters. The main entry point of the algorithm is the
 * run() method which executes the clustering algorithm on the graph
 * set earlier using the setGraph() method. The algorithm can also be
 * run in a separate thread as it implements the Callable interface.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class ClusterONE extends GraphAlgorithm implements Callable<Void> {
	/** The name of the application that will appear on the user interface */
	public static final String applicationName = "Cluster ONE";
	
	/** The version number of the application */
	public static final String version = "0.1";

	/** A thread pool used for asynchronous operations within Cluster ONE */
	private static Executor threadPool = null;

	/** The clustering result as a list of {@link ValuedNodeSet} objects */
	protected ValuedNodeSetList result = null;
	
	/** Algorithm settings for this instance */
	protected ClusterONEAlgorithmParameters params = null;

	/** A task monitor where the algorithm will report its progress */
	protected TaskMonitor monitor = new NullTaskMonitor();
	
	/** Whether we are running on a Mac or not */
	protected static boolean runningOnMac = false;
	
	static {
		runningOnMac = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	}
	
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
	 * Executes the algorithm in a separate thread and returns a future
	 */
	public Void call() throws ClusterONEException {
		run();
		return null;
	}
	
	/**
	 * Returns the clustering results or null if there was no clustering executed so far
	 */
	public List<ValuedNodeSet> getResults() {
		return result;
	}
	
	/**
	 * Returns a thread pool used by Cluster ONE for asynchronous operations
	 */
	public static Executor getThreadPool() {
		if (threadPool == null)
			threadPool = Executors.newSingleThreadExecutor();
		return threadPool;
	}
	
	/**
	 * Checks whether we are running on a Mac 
	 */
	public static boolean isRunningOnMac() {
		return runningOnMac;
	}
	
	/**
	 * Executes the algorithm on the graph set earlier by setGraph()
	 */
	public void run() throws ClusterONEException {
		boolean needHaircut = params.isHaircutNeeded();
		double minSize = params.getMinSize();
		double minDensity = params.getMinDensity();
		double haircutThreshold = params.getHaircutThreshold();
		
		ValuedNodeSetList result = new ValuedNodeSetList();
		HashSet<NodeSet> addedNodeSets = new HashSet<NodeSet>();
		
		SeedGenerator seedGenerator = params.getSeedGenerator();	
		seedGenerator.setGraph(graph);
		
		/* Simple sanity checks */
		if (ArrayUtils.min(graph.getEdgeWeights()) < 0.0)
			throw new ClusterONEException("Edge weights must all be non-negative");

		/* For each seed, start growing a cluster */
		monitor.setStatus("Growing clusters from seeds...");
		monitor.setPercentCompleted(0);
		
		SeedIterator it = seedGenerator.iterator();
		while (it.hasNext()) {
			MutableNodeSet cluster = it.next();
			ClusterGrowthProcess growthProcess = new GreedyClusterGrowthProcess(cluster, minDensity);
			while (!shouldStop && growthProcess.step());
			
			/* Were we stopped? */
			if (shouldStop)
				return;
			
			/* Do a haircut operation on the cluster if necessary */
			if (needHaircut)
				cluster.haircut(haircutThreshold);
			
			/* Check the size of the cluster -- if too small, skip it */
			if (cluster.size() < minSize)
				continue;
			
			/* Check the density of the cluster -- if too sparse, skip it */
			if (cluster.getDensity() < minDensity)
				continue;
			
			/* Convert the cluster to a valued nodeset */
			ValuedNodeSet frozenCluster = new ValuedNodeSet(cluster, 1);
			cluster = null;
			
			/* Add the cluster if we haven't found it before */
			if (!addedNodeSets.contains(frozenCluster)) {
				result.add(frozenCluster);
				addedNodeSets.add(frozenCluster);
				it.processFoundCluster(frozenCluster);
			}
			
			/* Increase counter, report progress */
			monitor.setPercentCompleted((int)it.getPercentCompleted());
		}
		monitor.setPercentCompleted(100);
		
		/* Throw away the addedNodeSets hash, we don't need it anymore */
		addedNodeSets.clear();
		addedNodeSets = null;
		
		/* Merge highly overlapping clusters */
		if (params.getMergingMethod() != null && !params.getMergingMethod().equals("none")) {
			monitor.setPercentCompleted(0);
			monitor.setStatus("Merging highly overlapping clusters...");
			result = result.mergeOverlapping(params.getMergingMethod(),
					params.getOverlapThreshold(), monitor);
			monitor.setPercentCompleted(100);
		}
		
		/* Return the result effectively */
		this.result = result;
	}
	
	/**
	 * Executes the algorithm on the given graph.
	 * 
	 * @param   graph    the graph being clustered
	 */
	public void runOnGraph(Graph graph) throws ClusterONEException {
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
