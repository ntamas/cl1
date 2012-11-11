package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import uk.ac.rhul.cs.utils.StringUtils;

/**
 * Action that copies the names of the members of the selected cluster
 * to the clipboard, separated by spaces
 * 
 * @author ntamas
 */
public class CopyClusterToClipboardAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;
	
	/**
	 * Whether this system supports a clipboard or not
	 */
	private boolean hasClipboard = true;

	/**
	 * Constructor
	 */
	public CopyClusterToClipboardAction(CytoscapeResultViewerPanel panel) {
		super("Copy to clipboard");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_C);
		
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.hasClipboard = (systemClipboard != null);
		this.setEnabled(true);
	}
	
	/**
	 * Sets the enabled state of this action, but only if the system supports clipboards
	 */
	public void setEnabled(boolean enabled) {
		if (!this.hasClipboard)
			super.setEnabled(false);
		super.setEnabled(enabled);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (!hasClipboard)
			return;
		
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (systemClipboard == null)
			return;
		
		CyNetwork network = this.resultViewer.getNetwork();
		List<List<CyNode>> selectedNodeLists = this.resultViewer.getSelectedCytoscapeNodeSets();
		List<String> lines = new ArrayList<String>();
		
		for (List<CyNode> selectedNodes: selectedNodeLists) {
			List<String> nodeNames = new ArrayList<String>();
			for (CyNode node: selectedNodes) {
				nodeNames.add(CyNodeUtil.getName(network, node));
			}
			lines.add(StringUtils.join(nodeNames.iterator(), ' '));
		}
		
		systemClipboard.setContents(new StringSelection(
				StringUtils.join(lines.iterator(), '\n')
		), null);
	}
}
