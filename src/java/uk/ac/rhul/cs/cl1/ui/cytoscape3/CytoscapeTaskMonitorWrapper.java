package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import org.cytoscape.work.TaskMonitor;

/**
 * Compatibility wrapper class between Cytoscape's TaskMonitor
 * and ClusterONE's TaskMonitor
 * 
 * This class disguises a Cytoscape TaskMonitor as a ClusterONE TaskMonitor.
 * 
 * @author ntamas
 */
public class CytoscapeTaskMonitorWrapper implements uk.ac.rhul.cs.cl1.TaskMonitor {
	/**
	 * A Cytoscape task monitor object that will receive all the calls from this wrapper
	 */
	protected TaskMonitor cytoscapeTaskMonitor;

	public CytoscapeTaskMonitorWrapper(org.cytoscape.work.TaskMonitor cytoscapeTaskMonitor) {
		this.cytoscapeTaskMonitor = cytoscapeTaskMonitor;
	}
	
	public void setEstimatedTimeRemaining(long time) {
		// NOP. Not supported by Cytoscape 3.
	}

	public void setException(Throwable t, String userErrorMessage) {
		// NOP. Not supported by Cytoscape 3.
	}

	public void setException(Throwable t, String userErrorMessage,
			String recoveryTip) {
		// NOP. Not supported by Cytoscape 3.
	}

	public void setPercentCompleted(int percent)
			throws IllegalArgumentException {
		this.cytoscapeTaskMonitor.setProgress(percent / 100.0);
	}

	public void setStatus(String message) {
		this.cytoscapeTaskMonitor.setStatusMessage(message);
	}
}
