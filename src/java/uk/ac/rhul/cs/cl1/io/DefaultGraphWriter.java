package uk.ac.rhul.cs.cl1.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.ac.rhul.cs.graph.Graph;

/**
 * Default implementation for some of the methods of {@link GraphWriter}
 * that will just call {@link writeGraph(Graph, OutputStream)} anyway.
 * 
 * @author tamas
 */
public abstract class DefaultGraphWriter implements GraphWriter {
	/**
	 * Saves the graph to the given file
	 * 
	 * @param graph     the graph to be saved
	 * @param filename  the filename
	 * @throws IOException
	 */
	public void writeGraph(Graph graph, String filename) throws IOException {
		writeGraph(graph, new File(filename));
	}
	
	/**
	 * Saves the graph to the given file
	 * 
	 * @param graph   the graph to be saved
	 * @param file    the file itself
	 * @throws IOException
	 */
	public void writeGraph(Graph graph, File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		writeGraph(graph, os);
		os.close();
	}
}
