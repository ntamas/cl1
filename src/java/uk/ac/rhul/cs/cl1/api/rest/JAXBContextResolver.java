package uk.ac.rhul.cs.cl1.api.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import uk.ac.rhul.cs.cl1.api.Cluster;
import uk.ac.rhul.cs.cl1.api.ClusterONEResult;

/**
 * Custom JAXB context resolver that customizes the JSON output so that
 * certain elements are always mapped as arrays, even if there is only
 * a single item in the array.
 * 
 * This is necessary to force JAXB to marshal {@link ClusterONEResult}
 * and {@link Cluster} properly.
 * 
 * @author tamas
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
	private JAXBContext context;
	
	public JAXBContextResolver() throws Exception {
		JSONConfiguration conf;
		
		conf = JSONConfiguration.mapped()
		                        .arrays("clusters", "members")
		                        .build();
		
		this.context = new JSONJAXBContext(conf, ClusterONEResult.class, Cluster.class);
	}
	
	public JAXBContext getContext(Class<?> objectType) {
		return ClusterONEResult.class.equals(objectType) ? context : null;
	}
}
