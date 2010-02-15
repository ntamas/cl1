package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.GraphPerspective;
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

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
		List<Node> selectedNodes = this.resultViewer.getSelectedCytoscapeNodeSet();
		CyNetwork network = this.resultViewer.getNetwork();
		
		if (network == null) {
			JOptionPane.showMessageDialog(this.resultViewer,
					"Cannot create network representation for the cluster:\n"+
					"The parent network has already been destroyed.",
					"Cannot create network", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int[] indices = new int[selectedNodes.size()];
		int i = 0;
		for (Node node: selectedNodes)
			indices[i++] = node.getRootGraphIndex();
		
		GraphPerspective graphPerspective = network.createGraphPerspective(indices);
		CyNetwork newNetwork = Cytoscape.createNetwork(
				graphPerspective.getNodeIndicesArray(),
				graphPerspective.getEdgeIndicesArray(),
				"Cluster "+(this.resultViewer.getSelectedNodeSetIndex()+1), network);
		CyNetworkView newNetworkView = Cytoscape.createNetworkView(newNetwork);
		newNetworkView.fitContent();
	}
}
