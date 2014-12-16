package uk.ac.rhul.cs.cl1.similarity;

import uk.ac.rhul.cs.cl1.Intersectable;
import uk.ac.rhul.cs.cl1.Sized;
import uk.ac.rhul.cs.cl1.similarity.SimilarityFunction;

/**
 * Calculates the matching score between two sets.
 * 
 * The matching score is defined as the size of the intersection squared,
 * divided by the product of the sizes of the two sets. An alternative
 * but equivalent definition is the product of the precision and the recall
 * between the two sets.
 * 
 * @author ntamas
 */
public class MatchingScore<T extends Object & Sized & Intersectable<? super T>> implements SimilarityFunction<T> {
	public String getName() {
		return "Matching score";
	}
	
	/**
	 * Returns the matching score between two sets.
	 * 
	 * The matching score is defined as the size of the intersection squared,
	 * divided by the product of the sizes of the two sets. An alternative
	 * but equivalent definition is the product of the precision and the recall
	 * between the two sets.
	 * 
	 * @param   set1  the first set
	 * @param   set2  the second set
	 * @return   the matching score
	 * @precondition   the two sets must belong to the same graph
	 */
	public double getSimilarity(T set1, T set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		
		return num * num / set1.size() / set2.size();
	}
}
