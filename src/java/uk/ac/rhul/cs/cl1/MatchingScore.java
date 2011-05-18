package uk.ac.rhul.cs.cl1;

/**
 * Calculates the matching score between two nodesets.
 * 
 * The matching score is defined as the size of the intersection squared,
 * divided by the product of the sizes of the two nodesets. An alternative
 * but equivalent definition is the product of the precision and the recall
 * between the two nodesets.
 * 
 * @author ntamas
 */
public class MatchingScore implements NodeSetSimilarityFunction {
	/**
	 * Returns the matching score between two nodesets.
	 * 
	 * The matching score is defined as the size of the intersection squared,
	 * divided by the product of the sizes of the two nodesets. An alternative
	 * but equivalent definition is the product of the precision and the recall
	 * between the two nodesets.
	 * 
	 * @param   set1  the first nodeset
	 * @param   set2  the second nodeset
	 * @return   the matching score
	 * @precondition   the two nodesets must belong to the same graph
	 */
	public double getSimilarity(NodeSet set1, NodeSet set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		
		return num / set1.size() / set2.size();
	}
}
