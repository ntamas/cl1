package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

/**
 * Action that colours each node in the network according to their affinity to
 * the selected nodes.
 * 
 * @author tamas
 */
public class AffinityColouringAction extends CytoscapeAction {
	private static AffinityColouringAction globalInstance;

	/**
	 * Constructor
	 */
	public AffinityColouringAction() {
		super("Color nodes by affinity");
		this.putValue(AbstractAction.LONG_DESCRIPTION,
				"Color the nodes of the selected network by their affinity to "+
				"the currently selected set of nodes as a cluster");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_A);
		this.setPreferredMenu("Plugins.ClusterONE");
	}
	
	/**
	 * Colours each node in the network according to their affinity to the selected nodes.
	 * 
	 * This is achieved by setting an appropriate node attribute on the nodes and selecting
	 * the corresponding visual style.
	 */
	public void actionPerformed(ActionEvent event) {
		/* Get the control panel */
		ControlPanel panel = ControlPanel.getShownInstance();
		if (panel == null)
			return;
		
		/* Get the current network */
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String weightAttr = panel.getWeightAttributeName();
		
		/* Get the Graph representation of the CyNetwork */
		Graph graph = CytoscapePlugin.convertCyNetworkToGraph(network, weightAttr);
		if (graph == null)
			return;
		
		/* Collect the indices of the selected nodes into a list */
		@SuppressWarnings("unchecked")
		List<Integer> indices = graph.getMappedNodeIndices(network.getSelectedNodes());
		
		/* Loop over all nodes and calculate the affinities */
		CytoscapePlugin.setAffinityAttributesOnGraph(graph, indices);
	}

	/**
	 * Returns a "global" instance of this action.
	 * 
	 * There should be at most one global instance at any given time. If the global instance was
	 * not used before, this method will construct it
	 * 
	 * @return   the global instance of this action
	 */
	synchronized public static AffinityColouringAction getGlobalInstance() {
		if (globalInstance == null)
			globalInstance = new AffinityColouringAction();
		return globalInstance;
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
}
