package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;

import uk.ac.rhul.cs.cl1.ClusterONE;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * An action that shows or hides the Cluster ONE control panel in Cytoscape
 * 
 * @author tamas
 */
public class ShowControlPanelAction extends CytoscapeAction {
	/**
	 * Constructs the action
	 */
	public ShowControlPanelAction() {
		super("Start");
		setPreferredMenu("Plugins.Cluster ONE");
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
	

	/**
	 * Adds the Cluster ONE control panel to the Cytoscape control panel
	 * 
	 * If the Cluster ONE control panel is already open, no new control panel
	 * will be added, the existing one will be selected instead.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		
		/* Ensure that the panel is visible */
		if (cytoPanel.getState() == CytoPanelState.HIDE)
			cytoPanel.setState(CytoPanelState.DOCK);
		
		ControlPanel c = ControlPanel.getShownInstance();
		if (c != null) {
			cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(c));
			return;
		}
		
		ControlPanel panel = new ControlPanel();
		cytoPanel.add(ClusterONE.applicationName, panel);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(panel));
	}
}
