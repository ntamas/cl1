package uk.ac.rhul.cs.cl1.seeding;

import uk.ac.rhul.cs.graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Seed generator class where every single node of a graph will be generated as
 * a seed nodeset, in decreasing order of their degrees.
 * 
 * @author tamas
 */
public class EveryNodeSeedGenerator extends SeedGenerator {
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	private class IteratorImpl extends SeedIterator {
		/** Queue containing the nodes to return in sorted order */
		private ArrayList<Integer> nodes;
		/** Node counter */
		private int nextNodeIndex;
		/** Maximum node count */
		private int totalSteps;

		/** Constructs the iterator */
		IteratorImpl() {
			totalSteps = graph.getNodeCount();

			nodes = new ArrayList<Integer>();
			for (int i = 0; i < totalSteps; i++) {
				nodes.add(i);
			}

			Collections.sort(nodes, new Comparator<Integer>() {
				public int compare(Integer foo, Integer bar) {
					// Compare by degrees and then by ID.
					int diff = graph.getDegree(bar) - graph.getDegree(foo);
					return (diff != 0) ? diff : foo-bar;
				}
			});

			nextNodeIndex = 0;
		}

		public int getEstimatedLength() {
			return totalSteps;
		}

		public boolean hasNext() {
			return nextNodeIndex < totalSteps;
		}

		public Seed next() {
			Seed result = new Seed(graph, nodes.get(nextNodeIndex));
			nextNodeIndex++;
			return result;
		}
	}
	
	/** Constructs a seed generator that is not associated to any graph yet */
	public EveryNodeSeedGenerator() {
		super();
	}
	
	/** Constructs a seed generator for the given graph that considers every node as a seed nodeset. */
	public EveryNodeSeedGenerator(Graph graph) {
		super(graph);
	}

	/**
	 * Iterates over each node of the graph as a MutableNodeSet.
	 * 
	 * The node count of the graph must stay the same while generating
	 * seed nodesets.
	 */
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
	
	/**
	 * Returns the number of nodes in the graph.
	 */
	public int size() {
		return this.graph.getNodeCount();
	}
}