package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;

import uk.ac.rhul.cs.cl1.ui.AboutDialog;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

/**
 * An action that shows the Cluster ONE About dialog.
 * 
 * @author tamas
 */
public class AboutAction extends CytoscapeAction {
	/**
	 * Constructs the action
	 */
	public AboutAction() {
		super("About...");
		setPreferredMenu("Plugins.Cluster ONE");
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		AboutDialog dlg = new AboutDialog(Cytoscape.getDesktop());
		dlg.setVisible(true);
	}

}
