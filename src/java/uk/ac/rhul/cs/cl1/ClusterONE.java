package uk.ac.rhul.cs.cl1;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uk.ac.rhul.cs.cl1.filters.DensityFilter;
import uk.ac.rhul.cs.cl1.filters.FilterChain;
import uk.ac.rhul.cs.cl1.filters.FluffingFilter;
import uk.ac.rhul.cs.cl1.filters.HaircutFilter;
import uk.ac.rhul.cs.cl1.filters.KCoreFilter;
import uk.ac.rhul.cs.cl1.filters.SizeFilter;
import uk.ac.rhul.cs.cl1.seeding.SeedGenerator;
import uk.ac.rhul.cs.cl1.seeding.SeedIterator;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.graph.GraphAlgorithm;
import uk.ac.rhul.cs.utils.ArrayUtils;

/**
 * Main class for the ClusterONE algorithm.
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
	public static final String applicationName = "ClusterONE";
	
	/** The version number of the application */
	public static final String version = "0.92";

	/** A thread pool used for asynchronous operations within ClusterONE */
	private static Executor threadPool = null;

	/** The clustering result as a list of {@link ValuedNodeSet} objects */
	protected ValuedNodeSetList result = null;
	
	/** Algorithm settings for this instance */
	protected ClusterONEAlgorithmParameters parameters = null;

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
			this.setParameters(new ClusterONEAlgorithmParameters());
		else
			this.setParameters(algorithmParameters);
	}

	/**
	 * Executes the algorithm in a separate thread and returns a future
	 */
	public Void call() throws ClusterONEException {
		run();
		return null;
	}
	
	/**
	 * Returns the current parameter setting of the algorithm
	 * 
	 * @return the parameters
	 */
	public ClusterONEAlgorithmParameters getParameters() {
		return parameters;
	}

	/**
	 * Returns the clustering results or null if there was no clustering executed so far
	 */
	public List<ValuedNodeSet> getResults() {
		return result;
	}
	
	/**
	 * Returns a thread pool used by ClusterONE for asynchronous operations
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
		Double minDensity = parameters.getMinDensity();
		AbstractNodeSetMerger merger;
		
		ValuedNodeSetList result = new ValuedNodeSetList();
		HashSet<NodeSet> addedNodeSets = new HashSet<NodeSet>();
		
		/* Simple sanity checks */
		if (ArrayUtils.min(graph.getEdgeWeights()) < 0.0)
			throw new ClusterONEException("Edge weights must all be non-negative");
		try {
			merger = AbstractNodeSetMerger.fromString(
					parameters.getMergingMethodName());
		} catch (InstantiationException ex) {
			throw new ClusterONEException(ex.getMessage());
		}	
		
		/* Set the minimum density automatically if needed */
		if (minDensity == null) {
			minDensity = ArrayUtils.getMedian(graph.getEdgeWeights());
			if (minDensity == null)
				minDensity = 1.0;
			minDensity *= 2.0/3;
		}
		
		/* Get the seed generator from the parameters */
		SeedGenerator seedGenerator = parameters.getSeedGenerator();	
		seedGenerator.setGraph(graph);
		
		/* Get the quality function from the parameters */
		QualityFunction qualityFunc = parameters.getQualityFunction();
		
		/* Construct a filter chain to postprocess the filters */
		FilterChain postFilters = new FilterChain();
		if (parameters.getHaircutThreshold() > 0)
			postFilters.add(new HaircutFilter(parameters.getHaircutThreshold(), true));
		if (parameters.isFluffClusters())
			postFilters.add(new FluffingFilter());
		postFilters.add(new SizeFilter(parameters.getMinSize()));
		postFilters.add(new DensityFilter(minDensity));
		if (parameters.getKCoreThreshold() > 0)
			postFilters.add(new KCoreFilter(parameters.getKCoreThreshold()));
		
		/* For each seed, start growing a cluster */
		monitor.setStatus("Growing clusters from seeds...");
		monitor.setPercentCompleted(0);
		
		SeedIterator it = seedGenerator.iterator();
		while (it.hasNext()) {
			/* Get the next seed */
			MutableNodeSet cluster = it.next();
			
			if (cluster == null) {
				/* This happens when we are using a seed generator running in a
				 * separate thread (such as MaximalCliqueSeedGenerator) and that
				 * thread is interrupted for whatever reason.
				 */
				shouldStop = true;
				return;
			}
			
			/* Construct a growth process from the seed */
			ClusterGrowthProcess growthProcess =
				new GreedyClusterGrowthProcess(cluster, minDensity, qualityFunc);
			if (debugMode)
				growthProcess.setDebugMode(debugMode);
			
			/* Run the growth process */
			while (!shouldStop && growthProcess.step());
			
			/* Were we interrupted by the user? */
			if (shouldStop)
				return;
			
			/* Do a haircut operation, then check the size and density of the cluster */
			if (!postFilters.filter(cluster))
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
		merger.setTaskMonitor(monitor);
		this.result = merger.mergeOverlapping(result,
				parameters.getSimilarityFunction(),
				parameters.getOverlapThreshold());
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
	 * Sets the current parameter settings of the algorithm
	 * @param parameters the new parameter settings
	 */
	public void setParameters(ClusterONEAlgorithmParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets the task monitor where the algorithm will report its progress
	 * @param monitor    the task monitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}
}
