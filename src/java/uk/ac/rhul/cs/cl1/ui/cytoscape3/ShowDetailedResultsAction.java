package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.net.URL;

public class ShowDetailedResultsAction extends
		uk.ac.rhul.cs.cl1.ui.ShowDetailedResultsAction {
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public ShowDetailedResultsAction(CytoscapeResultViewerPanel panel) {
		super(panel);
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

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

	protected URL getIconURL() {
		ClusterONECytoscapeApp app = ((CytoscapeResultViewerPanel)resultViewer).getCytoscapeApp();
		return app.getResource(app.getResourcePathName() + "/details.png");
	}
}
