package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
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
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;
import uk.ac.rhul.cs.cl1.ui.PopupMenuTrigger;
import uk.ac.rhul.cs.cl1.ui.ResultViewerPanel;

/**
 * Result viewer panel with some added functionality to ensure better integration
 * with Cytoscape
 * 
 * @author tamas
 */
public class CytoscapeResultViewerPanel extends ResultViewerPanel implements
	ListSelectionListener {
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
	 * The "Extract selected cluster" element of the popup menu
	 */
	protected AbstractAction extractClusterAction;
	
	/**
	 * The "Save selected cluster..." element of the popup menu
	 */
	protected AbstractAction saveClusterAction;
	
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
		/* JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JButton closeButton = new JButton(new CloseAction(this));
		buttonPanel.add(closeButton);
		this.add(buttonPanel, BorderLayout.SOUTH); */
		
		this.addAction(new FindAction(this));
		this.addAction(new SaveClusteringAction(this));
		this.addAction(new CloseAction(this));
	}
	
	/**
	 * Converts an integer iterable yielding node IDs to a list of Cytoscape nodes
	 * 
	 * As {@link NodeSet}s are iterable, this method works with {@link NodeSet}s directly.
	 */
	protected List<Node> convertIterableToCytoscapeNodeList(Iterable<Integer> iterable) {
		List<Node> result = new ArrayList<Node>();
		for (int idx: iterable) {
			Node node = this.nodeMapping.get(idx);
			if (node == null)
				continue;
			result.add(node);
		}
		return result;
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
	 * Retrieves the mapping from integer node IDs to real Cytoscape {@link Node} objects
	 */
	public List<Node> getNodeMapping() {
		return this.nodeMapping;
	}
	
	/**
	 * Retrieves the set of Cytoscape nodes associated to the selected {@link NodeSet}.
	 * 
	 * If multiple {@link NodeSet}s are selected, the corresponding Cytoscape nodes will be
	 * merged into a single set.
	 * 
	 * If nothing is selected in the table, an empty set will be returned.
	 */
	public List<Node> getSelectedCytoscapeNodeSet() {
		Set<Integer> selectedIndices = new TreeSet<Integer>();
		
		/* Take the union of all indices. This step is necessary because CyNodes are not hashable */
		for (NodeSet selectedNodeSet: this.getSelectedNodeSets()) {
			for (Integer idx: selectedNodeSet) {
				selectedIndices.add(idx);
			}
		}
		
		/* Convert indices to CyNodes */
		return this.convertIterableToCytoscapeNodeList(selectedIndices);
	}
	
	/**
	 * Retrieves the set of Cytoscape nodes associated to the selected {@link NodeSet}.
	 * 
	 * If multiple {@link NodeSet}s are selected, the corresponding Cytoscape nodes will be
	 * returned as individual lists.
	 * 
	 * If nothing is selected in the table, an empty list will be returned.
	 */
	public List<List<Node>> getSelectedCytoscapeNodeSets() {
		List<List<Node>> result = new ArrayList<List<Node>>();
		for (NodeSet selectedNodeSet: this.getSelectedNodeSets()) {
			result.add(this.convertIterableToCytoscapeNodeList(selectedNodeSet));			
		}
		return result;
	}
	
	/**
	 * Retrieves the set of Cytoscape nodes associated to all {@link NodeSet} instances
	 * in this result viewer.
	 */
	public List<List<Node>> getAllCytoscapeNodeSets() {
		NodeSetTableModel model = this.getTableModel();
		int numRows = model.getRowCount();
		
		List<List<Node>> result = new ArrayList<List<Node>>();
		for (int i = 0; i < numRows; i++) {
			result.add(this.convertIterableToCytoscapeNodeList(model.getNodeSetByIndex(i)));			
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
		
		saveClusterAction = new SaveClusterAction(this);
		saveClusterAction.setEnabled(false);
		clusterPopup.add(saveClusterAction);
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
			saveClusterAction.setEnabled(false);
			return;
		}
		
		List<Node> nodes = this.getSelectedCytoscapeNodeSet();
		
		network.unselectAllNodes();
		network.unselectAllEdges();
		network.setSelectedNodeState(nodes, true);
		network.setSelectedEdgeState(network.getConnectingEdges(nodes), true);
		networkView.redrawGraph(false, true);
		
		extractClusterAction.setEnabled(nodes.size() > 0);
		copyToClipboardAction.setEnabled(nodes.size() > 0);
		saveClusterAction.setEnabled(nodes.size() > 0);
	}

	class CloseAction extends AbstractAction {
		CytoscapeResultViewerPanel panel;
		
		public CloseAction(CytoscapeResultViewerPanel panel) {
			super("Close");
			this.panel = panel;
			this.putValue(AbstractAction.SMALL_ICON,
					new ImageIcon(this.getClass().getResource("../../resources/close.png"))
			);
			this.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Close this result panel");
		}
		
		public void actionPerformed(ActionEvent event) {
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			cytoPanel.remove(panel);
			if (cytoPanel.getCytoPanelComponentCount() == 0) {
				cytoPanel.setState(CytoPanelState.HIDE);
			}
		}
	}
}
