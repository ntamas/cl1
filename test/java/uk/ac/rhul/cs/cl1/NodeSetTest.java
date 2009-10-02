package uk.ac.rhul.cs.cl1;

import static junit.framework.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for the NodeSet class
 * 
 * @author tamas
 */
public class NodeSetTest {
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
	
	public NodeSet createNewNodeSet() {
		return new NodeSet(graph);
	}
	
	public NodeSet createNewNodeSet(int[] members) {
		return new NodeSet(graph, members);
	}
	
	@Test
	public void testCreation() {
		NodeSet nodeSet = createNewNodeSet();
		assertNotNull(nodeSet);
	}
	
	@Test
	public void testSetMembers() {
		int[] members = { 0, 1, 2, 6, 6 };
		NodeSet nodeSet = createNewNodeSet(members);
		assertNotNull(nodeSet);
		assertEquals(nodeSet.size(), 4);
	}
	
	@Test
	public void testGetTotalBoundaryWeight() {
		int[] members = { 0, 1, 2, 6, 6 };
		NodeSet nodeSet = createNewNodeSet();
		assertNotNull(nodeSet);
		assertEquals(0.0, nodeSet.getTotalBoundaryEdgeWeight());
		nodeSet = createNewNodeSet(members);
		assertEquals(27.0, nodeSet.getTotalBoundaryEdgeWeight());
	}
	
	@Test
	public void testGetTotalInternalWeight() {
		int[] members = { 0, 1, 2, 6, 6 };
		NodeSet nodeSet = createNewNodeSet();
		assertNotNull(nodeSet);
		assertEquals(0.0, nodeSet.getTotalBoundaryEdgeWeight());
		nodeSet = createNewNodeSet(members);
		assertEquals(9.0, nodeSet.getTotalInternalEdgeWeight());
	}
}
