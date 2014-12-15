package uk.ac.rhul.cs.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import uk.ac.rhul.cs.utils.IntegerRange;

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
	protected void findCliques(Collection<List<Integer>> result,
			              HashSet<Integer> potentialClique,
			              HashSet<Integer> candidates,
			              HashSet<Integer> alreadyFound) {
		if (isAnyConnectedToAllCandidates(alreadyFound, candidates))
			return;
		
		Graph graph = this.getGraph();
		Iterator<Integer> it = candidates.iterator();
		
		while (it.hasNext()) {
			Integer candidate = it.next();
			
			// create newCandidates and newAlreadyFound
			HashSet<Integer> newCandidates = new HashSet<Integer>();
			HashSet<Integer> newAlreadyFound = new HashSet<Integer>();
			
			// move candidate node to potentialClique
			potentialClique.add(candidate);
			it.remove();
			
			// create newCandidates by removing nodes in candidates that are
			// not connected to the candidate node
			int[] neis = graph.getAdjacentNodeIndicesArray(candidate, Directedness.ALL);
			for (int nei: neis) {
				if (candidates.contains(nei))
					newCandidates.add(nei);
				if (alreadyFound.contains(nei))
					newAlreadyFound.add(nei);
			}
			
			if (newCandidates.isEmpty() && newAlreadyFound.isEmpty()) {
				// this is a maximal clique
				result.add(new ArrayList<Integer>(potentialClique));
			} else {
				// recursive call
				findCliques(result, potentialClique, newCandidates, newAlreadyFound);
			}
			
			alreadyFound.add(candidate);
			potentialClique.remove(candidate);
		}
	}

	/**
	 * Finds all maximal cliques and stores them in the given list.
	 *
	 * @param  result  the collection in which the result will be stored
	 */
	public void collectMaximalCliques(Collection<List<Integer>> result) {
		HashSet<Integer> potentialClique = new HashSet<Integer>();
		HashSet<Integer> candidates = new HashSet<Integer>();
		HashSet<Integer> alreadyFound = new HashSet<Integer>();

		candidates.addAll(new IntegerRange(graph.getNodeCount()));
		findCliques(result, potentialClique, candidates, alreadyFound);
	}

	/**
	 * Returns the list of all maximal cliques
	 */
	public List<List<Integer>> getMaximalCliques() {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		collectMaximalCliques(result);
		return result;
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
	protected boolean isAnyConnectedToAllCandidates(HashSet<Integer> nodes,
			HashSet<Integer> candidates) {
		Graph graph = this.getGraph();
		HashSet<Integer> neiSet = new HashSet<Integer>();
		
		for (int node: nodes) {
			int[] neis = graph.getAdjacentNodeIndicesArray(node, Directedness.ALL);
			
			if (neis.length < candidates.size())
				continue;
			
			neiSet.clear();
			for (int nei: neis)
				neiSet.add(nei);
			if (neiSet.size() < candidates.size())
				continue;
			
			neiSet.retainAll(candidates);
			if (neiSet.size() == candidates.size())
				return true;
		}
		
		return false;
	}
}
