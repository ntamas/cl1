package uk.ac.rhul.cs.cl1.similarity;

import uk.ac.rhul.cs.cl1.Intersectable;
import uk.ac.rhul.cs.cl1.Sized;

/**
 * Calculates the Jaccard similarity between two objects.
 * 
 * The Jaccard similarity is defined as the size of the intersection, divided 
 * by the size of the union of the two objects.
 * 
 * @author ntamas
 */
public class JaccardSimilarity<T extends Object & Sized & Intersectable<? super T>>
implements SimilarityFunction<T> {
	public String getName() {
		return "Jaccard similarity";
	}
	
	/**
	 * Calculates the Jaccard similarity between two objects.
	 * 
	 * The Jaccard similarity is defined as the size of the intersection, divided 
	 * by the size of the union of the two objects.
	 * 
	 * @param   set1  the first object
	 * @param   set2  the second object
	 * @return   the Jaccard similarity
	 */
	public double getSimilarity(T set1, T set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		return num / (set1.size() + set2.size() - num);
	}
}
