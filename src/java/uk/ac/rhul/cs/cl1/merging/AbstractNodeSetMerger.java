package uk.ac.rhul.cs.cl1.merging;

import uk.ac.rhul.cs.cl1.TaskMonitor;
import uk.ac.rhul.cs.cl1.TaskMonitorSupport;

/**
 * Abstract node set merger from which concrete node set mergers will derive.
 * 
 * @author tamas
 */
public abstract class AbstractNodeSetMerger implements NodeSetMerger, TaskMonitorSupport {
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
	
	/**
	 * Constructs a nodeset merger from a string specification.
	 */
	public static AbstractNodeSetMerger fromString(String spec)
	throws InstantiationException {
		if (spec != null) {
			if (spec.equals("single"))
				return new SinglePassNodeSetMerger();
			else if (spec.equals("multi"))
				return new MultiPassNodeSetMerger();
			else if (spec.equals("none"))
				return new DummyNodeSetMerger();
			else if (spec.equals("dummy"))
				return new DummyNodeSetMerger();
		}
		throw new InstantiationException("unknown nodeset merger: " + spec);
	}
}
