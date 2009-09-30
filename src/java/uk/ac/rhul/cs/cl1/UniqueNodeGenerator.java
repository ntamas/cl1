package uk.ac.rhul.cs.cl1;

import giny.model.Node;
import giny.model.RootGraph;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class that is used in graph readers to generate unique GINY nodes for string IDs.
 * 
 * This class implements a single public method named get() which returns the numeric
 * ID for the given node name. Internally, the generator uses a map to keep track of
 * the name-ID assignments. Whenever an ID is requested for a name that is not in the
 * map yet, a new ID will be generated. The names used must be immutable objects.
 */
public class UniqueNodeGenerator {
	/// Internal storage for key-value assignments
	protected Map<String, Node> map = new TreeMap<String, Node>();
	
	/// The graph to which this node generator is associated
	protected RootGraph graph;
	
	/// Constructs a new node generator associated to a GINY graph
	public UniqueNodeGenerator(RootGraph graph) {
		this.graph = graph;
	}
	
	/// Returns the ID for the given name
	public Node get(String name) {
		Node result = map.get(name);
		if (result == null) {
			result = graph.getNode(graph.createNode());
			result.setIdentifier(name);
		}
		return result;
	}
}
