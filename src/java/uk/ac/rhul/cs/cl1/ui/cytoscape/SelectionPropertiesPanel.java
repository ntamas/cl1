package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import com.sosnoski.util.array.IntArray;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetPropertiesPanel;

/**
 * Extended {@link NodeSetPropertiesPanel} that always shows the properties
 * of the current Cytoscape selection
 * 
 * @author tamas
 *
 */
public class SelectionPropertiesPanel extends NodeSetPropertiesPanel
		implements PropertyChangeListener, GraphViewChangeListener {
	/** The network view we are watching for selection changes */
	CyNetworkView watchedNetworkView = null;
	
	public SelectionPropertiesPanel() {
		super();
		
		/* If there is a network view in focus right now, watch that */
		setWatchedNetworkView(Cytoscape.getCurrentNetworkView());
		
		/* Listen to network view focus changes */
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(
				CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this
		);
	}
	
	/**
	 * Method triggered when a network view gained focus
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (CytoscapeDesktop.NETWORK_VIEW_FOCUSED.equals(e.getPropertyName())) {
			Object newValue = e.getNewValue();
			if (newValue instanceof CyNetworkView)
				setWatchedNetworkView((CyNetworkView)e.getNewValue());
			updateNodeSetFromSelection();
		}
	}
	
	/** Sets the network being watched by the panel */
	private void setWatchedNetworkView(CyNetworkView networkView) {
		if (watchedNetworkView == networkView)
			return;
		
		if (watchedNetworkView != null) {
			/* Unregister ourselves from the network being watched */
			watchedNetworkView.removeGraphViewChangeListener(this);
		}
		watchedNetworkView = networkView;
		if (watchedNetworkView != null) {
			/* Register ourselves on the new network */
			watchedNetworkView.addGraphViewChangeListener(this);
		}
	}

	/**
	 * Updates the nodeset shown in the panel from the selection
	 */
	public void updateNodeSetFromSelection() {
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		
		if (networkView == null)
			return;
		
		/* Start watching this network if we are not watching it yet */
		setWatchedNetworkView(Cytoscape.getCurrentNetworkView());
		
		CyNetwork network = networkView.getNetwork();
		if (network == null)
			return;
		
		Set<?> selectedNodes = network.getSelectedNodes();
		
		Graph graph = null;
		
		try {
			graph = CytoscapePlugin.getNetworkCache().convertCyNetworkToGraph(network);
		} catch (NonNumericAttributeException e) {
			// TODO
			return;
		}
		
		if (graph == null) {
			// TODO
			return;
		}
		
		/* For each node in the graph, check whether it is selected and build the NodeSet */
		IntArray indices = new IntArray();
		int i = 0;
		for (Node node: graph.getNodeMapping()) {
			if (selectedNodes.contains(node))
				indices.add(i);
			i++;
		}
		this.setNodeSet(new NodeSet(graph, indices.toArray()));
	}
	
	/**
	 * Method called when the selection changes on the network view being watched
	 */
	public void graphViewChanged(GraphViewChangeEvent e) {
		if (e.getType() == GraphViewChangeEvent.NODES_SELECTED_TYPE ||
				e.getType() == GraphViewChangeEvent.NODES_UNSELECTED_TYPE
		) {
			/* Selection change happened */
			updateNodeSetFromSelection();
		}
	}
}
