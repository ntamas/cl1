package uk.ac.rhul.cs.stats;

/**
 * Class representing two samples, X and Y, of paired (related) data.
 * 
 * This class is similar to the eponymous class in the Java Statistical Classes
 * library, but it is reimplemented from scratch to avoid license restrictions.
 * (JSC is not licensed under the GNU GPL).
 * 
 * @author tamas
 */
public class PairedData {
	/**
	 * Size of the data
	 */
	protected int n = 0;
	
	/**
	 * Storage area for the first data
	 */
	protected double[] x = null;
	
	/**
	 * Storage area for the second data
	 */
	protected double[] y = null;
	
	/**
	 * Constructs the paired data structure from a matrix with two columns
	 * 
	 * @param   data   the data matrix
	 * @throws  IllegalArgumentException  if the number of pairs is zero
	 * @throws  ArrayIndexOutOfBoundsException   if a row of the data matrix has
	 *          less than two values
	 */
	public PairedData(double[][] data) {
		n = data.length;
		x = new double[n];
		y = new double[n];
		
		for (int i = 0; i < n; i++) {
			x[i] = data[i][0];
			y[i] = data[i][1];
		}
	}
	
	/**
	 * Constructs the paired data structure from two arrays
	 * 
	 * @param  x  the data of sample X
	 * @param  y  the data of sample Y
	 */
	public PairedData(double[] x, double[] y) {
		this.n = x.length;
		if (y.length != this.n)
			throw new IllegalArgumentException("length of arrays must be equal");
		
		this.x = new double[n];
		this.y = new double[n];
		System.arraycopy(x, 0, this.x, 0, n);
		System.arraycopy(y, 0, this.y, 0, n);
	}
	
	/**
	 * Subtracts the elements of Y from corresponding elements of X and returns the differences 
	 */
	double[] differences() {
		double[] result = new double[this.n];
		
		for (int i = 0; i < this.n; i++)
			result[i] = y[i] - x[i];
		
		return result;
	}
	
	/**
	 * Returns the number of paired data values
	 */
	public int getN() {
		return this.n;
	}
	
	/**
	 * Returns the data of sample X
	 */
	public double[] getX() {
		return this.x;
	}
	
	/**
	 * Returns the data of sample Y
	 */
	public double[] getY() {
		return this.y;
	}
	
	/**
	 * Returns a string representation of the paired data
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < this.n; i++) {
			sb.append(x[i]);
			sb.append(' ');
			sb.append(y[i]);
			sb.append('\n');
		}
		
		return sb.toString();
	}
}
