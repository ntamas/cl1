package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.stats.independentsamples.MannWhitneyTest;
import uk.ac.rhul.cs.stats.tests.H1;

public class MannWhitneyTestTest {
	static double[] xA = { 7, 3, 6, 2, 4, 3, 5, 5 };
	static double[] xB = { 3, 5, 6, 4, 6, 5, 7, 5 };
	static double[] xC = { 19, 22, 16, 29, 24 };
	static double[] xD = { 20, 11, 17, 12 };
	static double[] xE = { 1, 1, 1 };
	static double[] xF = { 0, 0, 0 };
	static double[] xG = { 1, 2, 1 };
	static double[] xH = { 0, 1, 3 };
	
	@Test
	public void testGetCorrectionFactor() {
		MannWhitneyTest test;
		test = new MannWhitneyTest(xA, xB);
		assertEquals(180, test.getCorrectionFactor(), 1e-15);
		test = new MannWhitneyTest(xC, xD);
		assertEquals(0, test.getCorrectionFactor(), 1e-15);
		
	}
	@Test
	public void testGetTestStatistic() {
		MannWhitneyTest test;
		test = new MannWhitneyTest(xA, xB);
		assertEquals(23, test.getTestStatistic(), 0.00000001);
		test = new MannWhitneyTest(xC, xD);
		assertEquals(3, test.getTestStatistic(), 0.00000001);
		test = new MannWhitneyTest(xE, xF);
		assertEquals(0, test.getTestStatistic(), 0.00000001);
	}
	
	@Test
	public void testGetSP() {
		MannWhitneyTest test;
		
		test = new MannWhitneyTest(xC, xD);
		assertEquals(0.1113, test.getSP(), 0.0001);
		test = new MannWhitneyTest(xA, xB);
		assertEquals(0.3612, test.getSP(), 0.01);
		test = new MannWhitneyTest(xA, xB, H1.LESS_THAN);
		assertEquals(0.1806, test.getSP(), 0.01);
		test = new MannWhitneyTest(xA, xB, H1.GREATER_THAN);
		assertEquals(0.8462, test.getSP(), 0.01);
		test = new MannWhitneyTest(xE, xE);
		assertEquals(Double.NaN, test.getSP(), 0.01);
		test = new MannWhitneyTest(xE, xE, H1.GREATER_THAN);
		assertEquals(Double.NaN, test.getSP(), 0.01);
		test = new MannWhitneyTest(xE, xE, H1.LESS_THAN);
		assertEquals(Double.NaN, test.getSP(), 0.01);
		
		test = new MannWhitneyTest(xG, xH);
		assertEquals(1.0, test.getSP(), 0.001);
		test = new MannWhitneyTest(xG, xH, H1.LESS_THAN);
		assertEquals(0.6786, test.getSP(), 0.01);
		test = new MannWhitneyTest(xG, xH, H1.GREATER_THAN);
		assertEquals(0.5, test.getSP(), 0.01);
	}
	
	@Test
	public void testSize() {
		MannWhitneyTest test = new MannWhitneyTest(xC, xD);
		assertEquals(5, test.sizeA());
		assertEquals(4, test.sizeB());
	}
}
