package uk.ac.rhul.cs.cl1;

import java.util.*;
import java.util.concurrent.*;

import uk.ac.rhul.cs.cl1.growth.ClusterGrowthWorker;
import uk.ac.rhul.cs.cl1.merging.AbstractNodeSetMerger;
import uk.ac.rhul.cs.cl1.seeding.Seed;
import uk.ac.rhul.cs.cl1.seeding.SeedGenerator;
import uk.ac.rhul.cs.cl1.seeding.SeedIterator;
import uk.ac.rhul.cs.cl1.support.OrderMaintainingQueue;
import uk.ac.rhul.cs.cl1.support.UsedNodeSet;
import uk.ac.rhul.cs.collections.IntObjectHashMap;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.graph.GraphAlgorithm;
import uk.ac.rhul.cs.graph.TransitivityCalculator;
import uk.ac.rhul.cs.utils.ArrayUtils;
import uk.ac.rhul.cs.utils.Ordered;

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
	public static final String version = "1.2";

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
	 * Internal enum that stores the state of the main loop of the algorithm.
	 */
	enum State {
		START,
		GENERATING_SEEDS,
		NOTIFYING_WORKERS_NO_MORE_SEEDS,
		WAITING_FOR_CLUSTERS,
		FINISHED(true),
		CANCELLED(true);

		boolean isTerminal;

		State() {
			this(false);
		}

		State(boolean isTerminal) {
			this.isTerminal = isTerminal;
		}
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
		Seed seed;
		Seed pendingSeed = null;
		ValuedNodeSet cluster;
		Ordered<ValuedNodeSet> orderedCluster;
		State state;
		UsedNodeSet usedNodes;

		int numGeneratedSeeds;
		int numPostedSeeds;
		int numProcessedClusters;

		ValuedNodeSetList result = new ValuedNodeSetList();
		IntObjectHashMap submittedSeeds = new IntObjectHashMap();
		OrderMaintainingQueue<ValuedNodeSet> receivedClusters = new OrderMaintainingQueue<ValuedNodeSet>();

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
			monitor.setStatus("Choosing density thresold...");
			monitor.setPercentCompleted(0);
			if (graph.isWeighted())
				minDensity = 0.3;
			else {
				TransitivityCalculator calc = new TransitivityCalculator(graph);
				calc.setTaskMonitor(monitor);
				if (calc.getGlobalTransitivity() < 0.1)
					minDensity = 0.6;
				else
					minDensity = 0.5;
			}
			monitor.setPercentCompleted(100);
		}

		/* Create an executor service that will run the workers */
		int numThreads = parameters.getNumThreads();
		if (numThreads <= 0) {
			numThreads = Math.max(1, Runtime.getRuntime().availableProcessors());
		}
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);

		// Create the input and output queue for the workers.
		// Limit the size of the seed queue so it does not run too much "ahead" the worker
		// threads. This is useful for seed generators that depend on the clusters produced
		// by the workers.
		LinkedBlockingQueue<Ordered<Seed>> seedQueue = new LinkedBlockingQueue<Ordered<Seed>>(numThreads);
		LinkedBlockingQueue<Ordered<ValuedNodeSet>> clusterQueue = new LinkedBlockingQueue<Ordered<ValuedNodeSet>>();

		/* Create the workers and post them to the executor */
		for (int i = 0; i < numThreads; i++) {
			ClusterGrowthWorker worker = new ClusterGrowthWorker(graph, parameters, minDensity,
					seedQueue, clusterQueue);
			worker.setDebugMode(debugMode);
			executor.execute(worker);
		}

		// Get the seed generator from the parameters
		SeedGenerator seedGenerator = parameters.getSeedGenerator();
		seedGenerator.setGraph(graph);

		// Create a used node set where we will mark nodes that have been used in clusters
		usedNodes = new UsedNodeSet(graph);

		// Set up the task monitor
		if (numThreads > 1) {
			monitor.setStatus("Growing clusters from seeds using " + numThreads + " threads...");
		} else {
			monitor.setStatus("Growing clusters from seeds...");
		}
		monitor.setPercentCompleted(0);

		// Set up the seed iterator
		SeedIterator it = seedGenerator.iterator();

		numGeneratedSeeds = 0;
		numPostedSeeds = 0;
		numProcessedClusters = 0;
		state = State.START;

		// Start iterating over the seeds and collecting the clusters
		while (!state.isTerminal) {
			switch (state) {
				case START:
					state = State.GENERATING_SEEDS;
					break;

				case GENERATING_SEEDS:
					// Try to fill the seed queue with seeds
					boolean shouldEnqueue = true;
					while (shouldEnqueue) {
						// Get the next seed that is acceptable
						boolean seedAccepted = false;

						seed = null;
						while (!seedAccepted) {
							if (pendingSeed != null) {
								seed = pendingSeed;
								numGeneratedSeeds++;
								pendingSeed = null;
							} else if (it.hasNext()) {
								seed = it.next();
								numGeneratedSeeds++;
							} else {
								seed = null;
							}
							seedAccepted = (seed == null || !parameters.shouldRejectSeedsWithOnlyUsedNodes() ||
									!usedNodes.areAllNodesUsedFromSeed(seed));
						}

						if (seed == null) {
							state = State.NOTIFYING_WORKERS_NO_MORE_SEEDS;
							shouldEnqueue = false;
						} else {
							// Offer the seed to the workers; if the queue is full, do nothing
							if (seedQueue.offer(new Ordered<Seed>(numPostedSeeds, seed))) {
								// Store the seed and increase the number of posted seeds
								submittedSeeds.add(numPostedSeeds, seed);
								numPostedSeeds++;
							} else {
								// Queue is full now. Store the seed so we can try it again in the next iteration.
								pendingSeed = seed;
								numGeneratedSeeds--;
								shouldEnqueue = false;
							}
						}
					}
					break;

				case NOTIFYING_WORKERS_NO_MORE_SEEDS:
					// Iterator has just became null, so inform workers that there will
					// be no more seeds.
					if (seedQueue.offer(new Ordered<Seed>(numPostedSeeds, ClusterGrowthWorker.NO_MORE_SEEDS))) {
						state = State.WAITING_FOR_CLUSTERS;
					}
					break;

				case WAITING_FOR_CLUSTERS:
					// If we have processed all the seeds, switch to the FINISHED state
					if (numPostedSeeds == numProcessedClusters) {
						state = State.FINISHED;
					}
					break;

				case FINISHED:
				case CANCELLED:
					// Nothing to do here; we should not get here anyway.
			}

			// Check for termination
			if (shouldStop) {
				state = State.CANCELLED;
			}

			// In GENERATING_SEEDS, NOTIFYING_WORKERS_NO_MORE_SEEDS and WAITING_FOR_CLUSTERS states,
			// try to read a cluster from the cluster queue if we still expect one.
			if (state == State.GENERATING_SEEDS || state == State.NOTIFYING_WORKERS_NO_MORE_SEEDS ||
					state == State.WAITING_FOR_CLUSTERS) {
				// Try to get clusters from the incoming queue if we expect them
				while (numProcessedClusters < numPostedSeeds) {
					orderedCluster = null;

					try {
						orderedCluster = clusterQueue.take();
					} catch (InterruptedException ignored) {
					}

					if (orderedCluster == null)
						break;

					// Add the cluster to the queue that will restore the ordering
					// according to the sequence numbers
					receivedClusters.add(orderedCluster);
					numProcessedClusters++;

					// Try to retrieve a few clusters from receivedClusters; note that
					// even though we have added a cluster above, the queue might still
					// appear empty if the cluster with the _next_ sequence number we
					// are waiting for has not arrived yet
					while (!receivedClusters.isEmpty()) {
						orderedCluster = receivedClusters.remove();

						cluster = orderedCluster.object;
						if (cluster != ClusterGrowthWorker.EMPTY_CLUSTER) {
							Seed originalSeed = (Seed) submittedSeeds.remove(orderedCluster.sequenceNumber);
							// Check whether the cluster would have been generated at all if we were working
							// sequentially.
							if (!usedNodes.areAllNodesUsedFromSeed(originalSeed)) {
								// Yes, so mark the nodes in the seed and the cluster as used and store the
								// cluster.
								result.add(cluster);
								usedNodes.markSeedAsUsed(originalSeed);
								usedNodes.markNodeSetAsUsed(cluster);
							}
						}
					}

					// We try to keep all our workers busy so we break out of the while
					// loop here if the queue through which we feed the workers has some
					// empty slots and we are still generating seeds.
					if (state == State.GENERATING_SEEDS && seedQueue.remainingCapacity() > 0)
						break;
				}
			}

			// Report progress.
			// Progress has to be calculated from numGeneratedSeeds and not numPostedSeeds
			// because some seeds may be skipped before posting them to workers
			monitor.setPercentCompleted((int) (numGeneratedSeeds * 100.0 / it.getEstimatedLength()));

			// Check for termination
			if (shouldStop) {
				state = State.CANCELLED;
			}
		}

		if (state == State.FINISHED) {
			// Merge highly overlapping clusters
			merger.setTaskMonitor(monitor);
			this.result = merger.mergeOverlapping(result, parameters.getSimilarityFunction(),
					parameters.getOverlapThreshold());
		}

		// Wait for the workers to terminate. 1 day is a reasonable upper bound on the timeout ;)
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
