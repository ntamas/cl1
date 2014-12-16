package uk.ac.rhul.cs.cl1.growth;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;
import uk.ac.rhul.cs.cl1.filters.*;
import uk.ac.rhul.cs.cl1.quality.QualityFunction;
import uk.ac.rhul.cs.cl1.seeding.Seed;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.graph.GraphAlgorithm;

import java.util.concurrent.BlockingQueue;

/**
 * Worker object that receives seeds from a synchronized queue, grows a cluster from
 * each of them and posts the clusters to another queue.
 */
public class ClusterGrowthWorker extends GraphAlgorithm implements Runnable {
    /**
     * Sentinel value to use in the input queue of the worker. When the worker finds this value in
     * its input queue, it will exit its own main loop and terminate.
     */
    public static final Seed NO_MORE_SEEDS = new Seed();

    /**
     * Sentinel value to use in the output queue of the worker. When the worker cannot grow a cluster
     * from a seed, it must post this value back to the main thread to signal that it is ready to take
     * another seed from the queue.
     */
    public static final ValuedNodeSet EMPTY_CLUSTER = new ValuedNodeSet();

    /**
     * The input queue where the seeds to process are posted to the worker.
     */
    private BlockingQueue<Seed> inputQueue;

    /**
     * The output queue where the worker has to post the clusters it has grown.
     */
    private BlockingQueue<ValuedNodeSet> outputQueue;

    /**
     * A mutable node set that the worker uses (and reuses) during the growth process.
     */
    private MutableNodeSet cluster;

    /**
     * The minimum density to enforce for each generated cluster.
     */
    private double minDensity;

    /**
     * The parameters of the ClusterONE algorithm being run. This will never be
     * modified by the worker.
     */
    private ClusterONEAlgorithmParameters parameters;

    /**
     * A filter chain that the generated clusters will pass through before they are put
     * in the output queue.
     */
    private FilterChain postFilters;

    /**
     * The quality function that the worker will use to assess the quality of a cluster.
     */
    private QualityFunction qualityFunction;

    /**
     * Constructs a new worker that uses the given queues to communicate with
     * the main thread.
     *
     * @param  graph        the graph that the worker will work on
     * @param  parameters   the parameters of the ClusterONE algorithm being run
     * @param  minDensity   the minimum density of each generated cluster
     * @param  inputQueue   the input queue that the worker has to read from
     * @param  outputQueue  the output queue that the worker has to write to
     */
    public ClusterGrowthWorker(Graph graph, ClusterONEAlgorithmParameters parameters,
                               double minDensity, BlockingQueue<Seed> inputQueue,
                               BlockingQueue<ValuedNodeSet> outputQueue) {
        super(graph);
        this.parameters = parameters;
        this.minDensity = minDensity;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

    protected void prepare() {
		// Get the quality function from the parameters
        qualityFunction = parameters.getQualityFunction();

		// Construct a filter chain to postprocess the filters
        postFilters = new FilterChain();
        if (parameters.getHaircutThreshold() > 0) {
            postFilters.add(new HaircutFilter(parameters.getHaircutThreshold(), true));
        }
        if (parameters.isFluffClusters()) {
            postFilters.add(new FluffingFilter());
        }
        postFilters.add(new SizeFilter(parameters.getMinSize()));
        postFilters.add(new DensityFilter(minDensity));
        if (parameters.getKCoreThreshold() > 0) {
            postFilters.add(new KCoreFilter(parameters.getKCoreThreshold()));
        }

        // Construct the initial mutable node set
        cluster = new MutableNodeSet(graph);
    }

    @Override
    public void run() {
        Seed seed;
        ValuedNodeSet result;

        if (cluster == null) {
            prepare();
        }

        // Enter the main loop
        while (!shouldStop) {
            try {
                seed = inputQueue.take();
                if (seed == ClusterGrowthWorker.NO_MORE_SEEDS) {
                    // This is how the main thread lets us know that there are no more seeds
                    // to process. We put it back to the queue so the next worker is notified
                    // and then exit.
                    while (true) {
                        try {
                            inputQueue.put(seed);
                            break;
                        } catch (InterruptedException ignored) {
                        }
                    }
                    halt();
                    break;
                }
            } catch (InterruptedException ex) {
                halt();
                break;
            }

			/* Initialize the node set from the seed */
            seed.initializeMutableNodeSet(cluster);

			/* Construct a growth process from the seed */
            GreedyClusterGrowthProcess growthProcess =
                    new GreedyClusterGrowthProcess(cluster, minDensity, qualityFunction);
            growthProcess.setDebugMode(debugMode);
            growthProcess.setKeepInitialSeeds(parameters.isKeepInitialSeeds());

			/* Run the growth process */
            while (!shouldStop && growthProcess.step());

			/* Were we interrupted by the user? */
            if (shouldStop)
                break;

			/* Do a haircut operation, then check the size and density of the cluster */
            if (!postFilters.filter(cluster)) {
                /* Filter rejected the cluster; send back an empty cluster instead */
                result = EMPTY_CLUSTER;
            } else {
                result = new ValuedNodeSet(cluster, 1);
            }

			/* Post the cluster back to the main thread */
            while (true) {
                try {
                    outputQueue.put(result);
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
