package uk.ac.rhul.cs.cl1.api.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Creates a HTTP 404 (Not Found) exception.
 * 
 * @author tamas
 */
public class NotFoundException extends WebApplicationException {
	/**
	 * Creates a HTTP 404 (Not Found) exception.
	 */
	public NotFoundException() {
		super(Response.Status.NOT_FOUND);
	}
	
	/**
	 * Creates a HTTP 404 (Not Found) exception with a message.
	 * 
	 * @param message that String that is the entity of the 404 response.
	 */
	public NotFoundException(String message) {
		super(Response.status(Response.Status.NOT_FOUND).
			  entity(message).type("text/plain").build());
	}
}
