package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * Graph class that also maintains a mapping between its own nodes and the corresponding
 * Cytoscape {@link CyNode} objects.
 * 
 * @todo  check whether we need weak references to {@link CyNode} instances in this class.
 *        If {@link CyNode} holds a reference to the corresponding {@link CyNetwork}, this
 *        might prevent the {@link CyNetwork} from being garbage collected.
 * @author ntamas
 *
 */
public class Graph extends uk.ac.rhul.cs.graph.Graph {
	/** The CyNetwork from which this graph was created */
	protected CyNetwork network;
	
	/** Mapping from integer node IDs to Cytoscape nodes */
	protected List<CyNode> nodeMapping = new ArrayList<CyNode>();
	
	public Graph(CyNetwork network) {
		super();
		this.setCyNetwork(network);
	}

	public Graph(boolean directed) {
		super(directed);
	}
	
	public List<CyNode> getNodeMapping() {
		return nodeMapping;
	}

	public CyNetwork getCyNetwork() {
		return network;
	}
	
	public void setCyNetwork(CyNetwork network) {
		this.network = network;
	}
	
	/**
	 * Sets the mapping from internal node IDs to Cytoscape nodes.
	 * 
	 * @param   nodeMapping  the node mapping to be set
	 * @throws  ArrayIndexOutOfBoundsException  if the node mapping list
	 *          is too short or too long for this graph
	 */
	public void setNodeMapping(List<CyNode> nodeMapping) {
		if (nodeMapping.size() != this.getNodeCount())
			throw new ArrayIndexOutOfBoundsException("node mapping list must be of length "+this.getNodeCount());
		
		this.nodeMapping = nodeMapping;
	}

	@Override
	public String getNodeName(int index) {
		CyNode node = nodeMapping.get(index);
		return CyNodeUtil.getName(network, node, "");
	}
	
	/**
	 * Returns the corresponding node indices for a collection of Cytoscape nodes.
	 * 
	 * @param   nodes   a collection of Cytoscape nodes for which we need the indices
	 * @return  the corresponding indices in arbitrary order
	 */
	public List<Integer> getMappedNodeIndices(Collection<CyNode> nodes) {
		List<Integer> result = new ArrayList<Integer>();
		for (CyNode node: nodes) {
			int index = this.nodeMapping.indexOf(node);
			if (index < 0)
				continue;
			result.add(index);
		}
		return result;
	}
}
