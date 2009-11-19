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
	
	/**
	 * Overlap threshold value.
	 * No pair of complexes will have an overlap larger than this
	 * in the result */
	protected double overlapThreshold = 0.8;
	
	/**
	 * Complex merging method.
	 * 
	 * Possible values:
	 * <ul>
	 * <li>match: match coefficient</li>
	 * <li>meet/min: meet/min coefficient</li>
	 * </ul>
	 */
	protected String mergingMethod = "meet/min";
	
	/**
	 * The seed generation method.
	 */
	protected SeedGenerator seedGenerator = new EveryNodeSeedGenerator();
	
	/**
	 * @return the mergingMethod
	 */
	public String getMergingMethod() {
		return mergingMethod;
	}

	/**
	 * Returns the minimum density of clusters
	 * @return the minimum density of clusters
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Returns the minimum size of the clusters that will be returned
	 * @return the minimum size
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * Returns the overlap threshold of the algorithm.
	 * 
	 * The overlap threshold controls whether two given clusters will be merged in the final
	 * result set. The complexes will be merged if their matching ratio or meet/min
	 * coefficient (depending on the current {@link mergingMethod}) is larger than
	 * this ratio.
	 * 
	 * @return the overlap threshold
	 */
	public double getOverlapThreshold() {
		return overlapThreshold;
	}

	/**
	 * Sets the merging method that will be used by the algorithm.
	 * 
	 * @param mergingMethod the merging method to use
	 */
	public void setMergingMethod(String mergingMethod) {
		this.mergingMethod = mergingMethod;
	}

	/**
	 * Sets the minimum density of clusters that can be considered acceptable.
	 * @param minDensity the minDensity to set
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = Math.max(0, minDensity);
	}

	/**
	 * Sets the minimum size of the clusters that will be returned
	 * @param minSize the minimum size
	 */
	public void setMinSize(int minSize) {
		this.minSize = Math.max(1, minSize);
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

	/**
	 * Returns the seed generation method of the algorithm.
	 * @return the seed generation method
	 */
	public SeedGenerator getSeedGenerator() {
		return seedGenerator;
	}

	/**
	 * Sets the seed generation method of the algorithm from a string specification
	 * 
	 * @param seedMethod the new seed generation method. Must be a specification
	 *                   that is understood by {@link SeedGenerator.fromString}
	 */
	public void setSeedGenerator(String seedMethodSpec) throws InstantiationException {
		this.seedGenerator = SeedGenerator.fromString(seedMethodSpec); 
	}

	/**
	 * Sets the seed generation method of the algorithm
	 * 
	 * @param  seedGenerator  the new seed generation method.
	 */
	public void setSeedGenerator(SeedGenerator seedGenerator) {
		this.seedGenerator = seedGenerator; 
	}
}
