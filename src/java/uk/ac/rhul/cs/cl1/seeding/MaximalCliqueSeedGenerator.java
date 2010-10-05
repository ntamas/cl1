package uk.ac.rhul.cs.cl1.seeding;

import java.util.Iterator;
import java.util.List;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.graph.BronKerboschMaximalCliqueFinder;
import uk.ac.rhul.cs.graph.Graph;

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
		 * The list of maximal cliques found by the clique finder.
		 */
		List<List<Integer>> cliques = null;
		
		/**
		 * An iterator over the list of maximal cliques
		 */
		Iterator<List<Integer>> iterator = null;
		
		public IteratorImpl() {
			cliqueFinder = new BronKerboschMaximalCliqueFinder();
			cliqueFinder.setGraph(graph);
			cliques = cliqueFinder.getMaximalCliques();
			
			iterator = cliques.iterator();
		}
		
		public boolean hasNext() {
			return iterator.hasNext();
		}

		public MutableNodeSet next() {
			return new MutableNodeSet(graph, iterator.next());
		}
	}
}
