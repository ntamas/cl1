package uk.ac.rhul.cs.cl1.filters;

import static org.junit.Assert.*;
import org.junit.BeforeClass;

import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.MutableNodeSet;

public class DensityFilterTest {
	static Graph graph = null;
	
	@BeforeClass
	public static void setUpBefore() {
		int[] edges = { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 };
		graph = new Graph();
		graph.createNodes(7);
		for (int i = 0; i < edges.length; i+=2) {
			graph.createEdge(edges[i], edges[i+1], 1);
		}
	}
	
	public void testFilter() {
		DensityFilter filter = new DensityFilter();
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		
		// Density is now 0.0, filter accepts everything
		assertTrue(filter.filter(nodeSet));
		
		// Density is now 1.0, filter accepts everything
		nodeSet.add(0); nodeSet.add(1); nodeSet.add(3);
		assertTrue(filter.filter(nodeSet));
		
		// Density is now 0.666, filter accepts >= 0.5
		nodeSet.remove(3); nodeSet.add(2);
		filter.setMinDensity(0.5);
		assertTrue(filter.filter(nodeSet));
		
		// Density is now 0.666, filter accepts >= 0.7
		filter.setMinDensity(0.7);
		assertFalse(filter.filter(nodeSet));
		
		// Density is now 0.666, filter accepts <= 0.7
		filter.setMinDensity(0.0);
		filter.setMaxDensity(0.7);
		assertTrue(filter.filter(nodeSet));
		
		// Density is now 0.666, filter accepts <= 0.5
		filter.setMaxDensity(0.5);
		assertFalse(filter.filter(nodeSet));
	}
}
