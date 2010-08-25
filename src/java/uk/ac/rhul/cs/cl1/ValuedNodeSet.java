package uk.ac.rhul.cs.cl1;

import java.util.Collection;

import uk.ac.rhul.cs.graph.Graph;

import com.sosnoski.util.hashmap.ObjectIntHashMap;

/**
 * Nodeset with a value associated to each node
 * 
 * This class can be used to attach arbitrary attributes to the nodes of a
 * {@link NodeSet}. Cluster ONE uses this to keep track of the number of
 * cohesive subgroups a given node participated in when merging overlapping
 * cohesive subgroups.
 * 
 * @author tamas
 */
public class ValuedNodeSet extends NodeSet {
	/**
	 * Storage area for the node values
	 */
	protected ObjectIntHashMap values;
	
	/**
	 * Constructs a new, empty valued nodeset on the given graph.
	 * 
	 * @param graph  the graph on which the nodeset is created
	 */
	public ValuedNodeSet(Graph graph) {
		super(graph);
		init();
	}
	
	/**
	 * Constructs a new valued nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  a collection containing the member IDs
	 */
	public ValuedNodeSet(Graph graph, Collection<Integer> members) {
		super(graph, members);
		init();
	}
	
	/**
	 * Constructs a new valued nodeset on the given graph.
	 * 
	 * @param graph    the graph on which the nodeset is created
	 * @param members  an array containing the member IDs
	 */
	public ValuedNodeSet(Graph graph, int[] members) {
		super(graph, members);
		init();
	}
	
	/**
	 * Constructs a valued nodeset from a non-valued one using a default value
	 * for each node
	 * 
	 * @param  nodeset    the non-valued nodeset
	 * @param  value      the default value
	 */
	public ValuedNodeSet(NodeSet nodeset, int value) {
		this(nodeset.getGraph(), nodeset.getMembers());
		for (int idx: this.members)
			setValue(idx, value);
	}
	
	/**
	 * Gets the value corresponding to the given node
	 * 
	 * @param  nodeIndex    index of the node whose value is being set
	 * @return the value itself
	 */
	public int getValue(int nodeIndex) {
		return values.get(nodeIndex);
	}
	
	protected void init() {
		values = new ObjectIntHashMap();
	}
	
	/**
	 * Removes the value associated to the given node
	 * 
	 * @return the value that was associated to the given node or null if
	 *         there was no value associated
	 */
	public int removeValue(int nodeIndex) {
		return values.remove(nodeIndex);
	}
	
	/**
	 * Sets the value corresponding to the given node
	 * 
	 * @param  nodeIndex    index of the node whose value is being set
	 * @param  value        the value itself
	 */
	public void setValue(int nodeIndex, int value) {
		values.add(nodeIndex, value);
	}
}
