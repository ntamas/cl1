package uk.ac.rhul.cs.cl1.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.UniqueIDGenerator;

/**
 * Reads a graph specified in SIF format from an input stream
 * @author ntamas
 */
public class SIFReader implements GraphReader {
	/**
	 * Reads a graph specified in SIF format from the given reader object
	 * 
	 * @param  reader  the reader being used
	 */
	public Graph readGraph(Reader reader) throws IOException {
		Graph result = new Graph();
		UniqueIDGenerator<String> nodeGen = new UniqueIDGenerator<String>(result);
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		String line;
		String[] parts;
		String separator = "\\s";
		
		int node1, node2, n;
		double weight;
		
		while ((line = bufferedReader.readLine()) != null) {
			if (line.length() == 0)
				continue;
			
			if (line.contains("\t")) {
				/* As soon as the first Tab character is seen, the
				 * parser switches to Tab mode. */
				separator = "\\t";
			}
			
			parts = line.split(separator);
			n = parts.length;
			if (n < 3)
				continue;
			
			node1 = nodeGen.get(parts[0]);
			weight = 1.0;
			
			for (int i = 2; i < n; i++) {
				node2 = nodeGen.get(parts[i]);
				result.createEdge(node1, node2, weight);
			}
		}
		
		return result;
	}

}
