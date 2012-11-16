package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;

/**
 * Task factory that creates a TaskIterator yielding a single ClusterONECytoscapeTask.
 * 
 * @author ntamas
 */
public class ClusterONECytoscapeTaskFactory extends AbstractNetworkViewTaskFactory {

	private final ClusterONECytoscapeApp app;
	
	/**
	 * The parameters of the ClusterONE algorithm.
	 */
	private ClusterONEAlgorithmParameters parameters;
	
	/**
	 * The result listener to notify when the algorithm has finished and the
	 * results are available.
	 */
	private ClusterONECytoscapeTask.ResultListener resultListener;
	
	/**
	 * The name of the edge attribute that contains the edge weights.
	 */
	private String weightAttr;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public ClusterONECytoscapeTaskFactory(ClusterONECytoscapeApp app) {
		this.app = app;
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------
	
	public ClusterONEAlgorithmParameters getParameters() {
		return parameters;
	}
	
	public String getWeightAttr() {
		return weightAttr;
	}
	
	public void setParameters(ClusterONEAlgorithmParameters parameters) {
		this.parameters = parameters;
	}
	
	public void setResultListener(ClusterONECytoscapeTask.ResultListener listener) {
		this.resultListener = listener;
	}
	
	public void setWeightAttr(String weightAttr) {
		this.weightAttr = weightAttr;
	}
	
	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		CyNetwork network = networkView.getModel();
		ClusterONECytoscapeTask task = new ClusterONECytoscapeTask();
		Graph graph = app.convertCyNetworkToGraph(network, weightAttr);
		task.setGraph(graph);
		task.setParameters(parameters);
		task.setNetworkView(networkView);
		task.setResultListener(resultListener);
		return new TaskIterator(task);
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
