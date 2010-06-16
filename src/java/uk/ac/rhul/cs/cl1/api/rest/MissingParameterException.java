package uk.ac.rhul.cs.cl1.api.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Exception thrown when a mandatory request parameter is missing
 * 
 * @author tamas
 */
public class MissingParameterException extends WebApplicationException {
	/**
	 * Creates a HTTP 400 (Bad Request) exception.
	 */
	public MissingParameterException() {
		super(Response.Status.BAD_REQUEST);
	}
	
	/**
	 * Creates a HTTP 400 (Bad Request) exception with a message.
	 * 
	 * @param message that String that is the entity of the 400 response.
	 */
	public MissingParameterException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).
			  entity(message).type("text/plain").build());
	}

}
