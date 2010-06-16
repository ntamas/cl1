package uk.ac.rhul.cs.cl1.api.rest;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import uk.ac.rhul.cs.cl1.api.EntityNotFoundException;
import uk.ac.rhul.cs.cl1.api.EntityStore;

/**
 * REST resource handling datasets on which Cluster ONE can operate.
 * 
 * @author tamas
 */
@Path("/dataset")
public class DatasetResource {
	@Context UriInfo uriInfo;
	
	EntityStore<String> storage = WebApplication.getDatasetStore();
	
	/**
	 * Uploads a new dataset into the web interface.
	 * 
	 * @param content the new dataset
	 * @return an HTTP response
	 * @throws IOException when the dataset cannot be stored
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response create(String content) throws IOException {
		Response resp;
		
		String newId = storage.create(content);
		
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		URI createdURI = builder.path(newId).build();
		resp = Response.created(createdURI).build();
		
		return resp;
	}

	/**
	 * Retrieves the contents of a dataset in the web interface.
	 * 
	 * @param id  the ID of the dataset.
	 * @return the dataset itself
	 * @throws IOException when the dataset cannot be retrieved
	 * @throws NotFoundException when the dataset does not exist
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String get(@PathParam("id") String id) throws IOException {
		String dataset;
		
		try {
			dataset = storage.get(id);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("DatasetResource "+id+" is not found");
		}
		
		return dataset;
		
	}
	
	/**
	 * Replaces the contents of an existing dataset in the web interface.
	 * 
	 * @throws IOException when the dataset cannot be updated
	 */
	@PUT
	@Path("{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	public void replace(@PathParam("id") String id, String content) throws IOException {
		try {
			storage.replace(id, content);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("DatasetResource "+id+" is not found");
		}
	}

	/**
	 * Deletes an existing dataset from the web interface.
	 * 
	 * @param id  the ID of the dataset.
	 * @throws IOException when the dataset cannot be deleted
	 * @throws NotFoundException when the dataset does not exist
	 */
	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) throws IOException {
		try {
			storage.delete(id);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("DatasetResource "+id+" is not found");
		}
	}
}
