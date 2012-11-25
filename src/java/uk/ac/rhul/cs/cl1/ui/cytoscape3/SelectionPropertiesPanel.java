package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.List;

import org.cytoscape.application.events.SetCurrentNetworkViewEvent;
import org.cytoscape.application.events.SetCurrentNetworkViewListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.view.model.CyNetworkView;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetPropertiesPanel;

import com.sosnoski.util.array.IntArray;

/**
 * Extended {@link NodeSetPropertiesPanel} that always shows the properties
 * of the current Cytoscape selection
 * 
 * @author tamas
 *
 */
public class SelectionPropertiesPanel extends NodeSetPropertiesPanel
		implements SetCurrentNetworkViewListener, RowsSetListener {
	
	/** The application in which this panel lives */
	private ClusterONECytoscapeApp app;
	
	/** The control panel to which this panel belongs */
	private ControlPanel panel;
	
	/** The network view we are watching for selection changes */
	CyNetworkView watchedNetworkView = null;
	
	/**
	 * Constructs the panel with the given control panel as parent.
	 * 
	 * @param  panel  the control panel where the selection properties
	 *                panel will be added to.
	 */
	public SelectionPropertiesPanel(ControlPanel panel) {
		super();
		this.app = panel.app;
		this.panel = panel;
		
		if (panel != null) {
			this.setQualityFunction(panel.getParameters().getQualityFunction());
		}
		
		/* If there is a network view in focus right now, watch that */
		setWatchedNetworkView(app.getCurrentNetworkView());
		
		/* Listen to network view focus changes */
		app.registerService(this, SetCurrentNetworkViewListener.class);
		app.registerService(this, RowsSetListener.class);
		
		updateNodeSetFromSelection();
	}
	
	/**
	 * Method triggered when a network view gained focus
	 */
	public void handleEvent(SetCurrentNetworkViewEvent event) {
		setWatchedNetworkView(event.getNetworkView());
		updateNodeSetFromSelection();
	}
	
	/**
	 * Method triggered when a row was set in the network.
	 */
	public void handleEvent(RowsSetEvent event) {
		if (watchedNetworkView == null)
			return;
		
		CyNetwork network = watchedNetworkView.getModel();
		if (network == null)
			return;
					
		if (event.getSource() != network.getDefaultNodeTable())
			return;
		
		if (!event.containsColumn(CyNetwork.SELECTED))
			return;
		
		updateNodeSetFromSelection();
	}
	
	/** Sets the network being watched by the panel */
	private void setWatchedNetworkView(CyNetworkView networkView) {
		if (watchedNetworkView == networkView)
			return;
		
		watchedNetworkView = networkView;
	}

	/**
	 * Updates the nodeset shown in the panel from the selection
	 */
	public void updateNodeSetFromSelection() {
		if (watchedNetworkView == null)
			return;
		
		CyNetwork network = watchedNetworkView.getModel();
		if (network == null)
			return;
		
		List<CyNode> selectedNodes = CyNetworkUtil.getSelectedNodes(network);
		
		Graph graph = null;
		
		graph = app.convertCyNetworkToGraph(network, panel.getWeightAttributeName());
		if (graph == null) {
			this.setNodeSet(null);
			return;
		}
		
		/* For each node in the graph, check whether it is selected and build the NodeSet */
		IntArray indices = new IntArray();
		int i = 0;
		for (CyNode node: graph.getNodeMapping()) {
			if (selectedNodes.contains(node))
				indices.add(i);
			i++;
		}
		this.setNodeSet(new NodeSet(graph, indices.toArray()));
	}
}
