package uk.ac.rhul.cs.cl1;

import com.sosnoski.util.array.IntArray;

/**
 * Greedy growth process that chooses a locally optimal step to improve some goal function
 * @author ntamas
 */
public class GreedyClusterGrowthProcess extends ClusterGrowthProcess {
	/** Density limit that is enforced while growing the complex */
	protected double minDensity;
	
	/**
	 * Whether to add or remove only a single node in each step if multiple nodes have the same affinity
	 */
	protected boolean onlySingleNode = false;
	
	/**
	 * Whether it is allowed to contract the nodeset during the growth process
	 */
	protected boolean contractionAllowed = true;
	
	/**
	 * Whether the initial seed nodes should always be kept as part of the cluster
	 */
	protected boolean keepInitialSeeds = false;
	
	/**
	 * The set of initial seed nodes
	 */
	private NodeSet initialSeeds = null;
	
	/**
	 * @return whether it is allowed to contract the cluster during the growth process
	 */
	public boolean isContractionAllowed() {
		return contractionAllowed;
	}

	/**
	 * Sets whether it is allowed to contract the cluster during growth
	 * 
	 * @param contractionAllowed  whether it is allowed to contract the cluster during growth
	 */
	public void setContractionAllowed(boolean contractionAllowed) {
		this.contractionAllowed = contractionAllowed;
	}

	/**
	 * Returns the minimum density that must be maintained while growing the cluster
	 * @return the minimum density
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Sets the minimum density that must be maintained while growing the cluster
	 * @param minDensity the minimum density
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = Math.max(0, minDensity);
	}

	/**
	 * Creates a new greedy growth process that operates on the given nodeset
	 */
	public GreedyClusterGrowthProcess(MutableNodeSet nodeSet, double minDensity) {
		super(nodeSet);
		this.setMinDensity(minDensity);
		initialSeeds = nodeSet.freeze();
	}

	/**
	 * Determines the suggested action by examining all possibilities and choosing
	 * the one that increases the goal function the most
	 * 
	 * @return  the locally optimal action
	 */
	@Override
	public ClusterGrowthAction getSuggestedAction() {
		IntArray bestNodes = new IntArray();
		final double quality = nodeSet.getQuality();
		double bestAffinity;
		boolean bestIsAddition = true;
		
		int n = nodeSet.size();
		double den = (n + 1) * n / 2.0;
		double internalWeightLimit = this.minDensity * den - nodeSet.getTotalInternalEdgeWeight();
		
		/* internalWeightLimit is a strict limit: if a node's connections to the current cluster
		 * are weaker than this weight limit, the node couldn't be added as it would decrease the
		 * density of the cluster under the prescribed limit
		 */
		
		/* Try the addition of some nodes */
		bestAffinity = quality;
		for (Integer node: nodeSet.getExternalBoundaryNodeIterator()) {
			double internalWeight = nodeSet.getTotalAdjacentInternalWeight(node);
			if (n >= 4 && internalWeight < internalWeightLimit)
				continue;
			
			double affinity = nodeSet.getAdditionAffinity(node);
			if (affinity > bestAffinity) {
				bestAffinity = affinity;
				bestNodes.clear();
				bestNodes.add(node);
			} else if (affinity == bestAffinity) {
				bestNodes.add(node);
			}
		}
		
		if (this.isContractionAllowed() && this.nodeSet.size() > 1) {
			/* Try removing nodes. Can we do better than adding nodes? */
			// bestAffinity = quality;
			// bestNodes.clear();
			for (Integer node: nodeSet) {
				// Don't process nodes that were in the initial seed
				if (keepInitialSeeds && initialSeeds.contains(node))
					continue;
				
				double affinity = nodeSet.getRemovalAffinity(node);
				
				// The following condition is necessary to avoid cases when a
				// node is repeatedly added and removed from the same set
				if (affinity <= quality)
					continue;
				
				if (affinity < bestAffinity)
					continue;
				
				// The following condition is necessary to avoid cases when a
				// tree-like cluster becomes disconnected due to the removal
				// of a non-leaf node
				if (nodeSet.isCutVertex(node))
					continue;
				
				if (affinity > bestAffinity) {
					bestAffinity = affinity;
					bestNodes.clear();
					bestNodes.add(node);
					bestIsAddition = false;
				} else  {
					/* affinity == bestAffinity */
					if (bestIsAddition) {
						bestNodes.clear();
						bestIsAddition = false;
					}
					bestNodes.add(node);
				}
			}
		}
		
		if (bestNodes.size() == 0 || bestAffinity == quality)
			return ClusterGrowthAction.terminate();
		
		if (bestNodes.size() > 1 && onlySingleNode)
			bestNodes.setSize(1);
		
		if (bestIsAddition)
			return ClusterGrowthAction.addition(bestNodes.toArray());
		else
			return ClusterGrowthAction.removal(bestNodes.toArray());
	}
}
