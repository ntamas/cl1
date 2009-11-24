package uk.ac.rhul.cs.cl1;

/**
 * Class representing an edge of a graph
 * 
 * Instances of this class are created on-the-fly for some methods of the
 * Graph object.
 * 
 * @author tamas
 */
public class Edge {
	/**
	 * Index of the edge itself
	 */
	public int index;
	
	/**
	 * Index of the source node of the edge
	 */
	public int source;
	
	/**
	 * Index of the target node of the edge
	 */
	public int target;
	
	/**
	 * Weight of the edge
	 */
	public double weight;
	
	/**
	 * Constructor
	 * 
	 * @param   graph      the graph from which we are taking an edge
	 * @param   edgeIndex  index of the edge
	 */
	public Edge(Graph graph, int edgeIndex) {
		this.index = edgeIndex;
		this.target = graph.edgesIn.get(edgeIndex);
		this.source = graph.edgesOut.get(edgeIndex);
		this.weight = graph.weights.get(edgeIndex);
	}
	
	/**
	 * Converts the edge to a string
	 */
	public String toString() {
		return this.source+" --> "+this.target+": "+this.weight;
	}
}
