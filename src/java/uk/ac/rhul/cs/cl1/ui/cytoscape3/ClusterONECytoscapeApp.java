package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.quality.CohesivenessFunction;
import uk.ac.rhul.cs.cl1.quality.QualityFunction;

public class ClusterONECytoscapeApp {
	
	/**
	 * The application activator.
	 */
	private CytoscapeAppActivator activator;
	
	/**
	 * The Cytoscape desktop handle.
	 */
	private CySwingApplication app;
	
	/**
	 * The control panel of the application.
	 */
	private ControlPanel controlPanel;
	
	/**
	 * The "Grow cluster from selected nodes" action of the application.
	 */
	private GrowClusterAction growClusterAction;
	
	/**
	 * Local cache for converted ClusterONE representations of Cytoscape networks
	 */
	private CyNetworkCache networkCache;
	
	/**
	 * Variable storing the name of the resource path wthin the bundle.
	 */
	private String resourcePathName;
	
	/**
	 * Manager for the visual styles used by the application.
	 */
	private VisualStyleManager visualStyleManager;
	
	// --------------------------------------------------------------------
	// Static
	// --------------------------------------------------------------------

	/**
	 * Attribute name used by ClusterONE to store status information for each node.
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
	 * Attribute name used by ClusterONE to store affinities of vertices to a
	 * given cluster.
	 */
	public static final String ATTRIBUTE_AFFINITY = "cl1.Affinity";
	
	/**
	 * The name of the menu in which the app lives.
	 */
	public static final String PREFERRED_MENU = "Apps." + ClusterONE.applicationName;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public ClusterONECytoscapeApp(CytoscapeAppActivator activator) {
		this.activator = activator;
		initialize();
	}
	
	private void initialize() {
		// Get the application handle
		this.app = activator.getService(CySwingApplication.class);
		
		// Create a new network cache
		networkCache = new CyNetworkCache(this);
		
		// Create the visual style manager
		visualStyleManager = new VisualStyleManager(this);
		
		// Create the control panel
		controlPanel = new ControlPanel(this);
		
		// Create the global actions
		growClusterAction = new GrowClusterAction(this);
		
		// Add the actions of the plugin
		app.addAction(new ShowControlPanelAction(controlPanel));
		app.addAction(growClusterAction);
		app.addAction(new AffinityColouringAction(this));
		app.addAction(new HelpAction(this, "introduction"));
		app.addAction(new AboutAction(this));
		
		// Register the node-specific context menu
		registerService(new NodeContextMenuFactory(this), CyNodeViewContextMenuFactory.class);
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	/**
	 * Returns the control panel of the application.
	 */
	public ControlPanel getControlPanel() {
		return controlPanel;
	}
	
	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------
	
	/**
	 * Returns the application manager from Cytoscape.
	 */
	public CyApplicationManager getApplicationManager() {
		return activator.getService(CyApplicationManager.class);
	}
	
	/**
	 * Returns the currently selected network.
	 */
	public CyNetwork getCurrentNetwork() {
		return getApplicationManager().getCurrentNetwork();
	}
	
	/**
	 * Returns the currently selected network view.
	 */
	public CyNetworkView getCurrentNetworkView() {
		return getApplicationManager().getCurrentNetworkView();
	}
	
	/**
	 * Returns the CySwingApplication in which the ClusterONE plugin lives.
	 */
	public CySwingApplication getCySwingApplication() {
		return app;
	}
	
	/**
	 * Returns the action that grows a cluster from the selected nodes of the current view.
	 */
	public AbstractCyAction getGrowClusterAction() {
		return growClusterAction;
	}
	
	/**
	 * Returns the app-wide network view manager from Cytoscape.
	 */
	public CyNetworkViewManager getNetworkViewManager() {
		return activator.getService(CyNetworkViewManager.class);
	}
	
	/**
	 * Returns URL of the resource with the given name from the plugin bundle.
	 */
	public URL getResource(String name) {
		return activator.getResource(name);
	}
	
	/**
	 * Returns an input stream pointing to the resource with the given name from
	 * the plugin bundle.
	 * 
	 * @throws IOException 
	 */
	public InputStream getResourceAsStream(String name) throws IOException {
		return activator.getResourceAsStream(name);
	}
	
	/**
	 * Returns the name of the path containing the resources of the bundle.
	 */
	public String getResourcePathName() {
		if (resourcePathName == null) {
			String packageName = this.getClass().getPackage().getName().replace('.', '/');
			packageName = packageName.substring(0, packageName.lastIndexOf('/'));
			packageName = packageName.substring(0, packageName.lastIndexOf('/'));
			resourcePathName = packageName + "/resources";
		}
		return resourcePathName;
	}
	
	/**
	 * Returns the Cytoscape service with the given interface.
	 */
	public <S> S getService(Class<S> cls) {
		return activator.getService(cls);
	}
	
	/**
	 * Returns the Cytoscape service with the given interface.
	 */
	public <S> S getService(Class<S> cls, String properties) {
		return activator.getService(cls, properties);
	}
	
	/**
	 * Returns the visual style manager of the app.
	 */
	public VisualStyleManager getVisualStyleManager() {
		return visualStyleManager;
	}
	
	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	/**
	 * Converts a {@link CyNetwork} to a {@link Graph} using the {@link CyNetworkCache}
	 * 
	 * @param  network     the network being converted
	 * @param  weightAttr  the attribute name used for the weights
	 * @return the converted graph or null if there was an error
	 */
	public Graph convertCyNetworkToGraph(CyNetwork network, String weightAttr) {
		Graph graph = null;
		
		try {
			graph = networkCache.convertCyNetworkToGraph(network, weightAttr);
		} catch (NonNumericAttributeException ex) {
			showErrorMessage("Weight attribute values must be numeric.");
			return null;
		}
		
		return graph;
	}
	
	/**
	 * Registers an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 * @param  properties  additional properties to use for registering
	 */
	public <S> void registerService(S object, Class<S> cls) {
		activator.registerService(object, cls);
	}
	
	/**
	 * Unregisters an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 */
	public <S> void unregisterService(S object, Class<S> cls) {
		activator.unregisterService(object, cls);
	}
	
	/**
	 * Runs ClusterONE with the given parameters on the given Cytoscape network
	 * asynchronously in a background thread.
	 * 
	 * @param network        the network view we are running the algorithm on
	 * @param parameters     the algorithm parameters of ClusterONE
	 * @param weightAttr     edge attribute holding edge weights
	 * @param listener       the listener to notify when the results are ready
	 */
	public void runAlgorithm(CyNetwork network, ClusterONEAlgorithmParameters parameters,
			String weightAttr, ClusterONECytoscapeTask.ResultListener listener) {
		networkCache.invalidate(network);
		if (network == null || network.getEdgeCount() == 0) {
			showErrorMessage("The selected network contains no edges");
			return;
		}
		
		ClusterONECytoscapeTaskFactory taskFactory = new ClusterONECytoscapeTaskFactory(this);
		taskFactory.setParameters(parameters);
		taskFactory.setWeightAttr(weightAttr);
		taskFactory.setResultListener(listener);
		
		DialogTaskManager taskManager = activator.getService(DialogTaskManager.class);
		taskManager.execute(taskFactory.createTaskIterator(network));
	}
	
	/**
	 * Runs ClusterONE with the given parameters on the given Cytoscape network view
	 * asynchronously in a background thread.
	 * 
	 * @param network        the network view we are running the algorithm on
	 * @param parameters     the algorithm parameters of ClusterONE
	 * @param weightAttr     edge attribute holding edge weights
	 * @param listener       the listener to notify when the results are ready
	 */
	public void runAlgorithm(CyNetworkView networkView, ClusterONEAlgorithmParameters parameters,
			String weightAttr, ClusterONECytoscapeTask.ResultListener listener) {
		CyNetwork network = networkView.getModel();
		
		networkCache.invalidate(network);
		if (network == null || network.getEdgeCount() == 0) {
			showErrorMessage("The selected network contains no edges");
			return;
		}
		
		ClusterONECytoscapeTaskFactory taskFactory = new ClusterONECytoscapeTaskFactory(this);
		taskFactory.setParameters(parameters);
		taskFactory.setWeightAttr(weightAttr);
		taskFactory.setResultListener(listener);
		
		DialogTaskManager taskManager = activator.getService(DialogTaskManager.class);
		taskManager.execute(taskFactory.createTaskIterator(networkView));
	}
	
	/**
	 * Sets the ClusterONE specific node affinity attributes on a CyNetwork that
	 * will be used by VizMapper later.
	 * 
	 * @param network  the Cytoscape network to manipulate
	 * @param graph    the ClusterONE graph representation of that network
	 * @param nodes    the list of the selected node indices
	 */
	public void setAffinityAttributes(CyNetwork network, Graph graph, List<Integer> nodes) {
		CyTable nodeTable = network.getDefaultNodeTable();
		
		CyColumn affinityColumn = nodeTable.getColumn(ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY);
		if (affinityColumn != null && affinityColumn.getType() != Double.class) {
			int response = JOptionPane.showConfirmDialog(app.getJFrame(),
					"A node attribute named "+ATTRIBUTE_STATUS+" already exists and "+
					"it is not a string attribute.\nDo you want to remove the existing "+
					"attribute and re-register it as a string attribute?",
					"Attribute type mismatch",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.NO_OPTION)
				return;
			
			nodeTable.deleteColumn(ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY);
			affinityColumn = null;
		}
		
		if (affinityColumn == null) {
			nodeTable.createColumn(ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY, Double.class, false, 0.0);
		}
		
		int i = 0;
		MutableNodeSet nodeSet = new MutableNodeSet(graph, nodes);
		QualityFunction func = new CohesivenessFunction(); // TODO: fix it, it should not be hardwired
		double currentQuality = func.calculate(nodeSet);
		double affinity;
		
		for (CyNode node: graph.getNodeMapping()) {
			if (nodeSet.contains(i))
				/* multiplying by -1 here: we want internal nodes to have a positive
				 * affinity if they "should" belong to the cluster
				 */
				affinity = - (func.getRemovalAffinity(nodeSet, i) - currentQuality);
			else
				affinity = func.getAdditionAffinity(nodeSet, i) - currentQuality;
			
			if (Double.isNaN(affinity))
				affinity = 0.0;

			CyRow row = network.getRow(node);
			if (row != null) {
				row.set(ClusterONECytoscapeApp.ATTRIBUTE_AFFINITY, affinity);
			}
			i++;
		}
	}
	
	/**
	 * Shows a message dialog box that informs the user about a possible bug in ClusterONE.
	 * 
	 * @param  message   the message to be shown
	 */
	public void showBugMessage(String message) {
		StringBuilder sb = new StringBuilder(message);
		sb.append("\n\n");
		sb.append("This is possibly a bug in ");
		sb.append(ClusterONE.applicationName);
		sb.append(".\nPlease inform the developers about what you were doing and\n");
		sb.append("what the expected result would have been.");
		
		JOptionPane.showMessageDialog(app.getJFrame(),
				sb.toString(), "Possible bug in "+ClusterONE.applicationName,
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Shows an error message in a dialog box
	 * 
	 * @param  message  the error message to be shown
	 */
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(app.getJFrame(), message,
				ClusterONE.applicationName, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows a message dialog box that informs the user about something
	 * 
	 * @param  message  the message to be shown
	 */
	public void showInformationMessage(String message) {
		JOptionPane.showMessageDialog(app.getJFrame(), message,
				ClusterONE.applicationName, JOptionPane.INFORMATION_MESSAGE);
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
