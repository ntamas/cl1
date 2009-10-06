package uk.ac.rhul.cs.cl1.ui;

import java.util.ArrayList;

import procope.data.complexes.Complex;
import procope.data.complexes.ComplexSet;
import procope.data.networks.ProteinNetwork;
import procope.methods.clustering.Clusterer;
import procope.tools.ProCopeException;
import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.UniqueIDGenerator;

/**
 * ProCope plugin version of Cluster ONE.
 * 
 * @author tamas
 */
public class ProcopePlugin implements Clusterer {
	public ComplexSet cluster(ProteinNetwork net) {
		ComplexSet result = new ComplexSet();
		ClusterONE algorithm = new ClusterONE();
		
		Graph graph = this.convertProteinNetworkToGraph(net);
		algorithm.runOnGraph(graph);
		for (NodeSet nodeSet: algorithm.getResults()) {
			result.addComplex(this.convertNodeSetToComplex(nodeSet));
		}
		
		return result;
	}

	/**
	 * Converts a NodeSet object back to a ProCope Complex object
	 * @param   nodeSet   the nodeset to be converted
	 * @return  the converted ProCope complex
	 */
	private Complex convertNodeSetToComplex(NodeSet nodeSet) {
		ArrayList<Integer> ids = new ArrayList<Integer>(nodeSet.size());
		Graph graph = nodeSet.getGraph();
		
		for (int node: nodeSet) {
			ids.add(Integer.parseInt(graph.getNodeName(node)));
		}
		
		return new Complex(ids);
	}

	/**
	 * Converts a ProteinNetwork object to a Graph object
	 * @param   net    the ProteinNetwork to be converted
	 * @return  a Graph object where every node is a protein and every edge is a weighted interaction
	 */
	protected Graph convertProteinNetworkToGraph(ProteinNetwork net) {
		Graph result = new Graph();
		UniqueIDGenerator idGen = new UniqueIDGenerator(result);
		
		int[] edges = net.getEdgesArray();
		
		for (int i = 0; i < edges.length; i += 2) {
			int protein1 = idGen.get(Integer.toString(edges[i]));
			int protein2 = idGen.get(Integer.toString(edges[i+1]));
			float weight = net.getEdge(edges[i], edges[i+1]);
			
			if (weight == Float.NaN)
				continue;
			if (weight < 0.0)
				throw new ProCopeException("negative weights are not supported by Cluster ONE");
			if (weight > 1.0)
				throw new ProCopeException("scores larger than 1.0 are not supported by Cluster ONE");
			
			result.createEdge(protein1, protein2, weight);
		}
		return result;
	}
}
