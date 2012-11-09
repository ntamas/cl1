package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.CytoscapeApp;
import uk.ac.rhul.cs.utils.Pair;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * An action that starts ClusterONE by showing it on the Cytoscape control panel.
 * 
 * @author tamas
 */
public class StartAction extends CytoscapeAction {
	/**
	 * Constructs the action
	 */
	public StartAction() {
		super("Start");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
		setPreferredMenu(CytoscapeApp.PREFERRED_MENU);
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		if (network == null || network.getNodeCount() == 0) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"You must select a non-empty network before starting " + ClusterONE.applicationName,
					"Error - no network selected",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		/* Get a handle to the control panel */
		ControlPanel panel = ControlPanel.getShownInstance();
		if (panel == null)
			return;
		
		/* Run the algorithm */
		Pair<List<ValuedNodeSet>, List<Node>> results;
		results = CytoscapePlugin.runAlgorithm(network, panel.getParameters(), panel.getWeightAttributeName(), true);
		if (results.getLeft() == null)
			return;
		
		/* Ensure that the ClusterONE visual styles are registered */
		VisualStyleManager.ensureVizMapperStylesRegistered(false);
		
		/* Set one of the ClusterONE visual styles */
		Cytoscape.getVisualMappingManager().setVisualStyle(VisualStyleManager.VISUAL_STYLE_BY_STATUS);
		Cytoscape.getVisualMappingManager().applyAppearances();
		networkView.redrawGraph(false, true);
		
		/* Add the results panel */
		CytoscapeResultViewerPanel resultsPanel = new CytoscapeResultViewerPanel(network, networkView);
		resultsPanel.setNodeSets(results.getLeft());
		resultsPanel.setNodeMapping(results.getRight());
		resultsPanel.addToCytoscapeResultPanel();
	}
}
