package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

/**
 * Static-only class for CyNode-related utility functions.
 * 
 * @author ntamas
 */
public class CyNodeUtil {
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	/**
	 * Returns the name of the given CyNode.
	 * 
	 * @param  network      the network in which the node lives
	 * @param  node         the node whose name is to be retrieved
	 * @return the name of the node if it has a name or null
	 */
	public static String getName(CyNetwork network, CyNode node) {
		return getName(network, node, null);
	}
	
	/**
	 * Returns the name of the given CyNode in the given CyNetwork.
	 * 
	 * @param  network      the network in which the node lives
	 * @param  node         the node whose name is to be retrieved
	 * @param  defaultName  the default name to return when the node has no name
	 * @return the name of the node
	 */
	public static String getName(CyNetwork network, CyNode node, String defaultName) {
		if (network == null || node == null)
			return defaultName;
		
		CyRow row = network.getRow(node);
		if (row == null)
			return defaultName;
		
		return row.get(CyNetwork.NAME, String.class, defaultName);
	}
	
	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
