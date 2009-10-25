package uk.ac.rhul.cs.cl1.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.UniqueIDGenerator;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class CytoscapePlugin extends cytoscape.plugin.CytoscapePlugin implements ActionListener {
	public CytoscapePlugin() {
		JMenu pluginsMenu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
		JMenu menu = new JMenu("Cluster ONE");
		JMenuItem item;
		
		item = new JMenuItem("About");
		item.addActionListener(this);
		item.setActionCommand("about");
		menu.add(item);
		
		item = new JMenuItem("Start");
		item.addActionListener(this);
		item.setActionCommand("start");
		menu.add(item);
		
		pluginsMenu.add(menu);
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		
		if (cmd.equals("about")) {
			AboutDialog dlg = new AboutDialog(Cytoscape.getDesktop());
			dlg.setVisible(true);
			return;
		}
		
		if (cmd.equals("start")) {
			ClusterONEAlgorithmParametersDialog dlg = new ClusterONEAlgorithmParametersDialog(Cytoscape.getDesktop());
			if (dlg.execute())
				runAlgorithm(dlg.getParameters(), "weight");
			
			return;
		}
	}

	/**
	 * Runs Cluster ONE with the given parameters on the current Cytoscape network
	 * @param parameters   the algorithm parameters of Cluster ONE
	 * @param weightAttr   edge attribute holding edge weights
	 */
	protected void runAlgorithm(ClusterONEAlgorithmParameters parameters, String weightAttr) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		
		if (network == null) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"You must select a network before starting Cluster ONE",
					"Error - no network selected",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Graph graph = this.convertCyNetworkToGraph(network, weightAttr);
		if (graph == null)
			return;
		
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(true);
		config.displayStatus(true);
		
		ClusterONECytoscapeTask task = new ClusterONECytoscapeTask(parameters);
		task.setGraph(graph);
		TaskManager.executeTask(task, config);
	}
	
	/**
	 * Converts a Cytoscape {@link CyNetwork} to a Cluster ONE {@link Graph}.
	 * 
	 * @param network     the Cytoscape network to be converted
	 * @param weightAttr  the name of the edge attribute storing the edge weight
	 * @return    the converted Cluster ONE graph
	 */
	@SuppressWarnings("unchecked")
	protected Graph convertCyNetworkToGraph(CyNetwork network, String weightAttr) {
		Graph graph = new Graph();
		UniqueIDGenerator nodeIdGen = new UniqueIDGenerator(graph);
		CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		
		/* Import all the edges into our graph */
		try {
			Iterator it = network.edgesIterator();
			while (it.hasNext()) {
				CyEdge edge = (CyEdge)it.next();
				int src = nodeIdGen.get(edge.getSource().getIdentifier());
				int dest = nodeIdGen.get(edge.getTarget().getIdentifier());
				Double weight = (Double)edgeAttrs.getAttribute(edge.getIdentifier(), weightAttr);
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
		
		return graph;
	}
}
