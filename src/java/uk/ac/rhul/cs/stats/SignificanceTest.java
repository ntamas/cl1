package uk.ac.rhul.cs.stats;

/**
 * Common interface for significance tests
 * 
 * This interface is intentionally made compatible with the eponymous
 * interface from the Java Statistical Classes library.
 * 
 * @author tamas
 */
public interface SignificanceTest {
	/**
	 * Returns the value of the significance probability
	 * 
	 * @return   the significance probability
	 */
	public double getSP();
	
	/**
	 * Returns the value of the test statistic
	 * 
	 * @return   the test statistic
	 */
	public double getTestStatistic();
}
