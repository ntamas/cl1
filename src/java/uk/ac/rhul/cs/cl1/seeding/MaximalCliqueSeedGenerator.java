package uk.ac.rhul.cs.cl1.seeding;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import uk.ac.rhul.cs.graph.BronKerboschMaximalCliqueFinder;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.BlockingQueueAdapter;

/**
 * Seed generator that returns every maximal clique of a graph as a seed.
 * 
 * Maximal cliques are sought for in a separate thread; a blocking queue is
 * used to store the detected cliques. The contents of the queue is produced
 * by a {@link BronKerboschMaximalCliqueFinder} and consumed by the user of
 * the maximal clique seed generator via its iterator.
 * 
 * @author ntamas
 */
public class MaximalCliqueSeedGenerator extends SeedGenerator {
	/**
	 * Constructs a maximal clique seed generator with no associated graph.
	 */
	public MaximalCliqueSeedGenerator() {
		super();
	}

	/**
	 * Constructs a maximal clique seed generator associated to the given graph.
	 */
	public MaximalCliqueSeedGenerator(Graph graph) {
		super(graph);
	}

	/**
	 * Returns -1 as we cannot know in advance how many seeds there will be.
	 */
	public int size() {
		return -1;
	}

	/**
	 * Returns an iterator that iterates over the maximal cliques of the associated graph
	 */
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
	
	class IteratorImpl extends SeedIterator {
		/**
		 * A maximal clique finder we will use
		 */
		BronKerboschMaximalCliqueFinder cliqueFinder;
		
		/**
		 * A blocking queue in which the clique finder stores the cliques.
		 */
		ArrayBlockingQueue<List<Integer>> cliques =
			new ArrayBlockingQueue<List<Integer>>(100);
		
		/**
		 * The thread in which the clique finder runs
		 */
		Thread cliqueFinderThread = null;

		public IteratorImpl() {
			cliqueFinder = new BronKerboschMaximalCliqueFinder();
			cliqueFinder.setGraph(graph);
			
			cliqueFinderThread = new Thread(new Runnable() {
				public void run() {
					BlockingQueueAdapter<List<Integer>> cliqueCollection =
						new BlockingQueueAdapter<List<Integer>>(cliques);
					cliqueFinder.collectMaximalCliques(cliqueCollection);
				}
			});
			cliqueFinderThread.start();
		}
		
		public boolean hasNext() {
			if (cliques.isEmpty()) {
				/* Queue is empty. Is the thread still running? */
				return (cliqueFinderThread.isAlive() &&
					!cliqueFinderThread.isInterrupted());
			}
			return true;
		}

		public Seed next() {
			List<Integer> clique;

			try {
				clique = cliques.take();
			} catch (InterruptedException ex) {
				return null;
			}

			int[] members = new int[clique.size()];
			int i = 0;
			for (int node: clique) {
				members[i++] = node;
			}

			return new Seed(graph, members);
		}
	}
}
