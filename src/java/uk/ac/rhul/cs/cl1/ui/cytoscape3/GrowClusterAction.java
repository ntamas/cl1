package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.seeding.NodeSetCollectionBasedSeedGenerator;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.Result;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.ResultListener;

public class GrowClusterAction extends AbstractClusterONEAction
	implements ResultListener {

	/**
	 * The seed node
	 */
	private CyNode node = null;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	/**
	 * Constructs the action
	 */
	public GrowClusterAction(ClusterONECytoscapeApp app) {
		super(app, "Grow cluster from selected nodes",
				ActionEnableSupport.ENABLE_FOR_SELECTED_NODES);
		installInMenu();
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
	}
	
	/**
	 * Grows a cluster from the given node
	 * 
	 * @param  node  the node to grow a cluster from.
	 */
	public GrowClusterAction(ClusterONECytoscapeApp app, CyNode node) {
		super(app, "Grow cluster from this node");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_G);
		this.node = node;
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

	public void actionPerformed(ActionEvent event) {
		/* Get a handle to the control panel. If the control panel is not shown yet,
		 * we don't do anything */
		ControlPanel panel = app.getControlPanel();
		if (panel == null) {
			app.showErrorMessage("You must open the Control Panel before starting " +
					ClusterONE.applicationName);
			return;
		}
		
		/* Get the currently selected network view */
		CyNetworkView networkView = app.getCurrentNetworkView();
			
		/* Get the algorithm parameters */
		ClusterONEAlgorithmParameters parameters = panel.getParameters();
		String weightAttr = panel.getWeightAttributeName();
		CyNetwork network = app.getCurrentNetwork();
		
		/* Get the Graph representation of the CyNetwork */
		Graph graph = app.convertCyNetworkToGraph(network, weightAttr);
		if (graph == null)
			return;
		
		/* Update the algorithm parameters: set the seeding method properly */
		List<CyNode> nodeMapping = graph.getNodeMapping();
		List<Integer> nodeIndices = new ArrayList<Integer>();
		if (node != null) {
			/* We are using the node given in the constructor */
			nodeIndices.add(nodeMapping.indexOf(node));
			if (nodeIndices.get(0) < 0) {
				app.showBugMessage("The selected node does not belong to the selected graph.");
				return;
			}
		} else {
			/* Fetch all the selected nodes from the current view */
			List<CyNode> selectedNodes = CyNetworkUtil.getSelectedNodes(network);
			for (int idx = 0; idx < nodeMapping.size(); idx++) {
				if (selectedNodes.contains(nodeMapping.get(idx))) {
					nodeIndices.add(idx);
				}
			}
		}
		if (nodeIndices.size() == 0) {
			app.showErrorMessage("There are no selected nodes in the current graph.");
			return;
		}
		parameters.setSeedGenerator(new NodeSetCollectionBasedSeedGenerator(new NodeSet(graph, nodeIndices)));
		
		/* Run the algorithm, wait for the results */
		app.runAlgorithm(networkView, parameters, weightAttr, this);
	}
	
	// --------------------------------------------------------------------
	// ResultListener methods
	// --------------------------------------------------------------------

	public void resultsCalculated(ClusterONECytoscapeTask task, Result result) {
		if (result == null || result.clusters == null) {
			app.showBugMessage("No results returned from ClusterONE.");
			return;
		}
		
		if (result.clusters.size() == 0) {
			app.showInformationMessage("There is no cluster associated to the selection.\n" +
		"Maybe it was filtered out by the size or the density filter?");
			return;
		}
		
		if (result.clusters.size() > 1) {
			app.showBugMessage("More than one cluster was returned from ClusterONE.");
			return;
		}
		
		NodeSet cluster = result.clusters.get(0);
		List<CyNode> selectedNodes = new ArrayList<CyNode>();
		
		for (int idx: cluster) {
			selectedNodes.add(result.nodeMapping.get(idx));
		}
		
		CyNetworkView networkView = task.getNetworkView();
		CyNetwork network = networkView != null ? networkView.getModel() : null;
		
		if (network != null) {
			CyNetworkUtil.unselectAllNodes(network);
			CyNetworkUtil.unselectAllEdges(network);
			CyNetworkUtil.setSelectedState(network, selectedNodes, true);
			CyNetworkUtil.setSelectedState(network,
				CyNetworkUtil.getConnectingEdges(network, selectedNodes), true);
		}
		
		if (networkView != null) {
			networkView.updateView();
		}
	}

	// --------------------------------------------------------------------
	// Private methods
	// -------------------------------------------------------------------
}
