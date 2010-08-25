package uk.ac.rhul.cs.cl1;

/**
 * Common interface for classes that calculate some quality measure
 * of a {@link NodeSet}.
 * 
 * @author tamas
 */
public interface QualityFunction {
	/**
	 * Calculates the quality measure of the nodeset.
	 * 
	 * @param   nodeSet  the nodeset for which we need the quality measure
	 * @return  the calculated quality measure
	 */
	public double calculate(NodeSet nodeSet);
	
	/**
	 * Returns the addition affinity of a node to a mutable nodeset
	 * 
	 * The addition affinity of an external node is defined as the value of the
	 * quality function when the nodeset is augmented by the given node.
	 * 
	 * @param   nodeSet     the nodeset being checked
	 * @param   index   the index of the node being added to the nodeset
	 * @precondition   the node is not in the set
	 */
	public double getAdditionAffinity(MutableNodeSet nodeSet, int index);
	
	/**
	 * Returns the removal affinity of a node to this nodeset
	 * 
	 * The removal affinity of an internal node is defined as the value of the quality
	 * function when the node is removed from the nodeset.
	 * 
	 * @param   nodeSet     the nodeset being checked
	 * @param   index   the index of the node
	 * @precondition    the node is already in the set
	 */
	public double getRemovalAffinity(MutableNodeSet nodeSet, int index);
}
