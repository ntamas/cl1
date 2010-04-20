package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.stats.descriptive.MeanVar;

public class MeanVarTest {
	static double[] data = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 27 };
	
	@Test
	public void testAddValue() {
		MeanVar mv = new MeanVar(29);
		for (int i = 0; i < data.length; i++)
			mv.addValue(data[i]);
		assertEquals(14.18182, mv.getMean(), 1e-3);
		assertEquals(11, mv.getN());
		assertEquals(9.558433, mv.getSd(), 1e-3);
		assertEquals(91.36364, mv.getVariance(), 1e-3);
	}
	
	@Test
	public void testClone() {
		MeanVar mv = new MeanVar(data), mv2 = (MeanVar)mv.clone();
		assertEquals(mv.getMean(), mv2.getMean(), 1e-3);
		assertEquals(mv.getN(), mv2.getN());
		assertEquals(mv.getSd(), mv2.getSd(), 1e-3);
		assertEquals(mv.getVariance(), mv2.getVariance(), 1e-3);
	}
	
	@Test
	public void testGetMean() {
		MeanVar mv = new MeanVar(data);
		assertEquals(12.7, mv.getMean(), 1e-7);
	}
	
	@Test
	public void testGetN() {
		MeanVar mv = new MeanVar(data);
		assertEquals(10, mv.getN());
	}
	
	@Test
	public void testGetSd() {
		MeanVar mv = new MeanVar(data);
		assertEquals(8.64163, mv.getSd(), 1e-3);
	}
	
	@Test
	public void testGetVariance() {
		MeanVar mv = new MeanVar(data);
		assertEquals(74.677777777777777, mv.getVariance(), 1e-3);
	}
}
