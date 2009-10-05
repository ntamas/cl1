package uk.ac.rhul.cs.cl1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class BreadthFirstSearchTest {
	static Graph graph = null;
	
	@BeforeClass
	public static void setUpBefore() {
		int[] edges = { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 };
		double[] weights = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		
		graph = new Graph();
		graph.createNodes(7);
		
		for (int i = 0; i < weights.length; i++) {
			graph.createEdge(edges[2*i], edges[2*i+1], weights[i]);
		}
	}
	
	@Test
	public void testBreadthFirstSearchConstructor() {
		BreadthFirstSearch bfs = new BreadthFirstSearch(graph, 2);
		assertEquals(graph, bfs.getGraph());
		assertEquals(2, bfs.getSeedNode());
	}

	@Test
	public void testIterator() {
		BreadthFirstSearch bfs = new BreadthFirstSearch(graph, 0);
		List<Integer> list = new ArrayList<Integer>();
		
		for (int node: bfs)
			list.add(node);
		
		assertEquals(7, list.size());
		assertEquals(new Integer(0), list.get(0));
		assertEquals(new Integer(6), list.get(6));
	}

}
