package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;

import cytoscape.Cytoscape;

/**
 * Action that closes the control panel if it is visible
 * 
 * @author tamas
 */
public class CloseControlPanelAction implements ActionListener {
	public void actionPerformed(ActionEvent event) {
		ControlPanel panel = ControlPanel.getShownInstance();
		if (panel == null)
			return;
		
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).remove(panel);
	}
}
