package uk.ac.rhul.cs.cl1.filters;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.MutableNodeSet;

public class FilterChainTest {

	@Test
	public void testAdd() {
		FilterChain chain = new FilterChain();
		SizeFilter filter = new SizeFilter(2);
		
		assertTrue(chain.size() == 0);
		assertTrue(chain.isEmpty());
		
		chain.add(filter);
		assertTrue(chain.contains(filter));
		assertTrue(chain.size() == 1);
	}

	@Test
	public void testClearIsEmptyAndSize() {
		SizeFilter filter = new SizeFilter(2);
		DensityFilter anotherFilter = new DensityFilter(0.5);
		
		FilterChain chain = new FilterChain();
		
		chain.add(filter);
		assertTrue(chain.size() == 1);
		assertFalse(chain.isEmpty());
		
		chain.add(anotherFilter);
		assertTrue(chain.size() == 2);
		assertFalse(chain.isEmpty());
		
		chain.clear();
		assertTrue(chain.size() == 0);
		assertTrue(chain.isEmpty());
	}

	@Test
	public void testFilter() {
		SizeFilter filter = new SizeFilter(2);
		DensityFilter anotherFilter = new DensityFilter(0.5);
		FilterChain chain = new FilterChain();
		
		int[] edges = { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 };
		Graph graph = new Graph();
		graph.createNodes(7);
		for (int i = 0; i < edges.length; i+=2) {
			graph.createEdge(edges[i], edges[i+1], 1);
		}
		
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		nodeSet.add(0); nodeSet.add(1); nodeSet.add(2);
		
		chain.add(filter);
		chain.add(anotherFilter);
		assertTrue(chain.filter(nodeSet));
		
		anotherFilter.setMinDensity(0.8);
		assertFalse(chain.filter(nodeSet));
		
		filter.setMinSize(4);
		anotherFilter.setMinDensity(0.5);
		assertFalse(chain.filter(nodeSet));
		
		chain.clear();
		assertTrue(chain.filter(nodeSet));
	}
}
