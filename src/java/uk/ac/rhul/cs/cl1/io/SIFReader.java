package uk.ac.rhul.cs.cl1.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import uk.ac.rhul.cs.cl1.TaskMonitor;
import uk.ac.rhul.cs.cl1.TaskMonitorSupport;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.UniqueIDGenerator;

/**
 * Reads a graph specified in SIF format from an input stream
 * @author ntamas
 */
public class SIFReader implements GraphReader, TaskMonitorSupport {
	/**
	 * Task monitor that will be used by the reader to report its progress.
	 */
	private TaskMonitor taskMonitor;

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
		int numEdges = 0;
		double weight;

		if (taskMonitor != null)
		{
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Reading graph...");
		}

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
				numEdges++;
			}

			if (taskMonitor != null && numEdges % 100000 == 0) {
				taskMonitor.setPercentCompleted(-1);
				taskMonitor.setStatus(result.getNodeCount() + " node(s), " + numEdges + " edge(s)");
			}
		}

		if (taskMonitor != null) {
			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus(result.getNodeCount() + " node(s), " + numEdges + " edge(s)");
		}

		return result;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

}
