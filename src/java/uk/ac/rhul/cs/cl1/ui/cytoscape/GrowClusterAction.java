package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;
import uk.ac.rhul.cs.cl1.seeding.NodeSetCollectionBasedSeedGenerator;

/**
 * Action that grows a cluster from a given node and selects the
 * grown cluster afterwards.
 * 
 * @author tamas
 */
public class GrowClusterAction extends CytoscapeAction {	
	private static GrowClusterAction globalInstance;
	
	/**
	 * The seed node
	 */
	private Node node = null;
	
	/**
	 * Grows a cluster from the currently selected nodes of the currently selected network
	 * 
	 * This constructor is private; if you need this functionality, use {@link getGlobalInstance()}
	 * instead.
	 */
	private GrowClusterAction() {
		super("Grow cluster from selected nodes");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
		this.setPreferredMenu("Plugins.ClusterONE");
	}
	
	/**
	 * Grows a cluster from the given node
	 * 
	 * @param  node  the node to grow a cluster from.
	 */
	public GrowClusterAction(Node node) {
		super("Grow cluster from this node");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_G);
		this.node = node;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		/* Get a handle to the control panel. If the control panel is not shown yet,
		 * we don't do anything */
		ControlPanel panel = ControlPanel.getShownInstance();
		if (panel == null)
			return;
		
		/* Get the currently selected network view */
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
			
		/* Get the algorithm parameters */
		ClusterONEAlgorithmParameters parameters = panel.getParameters();
		String weightAttr = panel.getWeightAttributeName();
		CyNetwork network = Cytoscape.getCurrentNetwork();
		
		/* Get the Graph representation of the CyNetwork */
		Graph graph = CytoscapePlugin.convertCyNetworkToGraph(network, weightAttr);
		if (graph == null)
			return;
		
		/* Update the algorithm parameters: set the seeding method properly */
		List<Node> nodeMapping = graph.getNodeMapping();
		List<Integer> nodeIndices = new ArrayList<Integer>();
		if (node != null) {
			/* We are using the node given in the constructor */
			nodeIndices.add(nodeMapping.indexOf(node));
			if (nodeIndices.get(0) < 0) {
				CytoscapePlugin.showBugMessage("The selected node does not belong to the selected graph.");
				return;
			}
		} else {
			/* Fetch all the selected nodes from the current view */
			@SuppressWarnings("unchecked") Set<Node> selectedNodes = network.getSelectedNodes();
			for (int idx = 0; idx < nodeMapping.size(); idx++) {
				if (selectedNodes.contains(nodeMapping.get(idx))) {
					nodeIndices.add(idx);
				}
			}
		}
		if (nodeIndices.size() == 0) {
			CytoscapePlugin.showErrorMessage("There are no selected nodes in the current graph.");
			return;
		}
		parameters.setSeedGenerator(new NodeSetCollectionBasedSeedGenerator(new NodeSet(graph, nodeIndices)));
		
		/* Run the algorithm, get the results */
		List<ValuedNodeSet> clusters = CytoscapePlugin.runAlgorithm(graph, parameters, weightAttr);
		if (clusters == null) {
			CytoscapePlugin.showBugMessage("No results returned from ClusterONE.");
			return;
		}
		
		if (clusters.size() == 0) {
			CytoscapePlugin.showInformationMessage("There is no cluster associated to this node.\nMaybe it was filtered out by the size or the density filter?");
			return;
		}
		
		if (clusters.size() > 1) {
			CytoscapePlugin.showBugMessage("More than one cluster was returned from ClusterONE.");
			return;
		}
		
		NodeSet cluster = clusters.get(0);
		List<Node> selectedNodes = new ArrayList<Node>();
		
		for (int idx: cluster) {
			selectedNodes.add(nodeMapping.get(idx));
		}
		
		network.unselectAllNodes();
		network.unselectAllEdges();
		network.setSelectedNodeState(selectedNodes, true);
		network.setSelectedEdgeState(network.getConnectingEdges(selectedNodes), true);
		
		if (networkView != null)
			networkView.redrawGraph(false, true);
	}

	/**
	 * Returns a "global" instance of this action that grows a cluster from the selected nodes
	 * of the current view.
	 * 
	 * There should be at most one global instance at any given time. If the global instance was
	 * not used before, this method will construct it
	 * 
	 * @return   the global instance of this action
	 */
	synchronized public static GrowClusterAction getGlobalInstance() {
		if (globalInstance == null)
			globalInstance = new GrowClusterAction();
		return globalInstance;
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
}
