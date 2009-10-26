package uk.ac.rhul.cs.cl1.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.UniqueIDGenerator;

/**
 * Reads a graph specified by an edge list representation from an input stream
 * @author ntamas
 */
public class EdgeListReader implements GraphReader {
	/**
	 * Reads a graph specified by an edge list representation from the given stream
	 * 
	 * @param  stream  the stream being read
	 */
	public Graph readGraph(InputStream stream) throws IOException {
		Graph result = new Graph();
		UniqueIDGenerator<String> nodeGen = new UniqueIDGenerator<String>(result);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		String[] parts;
		
		int node1, node2;
		double weight;
		
		while ((line = reader.readLine()) != null) {
			if (line.length() == 0)
				continue;
			
			if (line.charAt(0) == '#' || line.charAt(0) == '%')
				continue;
			
			parts = line.split("\\s");
			if (parts.length == 1)
				continue;
			
			node1 = nodeGen.get(parts[0]);
			node2 = nodeGen.get(parts[1]);

			if (parts.length >= 3)
				weight = Double.parseDouble(parts[2]);
			else
				weight = 1.0;
			
			result.createEdge(node1, node2, weight);
		}
		
		reader.close();
		
		return result;
	}
}
