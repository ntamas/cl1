package uk.ac.rhul.cs.cl1.filters;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.MutableNodeSet;

public class SizeFilterTest {
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
	
	@Test
	public void testFilter() {
		SizeFilter filter = new SizeFilter();
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		
		// Size is now 0, filter accepts everything
		assertTrue(filter.filter(nodeSet));
		
		// Size is now 3, filter accepts everything
		nodeSet.add(0); nodeSet.add(1); nodeSet.add(3);
		assertTrue(filter.filter(nodeSet));
		
		// Filter accepts >= 2
		filter.setMinSize(2);
		assertTrue(filter.filter(nodeSet));
		
		// Filter accepts >= 4
		filter.setMinSize(4);
		assertFalse(filter.filter(nodeSet));
		
		// Filter accepts <= 4
		filter.setMinSize(0);
		filter.setMaxSize(4);
		assertTrue(filter.filter(nodeSet));
		
		// Filter accepts <= 2
		filter.setMaxSize(2);
		assertFalse(filter.filter(nodeSet));
	}
}
