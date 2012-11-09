package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEException;
import uk.ac.rhul.cs.cl1.ValuedNodeSetList;

/**
 * Wrapper object for {@link ClusterONE} that makes it compatible with
 * Cytoscape's {@link Task} interface.
 * 
 * @author ntamas
 */
public class ClusterONECytoscapeTask extends ClusterONE implements Task {

	/**
	 * The network view that will show the results when the algorithm has
	 * finished.
	 */
	private CyNetworkView networkView;
	
	/**
	 * The result listener to notify when the algorithm has finished and the
	 * results are available.
	 */
	private ClusterONECytoscapeTask.ResultListener resultListener;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	/**
	 * Gets the network view that will show the results when the algorithm has
	 * finished.
	 */
	public CyNetworkView getNetworkView() {
		return this.networkView;
	}
	
	/**
	 * Sets the network view that will show the results when the algorithm has
	 * finished.
	 */
	public void setNetworkView(CyNetworkView networkView) {
		this.networkView = networkView;
	}
	
	/**
	 * Sets the result listener to notify when the algorithm has finished and the
	 * results are available.
	 */
	public void setResultListener(ClusterONECytoscapeTask.ResultListener listener) {
		this.resultListener = listener;
	}
	
	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	public void cancel() {
		halt();
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		try {
			setTaskMonitor(taskMonitor != null ? new CytoscapeTaskMonitorWrapper(taskMonitor) : null);
			taskMonitor.setTitle(ClusterONE.applicationName);
			super.run();
			
			// Construct the result and notify the listener
			if (resultListener != null) {
				Result result = new Result();
				result.clusters = this.result;
				if (this.graph instanceof Graph) {
					result.nodeMapping = ((Graph)this.graph).getNodeMapping();
				}
				resultListener.resultsCalculated(this, result);
			}
		} catch (ClusterONEException e) {
			throw e;
		} catch (Exception e) {
			throw new Exception("Unexpected error while running " + ClusterONE.applicationName + ". "+
					"Please notify the plugin authors!", e);
		}
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Result class
	// --------------------------------------------------------------------

	/**
	 * Class that contains the result of the task.
	 * 
	 * The task result actually has two components: the detected clusters
	 * and the mapping of the graph nodes back to CyNodes.
	 */
	public class Result {
		/**
		 * The clusters detected by the algorithm.
		 */
		public ValuedNodeSetList clusters;
		
		/**
		 * The mapping of graph node indices back to their corresponding CyNodes.
		 */
		public List<CyNode> nodeMapping;
	}
	
	// --------------------------------------------------------------------
	// ResultListener interface
	// --------------------------------------------------------------------

	/**
	 * Interface to be implemented by listeners interested in the result of a
	 * ClusterONECytoscapeTask.
	 */
	public interface ResultListener {
		/**
		 * Called when the results are ready.
		 */
		public void resultsCalculated(ClusterONECytoscapeTask task, Result result);
	}
}
