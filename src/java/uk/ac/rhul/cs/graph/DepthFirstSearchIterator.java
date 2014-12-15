package uk.ac.rhul.cs.graph;

import com.sosnoski.util.array.IntArray;
import com.sosnoski.util.stack.ObjectStack;
import uk.ac.rhul.cs.collections.IntIntHashMap;

import java.util.BitSet;
import java.util.Iterator;

/**
 * Iterator that traverses the vertices of a graph in depth first order.
 *
 * @author tamas
 */
public class DepthFirstSearchIterator implements Iterator<Integer> {
    /**
     * Graph which the iterator will traverse
     */
    protected Graph graph = null;

    /**
     * When the DFS is restricted to a subgraph, this mapping maps the set of nodes that
     * are allowed during the DFS to their indices that are used in auxiliary DFS arrays.
     * Otherwise it is null.
     */
    protected IntIntHashMap allowedNodeToIndexMapping = null;

    /**
     * When the DFS is restricted to a subgraph, this array contains the inverse of the
     * <code>allowedNodeToIndexMapping</code>. Otherwise it is null.
     */
    protected IntArray indexToAllowedNodeMapping = null;

    /**
     * Queue that holds the nodes that are to be visited, their distances from the seed , their
     * parents and the index of the next neighbor to visit.
     */
    protected ObjectStack q = new ObjectStack();

    /**
     * Bitset that holds flags that marks the nodes that have been visited.
     */
    protected BitSet visited;

    /**
     * Distance of the last returned node from the seed
     */
    protected int distance = -1;

    /**
     * Parent of the last returned node
     */
    protected int parent = -1;

    /**
     * The seed node of the depth first search.
     */
    protected int seedNode;

    /**
     * Constructs a new DFS iterator.
     *
     * @param graph    the graph to be traversed
     * @param seedNode the index of the seed node
     */
    public DepthFirstSearchIterator(Graph graph, int seedNode) {
        this(graph, seedNode, null);
    }

    /**
     * Constructs a new DFS iterator restricted to a set of nodes.
     *
     * @param graph    the graph to be traversed
     * @param seedNode the index of the seed node
     * @param subset   an array of node indices which must be traversed.
     *                 Nodes not in this nodeset are assumed to have been
     *                 already visited by the iterator. Can also be null,
     *                 which means that every node can be traversed.
     */
    public DepthFirstSearchIterator(Graph graph, int seedNode, int[] subset) {
        this.graph = graph;
        this.seedNode = seedNode;

        if (subset != null) {
            restrictToSubgraph(subset);
        }

        if (allowedNodeToIndexMapping == null) {
            visited = new BitSet(graph.getNodeCount());
        } else {
            visited = new BitSet(indexToAllowedNodeMapping.size());
        }

        if (allowedNodeToIndexMapping == null ||
                allowedNodeToIndexMapping.get(seedNode) != IntIntHashMap.DEFAULT_NOT_FOUND) {
            pushNode(seedNode, -1, 0);
        }
    }

    /**
     * Hook method that is called when the DFS traversal enters the subtree of a given node.
     *
     * @param  item  the item in the DFS queue that holds information about the node that the
     *               DFS traversal entered
     */
    public void enterSubtreeHook(Item item) {
    }

    /**
     * Hook method that is called when the DFS traversal exits the subtree of a given node.
     *
     * @param  item  the item in the DFS queue that holds information about the node that the
     *               DFS traversal exited from
     */
    public void exitSubtreeHook(Item item) {
    }

    /**
     * Hook method that is called when the DFS traversal attempts to visit a node that has already
     * been visited.
     *
     * @param  item  the item representing the node currently being visited
     * @param  neighbor  the neighbor that the traversal attempted to visit
     * @param  neighborIndex  index of the neighbor in the set of nodes that the iterator is restricted
     *                        to; equal to <code>neighbor</code> if the traversal is not restricted.
     */
    public void visitedNodeFoundHook(Item item, int neighbor, int neighborIndex) {
    }

    /**
     * Returns the distance of the last returned node from the seed.
     *
     * @return  the distance of the last node from the seed or -1 if the
     *          traversal has not yet started.
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Returns the parent of the last returned node from the seed.
     *
     * @return  the parent of the last node in the BFS tree or -1 if the
     *          traversal has not yet started or the node is the root.
     */
    public int getParent() {
        return parent;
    }

    /**
     * Returns whether there are more nodes left in the traversal.
     */
    public boolean hasNext() {
        return !q.isEmpty();
    }

    /**
     * Returns the index of the next visited node
     */
    public Integer next() {
        Item nextItem = (Item)q.peek();

        int result = nextItem.node;
        distance = nextItem.distance;
        parent = nextItem.parent;

        stepIterator();

        return result;
    }

    /**
     * Removal is not supported.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Restricts the BFS traversal to the given subset.
     *
     * @param  subset    an array of node indices to which we restrict
     *                   the traversal
     */
    public void restrictToSubgraph(int[] subset) {
        int i = 0;

        // The trick here is that we create a "remapping" between node indices in 'subset' to the range
        // of integers from 0 to n-1, where n is the number of unique elements in 'subset'. This allows
        // us to work with dense arrays instead of hash maps in the actual DFS iteration to maintain
        // state information related to the allowed nodes.
        //
        // allowedNodeToIndexMapping will map a node index to the index of the corresponding slot in DFS
        // arrays, while indexToAllowedNodeMapping will contain the opposite mapping.
        allowedNodeToIndexMapping = new IntIntHashMap();
        indexToAllowedNodeMapping = new IntArray(subset.length);
        for (int node: subset) {
            if (allowedNodeToIndexMapping.get(node) == IntIntHashMap.DEFAULT_NOT_FOUND) {
                allowedNodeToIndexMapping.add(node, i);
                indexToAllowedNodeMapping.add(node);
                i++;
            }
        }
    }

    /**
     * A single item of the DFS queue.
     *
     * Note that this object implements <code>Iterable&lt;Integer&gt;</code>, but we do not expose this
     * to disallow external objects holding a reference to an Item to advance the internal state
     * without the DFS iterator knowing about it.
     */
    protected class Item {
        /**
         * The node in the DFS queue.
         */
        public int node;

        /**
         * The index of the slot in DFS arrays that store information related to the node.
         */
        public int nodeIndex;

        /**
         * The distance of the node from the seed node of the DFS.
         */
        public int distance;

        /**
         * The parent of the node in the DFS tree or -1 if the node is the root of the
         * DFS tree.
         */
        public int parent;

        /**
         * The index of the slot in DFS arrays that store information related to the parent node
         * or -1 if the node is the root of the DFS tree.
         */
        public int parentIndex;

        /**
         * Array containing integers that define the neighbors of the node. Each integer must be looked up
         * in the <code>indexToAllowedNodeMapping</code> if the iterator is restricted.
         */
        int[] neighborIndexes;

        /**
         * Index pointer into the neighborIndexes array defining the next neighbor of the node to visit.
         */
        int neighborIndexesReadPtr;

        /**
         * The number of neighbors of this node.
         */
        int numNeighbors;

        /**
         * Constructs a new item for the DFS queue with the given distance and the given parent.
         */
        public Item(int node, int parent, int distance) {
            this.node = node;
            this.parent = parent;
            this.distance = distance;

            if (allowedNodeToIndexMapping == null) {
                this.nodeIndex = node;
                this.parentIndex = parent;
            } else {
                this.nodeIndex = allowedNodeToIndexMapping.get(node);
                assert this.nodeIndex != IntIntHashMap.DEFAULT_NOT_FOUND;

                if (parent >= 0) {
                    this.parentIndex = allowedNodeToIndexMapping.get(parent);
                    assert this.parentIndex != IntIntHashMap.DEFAULT_NOT_FOUND;
                } else {
                    this.parentIndex = -1;
                }
            }

            this.neighborIndexes = getAdjacentNodeIndicesArray(node, Directedness.ALL);
            this.neighborIndexesReadPtr = 0;
            this.numNeighbors = this.neighborIndexes.length;
        }

        /**
         * Returns whether this node has more neighbor nodes to visit
         */
        public boolean hasNext() {
            return neighborIndexesReadPtr < numNeighbors;
        }

        /**
         * Returns an item representing the <em>array index</em> of the next neighbor to visit and
         * advances the state of this item accordingly. You must look this index up in
         * <code>indexToAllowedNodeMapping</code> to get the actual node if the iterator
         * is restricted.
         */
        public int next() {
            int nextNeighbor = neighborIndexes[neighborIndexesReadPtr];
            neighborIndexesReadPtr++;
            return nextNeighbor;
        }

        /**
         * Returns the <em>array indices</em> of the nodes adjacent to the given node.
         */
        private int[] getAdjacentNodeIndicesArray(int node, Directedness mode) {
            if (allowedNodeToIndexMapping == null) {
                // Every node is allowed, so we query the graph directly
                return graph.getAdjacentNodeIndicesArray(node, Directedness.ALL);
            }

            int[] edges = graph.getAdjacentEdgeIndicesArray(node, mode);
            IntArray nodes = new IntArray(edges.length);
            for (int edge: edges) {
                int otherNode = graph.getEdgeEndpoint(edge, node);
                otherNode = allowedNodeToIndexMapping.get(otherNode);
                if (otherNode != IntIntHashMap.DEFAULT_NOT_FOUND) {
                    nodes.add(otherNode);
                }
            }

            return nodes.toArray();
        }
    }

    /**
     * Pushes the given item into the queue.
     *
     * @param  item  the item to push
     */
    private void pushItem(Item item) {
        visited.set(item.nodeIndex);
        q.push(item);
    }

    /**
     * Pushes the given node with the given distance into the queue if we are allowed
     * to visit the node.
     *
     * @param  node      the node to push to the queue
     * @param  parent    the parent of the node or -1 if the node is the root
     * @param  distance  the distance of the node from the start point
     */
    private void pushNode(int node, int parent, int distance) {
        pushItem(new Item(node, parent, distance));
    }

    /**
     * Advances the internal state of the iterator to the next node to visit.
     */
    private void stepIterator() {
        Item item = (Item)q.peek();
        int nextNeighbor;
        int indexOfNextNeighbor;

        while (item != null) {
            // Are we entering this item for the first time?
            if (item.neighborIndexesReadPtr == 0) {
                enterSubtreeHook(item);
            }

            while (item.hasNext()) {
                // Visit the next neighbor of the item
                indexOfNextNeighbor = item.next();
                if (indexOfNextNeighbor == item.parentIndex)
                    continue;

                if (indexToAllowedNodeMapping != null) {
                    nextNeighbor = indexToAllowedNodeMapping.get(indexOfNextNeighbor);
                } else {
                    nextNeighbor = indexOfNextNeighbor;
                }

                if (!visited.get(indexOfNextNeighbor)) {
                    pushNode(nextNeighbor, item.node, item.distance + 1);
                    return;
                } else {
                    visitedNodeFoundHook(item, nextNeighbor, indexOfNextNeighbor);
                }
            }

            // Neighbors exhausted; pop the item from the queue instead
            exitSubtreeHook((Item)q.pop());
            item = q.isEmpty() ? null : (Item)q.peek();
        }
    }

}
