package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.WeakHashMap;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

import uk.ac.rhul.cs.utils.ObjectUtils;
import uk.ac.rhul.cs.utils.Pair;
import uk.ac.rhul.cs.utils.UniqueIDGenerator;

/**
 * Class holding ClusterONE's own representations of Cytoscape networks.
 * 
 * This class encapsulates a weak hash map mapping {@link CyNetwork} instances to
 * {@link Graph} instances.
 * 
 * Whenever a Cytoscape network is processed by ClusterONE, it will check whether
 * a corresponding ClusterONE representation already exists in this hash map. If
 * so, the network representation is not created again. The hash map does not stop
 * Java from freeing the memory associated to the Cytoscape network when the network
 * is discarded by Cytoscape.
 */
public class CyNetworkCache implements NetworkAboutToBeDestroyedListener {
	/** Internal weak hash map used as a storage area */
	WeakHashMap<CyNetwork, Pair<String, Graph> > storage =
		new WeakHashMap<CyNetwork, Pair<String, Graph> >();
	
	/** The ClusterONE plugin app within Cytoscape that owns this cache */
	private CytoscapeApp app;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public CyNetworkCache(CytoscapeApp app) {
		this.app = app;
		
		app.registerService(this, NetworkAboutToBeDestroyedListener.class);
		// TODO: need to listen for events when new nodes/edges are added to a network,
		// nodes/edges are removed from a network, or attributes are modified
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	/**
	 * Returns the ClusterONE representation of the given {@link CyNetwork}.
	 * 
	 * If the {@link CyNetwork} was not seen before by this instance, a network
	 * listener will be registered on the {@link CyNetwork} to ensure that the
	 * cache entry is invalidated when the {@link CyNetwork} changes.
	 * 
	 * @param  network     the network being converted
	 * @param  weightAttr  name of the attribute that will be used for edge
	 *                     weights. null means that the network is unweighted.
	 * @throws NonNumericAttributeException  when a non-numeric weight attribute 
	 *         was used
	 */
	public Graph convertCyNetworkToGraph(CyNetwork network, String weightAttr)
			throws NonNumericAttributeException {
		Pair<String, Graph> attrNameAndGraph = storage.get(network);
		
		if (attrNameAndGraph != null &&
			ObjectUtils.equals(weightAttr, attrNameAndGraph.getLeft()))
			return attrNameAndGraph.getRight();
		
		Graph graph = new Graph(network);
		UniqueIDGenerator<CyNode> nodeIdGen = new UniqueIDGenerator<CyNode>(graph);
		Double weight;
		
		/* Import all the edges into our graph */
		try {
			for (CyEdge edge: network.getEdgeList()) {
				int src = nodeIdGen.get(edge.getSource());
				int dest = nodeIdGen.get(edge.getTarget());
				if (src == dest)
					continue;
				
				if (weightAttr == null) {
					weight = null;
				} else {
					CyRow row = network.getRow(edge);
					weight = row.get(weightAttr, Double.class, 1.0);
				}
				if (weight == null)
					weight = 1.0;
				
				graph.createEdge(src, dest, weight);
			}
		} catch (ClassCastException ex) {
			throw new NonNumericAttributeException(weightAttr);
		}
		
		graph.setNodeMapping(nodeIdGen.getReversedList());
		
		this.storage.put(network, Pair.create(weightAttr, graph));
		
		return graph;
	}
	
	/**
	 * Returns the ClusterONE representation of the given {@link CyNetwork}.
	 * 
	 * The network will be created using the currently selected edge weight
	 * attribute from the control panel. If the control panel is hidden,
	 * the result is null.
	 * 
	 * @param  network     the network being converted
	 * @throws NonNumericAttributeException  when a non-numeric weight attribute 
	 *         was used
	 */
	public Graph convertCyNetworkToGraph(CyNetwork network)
			throws NonNumericAttributeException {
		ControlPanel panel = app.getControlPanel();
		
		if (panel == null)
			return convertCyNetworkToGraph(network, null);
		
		return convertCyNetworkToGraph(network, panel.getWeightAttributeName());
	}
	
	/**
	 * Invalidates the cached representation of the given {@link CyNetwork}
	 */
	public void invalidate(CyNetwork network) {
		if (network != null) {
			this.storage.remove(network);
		}
	}
	
	// --------------------------------------------------------------------
	// Event handlers
	// --------------------------------------------------------------------

	/**
	 * Called when a network is destroyed in Cytoscape.
	 */
	public void handleEvent(NetworkAboutToBeDestroyedEvent event) {
		invalidate(event.getNetwork());
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
