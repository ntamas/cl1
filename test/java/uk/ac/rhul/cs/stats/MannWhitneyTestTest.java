package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

public class MannWhitneyTestTest {
	static double[] xA = { 7, 3, 6, 2, 4, 3, 5, 5 };
	static double[] xB = { 3, 5, 6, 4, 6, 5, 7, 5 };
	static double[] xC = { 19, 22, 16, 29, 24 };
	static double[] xD = { 20, 11, 17, 12 };
	
	@Test
	public void testGetTestStatistic() {
		MannWhitneyTest test;
		test = new MannWhitneyTest(xA, xB);
		assertEquals(23, test.getTestStatistic(), 0.00000001);
		test = new MannWhitneyTest(xC, xD);
		assertEquals(3, test.getTestStatistic(), 0.00000001);
	}
	
	@Test
	public void testGetSP() {
		MannWhitneyTest test;
		test = new MannWhitneyTest(xC, xD);
		assertEquals(0.08641, test.getSP(), 0.0001);
		test = new MannWhitneyTest(xA, xB);
		assertEquals(0.333, test.getSP(), 0.01);
		test = new MannWhitneyTest(xA, xB, H1.LESS_THAN);
		assertEquals(0.166, test.getSP(), 0.01);
		test = new MannWhitneyTest(xA, xB, H1.GREATER_THAN);
		assertEquals(0.833, test.getSP(), 0.01);
	}
	
	@Test
	public void testSize() {
		MannWhitneyTest test = new MannWhitneyTest(xC, xD);
		assertEquals(5, test.sizeA());
		assertEquals(4, test.sizeB());
	}
}
