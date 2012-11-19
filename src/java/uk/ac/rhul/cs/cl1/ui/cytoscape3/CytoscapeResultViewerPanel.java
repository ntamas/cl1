package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.EmptyIcon;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;
import uk.ac.rhul.cs.cl1.ui.PopupMenuTrigger;
import uk.ac.rhul.cs.cl1.ui.RemoveClusterFromResultAction;
import uk.ac.rhul.cs.cl1.ui.ResultViewerPanel;
import uk.ac.rhul.cs.cl1.ui.ShowDetailedResultsAction;
import uk.ac.rhul.cs.cl1.ui.cytoscape3.ClusterONECytoscapeTask.Result;

/**
 * Result viewer panel with some added functionality to ensure better integration
 * with Cytoscape
 * 
 * @author tamas
 */
public class CytoscapeResultViewerPanel extends ResultViewerPanel implements
	CytoPanelComponent, ListSelectionListener {
	/**
	 * The ClusterONE Cytoscape application in which this panel lives.
	 */
	private ClusterONECytoscapeApp app = null;
	
	/**
	 * Serial number of this result viewer panel.
	 */
	private Integer serialNumber = null;
	
	/**
	 * Last used serial number of result viewer panels.
	 * 
	 * This is used to assign unique numbers to each result panel in Cytoscape
	 */
	static private int lastUsedSerialNumber = 1;
	
	/**
	 * Mapping from node IDs to real Cytoscape {@link CyNode} objects
	 */
	protected List<CyNode> nodeMapping;

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
	 * The "Remove" element of the popup menu
	 */
	protected AbstractAction removeClusterAction;
	
	/**
	 * The "Convert to Cytoscape group..." element of the popup menu
	 */
	protected AbstractAction saveClusterAsCyGroupAction;
	
	/**
	 * The "Show detailed results" action
	 */
	protected ShowDetailedResultsAction showDetailedResultsAction;
	
	// --------------------------------------------------------------------
	// Constructor
	// --------------------------------------------------------------------

	/**
	 * Creates a result viewer panel associated to the given {@link CyNetwork}
	 * and {@link CyNetworkView}
	 * 
	 * It will be assumed that the results shown in this panel were generated
	 * from the given network, and the given view will be used to update the
	 * selection based on the current nodeset in the table.
	 * 
	 * @param app           reference to the global CytoscapeApp object
	 * @param networkView   a network view that will be used to show the clusters
	 */
	public CytoscapeResultViewerPanel(ClusterONECytoscapeApp app, CyNetworkView networkView) {
		super();
		this.app = app;
		
		initializeClusterPopup();
		
		this.networkRef = new WeakReference<CyNetwork>(networkView.getModel());
		this.networkViewRef = new WeakReference<CyNetworkView>(networkView);
		
		/* Listen to table selection changes */
		this.table.getSelectionModel().addListSelectionListener(this);
		
		/* Listen to double click events on the table */
		this.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					extractClusterAction.actionPerformed(null);
				}
			}
		});
		
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
		
		/* Fix the icon of the "Show detailed results" action */
		showDetailedResultsAction.setIconURL(
				app.getResource(app.getResourcePathName() + "/details.png"));
	}

	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------
	
	/**
	 * Returns the ClusterONE app in which this result viewer panel lives.
	 */
	public ClusterONECytoscapeApp getCytoscapeApp() {
		return app;
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
	 * Retrieves the mapping from integer node IDs to real Cytoscape {@link CyNode} objects
	 */
	public List<CyNode> getNodeMapping() {
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
	public List<CyNode> getSelectedCytoscapeNodeSet() {
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
	public List<List<CyNode>> getSelectedCytoscapeNodeSets() {
		List<List<CyNode>> result = new ArrayList<List<CyNode>>();
		for (NodeSet selectedNodeSet: this.getSelectedNodeSets()) {
			result.add(this.convertIterableToCytoscapeNodeList(selectedNodeSet));			
		}
		return result;
	}
	
	/**
	 * Retrieves the set of Cytoscape nodes associated to all {@link NodeSet} instances
	 * in this result viewer.
	 */
	public List<List<CyNode>> getAllCytoscapeNodeSets() {
		NodeSetTableModel model = this.getTableModel();
		int numRows = model.getRowCount();
		
		List<List<CyNode>> result = new ArrayList<List<CyNode>>();
		for (int i = 0; i < numRows; i++) {
			result.add(this.convertIterableToCytoscapeNodeList(model.getNodeSetByIndex(i)));			
		}
		return result;
	}

	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	/**
	 * Adds the result panel to Cytoscape's designated result panel area
	 */
	public void addToCytoscapeResultPanel() {
		if (serialNumber == null) {
			serialNumber = lastUsedSerialNumber;
			lastUsedSerialNumber++;
		}
		
		/* Register the panel */
		app.registerService(this, CytoPanelComponent.class);
		
		/* Ensure that the panel is visible */
		CytoPanel cytoPanel = app.getCySwingApplication().getCytoPanel(getCytoPanelName());
		if (cytoPanel.getState() == CytoPanelState.HIDE) {
			cytoPanel.setState(CytoPanelState.DOCK);
		}
		setVisible(true);
		
		/* Activate the panel */
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(getComponent()));
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	protected Icon constructProgressIcon() {
		URL url = app.getResource(app.getResourcePathName() + "/wait.jpg");
		return (url != null) ? new ImageIcon(url) : new EmptyIcon(32, 32);
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	protected ShowDetailedResultsAction constructShowDetailedResultsAction() {
		showDetailedResultsAction = super.constructShowDetailedResultsAction();
		return showDetailedResultsAction;
	};
	
	/**
	 * Converts an integer iterable yielding node IDs to a list of Cytoscape nodes
	 * 
	 * As {@link NodeSet}s are iterable, this method works with {@link NodeSet}s directly.
	 */
	protected List<CyNode> convertIterableToCytoscapeNodeList(Iterable<Integer> iterable) {
		List<CyNode> result = new ArrayList<CyNode>();
		for (int idx: iterable) {
			CyNode node = this.nodeMapping.get(idx);
			if (node == null)
				continue;
			result.add(node);
		}
		return result;
	}
	
	/**
	 * Closes the result panel.
	 */
	public void close() {
		app.unregisterService(this, CytoPanelComponent.class);
		
		/* Ensure that the panel is hidden if this was the last one */
		CytoPanel cytoPanel = app.getCySwingApplication().getCytoPanel(getCytoPanelName());
		if (cytoPanel.getCytoPanelComponentCount() == 0) {
			cytoPanel.setState(CytoPanelState.HIDE);
		}
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
		
		removeClusterAction = new RemoveClusterFromResultAction(this);
		removeClusterAction.setEnabled(false);
		clusterPopup.add(removeClusterAction);
		
		/*
		clusterPopup.addSeparator();
		
		saveClusterAsCyGroupAction = new SaveClusterAsCyGroupAction(this);
		saveClusterAsCyGroupAction.setEnabled(false);
		clusterPopup.add(saveClusterAsCyGroupAction);
		*/
	}

	/**
	 * Selects the given set of nodes in the associated network and redraws its view.
	 */
	public void selectNodes(Collection<? extends CyNode> nodes) {
		selectNodes(nodes, true);
	}
	
	/**
	 * Selects the given set of nodes in the associated network and optionally
	 * redraws its view.
	 */
	public void selectNodes(Collection<? extends CyNode> nodes, boolean redraw) {
		CyNetwork network = this.getNetwork();
		if (network == null)
			return;
		
		// Unselect all nodes and edges
		CyNetworkUtil.unselectAllNodes(network);
		CyNetworkUtil.unselectAllEdges(network);
		
		// Select the nodes of the cluster and the connecting edges
		CyNetworkUtil.setSelectedState(network, nodes, true);
		CyNetworkUtil.setSelectedState(network, CyNetworkUtil.getConnectingEdges(network, nodes), true);
		
		// Redraw the network
		getNetworkView().updateView();
	}
	
	/**
	 * Sets the mapping from integer node IDs to real Cytoscape {@link Node} objects
	 */
	public void setNodeMapping(List<CyNode> mapping) {
		this.nodeMapping = mapping;
	}
	
	/**
	 * Sets the results to be shown in this panel.
	 */
	public void setResult(Result result) {
		this.setNodeSets(result.clusters);
		this.setNodeMapping(result.nodeMapping);
	}
	
	/**
	 * Method called when the table selection changes
	 * @param event   event describing how the selection changed
	 */
	public void valueChanged(ListSelectionEvent event) {
		CyNetwork network = this.getNetwork();
		
		if (network == null) {
			copyToClipboardAction.setEnabled(false);
			extractClusterAction.setEnabled(false);
			saveClusterAction.setEnabled(false);
			return;
		}
		
		List<CyNode> nodes = this.getSelectedCytoscapeNodeSet();
		selectNodes(nodes);
		
		boolean enabled = nodes.size() > 0;
		extractClusterAction.setEnabled(enabled);
		copyToClipboardAction.setEnabled(enabled);
		saveClusterAction.setEnabled(enabled);
		removeClusterAction.setEnabled(enabled);
		// saveClusterAsCyGroupAction.setEnabled(enabled);
	}


	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// CloseAction class
	// --------------------------------------------------------------------
	
	class CloseAction extends AbstractAction {
		CytoscapeResultViewerPanel panel;
		
		public CloseAction(CytoscapeResultViewerPanel panel) {
			super("Close");
			this.panel = panel;
			this.putValue(AbstractAction.SHORT_DESCRIPTION,
					"Close this result panel");
			
			URL url = app.getResource(app.getResourcePathName() + "/close.png");
			if (url != null) {
				this.putValue(AbstractAction.SMALL_ICON, new ImageIcon(url));
			}
		}
		
		public void actionPerformed(ActionEvent event) {
			panel.close();
		}
	}

	// --------------------------------------------------------------------
	// CytoPanelComponent implementation
	// --------------------------------------------------------------------
	
	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	public Icon getIcon() {
		return null;
	}

	public String getTitle() {
		return ClusterONE.applicationName + " result " + serialNumber;
	}
}
