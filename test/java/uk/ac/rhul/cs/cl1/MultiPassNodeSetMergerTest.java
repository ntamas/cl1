package uk.ac.rhul.cs.cl1;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.graph.Graph;

public class MultiPassNodeSetMergerTest {

	@Test
	public void testMergeOverlapping() {
		Graph graph = new Graph();
		for (int i = 0; i < 12; i++)
			graph.createNode(Integer.toString(i));
		
		ValuedNodeSetList nodeSets = new ValuedNodeSetList();
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5));
		nodeSets.add(new ValuedNodeSet(graph, 4, 5, 6, 7, 8));
		nodeSets.add(new ValuedNodeSet(graph, 3, 4, 5, 6, 7, 8, 9, 10, 11));
		nodeSets.add(new ValuedNodeSet(graph, 3, 4, 5));
		
		SimilarityFunction<NodeSet> similarityFunc = new JaccardSimilarity<NodeSet>();
		double threshold = 0.3;
		
		MultiPassNodeSetMerger merger = new MultiPassNodeSetMerger();
		// merger.setDebugging(true);
		
		ValuedNodeSetList mergedNodeSets;
		mergedNodeSets = merger.mergeOverlapping(nodeSets, similarityFunc, threshold);
		
		assertEquals(2, mergedNodeSets.size());
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5)));
	}

}
