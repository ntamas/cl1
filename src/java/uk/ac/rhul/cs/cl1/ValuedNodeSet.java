package uk.ac.rhul.cs.cl1;

import java.util.Collection;

import uk.ac.rhul.cs.graph.Graph;

import com.sosnoski.util.hashmap.ObjectIntHashMap;

/**
 * Nodeset with a value associated to each node
 * 
 * This class can be used to attach arbitrary attributes to the nodes of a
 * {@link NodeSet}. ClusterONE uses this to keep track of the number of
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
	public ValuedNodeSet(Graph graph, int... members) {
		super(graph, members);
		init();
	}
	
	/**
	 * Constructs a valued nodeset from a non-valued one using a default
	 * value of 1 for each node.
	 * 
	 * @param  nodeset    the non-valued nodeset
	 */
	public ValuedNodeSet(NodeSet nodeset) {
		this(nodeset, 1);
		if (nodeset instanceof ValuedNodeSet) {
			this.values = (ObjectIntHashMap)((ValuedNodeSet) nodeset).values.clone();
		}
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
	 * @return the value itself or 0 if the node has no associated value
	 */
	public int getValue(int nodeIndex) {
		return this.getValue(nodeIndex, 0);
	}
	
	/**
	 * Gets the value corresponding to the given node
	 * 
	 * @param  nodeIndex    index of the node whose value is being set
	 * @param  defaultValue default value to return when the node has no value
	 * @return the value itself
	 */
	public int getValue(int nodeIndex, int defaultValue) {
		int result = values.get(nodeIndex);
		if (result == ObjectIntHashMap.DEFAULT_NOT_FOUND)
			result = defaultValue;
		return result;
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
