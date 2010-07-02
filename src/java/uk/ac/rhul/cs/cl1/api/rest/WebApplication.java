package uk.ac.rhul.cs.cl1.api.rest;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import uk.ac.rhul.cs.cl1.api.ClusterONEResult;
import uk.ac.rhul.cs.cl1.api.EntityStore;
import uk.ac.rhul.cs.cl1.api.PersistentEntityStore;

/**
 * Class that represents the Cluster ONE web interface as a whole
 * 
 * @author tamas
 *
 */
public class WebApplication {
	private final static String DATASOURCE_NAME = "jdbc/ClusterONEDatabase";
	
	private static EntityStore<String> datasetStore = null;
	private static EntityStore<ClusterONEResult> resultStore = null;

	static {
		init();
	}
	
	public static void init() {		
		try {
			Context env = (Context) new InitialContext().lookup("java:comp/env");
			DataSource dataSource = (DataSource)env.lookup(DATASOURCE_NAME);
			
			if (dataSource == null)
				throw new RuntimeException("`"+DATASOURCE_NAME+"' is an unknown DataSource");
			
			datasetStore = new PersistentEntityStore<String>(dataSource, "datasets");
			resultStore = new PersistentEntityStore<ClusterONEResult>(dataSource, "results");
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
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
}
