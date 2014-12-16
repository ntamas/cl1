package uk.ac.rhul.cs.cl1.merging;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.similarity.SimilarityFunction;
import uk.ac.rhul.cs.cl1.ValuedNodeSetList;

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
