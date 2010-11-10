package uk.ac.rhul.cs.stats.independentsamples;

import java.util.Arrays;

import uk.ac.rhul.cs.stats.StatsUtils;
import uk.ac.rhul.cs.stats.tests.H1;
import uk.ac.rhul.cs.stats.tests.SignificanceTest;
import uk.ac.rhul.cs.utils.ArrayUtils;

/**
 * Implementation of the Mann-Whitney U test.
 * 
 * Given two samples A and B, the test tests the null hypothesis that A and B
 * have the same distribution against one of the following alternative hypotheses:
 * 
 * <ul>
 * <li>A and B are different ({@link H1}.NOT_EQUAL)</li>
 * <li>A is stochastically less than B ({@link H1}.LESS_THAN)</li>
 * <li>A is stochastically greater than B {@link H1}.GREATER_THAN)</li>
 * </ul>
 * 
 * This class is similar to the eponymous class in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public class MannWhitneyTest implements SignificanceTest {
	/**
	 * Size of sample A
	 */
	private int nA;
	
	/**
	 * Size of sample B
	 */
	private int nB;
	
	/**
	 * The value of the test statistic
	 */
	private double U;
	
	/**
	 * The tie correction that was applied
	 */
	private int tieCorrection;
	
	/**
	 * The alternative hypothesis
	 */
	private H1 alternative;
	
	/**
	 * Constructs a two-sample Mann-Whitney test with the given samples.
	 * 
	 * The tolerance level is 1e-7.
	 * 
	 * @param xA   the first sample
	 * @param xB   the second sample
	 */
	public MannWhitneyTest(double[] xA, double[] xB) {
		this(xA, xB, H1.NOT_EQUAL, 1e-7);
	}
	
	/**
	 * Constructs a two-sample Mann-Whitney test with the given samples and
	 * the given alternative hypothesis.
	 * 
	 * The tolerance level is 1e-7.
	 * 
	 * @param xA           the first sample
	 * @param xB           the second sample
	 * @param alternative  the alternative hypothesis
	 */
	public MannWhitneyTest(double[] xA, double[] xB, H1 alternative) {
		this(xA, xB, alternative, 1e-7);
	}
	
	/**
	 * Constructs a two-sample Mann-Whitney test with the given samples,
	 * the given alternative hypothesis and the given tolerance.
	 * 
	 * @param xA            the first sample
	 * @param xB            the second sample
	 * @param alternative   the alternative hypothesis
	 * @param tolerance     tolerance value within which two values are considered equal
	 */
	public MannWhitneyTest(double[] xA, double[] xB, H1 alternative, double tolerance) {
		nA = xA.length; nB = xB.length;
		
		int i, n = nA+nB;
		double[] joined = new double[n];
		double uA, uB;
		
		/* Join the two arrays */
		System.arraycopy(xA, 0, joined, 0, nA);
		System.arraycopy(xB, 0, joined, nA, nB);
		
		/* Get the rank vector of the array */
		double[] ranks = ArrayUtils.getRanks(joined, tolerance);
		
		/* Calculate uA and uB */
		uA = uB = nA * nB;
		for (i = 0; i < nA; i++) {
			uA -= ranks[i];
		}
		uA += (nA * (nA+1)) / 2;
		
		if (alternative == H1.NOT_EQUAL) {
			uB = uB - uA;
			U = Math.min(uA, uB);
		} else
			U = uA;
		
		/* Calculate tie correction value */
		Arrays.sort(ranks);
		tieCorrection = 0;
		for (i = 0; i < n-1; i++) {
			if (ranks[i] == ranks[i+1]) {
				int nties = 1;
				while (i < n-1 && ranks[i] == ranks[i+1]) {
					nties++;
					i++;
				}
				tieCorrection += nties * (nties * nties - 1);
			}
		}
		
		this.alternative = alternative;
	}
	
	/**
	 * Returns the tie correction that was applied to the p-value
	 */
	public int getCorrectionFactor() {
		return tieCorrection;
	}
	
	public double getSP() {
		int n = nA + nB;
		double z = U - nA*nB/2.0;
		double sd = Math.sqrt((nA * nB / 12.0) * (n + 1) - tieCorrection / (n * (n-1.0)));
		double continuityCorrection = 0.0;
		
		switch (alternative) {
		case NOT_EQUAL:
			continuityCorrection = Math.signum(z) * 0.5;
			break;
		case LESS_THAN:
			continuityCorrection = 0.5;
			break;
		case GREATER_THAN:
			continuityCorrection = -0.5;
			break;
		}
		
		z = (z - continuityCorrection) / sd;
		
		switch (alternative) {
		case NOT_EQUAL:
			z = Math.abs(z);
			return 2 * StatsUtils.getZProbability(-z);
			
		case LESS_THAN:
			return 1.0 - StatsUtils.getZProbability(z);
			
		case GREATER_THAN:
			return StatsUtils.getZProbability(z);
		}
		
		return Double.NaN;
	}

	public double getTestStatistic() {
		return U;
	}
	
	/**
	 * Returns the size of the first sample
	 */
	public int sizeA() {
		return nA;
	}
	
	/**
	 * Returns the size of the second sample
	 */
	public int sizeB() {
		return nB;
	}
}
