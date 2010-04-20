package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.stats.curvefitting.LineFit;
import uk.ac.rhul.cs.stats.datastructures.PairedData;

public class LineFitTest {
	static double[] xA = { 1, 2, 3, 4, 5, 6 };
	static double[] xB = { 3, 5, 7, 9, 11, 13 };
	
	static double[] xC = { 7, 3, 6, 2, 4, 3, 5, 5 };
	static double[] xD = { 3, 5, 6, 4, 6, 5, 7, 5 };

	@Test
	public void testCalculate() {
		LineFit fit;
		
		fit = new LineFit(new PairedData(xA, xB));
		assertEquals(3.5, fit.getMeanX(), 1e-5);
		assertEquals(8, fit.getMeanY(), 1e-5);
		assertEquals(2, fit.getA(), 1e-5);
		assertEquals(1, fit.getB(), 1e-5);
		assertEquals(0, fit.getSumOfSquares(), 1e-5);
		
		fit = new LineFit(new PairedData(xC, xD));
		assertEquals(-0.01887, fit.getA(), 1e-5);
		assertEquals(5.20755, fit.getB(), 1e-5);
		assertEquals(10.86792, fit.getSumOfSquares(), 1e-4);
	}
}
