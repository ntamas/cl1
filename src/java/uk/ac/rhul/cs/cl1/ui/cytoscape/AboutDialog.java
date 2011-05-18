package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.Frame;

import cytoscape.Cytoscape;

/**
 * The about dialog box for ClusterONE, tailored to Cytoscape.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class AboutDialog extends uk.ac.rhul.cs.cl1.ui.AboutDialog {
	public AboutDialog(Frame owner, boolean modal) {
		super(owner, modal);
        setLocationRelativeTo(Cytoscape.getDesktop());
	}

	public AboutDialog(Frame owner) {
		super(owner);
        setLocationRelativeTo(Cytoscape.getDesktop());
	}
}
