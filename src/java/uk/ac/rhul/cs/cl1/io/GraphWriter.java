package uk.ac.rhul.cs.cl1.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import uk.ac.rhul.cs.graph.Graph;

/**
 * Interface specification for all the graph writers
 * @author tamas
 */
public interface GraphWriter {
	/**
	 * Writes the graph to the given stream
	 * 
	 * @param graph     the graph to be written
	 * @param stream    the stream to write to
	 * @throws IOException
	 */
	public void writeGraph(Graph graph, OutputStream stream) throws IOException;
	
	/**
	 * Saves the graph to the given file
	 * 
	 * @param graph     the graph to be saved
	 * @param file     the file itself
	 * @throws IOException
	 */
	public void writeGraph(Graph graph, File file) throws IOException;
	
	/**
	 * Saves the graph to the given file
	 * 
	 * @param graph     the graph to be saved
	 * @param filename  the filename
	 * @throws IOException
	 */
	public void writeGraph(Graph graph, String filename) throws IOException;
}
