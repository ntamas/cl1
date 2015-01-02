package uk.ac.rhul.cs.cl1.quality;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.quality.LogLikelihoodFunction;
import uk.ac.rhul.cs.graph.Graph;

public class LogLikelihoodFunctionTest {
	Graph graph = null;
	LogLikelihoodFunction func = null;
	
	@Before
	public void setUp() {
		graph = new Graph();
		graph.createNodes(6);
		graph.createEdge(0, 1);
		graph.createEdge(1, 2);
		graph.createEdge(2, 0);
		graph.createEdge(0, 3);
		graph.createEdge(1, 4);
		graph.createEdge(2, 5);
		
		func = new LogLikelihoodFunction();
	}
	
	@Test
	public void testCalculate() {
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		double expected;
		
		/* p = 0 */
		assertEquals(Double.NEGATIVE_INFINITY, func.calculate(nodeSet), 0);
		
		/* p = 1/6, p1 = 0, p2 = 3/5, n = 1, N = 6 */
		/* maxInternal = 0, maxBoundary = 5 */
		nodeSet.add(0);
		expected = 6 * ((1.0 / 6) * Math.log(1.0/6) + (5.0 / 6) * Math.log(5.0/6)) +
		           5 * ((3.0 / 5) * Math.log(3.0/5) + (2.0 / 5) * Math.log(2.0/5));
		assertEquals(expected, func.calculate(nodeSet), 1e-4);
		
		/* p = 1/2, p1 = 1, p2 = 1/3, n = 3, N = 6 */
		/* maxInternal = 3, maxBoundary = 9 */
		nodeSet.add(1); nodeSet.add(2);
		expected = 6 * Math.log(0.5) + 3 * Math.log(1) +
		           9 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0));
		assertEquals(expected, func.calculate(nodeSet), 1e-4);
		
		/* p = 1/2, p1 = 0, p2 = 1/3, n = 3, N = 6 */
		/* maxInternal = 3, maxBoundary = 9 */
		nodeSet.clear(); nodeSet.add(3); nodeSet.add(4); nodeSet.add(5);
		expected = 6 * Math.log(0.5) + 3 * Math.log(1) +
		           9 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0));
		assertEquals(expected, func.calculate(nodeSet), 1e-4);
	}

	@Test
	public void testGetAdditionAffinity() {
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		double expected;
		
		nodeSet.add(0); nodeSet.add(1);
		
		/* p = 1/2, p1 = 1, p2 = 1/3, n = 3, N = 6 */
		/* maxInternal = 3, maxBoundary = 9 */
		expected = 6 * Math.log(0.5) + 3 * Math.log(1) +
                   9 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0));
		assertEquals(expected, func.getAdditionAffinity(nodeSet, 2), 1e-4);
		
		/* p = 1/3, p1 = 1, p2 = 1/2, n = 2, N = 6 */
		/* maxInternal = 1, maxBoundary = 8 */
		expected = 6 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0)) +
		           1 * Math.log(1) +
                   8 * ((1/2.0) * Math.log(1/2.0) + (1/2.0) * Math.log(1/2.0));
		assertEquals(expected, func.getAdditionAffinity(nodeSet, 1), 1e-4);
	}

	@Test
	public void testGetRemovalAffinity() {
		MutableNodeSet nodeSet = new MutableNodeSet(graph);
		double expected;
		
		nodeSet.add(0); nodeSet.add(1); nodeSet.add(2);
		
		/* p = 1/2, p1 = 1, p2 = 1/3, n = 3, N = 6 */
		/* maxInternal = 3, maxBoundary = 9 */
		expected = 6 * Math.log(0.5) + 3 * Math.log(1) +
                   9 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0));
		assertEquals(expected, func.getRemovalAffinity(nodeSet, 4), 1e-4);
		
		/* p = 1/3, p1 = 1, p2 = 1/2, n = 2, N = 6 */
		/* maxInternal = 1, maxBoundary = 8 */
		expected = 6 * ((1/3.0) * Math.log(1/3.0) + (2/3.0) * Math.log(2/3.0)) +
		           1 * Math.log(1) +
                   8 * ((1/2.0) * Math.log(1/2.0) + (1/2.0) * Math.log(1/2.0));
		assertEquals(expected, func.getRemovalAffinity(nodeSet, 0), 1e-4);
	}
}
