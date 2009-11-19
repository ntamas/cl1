package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.view.NodeView;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import uk.ac.rhul.cs.cl1.ClusterONE;

import ding.view.NodeContextMenuListener;

/**
 * Action invoked when the user opens the context menu of a node in a
 * Cytoscape network view.
 * 
 * @author tamas
 */
public class NodeContextMenuAction implements NodeContextMenuListener {
	/**
	 * Adds the necessary context menu items to the given popup menu
	 * 
	 * @param   node   the node the user clicked on
	 * @param   menu   the popup menu that will be extended
	 */
	public void addNodeContextMenuItems(NodeView node, JPopupMenu menu) {
		JMenu submenu = this.getPopupMenu(node);
		
		if (submenu != null)
			menu.add(submenu);
	}
	
	/**
	 * Constructs the popup menu that will be injected into the popup menu of the node
	 * 
	 * @param   nodeView  the node view that was clicked on in the graph view
	 */
	protected JMenu getPopupMenu(NodeView nodeView) {
		/* Get a handle to the control panel. If the control panel is not shown yet,
		 * we don't put anything in the context menu */
		ControlPanel panel = ControlPanel.getShownInstance();
		if (panel == null)
			return null;
		
		JMenu menu = new JMenu(ClusterONE.applicationName);
		menu.add(new GrowClusterAction(nodeView.getNode()));
		menu.add(GrowClusterAction.getGlobalInstance());
		
		return menu;
	}
}
