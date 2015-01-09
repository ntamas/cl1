package uk.ac.rhul.cs.cl1.similarity;

/**
 * Interface for similarity functions defined over pairs of objects.
 *
 * @author ntamas
 */
public interface SimilarityFunction<T> {
	/**
	 * Returns the human-readable name of this function.
	 */
	public String getName();
	
	/**
	 * Calculates the similarity between the two given objects. It is assumed that the similarity
	 * of two "disjoint" objects is zero.
	 * 
	 * @param obj1  the first object
	 * @param obj2  the second object
	 * @return  the similarity score.
	 */
	public double getSimilarity(T obj1, T obj2);
}
