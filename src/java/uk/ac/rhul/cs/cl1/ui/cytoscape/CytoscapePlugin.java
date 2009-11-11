package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.Pair;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyMenus;

public class CytoscapePlugin extends cytoscape.plugin.CytoscapePlugin {
	/**
	 * Attribute name used by Cluster ONE to store status information for each node.
	 * 
	 * A node can have one and only one of the following status values:
	 * 
	 * <ul>
	 * <li>0 = the node is an outlier (it is not included in any cluster)</li>
	 * <li>1 = the node is included in only a single cluster</li>
	 * <li>2 = the node is an overlap (it is included in more than one cluster)</li>
	 * </ul>
	 */
	public static final String ATTRIBUTE_STATUS = "cl1.Status";
	
	/**
	 * Attribute name used by Cluster ONE to store affinities of vertices to a
	 * given cluster.
	 */
	public static final String ATTRIBUTE_AFFINITY = "cl1.Affinity";
	
	/**
	 * Local cache for converter Cluster ONE representations of Cytoscape networks
	 */
	private static CyNetworkCache networkCache = new CyNetworkCache();
	
	public CytoscapePlugin() {
		/* Set up menus */
		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		cyMenus.addAction(new ShowControlPanelAction());
		cyMenus.addAction(new AboutAction());
		
		/* Set up the attributes that will be used by Cluster ONE */
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttributeDescription(ATTRIBUTE_STATUS,
				"This attribute is used by the Cluster ONE plugin to indicate the status "+
				"of a node after a Cluster ONE run. The status codes are as follows:\n\n"+
				"0 = the node is not part of any cluster (i.e. it is an outlier)\n"+
				"1 = the node is part of exactly one cluster\n"+
				"2 = the node is part of multiple clusters (i.e. it is an overlap)"
		);
		nodeAttributes.setAttributeDescription(ATTRIBUTE_AFFINITY,
				"This attribute is used by the Cluster ONE plugin to indicate the "+
				"affinity of a node to a given cluster. The attribute values can be "+
				"(re)calculated manually by right-clicking on a cluster in the "+
				"Cluster ONE result table and selecting the appropriate menu item."
		);
	}
	
	/**
	 * Returns a reference to the network cache held by the plugin
	 */
	public static CyNetworkCache getNetworkCache() {
		return networkCache;
	}
	
	/**
	 * Runs Cluster ONE with the given parameters on the current Cytoscape network
	 * @param parameters   the algorithm parameters of Cluster ONE
	 * @param weightAttr   edge attribute holding edge weights
	 */
	protected static Pair<List<NodeSet>, List<Node>> runAlgorithm(CyNetwork network,
			ClusterONEAlgorithmParameters parameters, String weightAttr) {
		Graph graph;
		
		try {
			networkCache.invalidate(network);
			graph = networkCache.convertCyNetworkToGraph(network, weightAttr);
		} catch (NonNumericAttributeException ex) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Weight attribute values must be numeric.",
					"Error - invalid weight attribute",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (graph == null)
			return null;
		
		if (graph.getEdgeCount() == 0) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"The selected network contains no edges",
					"Error - no edges in network",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(true);
		config.displayStatus(true);
		
		ClusterONECytoscapeTask task = new ClusterONECytoscapeTask(parameters);
		task.setGraph(graph);
		TaskManager.executeTask(task, config);
		
		setStatusAttributesOnCyNetwork(network, task.getResults(), graph);
		
		return Pair.create(task.getResults(), graph.getNodeMapping());
	}
	
	/**
	 * Sets some Cluster ONE specific attributes on a CyNetwork that will be in
	 * VizMapper later.
	 * 
	 * @param network    the analysed network in Cytoscape's representation
	 * @param results    results of the analysis
	 * @param graph      the Cluster ONE graph representation
	 */
	private static void setStatusAttributesOnCyNetwork(CyNetwork network,
			List<NodeSet> results, Graph graph) {
		int[] occurrences = new int[network.getNodeCount()];
		Arrays.fill(occurrences, 0);
		
		for (NodeSet nodeSet: results) {
			for (Integer nodeIdx: nodeSet) {
				occurrences[nodeIdx]++;
			}
		}
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String[] values = {"Outlier", "Cluster", "Overlap"};
		
		byte attrType = nodeAttributes.getType(ATTRIBUTE_STATUS);
		if (attrType != CyAttributes.TYPE_UNDEFINED && attrType != CyAttributes.TYPE_STRING) {
			int response = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					"A node attribute named "+ATTRIBUTE_STATUS+" already exists and "+
					"it is not a string attribute.\nDo you want to remove the existing "+
					"attribute and re-register it as a string attribute?",
					"Attribute type mismatch",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.NO_OPTION)
				return;
			
			nodeAttributes.deleteAttribute(ATTRIBUTE_STATUS);
		}
		
		int i = 0;
		for (Node node: graph.getNodeMapping()) {
			if (occurrences[i] > 2)
				occurrences[i] = 2;
			nodeAttributes.setAttribute(node.getIdentifier(), ATTRIBUTE_STATUS,
					values[occurrences[i]]);
			i++;
		}
	}
}
