package uk.ac.rhul.cs.cl1;

/**
 * Calculates the log-likelihood of a nodeset according to a simple blockmodel.
 * 
 * This goal function assumes the following:
 * 
 * <ul>
 * <li>The edges in the nodeset are distributed randomly; i.e. each edge is present
 * with probability p1, where p1 is estimated from the nodeset itself according to
 * the maximum-likelihood principle.</li>
 * <li>The edges from the nodeset to other parts of the network are also distributed
 * randomly; i.e. each edge is present with probability p2, where p2 is estimated from
 * the surroundings of the nodeset according to the maximum-likelihood principle.</li>
 * <li>A randomly chosen node in the graph is included in the nodeset with probability
 * p, which is again estimated from the size of the graph and the size of the nodeset
 * using the maximum-likelihood principle.</li>
 * </ul>
 * 
 * The model goes as follows: each node in the graph is selected by probability p.
 * Internal edges between the selected nodes are generated randomly and independently
 * from each other by probability p1 for each node pair. Boundary edges from the
 * selected nodes to the rest of the network are also generated randomly and independently
 * from each other by probability p2 for each internal-external node pair. The value of the
 * goal function of our nodeset is then the log-likelihood of the event that the above model
 * generates exactly our nodeset.
 * 
 * This goal function is good to assess the quality of a cluster based on a strict,
 * model-based criterion, but it is not good to drive a greedy growth process, since
 * it is almost always "safer" to contract small communities completely. It would require
 * a fairly large seed to reach the point where the expansion of the community yields
 * better log-likelihood scores than contraction.
 * 
 * @author tamas
 *
 */
public class LogLikelihoodFunction implements QualityFunction {
	/**
	 * Calculates the entropy of a binary random variable.
	 * 
	 * @param x  the probability of the event that the random variable is 1.
	 * @return   the entropy of the variable.
	 */
	private static double binaryEntropy(double x) {
		if (x == 0 || x == 1)
			return 0.0;
		return x * Math.log(x) + (1-x) * Math.log(1-x);
	}
	
	public double calculate(NodeSet nodeSet) {
		if (nodeSet.isEmpty())
			return Double.NEGATIVE_INFINITY;
		
		double n = nodeSet.size();
		double N = nodeSet.getGraph().getNodeCount();
		double maxInternalEdges = n * (n-1) / 2;
		double maxBoundaryEdges = n * (N-n);
		double p = n / N;
		double p1 = (n == 1) ? 0 : nodeSet.getTotalInternalEdgeWeight() / maxInternalEdges;
		double p2 = (n == N) ? 0 : nodeSet.getTotalBoundaryEdgeWeight() / maxBoundaryEdges;
		double result;
		
		result  = N * binaryEntropy(p);
		result += maxInternalEdges * binaryEntropy(p1);
		result += maxBoundaryEdges * binaryEntropy(p2);
		
		return result;
	}

	public double getAdditionAffinity(MutableNodeSet nodeSet, int index) {
		// TODO more efficient implementation
		if (nodeSet.contains(index))
			return calculate(nodeSet);
		
		MutableNodeSet copy = new MutableNodeSet(nodeSet);
		copy.add(index);
		return calculate(copy);
	}

	public double getRemovalAffinity(MutableNodeSet nodeSet, int index) {
		// TODO more efficient implementation
		if (!nodeSet.contains(index))
			return calculate(nodeSet);
		
		MutableNodeSet copy = new MutableNodeSet(nodeSet);
		copy.remove(index);
		return calculate(copy);
	}
}
