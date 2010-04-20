package uk.ac.rhul.cs.stats.curvefitting;

import uk.ac.rhul.cs.stats.datastructures.PairedData;

/**
 * Calculates and stores the least squares straight line y = A + Bx, fitted
 * to an array of data pairs (x, y).
 * 
 * @author tamas
 */
public class LineFit implements StraightLineFit {
	/** The paired data we are working with */
	protected PairedData data;
	
	/** Whether the calculation was performed already */
	private boolean calculated = false;
	
	/** Whether the post-processign analysis was performed already */
	private boolean analysed = false;
	
	/** The mean of X */
	protected double meanX;
	
	/** The mean of Y */
	protected double meanY;
	
	/** The calculated estimate of A */
	protected double calculatedA;
	
	/** The calculated estimate of B */
	protected double calculatedB;
	
	/** The calculated sum of standard deviations */
	protected double rss;
	
	/**
	 * Creates the least squares estimate from the given paired data.
	 * 
	 * @param data: the data pairs (x, y)
	 */
	public LineFit(PairedData data) {
		this.data = data;
	}
	
	/**
	 * Performs the actual calculation.
	 * 
	 * This method sets up calculatedA, calculatedB, meanX and meanY
	 * accordingly.
	 */
	protected void calculate() {
		if (calculated)
			return;
		
		calculated = true;
		
		double[] xs = data.getX();
		double[] ys = data.getY();
		double sumsq_xs = 0.0;
		double xx_bar = 0.0, yy_bar = 0.0, xy_bar = 0.0;
		int n = xs.length;
		
		if (n == 0) {
			calculatedA = Double.NaN;
			calculatedB = Double.NaN;
			return;
		}
		
		/* Calculate meanX and sumsq_xs */
		for (double x: xs) {
			meanX += x;
			sumsq_xs += x*x;
		}
		meanX /= n;
		
		/* Calculate meanY */
		for (double y: ys) {
			meanY += y;
		}
		meanY /= n;
		
		/* Calculate xx_bar, yy_bar, xy_bar */
		for (int i = 0; i < n; i++) {
			double xdiff = xs[i] - meanX;
			double ydiff = ys[i] - meanY;
			xx_bar += xdiff * xdiff;
			xy_bar += xdiff * ydiff;
			yy_bar += ydiff * ydiff;
		}
		
		calculatedA = xy_bar / xx_bar;
		calculatedB = meanY - calculatedA * meanX;
	}
	
	/**
	 * Analyses the results
	 * 
	 * This method sets up rss accordingly.
	 */
	protected void analyse() {
		if (analysed)
			return;
		
		analysed = true;
		
		calculate();
		
		double[] xs = data.getX();
		double[] ys = data.getY();
		int n = xs.length;
		
		rss = 0.0;
		for (int i = 0; i < n; i++) {
			double fit = calculatedA * xs[i] + calculatedB;
			rss += (ys[i] - fit) * (ys[i] - fit);
		}
	}
	
	/**
	 * Returns the least squares estimate of A.
	 * 
	 * @return: the least squares estimate of A.
	 */
	public double getA() {
		calculate();
		return calculatedA;
	}

	/**
	 * Returns the least squares estimate of B.
	 * 
	 * @return: the least squares estimate of B.
	 */
	public double getB() {
		calculate();
		return calculatedB;
	}
	
	/**
	 * Returns the mean of X.
	 * 
	 * @return: the mean of X
	 */
	public double getMeanX() {
		calculate();
		return meanX;
	}
	
	/**
	 * Returns the mean of Y.
	 * 
	 * @return: the mean of Y
	 */
	public double getMeanY() {
		calculate();
		return meanY;
	}
	
	/**
	 * Returns the sum of squared differences between the fitted line and the data
	 */
	public double getSumOfSquares() {
		analyse();
		return rss;
	}
}
