package uk.ac.rhul.cs.cl1;

/**
 * Interface for monitoring the progress of a task.
 * 
 * This is essentially equivalent to Cytoscape's cytoscape.task.TaskMonitor
 * interface. Cluster ONE algorithms communicate with objects implementing this
 * interface to report their progress -- this means that they can easily communicate
 * with the Cytoscape UI as well.
 * 
 * @author tamas
 */
public interface TaskMonitor {
	/**
	 * Report estimate time until task completion.
	 * <p>
	 * This hook is primarily useful for very long-running processes.
	 * <p>
	 * Tasks are not required to report estimated time remaining. If a task does
	 * not report this value, or has no way of determining it, the task can
	 * safely choose not to invoke this method.
	 * 
	 * @param  time   estimated time until task completion, in milliseconds
	 */
	void setEstimatedTimeRemaining(long time);
	
	/**
	 * Report that an exception was thrown.
	 * <p>
	 * This hook is called when the calculation encountered an exception that it
	 * cannot deal with. The exception will be passed upstream to be shown on
	 * some kind of a user interface along with the given error message.
	 * 
	 * @param t                 the exception that happened
	 * @param userErrorMessage  the error message to be shown
	 */
	void setException(Throwable t, String userErrorMessage);
	
	/**
	 * Report that an exception was thrown.
	 * <p>
	 * This hook is called when the calculation encountered an exception that it
	 * cannot deal with. The exception will be passed upstream to be shown on
	 * some kind of a user interface along with the given error message and the
	 * recovery tip.
	 * 
	 * @param t                 the exception that happened
	 * @param userErrorMessage  the error message to be shown
	 * @param recoveryTip       tips to the user on how to fix this error
	 */
	void setException(Throwable t, String userErrorMessage, String recoveryTip);
	
	/**
	 * Report the progress of the task.
	 * <p>
	 * @param percent     percentage of the task that has completed - a value between
	 *                    0 and 100, or -1 to indicate that a task is indeterminate
	 */
	void setPercentCompleted(int percent) throws IllegalArgumentException;
	
	/**
	 * Report a textual description of the progress of the task.
	 * <p> 
	 * The description should be short, not more than 60 characters, even though that
	 * is not enforced.
	 * 
	 * @param  message   the message to be shown. Must be non-null; use an empty string
	 *                   to keep the current message.
	 */
	void setStatus(String message);
}
