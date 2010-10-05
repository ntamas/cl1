package uk.ac.rhul.cs.cl1.graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import uk.ac.rhul.cs.graph.BronKerboschMaximalCliqueFinder;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.StringUtils;

public class BronKerboschMaximalCliqueFinderTest {
	@Test
	public void testGetMaximalCliques() {
		int edges[] = { 0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3, 3, 5, 3, 4, 4, 5,
				5, 6, 5, 7, 4, 8, 4, 9 };
		int expectedCliques[] = {
				0, 1, 2, 3, -1,
				3, 4, 5, -1,
				4, 8, -1,
				4, 9, -1,
				5, 6, -1,
				5, 7, -1
		};
		Graph graph = new Graph();
		
		// Create the graph
		graph.createNodes(10);
		for (int i = 0; i < edges.length; i += 2)
			graph.createEdge(edges[i], edges[i+1]);
		
		// Find the maximal cliques
		BronKerboschMaximalCliqueFinder cfinder = new BronKerboschMaximalCliqueFinder();
		cfinder.setGraph(graph);
		
		List<List<Integer>> cliques = cfinder.getMaximalCliques();
		List<Integer> currentClique = new ArrayList<Integer>();
		
		for (List<Integer> clique: cliques) {
			Collections.sort(clique);
		}
		
		for (int item: expectedCliques) {
			if (item == -1) {
				assertTrue("Clique not detected: " +
						StringUtils.join(currentClique.iterator(), ", "),
						cliques.contains(currentClique)
				);
				currentClique.clear();
			} else {
				currentClique.add(item);
			}
		}
		assertTrue(currentClique.isEmpty());
		assertEquals(6, cliques.size());
	}
}
