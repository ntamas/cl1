package uk.ac.rhul.cs.stats.curvefitting;

/**
 * Defines the interface for a fitted straight line.
 * 
 * There are many methods for fitting a straight line of the form y = A + Bx
 * to n paired observations (x, y). This defines the common interface for classes
 * that fit such lines.
 * 
 * This interface is similar to the eponymous interface in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public interface StraightLineFit {
	/**
	 * Returns the estimate of the intercept term A.
	 * 
	 * @return: the estimate of A.
	 */
	public double getA();
	
	/**
	 * Returns the estimate of the slope term B.
	 * 
	 * @return: the estimate of B.
	 */
	public double getB();
}
