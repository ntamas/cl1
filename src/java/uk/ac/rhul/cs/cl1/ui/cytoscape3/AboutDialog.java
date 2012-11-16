package uk.ac.rhul.cs.cl1.ui.cytoscape3;

/**
 * The about dialog box for ClusterONE, tailored to Cytoscape 3.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class AboutDialog extends uk.ac.rhul.cs.cl1.ui.AboutDialog {
	public AboutDialog(ClusterONECytoscapeApp app, boolean modal) {
		super(app.getCySwingApplication().getJFrame(), modal);
	}

	public AboutDialog(ClusterONECytoscapeApp app) {
		super(app.getCySwingApplication().getJFrame());
	}
}
