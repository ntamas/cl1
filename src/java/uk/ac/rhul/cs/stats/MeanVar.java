package uk.ac.rhul.cs.stats;

/**
 * This class calculates and stores the sample mean and variance of an array of
 * n data values. The variance is calculated using n-1 in the denominator.
 * 
 * This class is similar to the eponymous class in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public class MeanVar implements Cloneable {
	/**
	 * The mean
	 */
	protected double mean = 0;
	
	/**
	 * The sum of squared differences
	 */
	protected double s = 0;
	
	/**
	 * The number of elements
	 */
	protected int n = 0;
	
	/**
	 * Creates the mean and variance object using a single value
	 */
	public MeanVar(double value) {
		addValue(value);
	}
	
	/**
	 * Creates the mean and variance from an array of data
	 */
	public MeanVar(double[] data) {
		for (int i = 0; i < data.length; i++)
			addValue(data[i]);
	}
	
	/**
	 * Protected constructor for cloning
	 */
	protected MeanVar(int n, double mean, double s) {
		this.n = n;
		this.mean = mean;
		this.s = s;
	}
	
	/**
	 * Adds a new value to the sample and updates the mean and variance
	 */
	public void addValue(double newValue) {
		double delta = newValue - mean;
		n += 1;
		mean += delta / n;
		s += delta * (newValue - mean);
	}
	
	/**
	 * Clones the mean-variance calculator
	 */
	@Override
	public Object clone() {
		return new MeanVar(n, mean, s);
	}
	
	/**
	 * Returns the mean
	 */
	public double getMean() {
		return mean;
	}
	
	/**
	 * Returns the number of elements
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * Returns the standard deviation
	 */
	public double getSd() {
		if (n <= 1)
			return Double.NaN;
		return Math.sqrt(s / (n - 1));
	}
	
	/**
	 * Returns the variance
	 */
	public double getVariance() {
		if (n <= 1)
			return Double.NaN;
		return s / (n-1);
	}
}
