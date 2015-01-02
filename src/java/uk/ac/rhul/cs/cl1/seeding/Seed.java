package uk.ac.rhul.cs.cl1.seeding;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a seed nodeset of the ClusterONE algorithm.
 *
 * This object is a lightweight wrapper around a {@link uk.ac.rhul.cs.graph.Graph} object
 * and an array of node indices. It can be used to initialize a {@link uk.ac.rhul.cs.cl1.NodeSet}
 * or {@link uk.ac.rhul.cs.cl1.MutableNodeSet}. Having a separate {@link Seed} class that
 * the seed generators return allows us to re-use the same {@link uk.ac.rhul.cs.cl1.MutableNodeSet}
 * in the algorithm to grow multiple seeds, which saves some time because the auxiliary data
 * structures in the {@link uk.ac.rhul.cs.cl1.MutableNodeSet} do not have to be built again
 * for every seed.
 */
public class Seed {
    /**
     * Constructs a seed that is not associated to any graph and has no members.
     */
    public Seed() {
        this(null, null);
    }

    /**
     * Constructs a new seed on the given graph with the given members.
     *
     * @param  graph    the graph
     * @param  members  the members of the seed
     */
    public Seed(Graph graph, int... members) {
        this.graph = graph;
        this.members = members;
    }

    /**
     * Constructs a new seed from the given nodeset.
     */
    public Seed(NodeSet nodeSet) {
        this(nodeSet.getGraph(), nodeSet.toArray());
    }

    /**
     * The graph to which the seed belongs.
     */
    public Graph graph;

    /**
     * The list of nodes in the seed.
     */
    public int[] members;

    /**
     * Creates a {@link uk.ac.rhul.cs.cl1.MutableNodeSet} from this seed.
     */
    public MutableNodeSet createMutableNodeSet() {
        return new MutableNodeSet(graph, members);
    }

    /**
     * Returns the names of the members of this seed
     * @return the names of the members
     */
    public String[] getMemberNames() {
        String[] result = new String[this.members.length];
        int i = 0;

        for (int member: this.members) {
            result[i] = this.graph.getNodeName(member);
            i++;
        }

        return result;
    }

    /**
     * Initializes the given {@link uk.ac.rhul.cs.cl1.MutableNodeSet} with this seed.
     */
    public void initializeMutableNodeSet(MutableNodeSet mutableNodeSet) {
        if (mutableNodeSet.getGraph() != graph) {
            throw new UnsupportedOperationException("cannot initialize a mutable node set with a seed " +
                    "that belongs to a different graph");
        }

        mutableNodeSet.clear();
        for (int member: members) {
            mutableNodeSet.add(member);
        }
    }

    /**
     * Prints the nodes in this set to a string
     */
    public String toString() {
        return toString(" ");
    }

    /**
     * Prints the nodes in this set to a string using a given separator
     */
    public String toString(String separator) {
        return StringUtils.join(getMemberNames(), separator);
    }
}
