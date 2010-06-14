package uk.ac.rhul.cs.cl1.api.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST resource handling datasets on which Cluster ONE can operate.
 * 
 * @author tamas
 */
@Path("/dataset")
@Produces("text/json")
public class Dataset {
	@POST
	public Response createDataset() throws URISyntaxException {
		Response resp;
		
		resp = Response.created(new URI("/abc")).build();
		
		return resp;
	}
	
	@DELETE
	public Response deleteDataset(@PathParam("id") String id) {
		Response resp;
		
		resp = Response.noContent().build();
		
		return resp;
	}
}
