package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import javax.swing.JMenu;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import uk.ac.rhul.cs.cl1.ClusterONE;

/**
 * Menu factory that creates the ClusterONE-specific menu item for a
 * Cytoscape node view.
 * 
 * @author ntamas
 */
public class NodeContextMenuFactory implements CyNodeViewContextMenuFactory {

	private ClusterONECytoscapeApp app;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public NodeContextMenuFactory(ClusterONECytoscapeApp app) {
		this.app = app;
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

	public CyMenuItem createMenuItem(CyNetworkView networkView,
			View<CyNode> nodeView) {
		JMenu menu = new JMenu(ClusterONE.applicationName);
		menu.add(new GrowClusterAction(app, nodeView.getModel()));
		menu.add(app.getGrowClusterAction());
		
		app.getGrowClusterAction().updateEnableState();
		
		return new CyMenuItem(menu, 1);
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
