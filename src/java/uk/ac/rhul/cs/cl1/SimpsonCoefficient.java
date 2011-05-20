package uk.ac.rhul.cs.cl1;

/**
 * Calculates the Simpson coefficient between two node sets.
 * 
 * @author ntamas
 */
public class SimpsonCoefficient<T extends Object & Sized & Intersectable<? super T> >
implements SimilarityFunction<T> {
	/**
	 * Returns the Simpson coefficient between two sets.
	 * 
	 * The Simpson coefficient is the size of the intersection of the two sets,
	 * divided by the minimum of the sizes of the two sets. It is sometimes
	 * also called the meet/min coefficient
	 * 
	 * @param   set1  the first set
	 * @param   set2  the second set
	 * @return   the Simpson coefficient
	 * @precondition   the two sets must belong to the same graph
	 */
	public double getSimilarity(T set1, T set2) {
		double den = Math.min(set1.size(), set2.size());
		if (den == 0)
			return 0.0;
		return set1.getIntersectionSizeWith(set2) / den;
	}
}
