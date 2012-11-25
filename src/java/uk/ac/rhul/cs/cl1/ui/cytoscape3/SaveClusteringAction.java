package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Action that saves the names of the members of all clusters
 * in the result viewer to a file on the disk.
 * 
 * @author ntamas
 */
public class SaveClusteringAction extends SaveClusterAction {
	public SaveClusteringAction(CytoscapeResultViewerPanel panel) {
		super(panel);
		this.putValue(AbstractAction.NAME, "Save clustering...");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_V);
		this.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Save the clustering to a file");
		
		ClusterONECytoscapeApp app = resultViewer.getCytoscapeApp();
		URL url = app.getResource(app.getResourcePathName() + "/save.png");
		if (url != null) {
			this.putValue(AbstractAction.SMALL_ICON, new ImageIcon(url));
		}
	}

	/**
	 * Returns the title of the dialog box where the destination file will be selected
	 */
	@Override
	protected String getFileDialogTitle() {
		return "Select the file to save the clustering to";
	}
	
	/**
	 * Returns the list of nodes that should be saved
	 */
	@Override
	protected List<NodeSet> getNodeListsToBeSaved() {
		return this.resultViewer.getAllNodeSets();
	}
	
}
