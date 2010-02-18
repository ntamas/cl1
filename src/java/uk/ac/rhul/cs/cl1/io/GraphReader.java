package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.rhul.cs.cl1.Graph;

/// Interface specification for all the graph readers
public interface GraphReader {
	/// Reads a graph from a stream
	Graph readGraph(InputStream stream) throws IOException;
}
