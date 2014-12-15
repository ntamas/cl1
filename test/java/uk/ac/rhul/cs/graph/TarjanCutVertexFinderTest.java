package uk.ac.rhul.cs.graph;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TarjanCutVertexFinderTest {
    @Test
    public void testSimple() {
        Graph graph = GraphFactory.createFromEdgeList(
                new int[] { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(1, cutVertices.size());
        assertTrue(cutVertices.contains(3));
    }

    @Test
    public void testSimpleRestricted() {
        Graph graph = GraphFactory.createFromEdgeList(
                new int[]{0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3});

        TarjanCutVertexFinder finder;
        Set<Integer> cutVertices;

        finder = new TarjanCutVertexFinder();
        finder.setGraph(graph);
        finder.restrictToSubgraph(new int[]{0, 1, 2, 3, 4});
        cutVertices = finder.findCutVertices();

        assertEquals(1, cutVertices.size());
        assertTrue(cutVertices.contains(3));

        finder = new TarjanCutVertexFinder();
        finder.setGraph(graph);
        finder.restrictToSubgraph(new int[]{0, 1, 3, 4, 6});
        cutVertices = finder.findCutVertices();

        assertEquals(2, cutVertices.size());
        assertTrue(cutVertices.contains(3));
        assertTrue(cutVertices.contains(4));
    }

    @Test
    public void testSimple2() {
        Graph graph = GraphFactory.createFromEdgeList(
                new int[] { 0, 1, 1, 2, 2, 0, 0, 3, 3, 4 });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(2, cutVertices.size());
        assertTrue(cutVertices.contains(0));
        assertTrue(cutVertices.contains(3));
    }

    @Test
    public void testSimple2Permuted() {
        Graph graph = GraphFactory.createFromEdgeList(
                new int[] { 4, 3, 3, 2, 2, 4, 4, 1, 1, 0 });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(2, cutVertices.size());
        assertTrue(cutVertices.contains(1));
        assertTrue(cutVertices.contains(4));
    }

    @Test
    public void testSimple3() {
        Graph graph = GraphFactory.createFromEdgeList(
                new int[] { 0, 1, 1, 2, 2, 0, 1, 3, 3, 5, 5, 4, 4, 1, 1, 6 });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(1, cutVertices.size());
        assertTrue(cutVertices.contains(1));
    }

    @Test
    public void testChain() {
        Graph graph = GraphFactory.createFromEdgeList(new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5 });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(4, cutVertices.size());
        assertTrue(cutVertices.contains(1));
        assertTrue(cutVertices.contains(2));
        assertTrue(cutVertices.contains(3));
        assertTrue(cutVertices.contains(4));
    }

    @Test
    public void testRing() {
        Graph graph = GraphFactory.createFromEdgeList(new int[]{0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0});
        assertTrue(findCutVertices(graph).isEmpty());
    }

    @Test
    public void testFullGraph() {
        Graph graph = GraphFactory.createFullGraph(5, false, false);
        assertTrue(findCutVertices(graph).isEmpty());
    }

    @Test
    public void testZachary() {
        Graph graph = GraphFactory.createFamousGraph("zachary");
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(1, cutVertices.size());
        assertTrue(cutVertices.contains(0));
    }

    @Test
    public void testComplex1() {
        Graph graph = GraphFactory.createFromEdgeList(new int[]{
                0, 1, 0, 2, 0, 4, 0, 7, 1, 4, 1, 8, 1, 9, 3, 4, 3, 7, 4, 7,
                5, 6, 5, 10, 6, 7, 6, 9, 6, 10, 7, 9
        });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(3, cutVertices.size());
        assertTrue(cutVertices.contains(0));
        assertTrue(cutVertices.contains(1));
        assertTrue(cutVertices.contains(6));
    }

    @Test
    public void testComplex2() {
        Graph graph = GraphFactory.createFromEdgeList(new int[]{
                0, 3, 0, 6, 1, 7, 1, 8, 2, 4, 2, 5, 2, 6, 2, 7, 3, 6, 4, 7, 6, 7, 7, 8
        });
        Set<Integer> cutVertices = findCutVertices(graph);

        assertEquals(3, cutVertices.size());
        assertTrue(cutVertices.contains(2));
        assertTrue(cutVertices.contains(6));
        assertTrue(cutVertices.contains(7));
    }

    private Set<Integer> findCutVertices(Graph graph) {
        TarjanCutVertexFinder finder = new TarjanCutVertexFinder();
        finder.setGraph(graph);
        return finder.findCutVertices();
    }
}
