package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.cytoscape.application.swing.ActionEnableSupport;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.Result;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.ResultListener;

/**
 * An action that starts the ClusterONE algorithm.
 * 
 * @author tamas
 */
public class StartAction extends AbstractClusterONEAction
implements ResultListener {

	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	/**
	 * Constructs the action
	 */
	public StartAction(ClusterONECytoscapeApp app) {
		super(app, "Start", ActionEnableSupport.ENABLE_FOR_NETWORK_AND_VIEW);
		installInMenu();
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
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
		if (panel == null) {
			app.showErrorMessage("You must open the Control Panel before starting " +
					ClusterONE.applicationName);
			return;
		}
		
		/* Run the algorithm */
		app.runAlgorithm(networkView, panel.getParameters(), panel.getWeightAttributeName(), this);
	}
	
	public void resultsCalculated(ClusterONECytoscapeTask task, Result result) {
		if (result == null || result.clusters == null)
			return;
		
		/* Set the status attributes of the graph */
		result.setStatusAttributes();
		
		/* Ensure that the ClusterONE visual styles are registered */
		VisualStyleManager vsm = app.getVisualStyleManager();
		vsm.ensureVizMapperStylesRegistered();
		
		/* Set one of the ClusterONE visual styles */
		app.getService(VisualMappingManager.class).setCurrentVisualStyle(
				vsm.getColorNodesByStatusVisualStyle());
		
		if (task.getNetworkView() != null) {
			task.getNetworkView().updateView();
			
			/* Add the results panel */
			CytoscapeResultViewerPanel resultsPanel = new CytoscapeResultViewerPanel(app,
					task.getNetworkView());
			resultsPanel.setResult(result);
			resultsPanel.addToCytoscapeResultPanel();
		}
	}

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

	protected void updateEnabledState() {
		this.setEnabled(app.getCurrentNetwork() != null && app.getControlPanel() != null);
	}
	
}
