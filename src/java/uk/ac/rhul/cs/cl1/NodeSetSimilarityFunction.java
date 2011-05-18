package uk.ac.rhul.cs.cl1;

/**
 * Interface for similarity functions defined over node sets.
 * 
 * @author ntamas
 */
public interface NodeSetSimilarityFunction {
	/**
	 * Calculates the similarity between the two given node sets.
	 * 
	 * @param set1  the first node set
	 * @param set2  the second node set
	 * @return  the similarity score, usually between 0 and 1.
	 */
	public double getSimilarity(NodeSet set1, NodeSet set2);
}
