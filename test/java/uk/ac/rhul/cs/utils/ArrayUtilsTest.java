package uk.ac.rhul.cs.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArrayUtilsTest {
	@Test
	public void testGetRanks() {
		final double eps = 1e-7;
		double[][] arrays = {
				{2, 4, 10, 6, 1},
				{2, 2, 2, 1, 1}
		};
		double[][] expectedResults = {
				{2, 3, 5, 4, 1},
				{4, 4, 4, 1.5, 1.5}
		};
		
		for (int i = 0; i < arrays.length; i++) {
			assertArrayEquals(expectedResults[i], ArrayUtils.getRanks(arrays[i], eps), eps);
		}
	}
}
