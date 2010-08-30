package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * Saves the selected clusters as CyGroups.
 * 
 * @author ntamas
 */
public class SaveClusterAsCyGroupAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;
	
	/**
	 * Constructor
	 */
	public SaveClusterAsCyGroupAction(CytoscapeResultViewerPanel panel) {
		super("Convert to Cytoscape group...");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_G);
		this.setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		List<List<Node>> selected =
			this.resultViewer.getSelectedCytoscapeNodeSets();
		
		if (selected.isEmpty()) {
			CytoscapePlugin.showErrorMessage("No clusters selected.");
			return;
		}
		
		if (selected.size() == 1) {
			String name =
				JOptionPane.showInputDialog(resultViewer,
						"Please enter the name of the Cytoscape group:",
						"");
			if (name == null)
				return;
			
			// TODO
		} else {
			String nameTemplate =
				JOptionPane.showInputDialog(resultViewer,
						"Please enter the name template of the Cytoscape group.\n\n"+
						"#{index} will be replaced with a unique numeric index.",
						"Cluster ONE group #{index}");
			if (nameTemplate == null)
				return;
			
			// TODO
		}
	}
}
