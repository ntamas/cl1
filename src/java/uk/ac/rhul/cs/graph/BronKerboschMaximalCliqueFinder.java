package uk.ac.rhul.cs.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.sosnoski.util.array.IntArray;
import com.sosnoski.util.hashset.IntHashSet;

/**
 * Finds all the maximal cliques in a graph using the Bron-Kerbosch algorithm.
 * 
 * This implementation is based on the implementation found in the JGraphT
 * library, but it is adapted to our {@link Graph} data type and uses more
 * efficient data structures.
 * 
 * @author ntamas
 */
public class BronKerboschMaximalCliqueFinder extends GraphAlgorithm {
	/**
	 * Internal method for finding maximal cliques.
	 * 
	 * @param  result  a collection in which the cliques are collected
	 * @param  potentialClique  a potential clique being built during the
	 *                          algorithm
	 * @param  candidates  candidate vertices with which we can extend the
	 *                     potential clique
	 * @param  alreadyFound  the set of vertices already found
	 */
	protected void findCliques(Collection<IntArray> result,
			              IntHashSet potentialClique,
			              HashSet<Integer> candidates,
			              IntArray alreadyFound) {
		if (isAnyConnectedToAllCandidates(alreadyFound, candidates))
			return;
		
		for (Integer candidate: candidates) {
			// TODO
		}
	}
	
	/**
	 * Returns the list of all maximal cliques
	 */
	public List<IntArray> getMaximalCliques() {
		List<IntArray> result = new ArrayList<IntArray>();
		getMaximalCliques(result);
		return result;
	}
	
	/**
	 * Finds all maximal cliques and stores them in the given list.
	 * 
	 * @param  result  the collection in which the result will be stored
	 */
	public void getMaximalCliques(Collection<IntArray> result) {
		IntHashSet potentialClique = new IntHashSet();
		HashSet<Integer> candidates = new HashSet<Integer>();
		IntArray alreadyFound = new IntArray();
		
		findCliques(result, potentialClique, candidates, alreadyFound);
	}
	
	/**
	 * Checks whether at least one node of the given node array is connected
	 * to all the candidates.
	 * 
	 * @param  nodes       the nodes being checked
	 * @param  candidates  the list of candidates
	 * @return  true if at least one node is connected to all the candidates,
	 *          false otherwise
	 */
	protected boolean isAnyConnectedToAllCandidates(IntArray nodes,
			HashSet<Integer> candidates) {
		Graph graph = this.getGraph();
		
		for (int i = 0; i < nodes.size(); i++) {
			// TODO
		}
		
		return false;
	}
}
