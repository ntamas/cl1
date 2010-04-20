package uk.ac.rhul.cs.stats.correlation;

import uk.ac.rhul.cs.stats.datastructures.PairedData;

/**
 * Calculates the Kendall rank correlation coefficient of a sample.
 * 
 * This class is similar to the eponymous class in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public class KendallCorrelation {
	/**
	 * The associated data
	 */
	protected PairedData data = null;
	
	/**
	 * Cached test statistic
	 */
	protected Double cachedR = null;
	
	/**
	 * Constructor to calculate the sample correlation coefficient of the given {@link PairedData}
	 */
	public KendallCorrelation(PairedData data) {
		this.data = data;
		this.cachedR = null;
	}
	
	/**
	 * Calculates the Kendall rank correlation coefficient on the given data
	 */
	public static double correlationCoeff(PairedData data) {
		return new KendallCorrelation(data).getR();
	}
	
	/**
	 * Returns the number of observations in the sample
	 */
	public int getN() {
		return this.data.getN();
	}
	
	/**
	 * Returns the correlation coefficient
	 */
	public double getR() {
		if (cachedR != null)
			return cachedR;
		
		long num = 0, n = this.getN();
		double[] x = data.getX(), y = data.getY();
		
		// Loop over all pairs
		for (int i = 0; i < n; i++)
			for (int j = i+1; j < n; j++) {
				double signX = Math.signum(x[i] - x[j]);
				double signY = Math.signum(y[i] - y[j]);
				num += signX*signY;
			}
		
		// Return the correlation coefficient
		cachedR = ((double)num) / n / (n-1) * 2.0;
		
		return cachedR;
	}
	
	/**
	 * Returns the test statistic
	 */
	public double getTestStatistic() {
		return this.getR();
	}
}
