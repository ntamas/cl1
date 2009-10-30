package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.PopupMenuTrigger;
import uk.ac.rhul.cs.cl1.ui.ResultViewerPanel;

/**
 * Result viewer panel with some added functionality to ensure better integration
 * with Cytoscape
 * 
 * @author tamas
 */
public class CytoscapeResultViewerPanel extends ResultViewerPanel implements
	ListSelectionListener, ActionListener {
	/**
	 * Mapping from node IDs to real Cytoscape {@link Node} objects
	 */
	protected List<Node> nodeMapping;

	/** Reference to the original Cytoscape network from which the results were calculated */
	protected WeakReference<CyNetwork> networkRef;
	
	/** Reference to a Cytoscape network view that will be used to highlight nodes in the selected nodeset */
	protected WeakReference<CyNetworkView> networkViewRef;
	
	/**
	 * The popup menu that comes up when right clicking on a cluster
	 */
	protected JPopupMenu clusterPopup;
	
	/**
	 * The "Copy to clipboard" element of the popup menu
	 */
	protected AbstractAction copyToClipboardAction;
	
	/**
	 * The "Extract cluster" element of the popup menu
	 */
	protected AbstractAction extractClusterAction;
	
	/**
	 * Creates a result viewer panel associated to the given {@link CyNetwork}
	 * and {@link CyNetworkView}
	 * 
	 * It will be assumed that the results shown in this panel were generated
	 * from the given network, and the given view will be used to update the
	 * selection based on the current nodeset in the table.
	 * 
	 * @param network       a network from which the results were generated
	 * @param networkView   a network view that will be used to show the clusters
	 */
	public CytoscapeResultViewerPanel(CyNetwork network, CyNetworkView networkView) {
		super();
		initializeClusterPopup();
		
		this.networkRef = new WeakReference<CyNetwork>(network);
		this.networkViewRef = new WeakReference<CyNetworkView>(networkView);
		
		/* Listen to table selection changes */
		this.table.getSelectionModel().addListSelectionListener(this);
		
		/* Add popup menu to the cluster selection table */
		this.table.addMouseListener(new PopupMenuTrigger(clusterPopup));
		
		/* Add the bottom buttons */
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Retrieves the Cytoscape network associated to this panel
	 */
	public CyNetwork getNetwork() {
		if (networkRef == null)
			return null;
		return networkRef.get();
	}
	
	/**
	 * Retrieves the Cytoscape network view associated to this panel
	 */
	public CyNetworkView getNetworkView() {
		if (networkViewRef == null)
			return null;
		return networkViewRef.get();
	}
	
	/**
	 * Retrieves the set of Cytoscape nodes associated to the selected {@link NodeSet}.
	 * 
	 * If nothing is selected in the table, an empty list will be returned.
	 */
	public List<Node> getSelectedCytoscapeNodeSet() {
		ArrayList<Node> result = new ArrayList<Node>();
		NodeSet selectedNodeSet = this.getSelectedNodeSet();
		
		if (selectedNodeSet == null)
			return result;
		
		for (Integer idx: selectedNodeSet) {
			Node node = nodeMapping.get(idx);
			if (node == null)
				continue;         // node deleted in the meanwhile
			result.add(node);
		}
		return result;
	}
	
	/**
	 * Initializes the cluster popup menu
	 */
	private void initializeClusterPopup() {
		clusterPopup = new JPopupMenu();
		
		copyToClipboardAction = new CopyClusterToClipboardAction(this);
		copyToClipboardAction.setEnabled(false);
		clusterPopup.add(copyToClipboardAction);
		
		extractClusterAction = new ExtractClusterAction(this);
		extractClusterAction.setEnabled(false);
		clusterPopup.add(extractClusterAction);
	}

	/**
	 * Sets the mapping from integer node IDs to real Cytoscape {@link Node} objects
	 */
	public void setNodeMapping(List<Node> mapping) {
		this.nodeMapping = mapping;
	}
	
	/**
	 * Method called when the table selection changes
	 * @param event   event describing how the selection changed
	 */
	public void valueChanged(ListSelectionEvent event) {
		CyNetwork network = this.getNetwork();
		CyNetworkView networkView = this.getNetworkView();
		
		if (network == null) {
			copyToClipboardAction.setEnabled(false);
			extractClusterAction.setEnabled(false);
			return;
		}
		
		List<Node> nodes = this.getSelectedCytoscapeNodeSet();
		
		network.unselectAllNodes();
		network.unselectAllEdges();
		network.setSelectedNodeState(nodes, true);
		networkView.redrawGraph(false, true);
		
		extractClusterAction.setEnabled(nodes.size() > 0);
		copyToClipboardAction.setEnabled(nodes.size() > 0);
	}

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		
		if (action == null)
			return;
		
		if (action.equals("close")) {
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			cytoPanel.remove(this);
			if (cytoPanel.getCytoPanelComponentCount() == 0) {
				cytoPanel.setState(CytoPanelState.HIDE);
			}
		}
	}
}
