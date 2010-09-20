package uk.ac.rhul.cs.cl1.ui.cytoscape;

import cytoscape.task.TaskMonitor;

/**
 * Compatibility wrapper class between Cytoscape's TaskMonitor
 * and ClusterONE's TaskMonitor
 * 
 * This class disguises a ClusterONE TaskMonitor as a Cytoscape TaskMonitor.
 * 
 * @author ntamas
 */
public class CytoscapeTaskMonitorWrapper implements uk.ac.rhul.cs.cl1.TaskMonitor {
	/**
	 * A Cytoscape task monitor object that will receive all the calls from this wrapper
	 */
	protected TaskMonitor cytoscapeTaskMonitor;

	public CytoscapeTaskMonitorWrapper(cytoscape.task.TaskMonitor cytoscapeTaskMonitor) {
		this.cytoscapeTaskMonitor = cytoscapeTaskMonitor;
	}
	
	public void setEstimatedTimeRemaining(long time) {
		this.cytoscapeTaskMonitor.setEstimatedTimeRemaining(time);
	}

	public void setException(Throwable t, String userErrorMessage) {
		this.cytoscapeTaskMonitor.setException(t, userErrorMessage);
	}

	public void setException(Throwable t, String userErrorMessage,
			String recoveryTip) {
		this.cytoscapeTaskMonitor.setException(t, userErrorMessage, recoveryTip);
	}

	public void setPercentCompleted(int percent)
			throws IllegalArgumentException {
		this.cytoscapeTaskMonitor.setPercentCompleted(percent);
	}

	public void setStatus(String message) {
		this.cytoscapeTaskMonitor.setStatus(message);
	}
}
