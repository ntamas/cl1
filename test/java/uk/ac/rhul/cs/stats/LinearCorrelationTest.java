package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.stats.correlation.LinearCorrelation;
import uk.ac.rhul.cs.stats.datastructures.PairedData;

public class LinearCorrelationTest {
	static double[] dataX = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29 };
	static double[] dataY = { -2, -3, -5, -7, -11, -13, -17, -19, -23, -29 };
	static double[] dataZ = { 10, 1, 9, 2, 8, 3, 7, 4, 6, 5 };
	
	@Test
	public void testCorrelationCoeff() {
		PairedData data = new PairedData(dataX, dataX);
		assertEquals(1.0, LinearCorrelation.correlationCoeff(data), 1e-8);
		data = new PairedData(dataX, dataY);
		assertEquals(-1.0, LinearCorrelation.correlationCoeff(data), 1e-8);
		data = new PairedData(dataX, dataZ);
		assertEquals(-0.09150255, LinearCorrelation.correlationCoeff(data), 1e-3);
	}
	
	@Test
	public void testGetSP() {
		PairedData data = new PairedData(dataX, dataX);
		LinearCorrelation corr = new LinearCorrelation(data);
		assertEquals(0.0, corr.getSP(), 1e-8);
		
		data = new PairedData(dataX, dataZ);
		corr = new LinearCorrelation(data);
		assertEquals(0.8082, corr.getSP(), 1e-3);
	}
}
