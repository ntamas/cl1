package uk.ac.rhul.cs.stats;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.ac.rhul.cs.stats.correlation.KendallCorrelation;
import uk.ac.rhul.cs.stats.datastructures.PairedData;


public class KendallCorrelationTest {
	static double[] dataX = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29 };
	static double[] dataY = { -2, -3, -5, -7, -11, -13, -17, -19, -23, -29 };
	static double[] dataZ = { 10, 1, 9, 2, 8, 3, 7, 4, 6, 5 };
	
	@Test
	public void testCorrelationCoeff() {
		PairedData data = new PairedData(dataX, dataX);
		assertEquals(1.0, KendallCorrelation.correlationCoeff(data), 1e-8);
		data = new PairedData(dataX, dataY);
		assertEquals(-1.0, KendallCorrelation.correlationCoeff(data), 1e-8);
		data = new PairedData(dataX, dataZ);
		assertEquals(-0.1111111, KendallCorrelation.correlationCoeff(data), 1e-3);
	}
}
