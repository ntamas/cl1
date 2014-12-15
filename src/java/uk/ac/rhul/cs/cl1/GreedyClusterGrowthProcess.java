package uk.ac.rhul.cs.cl1;

import java.util.Arrays;
import java.util.Set;

import com.sosnoski.util.array.IntArray;
import uk.ac.rhul.cs.graph.TarjanCutVertexFinder;

/**
 * Greedy growth process that chooses a locally optimal step to improve some goal function
 * @author ntamas
 */
public class GreedyClusterGrowthProcess extends ClusterGrowthProcess {
	/**
	 * Quality function used to assess the suitability of clusters
	 */
	protected QualityFunction qualityFunction;
	
	/**
	 * Density limit that is enforced while growing the cluster
	 */
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
	 * Creates a new greedy growth process that operates on the given nodeset
	 * and uses the given quality function.
	 * 
	 * @param  nodeSet      the initial nodeset that will be grown
	 * @param  minDensity   minimum density that should be maintained while growing
	 * @param  qualityFunc  the quality function being used
	 */
	public GreedyClusterGrowthProcess(MutableNodeSet nodeSet, double minDensity,
			QualityFunction qualityFunc) {
		super(nodeSet);
		this.setMinDensity(minDensity);
		this.setQualityFunction(qualityFunc);
		initialSeeds = nodeSet.freeze();
	}
	

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
	 * @return whether it is allowed to remove seed nodes during contraction
	 */
	public boolean isKeepInitialSeeds() {
		return keepInitialSeeds;
	}
	
	/**
	 * Sets whether it is allowed to remove seed nodes during contraction
	 * 
	 * @param keepInitialSeeds  true if seed nodes are always kept within the cluster,
	 *                          false otherwise
	 */
	public void setKeepInitialSeeds(boolean keepInitialSeeds) {
		this.keepInitialSeeds = keepInitialSeeds;
	}
	
	/**
	 * Returns the minimum density that must be maintained while growing the cluster
	 * @return the minimum density
	 */
	public double getMinDensity() {
		return minDensity;
	}

	/**
	 * Returns the quality function that is being used by the growth process
	 * @return the quality function
	 */
	public QualityFunction getQualityFunction() {
		return qualityFunction;
	}
	
	/**
	 * Sets the minimum density that must be maintained while growing the cluster
	 * @param minDensity the minimum density
	 */
	public void setMinDensity(double minDensity) {
		this.minDensity = Math.max(0, minDensity);
	}
	
	/**
	 * Sets the quality function that is being used by the growth process
	 * @param  qualityFunc  the new quality function to be used
	 */
	public void setQualityFunction(QualityFunction qualityFunc) {
		this.qualityFunction = qualityFunc;
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
		final double quality = qualityFunction.calculate(nodeSet);
		double bestAffinity;
		boolean bestIsAddition = true;
		
		int n = nodeSet.size();
		
		if (n == 0)
			return ClusterGrowthAction.terminate();

		double den = (n + 1) * n / 2.0;
		double internalWeightLimit = this.minDensity * den - nodeSet.getTotalInternalEdgeWeight();
		
		/* internalWeightLimit is a strict limit: if a node's connections to the current cluster
		 * are weaker than this weight limit, the node couldn't be added as it would decrease the
		 * density of the cluster under the prescribed limit
		 */
		
		if (debugMode) {
			System.err.println("Current nodeset: " + nodeSet);
			System.err.println("Current quality: " + quality);
		}
		
		/* Try the addition of some nodes */
		bestAffinity = quality;
		for (int node: nodeSet.getExternalBoundaryNodes()) {
			double internalWeight = nodeSet.getTotalAdjacentInternalWeight(node);
			if (n >= 4 && internalWeight < internalWeightLimit)
				continue;
			
			double affinity = qualityFunction.getAdditionAffinity(nodeSet, node);
			if (debugMode) {
				System.err.println("Considering addition of " + node + ", affinity = " + affinity);
			}
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
			Set<Integer> cutVertices = null;

			for (Integer node: nodeSet) {
				// Don't process nodes that were in the initial seed
				if (keepInitialSeeds && initialSeeds.contains(node))
					continue;
				
				double affinity = qualityFunction.getRemovalAffinity(nodeSet, node);
				if (debugMode) {
					System.err.println("Considering removal of " + node + ", affinity = " + affinity);
				}
				
				// The following condition is necessary to avoid cases when a
				// node is repeatedly added and removed from the same set.
				// The addition of 1e-12 counteracts rounding errors.
				if (affinity < quality + 1e-12)
					continue;
				
				if (affinity < bestAffinity)
					continue;
				
				// The following condition is necessary to avoid cases when a
				// tree-like cluster becomes disconnected due to the removal
				// of a non-leaf node
				if (nodeSet.isCutVertex(node))
					continue;

				// Note to self: the above code uses BFS to decide whether the given node is a cut
				// vertex or not. Theoretically, it would be better to use a DFS because a single
				// DFS could provide us all the cut vertices at once. However, in practice it
				// seems to be slower. If you want to try, uncomment the fragment below:
				/*
				if (cutVertices == null) {
					cutVertices = findCutVerticesForNodeSet(nodeSet);
				}
				if (cutVertices.contains(node))
					continue;
				*/

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
		
		if (bestNodes.size() == 0 || bestAffinity == quality) {
			if (debugMode)
				System.err.println("Proposing termination");
			return ClusterGrowthAction.terminate();
		}
		
		if (bestNodes.size() > 1 && onlySingleNode)
			bestNodes.setSize(1);
		
		if (bestIsAddition) {
			if (debugMode)
				System.err.println("Proposing addition of " + Arrays.toString(bestNodes.toArray()));
			return ClusterGrowthAction.addition(bestNodes.toArray());
		} else {
			if (debugMode)
				System.err.println("Proposing removal of " + Arrays.toString(bestNodes.toArray()));
			return ClusterGrowthAction.removal(bestNodes.toArray());
		}
	}

	/**
	 * Finds all the cut vertices in the given nodeset.
	 */
	private Set<Integer> findCutVerticesForNodeSet(NodeSet nodeSet) {
		TarjanCutVertexFinder finder = new TarjanCutVertexFinder();
		finder.setGraph(nodeSet.getGraph());
		finder.restrictToSubgraph(nodeSet.toArray());
		return finder.findCutVertices();
	}
}
