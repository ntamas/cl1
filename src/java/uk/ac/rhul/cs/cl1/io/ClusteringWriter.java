package uk.ac.rhul.cs.cl1.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import uk.ac.rhul.cs.cl1.ValuedNodeSet;

/**
 * Interface specification for all the clustering writers.
 * 
 * Clustering writes write a clustering to a stream using a specific format.
 * 
 * @author tamas
 */
public interface ClusteringWriter {
	/**
	 * Writes the clustering to the given stream
	 * 
	 * @param clustering   the clustering to be written
	 * @param stream       the stream to write to
	 * @throws IOException
	 */
	public void writeClustering(List<ValuedNodeSet> clustering,
			OutputStream stream) throws IOException;
	
	/**
	 * Writes the clustering to the given file
	 * 
	 * @param clustering   the clustering to be written
	 * @param file     the file itself
	 * @throws IOException
	 */
	public void writeClustering(List<ValuedNodeSet> clustering,
			File file) throws IOException;
	
	/**
	 * Writes the clustering to the given file
	 * 
	 * @param clustering   the clustering to be written
	 * @param filename  the filename
	 * @throws IOException
	 */
	public void writeClustering(List<ValuedNodeSet> clustering,
			String filename) throws IOException;
}
