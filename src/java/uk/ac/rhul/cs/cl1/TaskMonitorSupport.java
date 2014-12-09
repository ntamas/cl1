package uk.ac.rhul.cs.cl1;

/**
 * Interface specification for objects that are able to monitor their own progress
 * and report it via a {@link uk.ac.rhul.cs.cl1.TaskMonitor}.
 *
 * @author tamas
 */
public interface TaskMonitorSupport {
    /**
     * Associates a task monitor to the object. The object will then use the given
     * task monitor to report its progress.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor);
}
