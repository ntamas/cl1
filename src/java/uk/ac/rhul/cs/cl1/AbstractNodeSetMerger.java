package uk.ac.rhul.cs.cl1;

/**
 * Abstract node set merger from which concrete node set mergers will derive.
 * 
 * @author tamas
 */
public abstract class AbstractNodeSetMerger implements NodeSetMerger {
	/**
	 * The task monitor this node set merger will report its progress to.
	 */
	protected TaskMonitor taskMonitor;
	
	/**
	 * Returns the task monitor this merger reports its progress to.
	 */
	public TaskMonitor getTaskMonitor() {
		return this.taskMonitor;
	}
	
	/**
	 * Sets the task monitor to which the algorithm will report its
	 * progress.
	 * 
	 * @param monitor  the task monitor
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}
}
