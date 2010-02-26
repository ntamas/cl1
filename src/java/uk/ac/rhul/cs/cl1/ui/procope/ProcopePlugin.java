package uk.ac.rhul.cs.cl1.ui.procope;

import java.util.ArrayList;

import procope.data.complexes.Complex;
import procope.data.complexes.ComplexSet;
import procope.data.networks.ProteinNetwork;
import procope.methods.clustering.Clusterer;
import procope.tools.ProCopeException;
import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.ConsoleTaskMonitor;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.UniqueIDGenerator;
import uk.ac.rhul.cs.cl1.ui.ClusterONEAlgorithmParametersDialog;

/**
 * ProCope plugin version of Cluster ONE.
 * 
 * @author tamas
 */
public class ProcopePlugin implements Clusterer {
	public ComplexSet cluster(ProteinNetwork net) {
		ComplexSet result = new ComplexSet();
		Graph graph = this.convertProteinNetworkToGraph(net);
		
		ClusterONEAlgorithmParameters parameters = getAlgorithmParameters();
		if (parameters == null)
			return result;
		
		ClusterONE algorithm = new ClusterONE(parameters);
		algorithm.setTaskMonitor(new ConsoleTaskMonitor());
		algorithm.runOnGraph(graph);
		for (NodeSet nodeSet: algorithm.getResults()) {
			result.addComplex(this.convertNodeSetToComplex(nodeSet));
		}
		
		return result;
	}

	/**
	 * Obtains the algorithm parameters from the user using a standard ProCope dialog box
	 * @return  the parameters
	 */
	protected ClusterONEAlgorithmParameters getAlgorithmParameters() {
		ClusterONEAlgorithmParametersDialog dialog = new ClusterONEAlgorithmParametersDialog();
		dialog.setLocationRelativeTo(null);
		
		if (!dialog.execute())
			return null;
		
		return dialog.getParameters();
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
		UniqueIDGenerator<Integer> idGen = new UniqueIDGenerator<Integer>(result);
		
		if (net.isDirected()) 
			throw new ProCopeException("Cluster ONE supports undirected graphs only");
		
		int[] edges = net.getEdgesArray();
		
		for (int i = 0; i < edges.length; i += 2) {
			int protein1 = idGen.get(edges[i]);
			int protein2 = idGen.get(edges[i+1]);
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
