package uk.ac.rhul.cs.cl1.seeding;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.seeding.SeedGenerator;
import uk.ac.rhul.cs.graph.Edge;
import uk.ac.rhul.cs.graph.Graph;

public class SeedGeneratorTest {
	static Graph graph = null;
	
	@BeforeClass
	public static void setUpBefore() {
		int[] edges = { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 };
		graph = new Graph();
		graph.createNodes(7);
		for (int i = 0; i < edges.length; i+=2) {
			graph.createEdge(edges[i], edges[i+1], 1);
		}
	}
	
	@Test
	public void testEveryNodeSeedGenerator() {
		SeedGenerator gen = null;
		try {
			gen = SeedGenerator.fromString("nodes", graph);
		} catch (InstantiationException ex) {
			fail(ex.getMessage());
		}
		
		int i = 0;
		for (MutableNodeSet set: gen) {
			assert(set.contains(i));
			assert(set.size() == 1);
			i++;
		}
	}
	
	@Test
	public void testEveryEdgeSeedGenerator() {
		SeedGenerator gen = null;
		try {
			gen = SeedGenerator.fromString("edges", graph);
		} catch (InstantiationException ex) {
			fail(ex.getMessage());
		}
		
		Iterator<MutableNodeSet> it = gen.iterator();
		for (Edge edge: graph) {
			assert(it.hasNext());
			
			MutableNodeSet set = it.next();
			assert(set.contains(edge.source));
			assert(set.contains(edge.target));
			assert(set.size() == 2);
		}
	}
}
