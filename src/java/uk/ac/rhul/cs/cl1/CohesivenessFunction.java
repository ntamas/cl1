package uk.ac.rhul.cs.cl1;

/**
 * Calculates the cohesiveness measure of a nodeset.
 * 
 * The cohesiveness of a {@link NodeSet} is defined as the total internal weight
 * of the nodeset divided by the sum of the total internal and boundary weight
 * of the nodeset.
 * 
 * @author tamas
 */
public class CohesivenessFunction implements QualityFunction {
	/**
	 * Calculates the cohesiveness of a nodeset.
	 * 
	 * @param   nodeSet  the nodeset for which we need the cohesiveness
	 * @return  the cohesiveness
	 */
	public double calculate(NodeSet nodeSet) {
		return nodeSet.totalInternalEdgeWeight /
		  (nodeSet.totalInternalEdgeWeight + nodeSet.totalBoundaryEdgeWeight);
	}
	
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
	public double getAdditionAffinity(MutableNodeSet nodeSet, int index) {
		double num, den;
		
		num = nodeSet.totalInternalEdgeWeight + nodeSet.inWeights[index];
		den = nodeSet.totalInternalEdgeWeight + nodeSet.totalBoundaryEdgeWeight +
		      nodeSet.outWeights[index];
		
		return num/den;
	}
	
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
	public double getRemovalAffinity(MutableNodeSet nodeSet, int index) {
		double num, den;
		
		num = nodeSet.totalInternalEdgeWeight - nodeSet.inWeights[index];
		den = nodeSet.totalInternalEdgeWeight + nodeSet.totalBoundaryEdgeWeight -
		      nodeSet.outWeights[index];
		
		return num/den;
	}
}
