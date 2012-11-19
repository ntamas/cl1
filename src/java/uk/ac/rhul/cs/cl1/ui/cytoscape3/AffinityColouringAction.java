package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.vizmap.VisualMappingManager;

import uk.ac.rhul.cs.cl1.ClusterONE;

/**
 * Action that colours each node in the network according to their affinity to
 * the selected nodes.
 * 
 * @author tamas
 */
public class AffinityColouringAction extends AbstractClusterONEAction {
	/**
	 * Constructor
	 */
	public AffinityColouringAction(ClusterONECytoscapeApp app) {
		super(app, "Color nodes by affinity", ActionEnableSupport.ENABLE_FOR_SELECTED_NODES);
		this.putValue(AbstractAction.LONG_DESCRIPTION,
				"Color the nodes of the selected network by their affinity to "+
				"the currently selected set of nodes as a cluster");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_A);
		installInMenu();
	}
	
	/**
	 * Colours each node in the network according to their affinity to the selected nodes.
	 * 
	 * This is achieved by setting an appropriate node attribute on the nodes and selecting
	 * the corresponding visual style.
	 */
	public void actionPerformed(ActionEvent event) {
		/* Get a handle to the control panel. If the control panel is not shown yet,
		 * we don't do anything */
		ControlPanel panel = app.getControlPanel();
		if (panel == null) {
			app.showErrorMessage("You must open the Control Panel before starting " +
					ClusterONE.applicationName);
			return;
		}
		
		/* Get the current network */
		CyNetwork network = app.getCurrentNetwork();
		String weightAttr = panel.getWeightAttributeName();
		
		/* Get the Graph representation of the CyNetwork */
		Graph graph = app.convertCyNetworkToGraph(network, weightAttr);
		if (graph == null)
			return;
		
		/* Collect the indices of the selected nodes into a list */
		List<Integer> indices = graph.getMappedNodeIndices(CyNetworkUtil.getSelectedNodes(network));
		
		/* Loop over all nodes and calculate the affinities */
		app.setAffinityAttributes(network, graph, indices);
		
		/* Ensure that the ClusterONE visual styles are registered */
		VisualStyleManager vsm = app.getVisualStyleManager();
		vsm.ensureVizMapperStylesRegistered();
		vsm.updateAffinityStyleRange(network);
		
		/* Set one of the ClusterONE visual styles */
		app.getService(VisualMappingManager.class).setCurrentVisualStyle(
				vsm.getColorNodesByAffinityVisualStyle());
		
		if (app.getCurrentNetworkView() != null) {
			app.getCurrentNetworkView().updateView();
		}
	}
}
