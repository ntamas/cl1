package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;

import cytoscape.CyNetwork;

/**
 * Action that finds the next cluster in the result list that contains
 * any of the selected nodes.
 */
public class FindAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;

	/**
	 * Constructor
	 */
	public FindAction(CytoscapeResultViewerPanel panel) {
		super("Find clusters of selected node");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_F);
		this.putValue(AbstractAction.SMALL_ICON,
				new ImageIcon(this.getClass().getResource("../../resources/find.png"))
		);
	}
	
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent event) {
		CyNetwork network = this.resultViewer.getNetwork();
		if (network == null)
			return;
		
		Set<Node> selectedNodes = network.getSelectedNodes();
		List<Node> nodeMapping = this.resultViewer.getNodeMapping();
		HashSet<Integer> selectedIndices = new HashSet<Integer>();
		for (Node node: selectedNodes) {
			int pos = nodeMapping.indexOf(node);
			if (pos >= 0)
				selectedIndices.add(pos);
		}
		
		ArrayList<Integer> selectedRowIndices = new ArrayList<Integer>();
		NodeSetTableModel model = this.resultViewer.getTableModel();
		for (int row = 0; row < model.getRowCount(); row++) {
			NodeSet nodeSet = model.getNodeSetByIndex(row);
			if (nodeSet.containsAny(selectedIndices))
				selectedRowIndices.add(row);
		}
		
		resultViewer.setSelectedNodeSetIndices(selectedRowIndices);
	}
}
