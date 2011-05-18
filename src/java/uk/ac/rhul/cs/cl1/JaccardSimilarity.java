package uk.ac.rhul.cs.cl1;

/**
 * Calculates the Jaccard similarity between two nodesets.
 * 
 * The Jaccard similarity is defined as the size of the intersection, divided 
 * by the size of the union of the two nodesets.
 * 
 * @author ntamas
 */
public class JaccardSimilarity implements NodeSetSimilarityFunction {
	/**
	 * Calculates the Jaccard similarity between two nodesets.
	 * 
	 * The Jaccard similarity is defined as the size of the intersection, divided 
	 * by the size of the union of the two nodesets.
	 * 
	 * @param   set1  the first nodeset
	 * @param   set2  the second nodeset
	 * @return   the Jaccard similarity
	 * @precondition   the two nodesets must belong to the same graph
	 */
	public double getSimilarity(NodeSet set1, NodeSet set2) {
		double num = set1.getIntersectionSizeWith(set2);
		if (num == 0)
			return 0;
		return num / (set1.size() + set2.size() - num);
	}
}
