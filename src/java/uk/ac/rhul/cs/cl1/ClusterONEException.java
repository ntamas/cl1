package uk.ac.rhul.cs.cl1;

/**
 * Exception thrown when an error happens in Cluster ONE.
 * 
 * @author ntamas
 */
public class ClusterONEException extends Exception {
	public ClusterONEException() {
		super();
	}

	public ClusterONEException(String message) {
		super(message);
	}

	public ClusterONEException(Throwable cause) {
		super(cause);
	}

	public ClusterONEException(String message, Throwable cause) {
		super(message, cause);
	}
}
