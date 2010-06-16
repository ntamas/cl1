package uk.ac.rhul.cs.cl1.api;

/**
 * Exception thrown when a dataset with a given ID was not found
 * 
 * @author tamas
 */
public class EntityNotFoundException extends Exception {
	public EntityNotFoundException(String id) {
		super("Dataset not found: "+id);
	}

	public EntityNotFoundException(String id, Throwable cause) {
		super("Dataset not found: "+id, cause);
	}
}
