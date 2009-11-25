package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * Action that saves the names of the members of all clusters
 * in the result viewer to a file on the disk, separated by spaces, one per line
 * 
 * @author ntamas
 */
public class SaveClusteringAction extends SaveClusterAction {
	public SaveClusteringAction(CytoscapeResultViewerPanel panel) {
		super(panel);
		this.putValue(AbstractAction.NAME, "Save clustering...");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_V);
		this.putValue(AbstractAction.SMALL_ICON,
				new ImageIcon(this.getClass().getResource("../../resources/save.png"))
		);
		this.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Save the clustering to a file");
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
	protected List<List<Node>> getNodeListsToBeSaved() {
		return this.resultViewer.getAllCytoscapeNodeSets();
	}
	
}
