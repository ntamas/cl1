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
public class LinearCorrelation {
	/**
	 * The associated data
	 */
	protected PairedData data = null;
	
	/**
	 * Constructor to calculate the sample correlation coefficient of the given {@link PairedData}
	 */
	public LinearCorrelation(PairedData data) {
		this.data = data;
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
		return cov / (mvX.getSd() * mvY.getSd());
	}
	
	/**
	 * Returns the test statistic
	 */
	public double getTestStatistic() {
		return this.getR();
	}
}
