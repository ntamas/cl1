package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The about dialog box for ClusterONE, tailored to Cytoscape 3.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class AboutDialog extends uk.ac.rhul.cs.cl1.ui.AboutDialog {
	ClusterONECytoscapeApp app;
	
	public AboutDialog(ClusterONECytoscapeApp app, boolean modal) {
		super(app.getCySwingApplication().getJFrame(), modal);
		this.app = app;
	}

	public AboutDialog(ClusterONECytoscapeApp app) {
		super(app.getCySwingApplication().getJFrame());
		this.app = app;
	}
	
	/**
	 * Returns the text of the About box as a resource stream from the OSGi bundle.
	 * 
	 * @throws IOException 
	 */
	@Override
	protected InputStream getAboutTextResourceAsStream() throws IOException {
		return app.getResourceAsStream(app.getResourcePathName() + "/about_dialog.txt");
	}
	
	/**
	 * Returns the URL of the resource containing the ClusterONE logo from the OSGI bundle.
	 */
	@Override
	protected URL getLogoResourceURL() {
		return app.getResource(app.getResourcePathName() + "/logo.png");
	}
}
