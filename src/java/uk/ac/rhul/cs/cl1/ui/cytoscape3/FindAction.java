package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;

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
		super("Find clusters of this node");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_F);
		this.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Select all the clusters corresponding to the selected nodes in the result panel");
		
		ClusterONECytoscapeApp app = resultViewer.getCytoscapeApp();
		URL url = app.getResource(app.getResourcePathName() + "/find.png");
		if (url != null) {
			this.putValue(AbstractAction.SMALL_ICON, new ImageIcon(url));
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		CyNetwork network = this.resultViewer.getNetwork();
		if (network == null)
			return;
		
		List<CyNode> selectedNodes = CyNetworkUtil.getSelectedNodes(network);
		List<CyNode> nodeMapping = this.resultViewer.getNodeMapping();
		HashSet<Integer> selectedIndices = new HashSet<Integer>();
		for (CyNode node: selectedNodes) {
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
