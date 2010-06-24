package uk.ac.rhul.cs.cl1.api.rest;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.api.ClusterONEResult;
import uk.ac.rhul.cs.cl1.api.EntityStore;
import uk.ac.rhul.cs.cl1.api.InMemoryEntityStore;

/**
 * Class that represents the Cluster ONE web interface as a whole
 * 
 * @author tamas
 *
 */
public class WebApplication {
	private static EntityStore<String> datasetStore =
		new InMemoryEntityStore<String>();
	private static EntityStore<ClusterONEResult> resultStore =
		new InMemoryEntityStore<ClusterONEResult>();
	private static ClusterONE clusterOneImpl =
		new ClusterONE();
	
	/**
	 * Returns the encapsulated dataset store instance.
	 */
	public static EntityStore<String> getDatasetStore() {
		return datasetStore;
	}
	
	/**
	 * Returns the encapsulated result store instance.
	 */
	public static EntityStore<ClusterONEResult> getResultStore() {
		return resultStore;
	}
	
	/**
	 * Returns the implementation of the Cluster ONE algorithm
	 */
	public static ClusterONE getClusterONE() {
		return clusterOneImpl;
	}
}
