package uk.ac.rhul.cs.cl1;

/**
 * Interface for similarity functions defined over pairs of objects.
 * 
 * @author ntamas
 */
public interface SimilarityFunction<T> {
	/**
	 * Calculates the similarity between the two given objects.
	 * 
	 * @param set1  the first object
	 * @param set2  the second object
	 * @return  the similarity score.
	 */
	public double getSimilarity(T obj1, T obj2);
}
