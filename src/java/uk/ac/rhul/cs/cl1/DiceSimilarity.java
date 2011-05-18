package uk.ac.rhul.cs.cl1;

/**
 * Calculates the Dice similarity between two nodesets.
 * 
 * The Dice similarity is defined as twice the size of the intersection, divided 
 * by the sum of the sizes of the two nodesets.
 * 
 * @author ntamas
 */
public class DiceSimilarity implements NodeSetSimilarityFunction {
	/**
	 * Calculates the Dice similarity between two nodesets.
	 * 
	 * The Dice similarity is defined as twice the size of the intersection, divided 
	 * by the sum of the sizes of the two nodesets.
	 * 
	 * @param   set1  the first nodeset
	 * @param   set2  the second nodeset
	 * @return   the Dice similarity
	 * @precondition   the two nodesets must belong to the same graph
	 */
	public double getSimilarity(NodeSet set1, NodeSet set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		return 2 * num / (set1.size() + set2.size());
	}
}
