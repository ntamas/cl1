package uk.ac.rhul.cs.cl1.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Action that removes the selected clusters from the result list
 * 
 * @author ntamas
 */
public class RemoveClusterFromResultAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected ResultViewerPanel resultViewer;
	
	/**
	 * Constructor
	 */
	public RemoveClusterFromResultAction(ResultViewerPanel panel) {
		super("Remove");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_R);
		this.setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		NodeSetTableModel model = this.resultViewer.getTableModel();
		
		for (NodeSet nodeSet: this.resultViewer.getSelectedNodeSets())
			model.remove(nodeSet);
	}
}
