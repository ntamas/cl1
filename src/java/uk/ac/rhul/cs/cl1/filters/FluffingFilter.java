package uk.ac.rhul.cs.cl1.filters;

import java.util.SortedSet;

import com.sosnoski.util.array.IntArray;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.graph.Directedness;
import uk.ac.rhul.cs.graph.Graph;

/**
 * Filter that accepts each nodeset after performing a fluffing operation
 * on them.
 * 
 * A fluffing operation expands a nodeset by adding neighbouring nodes that
 * are connected to at least two third of the members of the original nodeset.
 * 
 * The fluffing operation may be iterative or non-iterative; iterative fluffing
 * repeats the fluffing operation until no new nodes can be added.
 * @author tamas
 *
 */
public class FluffingFilter implements NodeSetFilter {
	
	protected boolean iterative = false;
	
	/**
	 * Constructs a fluffing filter
	 */
	public FluffingFilter() {}
	
	/**
	 * Fluffs the given nodeset and accepts it.
	 */
	public boolean filter(MutableNodeSet nodeSet) {
		IntArray toAdd = new IntArray();
		Graph graph = nodeSet.getGraph();
		
		/* We won't fluff clusters smaller than five elements,
		 * there's not enough evidence to extend it more.
		 */
		if (nodeSet.size() < 5)
			return true;
		
		do {
			int minCount = (int)Math.floor(2.0 * nodeSet.size() / 3.0);
			SortedSet<Integer> members = nodeSet.getMembers();
			toAdd.clear();
			
			for (int i: nodeSet.getExternalBoundaryNodeIterator()) {
				int[] neis =
					graph.getAdjacentNodeIndicesArray(i, Directedness.ALL);
				
				int intersectionSize = 0;
				for (int j: neis)
					if (members.contains(j))
						intersectionSize++;
				
				if (intersectionSize >= minCount)
					toAdd.add(i);
			}
			
			nodeSet.add(toAdd.toArray());
		} while (iterative && toAdd.size() > 0);
		
		return true;
	}

}
