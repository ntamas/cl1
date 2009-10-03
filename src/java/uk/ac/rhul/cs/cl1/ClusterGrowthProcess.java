package uk.ac.rhul.cs.cl1;

/**
 * Growth process applied to a mutable nodeset.
 * 
 * A growth process takes a nodeset and tries to evolve it step by step
 * according to some quality function. In every step, the growth process
 * suggests one of the following actions:
 * 
 * - add some new nodes to the set
 * - remove some nodes from the set
 * - declare the current state as an optimal solution.
 * 
 * @author ntamas
 *
 */
public abstract class ClusterGrowthProcess {
	/**
	 * The mutable nodeset on which this growth process is operating
	 */
	MutableNodeSet nodeSet = null;
	
	/**
	 * Creates a new growth process that operates on the given nodeset
	 */
	public ClusterGrowthProcess(MutableNodeSet nodeSet) {
		this.nodeSet = nodeSet;
	}
	
	/**
	 * Examines the current nodeset and suggests an action to be taken
	 * @return the action to be taken
	 */
	public abstract ClusterGrowthAction getSuggestedAction();
	
	/**
	 * Takes a step in the growth process.
	 * 
	 * This method first determines the suggested action in the current
	 * state by calling getSuggestedAction, then performs the action on
	 * the nodeset.
	 * 
	 * @return true if the process should continue, false otherwise
	 */
	public boolean step() {
		ClusterGrowthAction nextAction = this.getSuggestedAction();
		nextAction.executeOn(this.nodeSet);
		return (nextAction.getType() == ClusterGrowthAction.Type.TERMINATE);
	}
}
