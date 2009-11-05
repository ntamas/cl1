package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Edge;
import giny.model.Node;

import java.util.Iterator;
import java.util.WeakHashMap;

import uk.ac.rhul.cs.cl1.UniqueIDGenerator;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Class holding Cluster ONE's own representations of Cytoscape networks.
 * 
 * This class encapsulates a weak hash map mapping {@link CyNetwork} instances to
 * {@link Graph} instances.
 * 
 * Whenever a Cytoscape network is processed by Cluster ONE, it will check whether
 * a corresponding Cluster ONE representation already exists in this hash map. If
 * so, the network representation is not created again. The hash map does not stop
 * Java from freeing the memory associated to the Cytoscape network when the network
 * is discarded by Cytoscape.
 * 
 * A desirable behaviour would be that a network listener is registered on all
 * Cytoscape networks that have a corresponding entry in the cache so the cached
 * entry would be invalidated immediately when the network is modified. Unfortunately
 * this does not seem possible with Cytoscape at the moment (2.6.3), therefore the
 * cache entry is invalidated manually before starting a whole clustering process
 * on a network, but not when the local cluster of a given node is explored in
 * the interactive mode.
 */
public class CyNetworkCache {
	/** Internal weak hash map used as a storage area */
	WeakHashMap<CyNetwork, Graph> storage = new WeakHashMap<CyNetwork, Graph>();
	
	/**
	 * Returns the Cluster ONE representation of the given {@link CyNetwork}.
	 * 
	 * If the {@link CyNetwork} was not seen before by this instance, a network
	 * listener will be registered on the {@link CyNetwork} to ensure that the
	 * cache entry is invalidated when the {@link CyNetwork} changes.
	 * 
	 * @throws NonNumericAttributeException  when a non-numeric weight attribute 
	 *         was found in the network
	 */
	@SuppressWarnings("unchecked")
	public Graph convertCyNetworkToGraph(CyNetwork network, String weightAttr)
			throws NonNumericAttributeException {
		Graph graph = storage.get(network);
		
		if (graph != null)
			return graph;
		
		graph = new Graph();
		UniqueIDGenerator<Node> nodeIdGen = new UniqueIDGenerator<Node>(graph);
		CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		Double weight;
		
		/* Import all the edges into our graph */
		try {
			Iterator it = network.edgesIterator();
			while (it.hasNext()) {
				Edge edge = (Edge)it.next();
				int src = nodeIdGen.get(edge.getSource());
				int dest = nodeIdGen.get(edge.getTarget());
				if (src == dest)
					continue;
				
				weight = (weightAttr == null) ? null :
					(Double)edgeAttrs.getAttribute(edge.getIdentifier(), weightAttr);
				if (weight == null)
					weight = 1.0;
				
				graph.createEdge(src, dest, weight);
			}
		} catch (ClassCastException ex) {
			throw new NonNumericAttributeException(weightAttr);
		}
		
		graph.setNodeMapping(nodeIdGen.getReversedList());
		
		this.storage.put(network, graph);
		
		// network.addCyNetworkListener(this);
		
		return graph;
	}
	
	/**
	 * Invalidates the cached representation of the given {@link CyNetwork}
	 */
	public void invalidate(CyNetwork network) {
		this.storage.remove(network);
	}
}
