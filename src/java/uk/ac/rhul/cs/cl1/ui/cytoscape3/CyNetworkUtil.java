package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;

/**
 * Static-only class for CyNetwork-related utility functions.
 * 
 * @author ntamas
 */
public class CyNetworkUtil {
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	/**
	 * Returns the set of edges connecting a given set of nodes.
	 */
	public static Collection<CyEdge> getConnectingEdges(CyNetwork network,
			Collection<? extends CyNode> nodes) {
		HashSet<CyNode> nodeSet = new HashSet<CyNode>(nodes);
		HashSet<CyEdge> resultSet = new HashSet<CyEdge>();
		for (CyNode node: nodes) {
			for (CyEdge edge: network.getAdjacentEdgeIterable(node, Type.ANY)) {
				if (nodeSet.contains(edge.getSource()) && nodeSet.contains(edge.getTarget())) {
					resultSet.add(edge);
				}
			}
		}
		return resultSet;
	}
	
	/**
	 * Returns the selected edges from the given network.
	 */
	public static List<CyEdge> getSelectedEdges(CyNetwork network) {
		return CyTableUtil.getEdgesInState(network, CyNetwork.SELECTED, true);
	}
	
	/**
	 * Returns the selected nodes from the given network.
	 */
	public static List<CyNode> getSelectedNodes(CyNetwork network) {
		return CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);
	}
	
	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------
	
	/**
	 * Sets the "selected" state of the given nodes or edges to the given value.
	 */
	public static void setSelectedState(CyNetwork network,
			Collection<? extends CyIdentifiable> nodesOrEdges, boolean value) {
		for (CyIdentifiable foo: nodesOrEdges) {
			network.getRow(foo).set(CyNetwork.SELECTED, value);
		}
	}
	
	/**
	 * Unselects all edges in the given CyNetwork.
	 */
	public static void unselectAllEdges(CyNetwork network) {
		CyTable edgeTable = network.getDefaultEdgeTable();
		for (CyRow row: edgeTable.getMatchingRows(CyNetwork.SELECTED, true)) {
			row.set(CyNetwork.SELECTED, false);
		}
	}
	
	/**
	 * Unselects all nodes in the given CyNetwork.
	 */
	public static void unselectAllNodes(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		for (CyRow row: nodeTable.getMatchingRows(CyNetwork.SELECTED, true)) {
			row.set(CyNetwork.SELECTED, false);
		}		
	}
	
	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
