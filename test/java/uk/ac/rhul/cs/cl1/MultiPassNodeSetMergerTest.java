package uk.ac.rhul.cs.cl1;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.rhul.cs.graph.Graph;

public class MultiPassNodeSetMergerTest {
	Graph graph = new Graph();
	
	@Test
	public void testMergeOverlapping() {
		for (int i = 0; i < 17; i++)
			graph.createNode(Integer.toString(i));
		
		ValuedNodeSetList nodeSets = new ValuedNodeSetList();
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5));
		nodeSets.add(new ValuedNodeSet(graph, 4, 5, 6, 7, 8));
		nodeSets.add(new ValuedNodeSet(graph, 3, 4, 5, 6, 7, 8, 9, 10, 11));
		nodeSets.add(new ValuedNodeSet(graph, 3, 4, 5));
		nodeSets.add(new ValuedNodeSet(graph, 12, 13));
		nodeSets.add(new ValuedNodeSet(graph, 13, 14));
		nodeSets.add(new ValuedNodeSet(graph, 15, 16));
		
		SimilarityFunction<NodeSet> similarityFunc = new JaccardSimilarity<NodeSet>();
		double threshold = 0.3;
		
		MultiPassNodeSetMerger merger = new MultiPassNodeSetMerger();
		merger.setVerificationMode(true);
		// merger.setDebugging(true);
		
		ValuedNodeSetList mergedNodeSets;
		mergedNodeSets = merger.mergeOverlapping(nodeSets, similarityFunc, threshold);
		
		assertEquals(4, mergedNodeSets.size());
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5)));
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 12, 13, 14)));
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 15, 16)));
	}
	
	@Test
	public void testMergeOverlapping2() {
		// More pathological test case
		for (int i = 0; i < 17; i++)
			graph.createNode(Integer.toString(i));
		
		ValuedNodeSetList nodeSets = new ValuedNodeSetList();
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14));
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14));
		nodeSets.add(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14));
		
		SimilarityFunction<NodeSet> similarityFunc = new JaccardSimilarity<NodeSet>();
		double threshold = 0.3;
		
		MultiPassNodeSetMerger merger = new MultiPassNodeSetMerger();
		merger.setVerificationMode(true);
		// merger.setDebugging(true);
		
		ValuedNodeSetList mergedNodeSets;
		mergedNodeSets = merger.mergeOverlapping(nodeSets, similarityFunc, threshold);
		
		assertEquals(1, mergedNodeSets.size());
		assertTrue(mergedNodeSets.contains(new ValuedNodeSet(graph, 0, 1, 2, 3, 4, 5, 6, 7,
				8, 9, 10, 11, 12, 13, 14)));
	}
}
