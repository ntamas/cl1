package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;

/**
 * Action that closes the given control panel if it is visible
 * 
 * @author tamas
 */
public class CloseControlPanelAction extends AbstractCyAction {
	/**
	 * The control panel managed by this action.
	 */
	private ControlPanel panel;
	

	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public CloseControlPanelAction(ControlPanel panel) {
		super("Close");
		this.panel = panel;
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
		/* Disable actions depending on the control panel */
//		GrowClusterAction.getGlobalInstance().setEnabled(false);
//		AffinityColouringAction.getGlobalInstance().setEnabled(false);
		
		panel.deactivate();
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
