package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import com.sosnoski.util.hashset.IntHashSet;

/**
 * Seed generator class that returns the unused nodes of a graph in decreasing order of their degrees.
 * 
 * Seed generator class where nodes of a graph are returned one by one in decreasing
 * order of their degree, but nodes that participate already in at least one cluster
 * are not returned.
 * 
 * @author tamas
 */
public class UnusedNodesSeedGenerator extends EveryNodeSeedGenerator {
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	private class IteratorImpl extends SeedIterator {
		/** Queue containing nodes that have not been returned yet */
		ArrayList<Integer> nodes;
		/** Nodes that have been used so far */
		IntHashSet usedNodes;
		/** Iterator for the queue */
		ListIterator<Integer> it;
		
		/** Constructs the iterator */
		IteratorImpl() {
			int n = graph.getNodeCount();
			
			nodes = new ArrayList<Integer>();
			for (int i = 0; i < n; i++)
				nodes.add(i);
			
			Collections.sort(nodes, new Comparator<Integer>() {
				public int compare(Integer foo, Integer bar) {
					int comp = graph.getDegree(bar) - graph.getDegree(foo);
					if (comp != 0)
						return comp;
					return foo - bar;
				}
			});
			it = nodes.listIterator();
			usedNodes = new IntHashSet();
		}
		
		public boolean hasNext() {
			while (it.hasNext()) {
				if (!usedNodes.contains(it.next())) {
					it.previous();
					return true;
				}
			}
			return false;
		}

		public MutableNodeSet next() {
			MutableNodeSet result = new MutableNodeSet(graph);
			result.add(it.next());
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public void processFoundCluster(NodeSet cluster) {
			for (int i: cluster)
				usedNodes.add(i);
		}
	}
	
	public UnusedNodesSeedGenerator() {
		super();
	}

	public UnusedNodesSeedGenerator(Graph graph) {
		super(graph);
	}
	
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
}
