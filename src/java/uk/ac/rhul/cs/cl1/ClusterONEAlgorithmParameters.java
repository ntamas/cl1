package uk.ac.rhul.cs.cl1;

/**
 * Stores the parameters of a Cluster ONE algorithm instance.
 * 
 * The following parameters are supported:
 * <ul>
 * <li>minSize: minimum size of the clusters that will be returned</li>
 * <li>minDensity: minimum density of the clusters that will be returned</li>
 * <li>overlapThreshold: cluster pairs whose match coefficient is larger than this threshold
 * will be merged to a single cluster in a post-processing step</li>
 * </ul>
 *   
 * @author tamas
 */
public class ClusterONEAlgorithmParameters {
	/** Minimum size of the clusters that will be returned */
	protected int minSize = 2;
	
	/** Minimum density of the clusters that will be returned */
	protected double minDensity = 0.2;
	
	/** Overlap threshold value: no pair of complexes will have an overlap larger than this in the result */
	protected double overlapThreshold = 0.8;
	
	/**
	 * Returns the minimum density of clusters
	 * @return the minimum density of clusters
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Sets the minimum density of clusters that can be considered acceptable.
	 * @param minDensity the minDensity to set
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = Math.max(0, minDensity);
	}

	/**
	 * Returns the minimum size of the clusters that will be returned
	 * @return the minimum size
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * Sets the minimum size of the clusters that will be returned
	 * @param minSize the minimum size
	 */
	public void setMinSize(int minSize) {
		this.minSize = Math.max(1, minSize);
	}

	/**
	 * Returns the overlap threshold of the algorithm.
	 * 
	 * The overlap threshold controls whether two given clusters will be merged in the final
	 * result set. The complexes will be merged if their matching ratio is larger than
	 * this ratio.
	 * 
	 * @return the overlapThreshold
	 */
	public double getOverlapThreshold() {
		return overlapThreshold;
	}

	/**
	 * Sets the overlap threshold of the algorithm.
	 * 
	 * @param overlapThreshold the new overlap threshold
	 * @see getOverlapThreshold()
	 */
	public void setOverlapThreshold(double overlapThreshold) {
		this.overlapThreshold = Math.max(0, overlapThreshold);
	}
}
