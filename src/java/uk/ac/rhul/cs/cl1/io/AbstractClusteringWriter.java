package uk.ac.rhul.cs.cl1.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import uk.ac.rhul.cs.cl1.ValuedNodeSet;

/**
 * Default implementation for some of the methods of {@link ClusteirngWriter}
 * that will just call {@link writeClustering(List<NodeSet>, OutputStream)} anyway.
 * 
 * @author tamas
 */
public abstract class AbstractClusteringWriter implements ClusteringWriter {
	/**
	 * Writes the clustering to the given file
	 * 
	 * @param clustering   the clustering to be written
	 * @param filename  the filename
	 * @throws IOException
	 */
	public void writeClustering(List<ValuedNodeSet> clustering, String filename)
			throws IOException {
		writeClustering(clustering, new File(filename));
	}

	/**
	 * Writes the clustering to the given file
	 * 
	 * @param clustering   the clustering to be written
	 * @param file     the file itself
	 * @throws IOException
	 */
	public void writeClustering(List<ValuedNodeSet> clustering, File file)
			throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		writeClustering(clustering, os);
		os.close();
	}

}
