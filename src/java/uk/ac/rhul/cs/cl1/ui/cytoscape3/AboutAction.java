package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import uk.ac.rhul.cs.cl1.ui.AboutDialog;

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
		JFrame parent = app.getCySwingApplication().getJFrame();
		AboutDialog dlg = new AboutDialog(parent);
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
	}

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
