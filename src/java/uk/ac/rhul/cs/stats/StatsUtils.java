package uk.ac.rhul.cs.stats;

/**
 * Various statistics-related utility functions
 * 
 * @author tamas
 */
public class StatsUtils {
	/**
	 * Returns the area under the normal curve to the left of the given z value
	 * 
	 * Adapted from Gary Perlman's z.c
	 */
	public static double getZProbability(double z) {
		final double Z_MAX = 6.0;
		double x, y, w;
		
		if (z == 0.0)
			x = 0.0;
		else {
			y = 0.5 * Math.abs(z);
			if (y >= Z_MAX * 0.5)
				x = 1.0;
			else if (y < 1.0) {
				w = y * y;
	            x = ((((((((0.000124818987 * w
                        -0.001075204047) * w +0.005198775019) * w
                      -0.019198292004) * w +0.059054035642) * w
                    -0.151968751364) * w +0.319152932694) * w
                  -0.531923007300) * w +0.797884560593) * y * 2.0;
			} else {
				y = y - 2.0;
				x = (((((((((((((-0.000045255659 * y
                        +0.000152529290) * y -0.000019538132) * y
                      -0.000676904986) * y +0.001390604284) * y
                    -0.000794620820) * y -0.002034254874) * y
                  +0.006549791214) * y -0.010557625006) * y
                +0.011630447319) * y -0.009279453341) * y
              +0.005353579108) * y -0.002141268741) * y
            +0.000535310849) * y +0.999936657524;
			}
		}
		
		if (z > 0.0)
			return ((x + 1.0) * 0.5);
		return (1.0-x) * 0.5;
	}
}
