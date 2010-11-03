package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import uk.ac.rhul.cs.cl1.ValuedNodeSet;

/**
 * Writes a clustering to a stream in a simple tab-separated format.
 * 
 * Each line will contain a single cluster. Members of a cluster will be separated by
 * Tab characters. No other information is reported by a cluster at all.
 * 
 * @author tamas
 */
public class PlainTextClusteringWriter extends AbstractClusteringWriter {
	public void writeClustering(List<ValuedNodeSet> clustering,
			OutputStream stream) throws IOException {
		PrintWriter wr = new PrintWriter(stream);
		for (ValuedNodeSet cluster: clustering) {
			wr.println(cluster.toString("\t"));
		}
		wr.flush();
	}
}
