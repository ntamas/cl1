package uk.ac.rhul.cs.graph;

import uk.ac.rhul.cs.utils.IteratorUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds all the cut vertices (articulation points) in a graph or one of its subgraphs
 * using Tarjan's algorithm.
 *
 * @author ntamas
 */
public class TarjanCutVertexFinder extends GraphAlgorithm {

    /**
     * Subgraph on which we search for cut vertices or null if we run it on the whole graph
     */
    protected int[] subgraph = null;

    /**
     * Finds all the cut vertices of the associated graph or subgraph and returns them
     * in an array.
     *
     * @return  the set of cut vertices of the graph
     */
    public Set<Integer> findCutVertices() {
        Set<Integer> result = new HashSet<Integer>();

        if (graph != null && graph.getNodeCount() > 0) {
            DepthFirstSearchIteratorWithLowpoints iterator;
            int seedNode = (subgraph != null) ? subgraph[0] : 0;

            iterator = new DepthFirstSearchIteratorWithLowpoints(graph, seedNode, subgraph, result);
            IteratorUtils.exhaust(iterator);
        }

        return result;
    }

    /**
     * Restricts the traversal to the given subgraph if supported.
     *
     * @throws  java.lang.UnsupportedOperationException  if the traversal does not support
     *          restrictions to subgraphs.
     */
    public void restrictToSubgraph(int[] subgraph) {
        this.subgraph = subgraph.clone();
        Arrays.sort(this.subgraph);
    }

    /**
     * Subclass of a depth first search iterator that maintains the lowpoints of each DFS
     * subtree.
     */
    class DepthFirstSearchIteratorWithLowpoints extends DepthFirstSearchIterator {
        private int[] depths;
        private int[] lowPoints;
        private int rootChildCount = 0;
        private Set<Integer> cutVertices;

        public DepthFirstSearchIteratorWithLowpoints(Graph graph, int seedNode, int[] subset,
                                                     Set<Integer> cutVertices) {
            super(graph, seedNode, subset);
            this.cutVertices = cutVertices;

            int numNodes = (subset != null) ? subset.length : graph.getNodeCount();
            depths = new int[numNodes];
            lowPoints = new int[numNodes];
        }

        @Override
        public void enterSubtreeHook(Item item) {
            depths[item.nodeIndex] = item.distance;
            lowPoints[item.nodeIndex] = item.distance;
        }

        @Override
        public void exitSubtreeHook(Item item) {
            // TODO: this can be made faster by using an array if subset == null

            int child = item.node;
            int childIndex = item.nodeIndex;
            int parent = item.parent;
            int parentIndex = item.parentIndex;

            if (parent == -1) {
                // This is the seed vertex. We must check whether there were at least two
                // children to decide whether the seed is a cut vertex.
                if (rootChildCount > 1) {
                    cutVertices.add(child);
                }
            } else {
                // Parent may become a cut vertex if it is not the root and
                // lowPoints[child] >= lowPoints[parent]
                int childLowPoint = lowPoints[childIndex];
                int parentLowPoint = lowPoints[parentIndex];
                if ((parent != seedNode) && (childLowPoint >= depths[parentIndex])) {
                    cutVertices.add(parent);
                }

                if (childLowPoint < parentLowPoint) {
                    lowPoints[parentIndex] = childLowPoint;
                }

                if (parent == seedNode) {
                    rootChildCount++;
                }
            }
        }

        @Override
        public void visitedNodeFoundHook(Item item, int neighbor, int neighborIndex) {
            // Update the lowpoint of the current node with the depth of the neighbor if needed
            int ownLowpoint = lowPoints[item.nodeIndex];
            int neighborDepth = depths[neighborIndex];
            if (neighborDepth < ownLowpoint) {
                lowPoints[item.nodeIndex] = neighborDepth;
            }
        }
    }
}
