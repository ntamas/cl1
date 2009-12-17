package uk.ac.rhul.cs.stats;

import java.util.Arrays;

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
	private double tieCorrection;
	
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
		uB = uB - uA;
		U = Math.min(uA, uB);
		
		/* Calculate tie correction value */
		Arrays.sort(ranks);
		tieCorrection = 0.0;
		for (i = 0; i < n-1; i++) {
			if (ranks[i] == ranks[i+1]) {
				int nties = 1;
				while (i < n-1 && ranks[i] == ranks[i+1]) {
					nties++;
					i++;
				}
				tieCorrection += Math.pow(nties, 3) - nties;
			}
		}
		tieCorrection = Math.sqrt(1 - tieCorrection / (Math.pow(n, 3) - n));
		
		this.alternative = alternative;
	}
	
	public double getSP() {
		double sd = Math.sqrt(tieCorrection * nA * nB * (nA + nB + 1) / 12.0);
		double z = (U - nA*nB/2.0) / sd;
		
		if (alternative == H1.NOT_EQUAL) {
			z = Math.abs(z);
			return 2 * StatsUtils.getZProbability(-z);
		} else if (alternative == H1.LESS_THAN) {
			return StatsUtils.getZProbability(z);
		} else {
			return 1.0 - StatsUtils.getZProbability(z);
		}
	}

	public double getTestStatistic() {
		return U;
	}
	
	/**
	 * Returns the tie correction that was applied to the p-value
	 */
	public double getTieCorrection() {
		return tieCorrection;
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
