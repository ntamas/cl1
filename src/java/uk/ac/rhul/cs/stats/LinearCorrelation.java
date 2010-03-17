package uk.ac.rhul.cs.stats;

/**
 * Calculates the sample linear (a.k.a. Pearson product-moment) correlation coefficient.
 * 
 * This class is similar to the eponymous class in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public class LinearCorrelation implements SignificanceTest {
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
	public LinearCorrelation(PairedData data) {
		this.data = data;
		this.cachedR = null;
	}
	
	/**
	 * Calculates the linear (Pearson product-moment) correlation coefficient on the given data
	 */
	public static double correlationCoeff(PairedData data) {
		return new LinearCorrelation(data).getR();
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
		if (this.cachedR != null)
			return this.cachedR;
		
		// First get the means and variances
		double[] x = data.getX(), y = data.getY();
		MeanVar mvX = new MeanVar(x);
		MeanVar mvY = new MeanVar(y);
		double meanX = mvX.getMean(), meanY = mvY.getMean();
		
		// Calculate the covariance
		double cov = 0.0;
		int i, n = data.getN();
		for (i = 0; i < n; i++) {
			cov += (x[i] - meanX) * (y[i] - meanY);
		}
		cov /= (n - 1);
		
		// Return the correlation coefficient
		cachedR = cov / (mvX.getSd() * mvY.getSd());
		return cachedR;
	}
	
	/**
	 * Returns the significance level of the test under the assumption of normality
	 * 
	 * This is done by using the standard Fisher transformation on the correlation
	 * coefficient.
	 */
	public double getSP() {
		double r = this.getR();
		int n = this.getN();
		
		if (n <= 1)
			return Double.NaN;
		
		if (r >= 1 || r <= -1)
			return 2.2e-16;
		
		if (n <= 3) {
			// TODO
			return 1;
		}
		
		double f = 0.5 * Math.log1p(2 * r / (1 - r));
		double z = Math.sqrt(this.getN() - 3) * f;
		return 2 * StatsUtils.getZProbability(z);
	}
	
	/**
	 * Returns the test statistic
	 */
	public double getTestStatistic() {
		return this.getR();
	}
}
