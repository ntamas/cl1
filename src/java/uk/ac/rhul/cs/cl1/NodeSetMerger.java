package uk.ac.rhul.cs.cl1;

/**
 * Common interface for algorithms that merge highly overlapping nodesets
 * in a {@link ValuedNodeSetList}.
 * 
 * @author tamas
 *
 */
public interface NodeSetMerger {
	/**
	 * Merges highly overlapping nodesets in the given {@link ValuedNodeSetList}.
	 * 
	 * @param nodeSets        the list of nodesets to merge
	 * @param similarityFunc  a function to measure the similarity between pairs
	 *                        of {@link NodeSet} instances.
	 * @param threshold       similarity threshold above which two nodesets are
	 *                        considered to be similar.
	 * @return  the merged nodeset.
	 */
	public ValuedNodeSetList mergeOverlapping(
			ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc,
			double threshold
	);
}
