package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.InputStream;

import giny.model.RootGraph;

/// Interface specification for all the graph readers
public interface GraphReader {
	/// Reads a GINY graph from a stream
	RootGraph readGraph(InputStream stream) throws IOException;
}
