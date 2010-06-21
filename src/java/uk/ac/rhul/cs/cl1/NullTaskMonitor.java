package uk.ac.rhul.cs.cl1;

/**
 * Task monitor that does nothing.
 * 
 * This task monitor is used when the Cluster ONE algorithm is
 * invoked from the web interface.
 * 
 * @author tamas
 */
public class NullTaskMonitor implements TaskMonitor {
	public void setEstimatedTimeRemaining(long time) {
	}

	public void setException(Throwable t, String userErrorMessage) {
	}

	public void setException(Throwable t, String userErrorMessage,
			String recoveryTip) {
	}

	public void setPercentCompleted(int percent)
			throws IllegalArgumentException {
	}

	public void setStatus(String message) {
	}
}
