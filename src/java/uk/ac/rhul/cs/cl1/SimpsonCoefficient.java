package uk.ac.rhul.cs.cl1;

/**
 * Calculates the Simpson coefficient between two node sets.
 * 
 * @author ntamas
 */
public class SimpsonCoefficient implements NodeSetSimilarityFunction {
	/**
	 * Returns the Simpson coefficient of this nodeset with another
	 * 
	 * The Simpson coefficient is the size of the intersection of the two nodesets,
	 * divided by the minimum of the sizes of the two nodesets. It is sometimes
	 * also called the meet/min coefficient
	 * 
	 * @param   set1  the first nodeset
	 * @param   set2  the second nodeset
	 * @return   the Simpson coefficient
	 * @precondition   the two nodesets must belong to the same graph
	 */
	public double getSimilarity(NodeSet set1, NodeSet set2) {
		double den = Math.min(set1.size(), set2.size());
		if (den == 0)
			return 0.0;
		return set1.getIntersectionSizeWith(set2) / den;
	}
}
