package uk.ac.rhul.cs.graph;

import static org.junit.Assert.*;

import com.sosnoski.util.array.IntArray;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.rhul.cs.utils.IteratorUtils;

public class DepthFirstSearchTest {
    static Graph graph = null;

    @BeforeClass
    public static void setUpBefore() {
        int[] edges = { 0, 1, 1, 3, 3, 4, 4, 6, 6, 5, 5, 3, 3, 2, 2, 0, 0, 3 };
        double[] weights = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        graph = GraphFactory.createFromEdgeList(edges, weights);
    }

    @Test
    public void testDepthFirstSearchConstructor() {
        DepthFirstSearch dfs = new DepthFirstSearch(graph, 2);
        assertEquals(graph, dfs.getGraph());
        assertEquals(2, dfs.getSeedNode());
    }

    @Test
    public void testIterator() {
        DepthFirstSearch dfs = new DepthFirstSearch(graph, 0);
        assertArrayEquals(new int[]{0, 1, 3, 4, 6, 5, 2}, dfs.toArray());
    }

    @Test
    public void testDistancesAndParents() {
        DepthFirstSearch dfs = new DepthFirstSearch(graph, 0);
        DepthFirstSearchIterator iterator = dfs.iterator();

        assertEquals(-1, iterator.getDistance());
        assertEquals(-1, iterator.getParent());

        // Visiting node 0
        assertTrue(iterator.hasNext());
        assertEquals(0, iterator.next().intValue());
        assertEquals(0, iterator.getDistance());
        assertEquals(-1, iterator.getParent());

        // Visiting node 1
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next().intValue());
        assertEquals(1, iterator.getDistance());
        assertEquals(0, iterator.getParent());

        // Visiting node 3
        assertTrue(iterator.hasNext());
        assertEquals(3, iterator.next().intValue());
        assertEquals(2, iterator.getDistance());
        assertEquals(1, iterator.getParent());

        // Visiting node 4
        assertTrue(iterator.hasNext());
        assertEquals(4, iterator.next().intValue());
        assertEquals(3, iterator.getDistance());
        assertEquals(3, iterator.getParent());

        // Visiting node 6
        assertTrue(iterator.hasNext());
        assertEquals(6, iterator.next().intValue());
        assertEquals(4, iterator.getDistance());
        assertEquals(4, iterator.getParent());

        // Visiting node 5
        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.next().intValue());
        assertEquals(5, iterator.getDistance());
        assertEquals(6, iterator.getParent());

        // Visiting node 2
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next().intValue());
        assertEquals(3, iterator.getDistance());
        assertEquals(3, iterator.getParent());

        // Did the traversal really end?
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testHooks() {
        DepthFirstSearchIteratorWithLoggingHooks iterator;

        iterator = new DepthFirstSearchIteratorWithLoggingHooks(graph, 0);
        IteratorUtils.exhaust(iterator);

        assertArrayEquals(new int[] {0, 1, 3, 4, 6, 5, 10503, 105, 106, 104, 10305, 2,
                        10200, 102, 10300, 103, 101, 10002, 10003, 100},
                iterator.log.toArray());

        iterator = new DepthFirstSearchIteratorWithLoggingHooks(graph, 0, new int[] { 0, 1, 2, 3, 4 });
        IteratorUtils.exhaust(iterator);

        assertArrayEquals(new int[]{0, 1, 3, 4, 104, 2, 10200, 102, 10300, 103, 101, 10002, 10003, 100},
                iterator.log.toArray());
    }

    @Test
    public void testRestrictedIterator() {
        DepthFirstSearch dfs = new DepthFirstSearch(graph, 0);
        int[][] subsets = {
                { 0, 1, 3, 4, 5, 6 },
                { 0, 2, 4 },
                { 0, 4, 6 },
                { 0, 1, 2, 3, 4 }
        };
        int[][] results = {
                { 0, 1, 3, 4, 6, 5 },
                { 0, 2 },
                { 0 },
                { 0, 1, 3, 4, 2 }
        };

        for (int i = 0; i < subsets.length; i++) {
            dfs.restrictToSubgraph(subsets[i]);
            assertArrayEquals(dfs.toArray(), results[i]);
        }
    }


    class DepthFirstSearchIteratorWithLoggingHooks extends DepthFirstSearchIterator {
        public IntArray log = new IntArray();

        public DepthFirstSearchIteratorWithLoggingHooks(Graph graph, int seedNode) {
            this(graph, seedNode, null);
        }

        public DepthFirstSearchIteratorWithLoggingHooks(Graph graph, int seedNode, int[] subset) {
            super(graph, seedNode, subset);
        }

        @Override
        public void enterSubtreeHook(Item item) {
            log.add(item.node);
        }

        @Override
        public void exitSubtreeHook(Item item) {
            log.add(item.node+100);
        }

        @Override
        public void visitedNodeFoundHook(Item item, int neighbor, int neighborIndex) {
            log.add(10000+item.node*100+neighbor);
        }
    }
}
