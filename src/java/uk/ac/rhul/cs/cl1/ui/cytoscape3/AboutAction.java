package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

/**
 * An action that shows the ClusterONE About dialog.
 * 
 * @author tamas
 */
public class AboutAction extends AbstractClusterONEAction {

	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public AboutAction(CytoscapeApp app) {
		super(app, "About...");
		installInMenu();
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_A);
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
		AboutDialog dlg = new AboutDialog(app);
		dlg.setVisible(true);
	}

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
