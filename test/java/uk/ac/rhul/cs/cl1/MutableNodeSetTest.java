package uk.ac.rhul.cs.cl1;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test cases for the MutableNodeSet class
 * 
 * @author tamas
 */
public class MutableNodeSetTest extends NodeSetTest {
	@Override
	public NodeSet createNewNodeSet() {
		return new MutableNodeSet(graph);
	}
	
	@Override
	public NodeSet createNewNodeSet(int[] members) {
		return new MutableNodeSet(graph, members);
	}
	
	@Test
	public void testAdd() {
		int[] members = { 0, 1, 4, 3, 3, 4 };
		boolean[] success = { true, true, true, true, false, false };
		double[] boundaryEdgeWeights = { 18.0, 19.0, 26.0, 25.0, 25.0, 25.0 };
		double[] internalEdgeWeights = {  0.0,  1.0,  1.0, 15.0, 15.0, 15.0 };
		int[][] externalBoundaryNodes = {
				{1, 2, 3},
				{2, 3},
				{2, 3, 6},
				{2, 5, 6},
				{2, 5, 6},
				{2, 5, 6}
		};

		MutableNodeSet nodeSet = (MutableNodeSet)createNewNodeSet();

		assertEquals(0.0, nodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
		assertEquals(0.0, nodeSet.getTotalInternalEdgeWeight(), 1e-6);
		assertArrayEquals(new int[] {}, nodeSet.getExternalBoundaryNodes());

		for (int i = 0; i < members.length; i++) {
			assertEquals(success[i], nodeSet.add(members[i]));
			assertEquals(boundaryEdgeWeights[i], nodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
			assertEquals(internalEdgeWeights[i], nodeSet.getTotalInternalEdgeWeight(), 1e-6);
			assertArrayEquals(externalBoundaryNodes[i], nodeSet.getExternalBoundaryNodes());
		}
	}

	@Test
	public void testClear() {
		int[] members = { 1, 2, 3, 4 };
		MutableNodeSet nodeSet = (MutableNodeSet)createNewNodeSet(members);
		assertEquals(4, nodeSet.size());
		nodeSet.clear();
		assertEquals(0, nodeSet.size());
		assertEquals(0.0, nodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
		assertEquals(0.0, nodeSet.getTotalInternalEdgeWeight(), 1e-6);
	}
	
	@Test
	public void testFreeze() {
		int[] members = { 1, 2, 3, 4 };
		MutableNodeSet nodeSet = (MutableNodeSet)createNewNodeSet(members);
		NodeSet frozenNodeSet = nodeSet.freeze();
		assertEquals(nodeSet.members, frozenNodeSet.members);
		assertEquals(nodeSet.getTotalBoundaryEdgeWeight(), frozenNodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
		assertEquals(nodeSet.getTotalInternalEdgeWeight(), frozenNodeSet.getTotalInternalEdgeWeight(), 1e-6);
		assertEquals(nodeSet.size(), frozenNodeSet.size());
		assertNotSame(nodeSet, frozenNodeSet);
		assertTrue(nodeSet.equals(frozenNodeSet));
	}

	@Test
	public void testRemove() {
		int[] startMembers = { 0, 1, 3, 5, 6 };
		int[] members = { 0, 4, 6, 1, 5, 5, 3 };
		boolean[] success = { true, false, true, true, true, false, true };
		double[] boundaryEdgeWeights = { 24.0, 24.0, 25.0, 26.0, 27.0, 27.0, 0.0 };
		double[] internalEdgeWeights = { 13.0, 13.0,  8.0,  6.0,  0.0,  0.0, 0.0 };
		int[][] externalBoundaryNodes = {
				{0, 2, 4},
				{0, 2, 4},
				{0, 2, 4, 6},
				{0, 1, 2, 4, 6},
				{0, 1, 2, 4, 5},
				{0, 1, 2, 4, 5},
				{}
		};

		MutableNodeSet nodeSet = (MutableNodeSet)createNewNodeSet(startMembers);

		assertEquals(22.0, nodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
		assertEquals(23.0, nodeSet.getTotalInternalEdgeWeight(), 1e-6);
		assertArrayEquals(new int[] { 2, 4 }, nodeSet.getExternalBoundaryNodes());

		for (int i = 0; i < members.length; i++) {
			assertEquals(success[i], nodeSet.remove(members[i]));
			assertEquals(boundaryEdgeWeights[i], nodeSet.getTotalBoundaryEdgeWeight(), 1e-6);
			assertEquals(internalEdgeWeights[i], nodeSet.getTotalInternalEdgeWeight(), 1e-6);
			assertArrayEquals(externalBoundaryNodes[i], nodeSet.getExternalBoundaryNodes());
		}
	}

}
