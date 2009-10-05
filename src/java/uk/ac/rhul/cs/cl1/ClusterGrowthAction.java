package uk.ac.rhul.cs.cl1;

import java.util.Arrays;

/**
 * An action that can be taken while growing a cluster according to some rules.
 * 
 * The possible actions are:
 * 
 * - add some new nodes to the cluster
 * - remove some nodes from the cluster
 * - declare the cluster as the final solution
 * 
 * @author ntamas
 *
 */
public class ClusterGrowthAction {
	public enum Type {
		ADD, REMOVE, TERMINATE;
	}
	
	/**
	 * The type of the action to be taken
	 */
	protected Type type = null;
	
	/**
	 * Optional set of nodes associated with the action
	 */
	protected int[] nodes = null;
	
	/**
	 * Locally cached instance of a ClusterGrowthAction storing Type.TERMINATE
	 */
	private static ClusterGrowthAction cachedTerminateAction = new ClusterGrowthAction(Type.TERMINATE);
	
	/**
	 * Creates an instance corresponding to the given action type
	 */
	protected ClusterGrowthAction(Type type) {
		this.type = type;
	}
	
	/**
	 * Creates an instance representing the addition of a single node
	 * @param index  the node to be added
	 */
	public static ClusterGrowthAction addition(int index) {
		ClusterGrowthAction result = new ClusterGrowthAction(Type.ADD);
		result.nodes = new int[1];
		result.nodes[0] = index;
		return result;
	}

	/**
	 * Creates an instance representing the addition of several nodes
	 * @param    array    indices of the nodes to be added
	 */
	public static ClusterGrowthAction addition(int[] array) {
		ClusterGrowthAction result = new ClusterGrowthAction(Type.ADD);
		result.nodes = Arrays.copyOf(array, array.length);
		return result;
	}
	
	/**
	 * Creates an instance representing the removal of a single node
	 * @param index  the node to be added
	 */
	public static ClusterGrowthAction removal(int index) {
		ClusterGrowthAction result = new ClusterGrowthAction(Type.REMOVE);
		result.nodes = new int[1];
		result.nodes[0] = index;
		return result;
	}
	
	/**
	 * Creates an instance representing the removal of several nodes
	 * @param    array    indices of the nodes to be removed
	 */
	public static ClusterGrowthAction removal(int[] array) {
		ClusterGrowthAction result = new ClusterGrowthAction(Type.REMOVE);
		result.nodes = Arrays.copyOf(array, array.length);
		return result;
	}
	
	/**
	 * Creates an instance representing the termination of the growth process
	 */
	public static ClusterGrowthAction terminate() {
		return cachedTerminateAction;
	}

	/**
	 * Returns the type of this action
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Executes the action on the given mutable node set
	 * @param nodeSet   the nodeset to modify
	 */
	public void executeOn(MutableNodeSet nodeSet) {
		if (this.type == Type.ADD) {
			for (int node: this.nodes)
				nodeSet.add(node);
			return;
		}
		
		if (this.type == Type.REMOVE) {
			for (int node: this.nodes)
				nodeSet.remove(node);
			return;
		}
	}
}
