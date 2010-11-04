package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.graph.Graph;

/**
 * Writes a clustering to a stream in GenPro format.
 * 
 * Each line will contain a cluster ID and a node ID, separated by tabs.
 * Cluster IDs are made up on-the-fly in the format "Cluster X", where X
 * is a number starting from 1.
 * 
 * @author tamas
 */
public class GeneProClusteringWriter extends AbstractClusteringWriter {
	public void writeClustering(List<? extends NodeSet> clustering,
			OutputStream stream) throws IOException {
		PrintWriter wr = new PrintWriter(stream);
		
		int clusterIndex = 0;
		String clusterName;
		
		for (NodeSet nodeSet: clustering) {
			Graph graph = nodeSet.getGraph();
			
			clusterIndex++;
			clusterName = "Cluster " + clusterIndex;
			
			for (int nodeIndex: nodeSet) {
				wr.printf("%s\t%s\n", clusterName, graph.getNodeName(nodeIndex));
			}
		}
		
		wr.flush();
	}
}
