package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph class that also maintains a mapping between its own nodes and the corresponding
 * Cytoscape {@link Node} objects.
 * 
 * @todo  check whether we need weak references to {@link Node} instances in this class.
 *        If {@link Node} holds a reference to the corresponding {@link CyNetwork}, this
 *        might prevent the {@link CyNetwork} from being garbage collected. On the
 *        other hand, it is unlikely that a node refers to {@link CyNetwork} as it may
 *        be a part of multiple {@link CyNetwork} instances.
 * @author ntamas
 *
 */
public class Graph extends uk.ac.rhul.cs.cl1.Graph {
	/** Mapping from integer node IDs to Cytoscape nodes */
	protected List<Node> nodeMapping = new ArrayList<Node>();
	
	public Graph() {
		super();
	}

	public Graph(boolean directed) {
		super(directed);
	}
	
	/**
	 * Sets the mapping from internal node IDs to Cytoscape nodes.
	 * 
	 * @param   nodeMapping  the node mapping to be set
	 * @throws  ArrayIndexOutOfBoundsException  if the node mapping list
	 *          is too short or too long for this graph
	 */
	public void setNodeMapping(List<Node> nodeMapping) {
		if (nodeMapping.size() != this.getNodeCount())
			throw new ArrayIndexOutOfBoundsException("node mapping list must be of length "+this.getNodeCount());
		
		this.nodeMapping = nodeMapping;
	}

	public List<Node> getNodeMapping() {
		return nodeMapping;
	}
}
