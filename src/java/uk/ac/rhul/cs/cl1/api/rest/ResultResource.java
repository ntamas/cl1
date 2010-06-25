package uk.ac.rhul.cs.cl1.api.rest;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.ClusterONEException;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.ValuedNodeSetList;
import uk.ac.rhul.cs.cl1.api.ClusterONEResult;
import uk.ac.rhul.cs.cl1.api.EntityNotFoundException;
import uk.ac.rhul.cs.cl1.api.EntityStore;
import uk.ac.rhul.cs.cl1.io.GraphReader;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory;

/**
 * REST resource handling results produced by Cluster ONE.
 * 
 * @author tamas
 */
@Path("/result")
public class ResultResource {
	@Context UriInfo uriInfo;
	
	EntityStore<ClusterONEResult> resultStore = WebApplication.getResultStore();
	
	/**
	 * Creates a new result by running Cluster ONE on a dataset.
	 * 
	 * @param datasetId  the ID of the dataset
	 * @return an HTTP response containing the location of the result
	 * 
	 * @throws NotFoundException when the dataset does not exist
	 * @throws IOException when the dataset cannot be read
	 * @throws ClusterONEException when an error happened while running the algorithm
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response create(@FormParam("dataset_id") String datasetId,
			               @FormParam("min_size") Integer minSize,
			               @FormParam("min_density") Double minDensity,
			               @FormParam("overlap_threshold") Double overlapThreshold,
			               @FormParam("haircut_threshold") Double haircutThreshold,
			               @FormParam("merging_method") String mergingMethod,
			               @FormParam("seeding_method") String seedingMethod)
	throws IOException, ClusterONEException, InstantiationException {
		EntityStore<String> datasetStore = WebApplication.getDatasetStore();
		Response resp;
		String dataset = null;
		
		if (datasetId == null)
			throw new MissingParameterException("dataset_id");
		
		try {
			dataset = datasetStore.get(datasetId);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Dataset "+datasetId+" not found");
		}
		
		// Construct a graph from the dataset
		StringReader reader = new StringReader(dataset);
		GraphReader graphReader = GraphReaderFactory.fromFilename("test.txt");
		Graph graph = graphReader.readGraph(reader);
		reader.close();
		
		// Construct the algorithm parameter object
		ClusterONEAlgorithmParameters params = new ClusterONEAlgorithmParameters();
		if (minSize != null)
			params.setMinSize(minSize);
		if (minDensity != null)
			params.setMinDensity(minDensity);
		if (overlapThreshold != null)
			params.setOverlapThreshold(overlapThreshold);
		if (haircutThreshold != null)
			params.setHaircutThreshold(haircutThreshold);
		if (mergingMethod != null)
			params.setMergingMethod(mergingMethod);
		if (seedingMethod != null)
			params.setSeedGenerator(seedingMethod);
		
		// Run the algorithm and fetch the results
		ClusterONE algorithm = new ClusterONE(params);
		algorithm.runOnGraph(graph);
		ClusterONEResult result =
			ClusterONEResult.fromNodeSetList((ValuedNodeSetList)algorithm.getResults());
		result.setParameters(params);
		
		// Store the results
		String resultId = resultStore.create(result);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		URI uri = builder.path(resultId).build();
		resp = Response.created(uri).build();
		
		return resp;
	}
	
	/**
	 * Retrieves the contents of a result in the web interface.
	 * 
	 * @param id  the ID of the result.
	 * @return an HTTP response
	 * @throws IOException when the result cannot be retrieved
	 * @throws NotFoundException when the result does not exist
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClusterONEResult get(@PathParam("id") String id) throws IOException {
		ClusterONEResult result;
		
		try {
			result = resultStore.get(id);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Result "+id+" is not found");
		}
		
		return result;
	}
	
	/**
	 * Deletes an existing result from the web interface.
	 * 
	 * @param id  the ID of the result.
	 * @return an HTTP response
	 * @throws IOException when the result cannot be deleted
	 * @throws NotFoundException when the result does not exist
	 */
	@DELETE
	@Path("{id}")
	public void delete(@PathParam("id") String id) throws IOException {
		try {
			resultStore.delete(id);
		} catch (EntityNotFoundException ex) {
			throw new NotFoundException("Result "+id+" is not found");
		}
	}
}
