package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.view.model.CyNetworkView;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.Result;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.ResultListener;

/**
 * An action that starts ClusterONE by showing it on the Cytoscape control panel.
 * 
 * @author tamas
 */
public class StartAction extends AbstractClusterONEAction implements ResultListener,
	SetCurrentNetworkListener {

	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	/**
	 * Constructs the action
	 */
	public StartAction(ClusterONECytoscapeApp app) {
		super(app, "Start");
		installInMenu();
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
		this.setEnabled(app.getCurrentNetwork() != null);
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
		CyNetworkView networkView = app.getCurrentNetworkView();
		
		if (networkView == null || networkView.getModel() == null ||
				networkView.getModel().getNodeCount() == 0) {
			app.showErrorMessage("You must select a non-empty network before starting " +
					ClusterONE.applicationName);
			return;
		}
		
		/* Get a handle to the control panel */
		ControlPanel panel = app.getControlPanel();
		if (panel == null)
			return;
		
		/* Run the algorithm */
		app.runAlgorithm(networkView, panel.getParameters(), panel.getWeightAttributeName(), this);
	}
	
	public void resultsCalculated(ClusterONECytoscapeTask task, Result result) {
		if (result == null || result.clusters == null)
			return;
		
		// TODO: set the attributes for the visual style
		
		/* Ensure that the ClusterONE visual styles are registered */
//		VisualStyleManager.ensureVizMapperStylesRegistered(false);
		
		/* Set one of the ClusterONE visual styles */
//		Cytoscape.getVisualMappingManager().setVisualStyle(VisualStyleManager.VISUAL_STYLE_BY_STATUS);
//		Cytoscape.getVisualMappingManager().applyAppearances();
//		networkView.redrawGraph(false, true);
		
		/* Add the results panel */
		CytoscapeResultViewerPanel resultsPanel = new CytoscapeResultViewerPanel(app,
				task.getNetworkView());
		resultsPanel.setResult(result);
		resultsPanel.addToCytoscapeResultPanel();
	}

	// --------------------------------------------------------------------
	// Event handlers
	// --------------------------------------------------------------------

	public void handleEvent(SetCurrentNetworkEvent event) {
		this.setEnabled(event.getNetwork() != null);
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
