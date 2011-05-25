package uk.ac.rhul.cs.cl1;

/**
 * Dummy nodeset merger that always returns the input nodeset without
 * merging.
 * 
 * @author tamas
 *
 */
public class DummyNodeSetMerger extends AbstractNodeSetMerger {
	public ValuedNodeSetList mergeOverlapping(ValuedNodeSetList nodeSets,
			SimilarityFunction<NodeSet> similarityFunc, double threshold) {
		return nodeSets;
	}
}
