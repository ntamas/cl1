package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.osgi.framework.BundleContext;

/**
 * App activator for the Cytoscape 3 plugin.
 * 
 * @author ntamas
 */
public class CytoscapeAppActivator extends AbstractCyActivator {

	/**
	 * The bundle context of the Cytoscape 3 plugin.
	 */
	private BundleContext bundleContext;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------
	
	public CytoscapeAppActivator() {
		super();
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	/**
	 * Returns the bundle context.
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}
	
	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	/**
	 * Returns the service registered with the givan class.
	 */
	public <S> S getService(Class<S> cls) {
		return this.getService(bundleContext, cls);
	}
	
	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	/**
	 * Registers an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 * @param  properties  additional properties to use for registering
	 */
	public <S> void registerService(S object, Class<S> cls) {
		registerService(object, cls, new Properties());
	}
	
	/**
	 * Registers an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 * @param  properties  additional properties to use for registering
	 */
	public void registerService(Object object, Class<?> cls, Properties properties) {
		CyServiceRegistrar registrar = this.getService(CyServiceRegistrar.class);
		registrar.registerService(object, cls, properties);
	}
	
	/**
	 * Starts the Cytoscape plugin.
	 */
	public void start(BundleContext context) throws Exception {
		this.bundleContext = context;
		registerService(new ClusterONECytoscapeApp(this), ClusterONECytoscapeApp.class);
	}
	
	/**
	 * Unregisters an object as a service in the Cytoscape Swing application.
	 * 
	 * @param  object      the object to register
	 * @param  cls         the class of the object
	 */
	public <S> void unregisterService(S object, Class<S> cls) {
		CyServiceRegistrar registrar = this.getService(CyServiceRegistrar.class);
		registrar.unregisterService(object, cls);
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
