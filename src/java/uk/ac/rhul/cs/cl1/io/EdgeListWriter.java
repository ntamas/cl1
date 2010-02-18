package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import uk.ac.rhul.cs.cl1.Edge;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.utils.ArrayUtils;

/**
 * Writes a graph specified by an edge list representation to an output stream
 * @author ntamas
 */
public class EdgeListWriter extends DefaultGraphWriter {
	/**
	 * Writes the edge list representation of a graph to the given stream
	 * 
	 * Weights will be stored in the output if not all the weights are equal to 1.
	 * 
	 * @param  graph   the graph being written
	 * @param  stream  the stream being written to
	 */
	public void writeGraph(Graph graph, OutputStream stream) throws IOException {
		String[] names = graph.getNodeNames();
		double[] weights = graph.getEdgeWeights();
		PrintWriter wr = new PrintWriter(stream);
		
		boolean writeWeights = (weights.length > 0);
		
		if (writeWeights) {
			double minWeight = ArrayUtils.min(weights);
			if ((minWeight == 1 || minWeight == 0) && ArrayUtils.max(weights) == minWeight)
				writeWeights = false;
		}

		for (int i = 0; i < names.length; i++)
			if (names[i] == null)
				names[i] = Integer.toString(i);
		
		if (writeWeights) {
			for (Edge e: graph) {
				wr.println(names[e.source]+'\t'+names[e.target]+'\t'+weights[e.index]);
			}
		} else {
			for (Edge e: graph) {
				wr.println(names[e.source]+'\t'+names[e.target]);
			}
		}
		
		wr.flush();
	}
}
