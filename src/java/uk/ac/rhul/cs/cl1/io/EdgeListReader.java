package uk.ac.rhul.cs.cl1.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import uk.ac.rhul.cs.cl1.TaskMonitor;
import uk.ac.rhul.cs.cl1.TaskMonitorSupport;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.utils.UniqueIDGenerator;

/**
 * Reads a graph specified by an edge list representation from an input stream
 * @author ntamas
 */
public class EdgeListReader implements GraphReader, TaskMonitorSupport {
	/**
	 * Task monitor that will be used by the reader to report its progress.
	 */
	private TaskMonitor taskMonitor;

	/**
	 * Reads a graph specified by an edge list representation
	 * from the given reader object.
	 * 
	 * @param  reader  the reader being used
	 */
	public Graph readGraph(Reader reader) throws IOException {
		Graph result = new Graph();
		UniqueIDGenerator<String> nodeGen = new UniqueIDGenerator<String>(result);
		BufferedReader bufferedReader = new BufferedReader(reader);
		String line;

		int node1, node2;
		int startIndex, endIndex;
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
			
			if (line.charAt(0) == '#' || line.charAt(0) == '%')
				continue;

			startIndex = nextNonWhitespace(line, 0);
			if (startIndex == -1)
				continue;

			endIndex = nextWhitespace(line, startIndex);
			if (endIndex == -1)
				continue;

			node1 = nodeGen.get(line.substring(startIndex, endIndex));
			startIndex = nextNonWhitespace(line, endIndex);
			if (startIndex == -1)
				continue;

			endIndex = nextWhitespace(line, startIndex);
			if (endIndex == -1) {
				node2 = nodeGen.get(line.substring(startIndex));
				weight = 1.0;
			} else {
				node2 = nodeGen.get(line.substring(startIndex, endIndex));
				startIndex = nextNonWhitespace(line, endIndex);
				if (startIndex == -1) {
					weight = 1.0;
				} else {
					weight = Double.parseDouble(line.substring(startIndex));
				}
			}

			result.createEdge(node1, node2, weight);

			numEdges++;
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

	/**
	 * @inheritDoc
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Given a string and a starting index, returns the next index where
	 * a space or Tab character occurs.
	 *
	 * @param  string      the string
	 * @param  fromIndex   the start index
	 * @return the smallest index no smaller than startIndex such that the char
	 *         at the given index is a space or a Tab, or -1 if there are
	 *         no whitespace characters after the given start index
	 */
	private int nextWhitespace(String string, int fromIndex) {
		int i, n = string.length();
		char ch;

		for (i = fromIndex; i < n; i++) {
			ch = string.charAt(i);
			if (ch == ' ' || ch == '\t') {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Given a string and a starting index, returns the next index where
	 * anything else than a space or Tab character occurs.
	 *
	 * @param  string      the string
	 * @param  fromIndex   the start index
	 * @return the smallest index no smaller than startIndex such that the char
	 *         at the given index is not a space or a Tab, or -1 if there are
	 *         no non-whitespace characters after the given start index
	 */
	private int nextNonWhitespace(String string, int fromIndex) {
		int i, n = string.length();
		char ch;

		for (i = fromIndex; i < n; i++) {
			ch = string.charAt(i);
			if (ch != ' ' && ch != '\t') {
				return i;
			}
		}

		return -1;
	}
}
