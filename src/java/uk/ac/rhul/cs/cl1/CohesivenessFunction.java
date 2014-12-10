package uk.ac.rhul.cs.cl1;

/**
 * Calculates the cohesiveness measure of a nodeset.
 * 
 * The cohesiveness of a {@link NodeSet} is defined as the total internal weight
 * of the nodeset divided by the sum of the total internal and boundary weight
 * of the nodeset.
 * 
 * Optionally, the user may also specify a penalty value for each node. If the
 * penalty is nonzero, it is assumed that each node in the nodeset (and also the
 * ones to be added) has an extra boundary weight equal to the value of the
 * penalty. This can be used to account for situations when the dataset is noisy;
 * in this case, the fact that an external boundary node of a nodeset has a single
 * boundary edge towards the nodeset and no other (completely external) edges does
 * not necessarily mean that the node should belong to the nodeset; it may happen
 * that the node has more external connections, which are not present in the dataset
 * due to noise. Therefore, the addition of such nodes is feasible only if the
 * connections to the nodeset are strong enough to counterbalance the effect of the
 * penalty.
 * 
 * @author tamas
 */
public class CohesivenessFunction implements QualityFunction {
	/**
	 * Penalty value associated with each node.
	 */
	double penalty = 0.0;
	
	/**
	 * Constructs a new cohesiveness function instance with no penalty.
	 */
	public CohesivenessFunction() {
		this(0.0);
	}
	
	/**
	 * Constructs a new cohesiveness function instance.
	 * 
	 * @param  penalty  the penalty value associated with each internal node.
	 */
	public CohesivenessFunction(double penalty) {
		this.penalty = penalty;
	}
	
	/**
	 * Calculates the cohesiveness of a nodeset.
	 * 
	 * @param   nodeSet  the nodeset for which we need the cohesiveness
	 * @return  the cohesiveness
	 */
	public double calculate(NodeSet nodeSet) {
		double num, den;
		
		num = nodeSet.totalInternalEdgeWeight;
		den = nodeSet.totalInternalEdgeWeight + nodeSet.totalBoundaryEdgeWeight +
		      nodeSet.size() * penalty;
		
		return num/den;
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
				(nodeSet.totalWeights[index] - nodeSet.inWeights[index]) +
				(nodeSet.size() + 1) * penalty;
		
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
				(nodeSet.totalWeights[index] - nodeSet.inWeights[index]) +
				(nodeSet.size() - 1) * penalty;

		return num/den;
	}
}
