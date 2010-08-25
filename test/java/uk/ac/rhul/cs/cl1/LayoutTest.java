package uk.ac.rhul.cs.cl1;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

import uk.ac.rhul.cs.graph.Layout;

public class LayoutTest {
	Layout layout = null;
	
	@Before
	public void setUp() {
		layout = new Layout(5);
		
		layout.setCoordinates(0, -1, -1);
		layout.setCoordinates(1, -1,  1);
		layout.setCoordinates(2,  1,  1);
		layout.setCoordinates(3,  1, -1);
		layout.setCoordinates(4,  0,  0);
	}
	
	@Test
	public void testGetBoundingRectangle() {
		Rectangle2D expectedRect = new Rectangle2D.Double(-1, -1, 2, 2);
		assertEquals(expectedRect, layout.getBoundingRectangle());
	}
	
	@Test
	public void testFitToRectangle() {
		Rectangle2D rectToFitTo = new Rectangle2D.Double(5, 5, 8, 8);
		layout.fitToRectangle(rectToFitTo);
		assertEquals(rectToFitTo, layout.getBoundingRectangle());
	}
}
