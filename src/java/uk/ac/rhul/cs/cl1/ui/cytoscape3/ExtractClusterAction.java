package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.create.NewNetworkSelectedNodesAndEdgesTaskFatory;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * Action called when a cluster must be extracted as a separate network
 * 
 * @author tamas
 */
public class ExtractClusterAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;

	/**
	 * Constructs the action
	 */
	public ExtractClusterAction(CytoscapeResultViewerPanel panel) {
		super("Extract selected cluster(s)");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_E);
	}
	
	public void actionPerformed(ActionEvent event) {
		List<CyNode> selectedNodes = this.resultViewer.getSelectedCytoscapeNodeSet();
		CyNetwork network = this.resultViewer.getNetwork();
		ClusterONECytoscapeApp app = this.resultViewer.getCytoscapeApp();
		
		if (network == null) {
			app.showErrorMessage("Cannot create network representation for the cluster:\n" +
					"The parent network has already been destroyed.");
			return;
		}
		
		resultViewer.selectNodes(selectedNodes);
		
		NewNetworkSelectedNodesAndEdgesTaskFatory taskFactory =
				this.resultViewer.getCytoscapeApp().getService(NewNetworkSelectedNodesAndEdgesTaskFatory.class);
		if (taskFactory == null) {
			app.showBugMessage("Cannot create network representation for the cluster:\n" +
					"New network creation factory is not registered.");
			return;
		}
		
		DialogTaskManager taskManager =
				this.resultViewer.getCytoscapeApp().getService(DialogTaskManager.class);
		if (taskManager == null) {
			app.showBugMessage("Cannot create network representation for the cluster:\n" +
					"Dialog task manager is not registered.");
			return;
		}
		
		taskManager.execute(taskFactory.createTaskIterator(network));
	}
}
