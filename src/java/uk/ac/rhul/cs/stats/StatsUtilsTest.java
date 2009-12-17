package uk.ac.rhul.cs.stats;

import static org.junit.Assert.*;

import org.junit.Test;

public class StatsUtilsTest {
	@Test
	public void testGetZProbability() {
		final double eps = 1e-3;
		assertEquals(0.5, StatsUtils.getZProbability(0), eps);
		assertEquals(0.682689, StatsUtils.getZProbability(1) - StatsUtils.getZProbability(-1), eps);
		assertEquals(0.954499, StatsUtils.getZProbability(2) - StatsUtils.getZProbability(-2), eps);
		assertEquals(0.997300, StatsUtils.getZProbability(3) - StatsUtils.getZProbability(-3), eps);
	}
}
