package uk.ac.rhul.cs.cl1;

/**
 * Calculates the Dice similarity between two nodesets.
 * 
 * The Dice similarity is defined as twice the size of the intersection, divided 
 * by the sum of the sizes of the two nodesets.
 * 
 * @author ntamas
 */
public class DiceSimilarity<T extends Object & Sized & Intersectable<? super T> > implements SimilarityFunction<T> {
	public String getName() {
		return "Dice similarity";
	}
	
	/**
	 * Calculates the Dice similarity between two objects.
	 * 
	 * The Dice similarity is defined as twice the size of the intersection, divided 
	 * by the sum of the sizes of the two objects.
	 * 
	 * @param   set1  the first object
	 * @param   set2  the second object
	 * @return   the Dice similarity
	 */
	public double getSimilarity(T set1, T set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		return 2 * num / (set1.size() + set2.size());
	}
}
