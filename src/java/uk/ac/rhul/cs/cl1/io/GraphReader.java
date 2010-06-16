package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.Reader;

import uk.ac.rhul.cs.cl1.Graph;

/// Interface specification for all the graph readers
public interface GraphReader {
	/// Reads a graph from a reader
	Graph readGraph(Reader reader) throws IOException;
}
