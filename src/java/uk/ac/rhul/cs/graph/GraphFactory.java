package uk.ac.rhul.cs.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Factory methods to generate several simple graphs.
 * 
 * @author tamas
 */
public class GraphFactory {
	/**
	 * Specifications for famous graphs.
	 * 
	 * Each specification is a numeric array where the first item is the
	 * number of nodes, the second item stores the number of edges, the
	 * third stores whether the graph is
	 * directed, and the remaining items represent the edge list.
	 */
	private static HashMap<String, ArrayList<Integer>> famousGraphs;
	
	static {
		famousGraphs = new HashMap<String, ArrayList<Integer>>();
		famousGraphs.put("zachary", new ArrayList<Integer>(
				Arrays.asList(34, 78, 0,
						0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8,
						0,10, 0,11, 0,12, 0,13, 0,17, 0,19, 0,21, 0,31,
						1, 2, 1, 3, 1, 7, 1,13, 1,17, 1,19, 1,21, 1,30,
						2, 3, 2, 7, 2,27, 2,28, 2,32, 2, 9, 2, 8, 2,13,
						3, 7, 3,12, 3,13, 4, 6, 4,10, 5, 6, 5,10, 5,16,
						6,16, 8,30, 8,32, 8,33, 9,33,13,33,14,32,14,33,
						15,32,15,33,18,32,18,33,19,33,20,32,20,33,
						22,32,22,33,23,25,23,27,23,32,23,33,23,29,
						24,25,24,27,24,31,25,31,26,29,26,33,27,33,
						28,31,28,33,29,32,29,33,30,32,30,33,31,32,31,33,
						32,33)
		));
	}
	
	/**
	 * Creates an empty graph with the given number of vertices
	 * 
	 * @param  n        the number of vertices
	 * @param  directed  whether the graph is directed
	 */
	public static Graph createEmptyGraph(int n, boolean directed) {
		Graph result = new Graph(directed);
		result.createNodes(n);
		return result;
	}
	
	/**
	 * Creates one of the "famous" test graphs by name.
	 * 
	 * The following test graphs are supported:
	 * 
	 * <ul>
	 * <li><code>zachary</code> - the Zachary karate klub graph
	 * </ul>
	 * 
	 * @param  name      the name of the famous graph to create.
	 */
	public static Graph createFamousGraph(String name) {
		ArrayList<Integer> edges = famousGraphs.get(name);
		Graph result = new Graph(edges.get(2) != 0);
		int i, n = edges.size();
		
		result.createNodes(edges.get(0));
		for (i = 3; i < n; i += 2) {
			result.createEdge(edges.get(i), edges.get(i+1));
		}
		
		return result;
	}

	/**
	 * Creates a graph from an edge list.
	 *
	 * @param  edges    the edge list of the graph
	 */
	public static Graph createFromEdgeList(int[] edges) {
		int twiceNumEdges = edges.length;
		int i;
		Graph graph;

		graph = new Graph();
		for (i = 0; i < twiceNumEdges; i+=2) {
			graph.createEdge(edges[i], edges[i+1]);
		}

		return graph;
	}

	/**
	 * Creates a graph from an edge list with weights.
	 *
	 * @param  edges    the edge list of the graph
	 * @param  weights  the weight of each edge in the graph
	 */
	public static Graph createFromEdgeList(int[] edges, double[] weights) {
		int numEdges = weights.length;
		int i, j;
		Graph graph;

		graph = new Graph();
		for (i = 0, j = 0; j < numEdges; i+=2, j++) {
			graph.createEdge(edges[i], edges[i+1], weights[j]);
		}

		return graph;
	}

	/**
	 * Creates a full graph with the given number of vertices
	 * 
	 * @param  n         the number of vertices
	 * @param  directed  whether the graph is directed
	 * @param  loops     whether to include loop edges
	 */
	public static Graph createFullGraph(int n, boolean directed, boolean loops) {
		Graph result = new Graph(directed);
		int i, j;
		
		result.createNodes(n);
		
		if (directed) {
			for (i = 0; i < n; i++) {
				for (j = 0; j < n; j++) {
					if (loops || i != j)
						result.createEdge(i, j);
				}
			}
		} else {
			for (i = 0; i < n; i++) {
				for (j = (loops ? i : i+1); j < n; j++) {
					result.createEdge(i, j);
				}
			}
		}
		
		return result;
	}
}
