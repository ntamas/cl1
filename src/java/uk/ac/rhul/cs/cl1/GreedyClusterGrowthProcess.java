package uk.ac.rhul.cs.cl1;

/**
 * Greedy growth process that chooses a locally optimal step to improve some goal function
 * @author ntamas
 */
public class GreedyClusterGrowthProcess extends ClusterGrowthProcess {
	/**
	 * Creates a new greedy growth process that operates on the given nodeset
	 */
	public GreedyClusterGrowthProcess(MutableNodeSet nodeSet) {
		super(nodeSet);
	}

	/**
	 * Determines the suggested action by examining all possibilities and choosing
	 * the one that increases the goal function the most
	 * 
	 * @return  the locally optimal action
	 */
	@Override
	public ClusterGrowthAction getSuggestedAction() {
		return ClusterGrowthAction.terminate();
	}
}
