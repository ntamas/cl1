package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Edge;
import giny.model.Node;

import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.Pair;
import uk.ac.rhul.cs.cl1.UniqueIDGenerator;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyMenus;

public class CytoscapePlugin extends cytoscape.plugin.CytoscapePlugin {
	public CytoscapePlugin() {
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		cyMenus.addAction(new ShowControlPanelAction());
		cyMenus.addAction(new AboutAction());
	}

	/**
	 * Runs Cluster ONE with the given parameters on the current Cytoscape network
	 * @param parameters   the algorithm parameters of Cluster ONE
	 * @param weightAttr   edge attribute holding edge weights
	 */
	protected static Pair<List<NodeSet>, List<Node>> runAlgorithm(CyNetwork network,
			ClusterONEAlgorithmParameters parameters, String weightAttr) {
		Pair<Graph, List<Node>> graphAndMapping = convertCyNetworkToGraph(network, weightAttr);
		if (graphAndMapping == null)
			return null;
		
		Graph graph = graphAndMapping.getLeft();
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(true);
		config.displayStatus(true);
		
		ClusterONECytoscapeTask task = new ClusterONECytoscapeTask(parameters);
		task.setGraph(graph);
		TaskManager.executeTask(task, config);
		
		return Pair.create(task.getResults(), graphAndMapping.getRight());
	}
	
	/**
	 * Converts a Cytoscape {@link CyNetwork} to a Cluster ONE {@link Graph}.
	 * 
	 * @param network     the Cytoscape network to be converted
	 * @param weightAttr  the name of the edge attribute storing the edge weight
	 * @return    a pair of the converted Cluster ONE graph and a mapping from IDs to Cytoscape nodes
	 */
	@SuppressWarnings("unchecked")
	protected static Pair<Graph, List<Node> >
		convertCyNetworkToGraph(CyNetwork network, String weightAttr) {
		Graph graph = new Graph();
		UniqueIDGenerator<Node> nodeIdGen = new UniqueIDGenerator<Node>(graph);
		CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		
		/* Import all the edges into our graph */
		try {
			Iterator it = network.edgesIterator();
			while (it.hasNext()) {
				Edge edge = (Edge)it.next();
				int src = nodeIdGen.get(edge.getSource());
				int dest = nodeIdGen.get(edge.getTarget());
				if (src == dest)
					continue;
				
				Double weight = weightAttr == null ? null :
					(Double)edgeAttrs.getAttribute(edge.getIdentifier(), weightAttr);
				if (weight == null)
					weight = 1.0;
				
				graph.createEdge(src, dest, weight);
			}
		} catch (ClassCastException ex) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Weight attribute values must be numeric.",
					"Error - invalid weight attribute",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (graph.getEdgeCount() == 0) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"The selected network contains no edges",
					"Error - no edges in network",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		return Pair.create(graph, nodeIdGen.getReversedList());
	}
}
