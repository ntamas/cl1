package uk.ac.rhul.cs.graph;

import static org.junit.Assert.*;

import org.junit.Test;

public class TransitivityCalculatorTest {

	@Test
	public void testGetGlobalTransitivity() {
		Graph g;
		TransitivityCalculator calc = new TransitivityCalculator();
		
		calc.setGraph(GraphFactory.createFullGraph(4, false, false));
		assertEquals(1.0, calc.getGlobalTransitivity(), 1e-6);
		
		g = GraphFactory.createEmptyGraph(4, false);
		calc.setGraph(g);
		assertEquals(0.0, calc.getGlobalTransitivity(), 1e-6);
		
		g.createEdge(0, 1); g.createEdge(0, 2); g.createEdge(3, 0);
		g.createEdge(1, 2);
		assertEquals(0.6, calc.getGlobalTransitivity(), 1e-6);
		
		calc.setGraph(GraphFactory.createFamousGraph("zachary"));
		assertEquals(0.2556818181818, calc.getGlobalTransitivity(), 1e-6);
	}
}
