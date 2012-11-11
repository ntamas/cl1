package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.Properties;

import javax.swing.JOptionPane;

import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;

public class CytoscapeApp extends AbstractCySwingApp {
	
	/**
	 * The application adapter.
	 */
	private CySwingAppAdapter adapter;
	
	/**
	 * The Swing application handle.
	 */
	private CySwingApplication app;
	
	/**
	 * The control panel of the application.
	 */
	private ControlPanel controlPanel;
	
	/**
	 * Local cache for converted ClusterONE representations of Cytoscape networks
	 */
	private CyNetworkCache networkCache;
	
	// --------------------------------------------------------------------
	// Static
	// --------------------------------------------------------------------

	/**
	 * The name of the menu in which the app lives.
	 */
	public static final String PREFERRED_MENU = "Apps." + ClusterONE.applicationName;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public CytoscapeApp(CySwingAppAdapter adapter) {
		super(adapter);
		initialize(adapter);
	}
	
	private void initialize(CySwingAppAdapter adapter) {
		// Store the adapter
		this.adapter = adapter;
		
		// Get the application handle
		this.app = adapter.getCySwingApplication();
		
		// Create a new network cache
		networkCache = new CyNetworkCache(this);
		
		// Create the control panel
		controlPanel = new ControlPanel(this);
		
		// Add the actions of the plugin
		app.addAction(new ShowControlPanelAction(controlPanel));
		app.addAction(new HelpAction(this));
		app.addAction(new AboutAction(this));
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
	 * Returns the currently selected network.
	 */
	public CyNetwork getCurrentNetwork() {
		return adapter.getCyApplicationManager().getCurrentNetwork();
	}
	
	/**
	 * Returns the CySwingAppAdapter associated to the ClusterONE plugin.
	 */
	public CySwingAppAdapter getCySwingAppAdapter() {
		return adapter;
	}
	
	/**
	 * Returns the CySwingApplication in which the ClusterONE plugin lives.
	 */
	public CySwingApplication getCySwingApplication() {
		return app;
	}
	
	/**
	 * Returns the Cytoscape service with the given interface.
	 */
	public <S> S getService(Class<S> cls) {
		return adapter.getCyServiceRegistrar().getService(cls);
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
	public void registerService(Object object, Class<?> cls) {
		registerService(object, cls, new Properties());
	}
	
	/**
	 * Registers an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 * @param  properties  additional properties to use for registering
	 */
	public void registerService(Object object, Class<?> cls, Properties properties) {
		adapter.getCyServiceRegistrar().registerService(object, cls, properties);
	}
	
	/**
	 * Unregisters an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 */
	public void unregisterService(Object object, Class<?> cls) {
		adapter.getCyServiceRegistrar().unregisterService(object, cls);
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
		
		DialogTaskManager taskManager = adapter.getCyServiceRegistrar().getService(DialogTaskManager.class);
		taskManager.execute(taskFactory.createTaskIterator(networkView));
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
