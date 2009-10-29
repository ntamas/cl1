package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

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
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_A);
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
