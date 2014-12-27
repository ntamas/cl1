package uk.ac.rhul.cs.cl1.support;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.seeding.Seed;
import uk.ac.rhul.cs.graph.Graph;

import java.util.BitSet;

/**
 * Set that keeps track of which nodes are/were used during a single instance of
 * the ClusterONE algorithm.
 */
public class UsedNodeSet {

    /**
     * An internal bit set that marks the nodes that are/were used during the algorithm.
     */
    private BitSet usedNodes;

    /**
     * Constructor.
     *
     * @param graph  the graph on which the algorithm is being run.
     */
    public UsedNodeSet(Graph graph) {
        usedNodes = new BitSet(graph.getNodeCount());
    }

    /**
     * Returns whether all the nodes from the given seed are used.
     */
    public boolean areAllNodesUsedFromSeed(Seed seed) {
        for (int i : seed.members) {
            if (!usedNodes.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears the used node set; in other words, marks every node as unused.
     */
    public void clear() {
        usedNodes.clear();
    }

    /**
     * Marks the members of the given seed as used.
     */
    public void markSeedAsUsed(Seed seed) {
        for (int i : seed.members) {
            usedNodes.set(i);
        }
    }

    /**
     * Marks the members of the given node set as used.
     */
    public void markNodeSetAsUsed(NodeSet nodeSet) {
        for (int i : nodeSet) {
            usedNodes.set(i);
        }
    }
}
