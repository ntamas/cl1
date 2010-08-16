package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;

/**
 * Action that removes the selected clusters from the result list
 * 
 * @author ntamas
 */
public class RemoveClusterFromResultAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;
	
	/**
	 * Constructor
	 */
	public RemoveClusterFromResultAction(CytoscapeResultViewerPanel panel) {
		super("Remove");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_R);
		this.setEnabled(true);
	}
	
	/**
	 * Returns the list of nodes that should be saved
	 */
	protected List<List<Node>> getNodeListsToBeSaved() {
		return this.resultViewer.getSelectedCytoscapeNodeSets();
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO
	}
}
