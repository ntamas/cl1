package uk.ac.rhul.cs.cl1;

import com.sosnoski.util.array.IntArray;

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
		IntArray bestNodes = new IntArray();
		double quality = nodeSet.getQuality();
		double bestAffinity;
		
		/* Try the addition of some nodes */
		bestAffinity = quality;
		for (Integer node: nodeSet.getExternalBoundaryNodeIterator()) {
			double affinity = nodeSet.getAdditionAffinity(node);
			if (affinity > bestAffinity) {
				bestAffinity = affinity;
				bestNodes.clear();
				bestNodes.add(node);
			} else if (affinity == bestAffinity) {
				bestNodes.add(node);
			}
		}
		
		if (bestNodes.size() > 0)
			return ClusterGrowthAction.addition(bestNodes.toArray());
		
		if (this.isContractionAllowed()) {
			/* No gain can be achieved by adding nodes. Try removing nodes. */
			bestAffinity = quality;
			bestNodes.clear();
			for (Integer node: nodeSet) {
				double affinity = nodeSet.getRemovalAffinity(node);
				if (affinity > bestAffinity) {
					bestAffinity = affinity;
					bestNodes.clear();
					bestNodes.add(node);
				}
			}
			
			if (bestNodes.size() > 0)
				return ClusterGrowthAction.removal(bestNodes.toArray());
		}
		
		return ClusterGrowthAction.terminate();
	}
}
