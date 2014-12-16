package uk.ac.rhul.cs.cl1;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;

import uk.ac.rhul.cs.cl1.growth.ClusterGrowthWorker;
import uk.ac.rhul.cs.cl1.merging.AbstractNodeSetMerger;
import uk.ac.rhul.cs.cl1.seeding.Seed;
import uk.ac.rhul.cs.cl1.seeding.SeedGenerator;
import uk.ac.rhul.cs.cl1.seeding.SeedIterator;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.graph.GraphAlgorithm;
import uk.ac.rhul.cs.graph.TransitivityCalculator;
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
public class ClusterONE extends GraphAlgorithm implements Callable<Void>, TaskMonitorSupport {
	/** The name of the application that will appear on the user interface */
	public static final String applicationName = "ClusterONE";
	
	/** The version number of the application */
	public static final String version = "1.1";

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
		ValuedNodeSet cluster;

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
			if (graph.isWeighted())
				minDensity = 0.3;
			else {
				TransitivityCalculator calc = new TransitivityCalculator(graph);
				if (calc.getGlobalTransitivity() < 0.1)
					minDensity = 0.6;
				else
					minDensity = 0.5;
			}
		}

		/* Create the input and output queue for the workers */
		LinkedBlockingQueue<Seed> seedQueue = new LinkedBlockingQueue<Seed>();
		LinkedBlockingQueue<ValuedNodeSet> clusterQueue = new LinkedBlockingQueue<ValuedNodeSet>();

		/* Create a single worker that we will run on a separate thread */
		ClusterGrowthWorker worker = new ClusterGrowthWorker(graph, parameters, minDensity,
				seedQueue, clusterQueue);
		worker.setDebugMode(debugMode);

		/* Create an executor service that will run the worker */
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(worker);

		/* Get the seed generator from the parameters */
		SeedGenerator seedGenerator = parameters.getSeedGenerator();	
		seedGenerator.setGraph(graph);
		
		/* For each seed, start growing a cluster */
		monitor.setStatus("Growing clusters from seeds...");
		monitor.setPercentCompleted(0);

		SeedIterator it = seedGenerator.iterator();
		while (!shouldStop && it.hasNext()) {
			/* Get the next seed */
			Seed seed = it.next();
			if (seed == null) {
				/* This happens when we are using a seed generator running in a
				 * separate thread (such as MaximalCliqueSeedGenerator) and that
				 * thread is interrupted for whatever reason.
				 */
				halt();
				continue;
			}

			/* Post the seed to the worker */
			try {
				seedQueue.put(seed);
			} catch (InterruptedException ex) {
				halt();
				break;
			}

			/* Were we interrupted by the user? */
			if (shouldStop)
				break;

			/* Get the result from the worker */
			try {
				cluster = clusterQueue.take();
			} catch (InterruptedException ex) {
				halt();
				break;
			}

			/* Were we interrupted by the user? */
			if (shouldStop)
				break;

			/* Add the cluster if we haven't found it before */
			if (cluster != ClusterGrowthWorker.EMPTY_CLUSTER && !addedNodeSets.contains(cluster)) {
				result.add(cluster);
				addedNodeSets.add(cluster);
				it.processFoundCluster(cluster);
			}
			
			/* Increase counter, report progress */
			monitor.setPercentCompleted((int)it.getPercentCompleted());
		}

		/* Okay, we are done */
		monitor.setPercentCompleted(100);
		
		/* Throw away the addedNodeSets hash, we don't need it anymore */
		addedNodeSets.clear();

		/* Post a signal to the workers to let them know that we are ready */
		while (true) {
			try {
				seedQueue.put(ClusterGrowthWorker.NO_MORE_SEEDS);
				break;
			} catch (InterruptedException ignored) {
			}
		}

		/* Merge highly overlapping clusters */
		merger.setTaskMonitor(monitor);
		this.result = merger.mergeOverlapping(result, parameters.getSimilarityFunction(),
				parameters.getOverlapThreshold());

		/* Wait for the workers to terminate. 1 day is a reasonable upper bound on the timeout ;) */
		executor.shutdown();
		while (true) {
			try {
				executor.awaitTermination(1, TimeUnit.DAYS);
				break;
			} catch (InterruptedException ignored) {
			}
		}
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
	 *
	 * @param monitor    the task monitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}
}
