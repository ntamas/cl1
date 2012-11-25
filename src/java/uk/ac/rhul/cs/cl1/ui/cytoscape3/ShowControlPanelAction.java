package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;

/**
 * An action that shows or hides the ClusterONE control panel in Cytoscape
 * 
 * @author tamas
 */
public class ShowControlPanelAction extends AbstractCyAction {
	
	/**
	 * The control panel that is activated by this action.
	 */
	private final ControlPanel controlPanel;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	/**
	 * Constructs the action
	 */
	public ShowControlPanelAction(ControlPanel panel) {
		super("Start");
		setPreferredMenu("Apps.ClusterONE");
		
		this.controlPanel = panel;
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
	 * Adds the ClusterONE control panel to the Cytoscape control panel
	 * 
	 * If the ClusterONE control panel is already open, no new control panel
	 * will be added, the existing one will be selected instead.
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (controlPanel != null) {
			controlPanel.activate();
		}
		
		/* Enable actions depending on the existence of a ControlPanel */
//		GrowClusterAction.getGlobalInstance().setEnabled(true);
//		AffinityColouringAction.getGlobalInstance().setEnabled(true);
	}
}
