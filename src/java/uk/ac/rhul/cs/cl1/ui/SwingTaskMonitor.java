package uk.ac.rhul.cs.cl1.ui;

import java.awt.Frame;

import javax.swing.ProgressMonitor;

import uk.ac.rhul.cs.cl1.TaskMonitor;

/**
 * A task monitor that shows the progress of a task using a {@link ProgressMonitor}
 * @author tamas
 */
public class SwingTaskMonitor implements TaskMonitor {
	/** A ProgressMonitor handled by this class */
	protected ProgressMonitor monitor;
	
	public SwingTaskMonitor() {
		this(null);
	}
	
	public SwingTaskMonitor(Frame parent) {
		this.monitor = new ProgressMonitor(parent, "Please wait...", "", 0, 100);
	}
	
	public void setEstimatedTimeRemaining(long time) {
		// Intentionally left empty, this is not supported by the SwingTaskMonitor
	}

	public void setException(Throwable t, String userErrorMessage) {
		// TODO Auto-generated method stub
	}

	public void setException(Throwable t, String userErrorMessage,
			String recoveryTip) {
		// TODO Auto-generated method stub
	}

	public void setPercentCompleted(int percent)
			throws IllegalArgumentException {
		this.monitor.setProgress(percent);
	}

	public void setStatus(String message) {
		this.monitor.setNote(message);
	}
}
