package uk.ac.rhul.cs.cl1.io;

/**
 * Factory class that constructs {@link GraphReader} objects
 * 
 * @author ntamas
 */
public class GraphReaderFactory {
	/**
	 * Constructs a {@link GraphReader} based on the given filename
	 * 
	 * The {@link GraphReader} returned depends on the extension of the
	 * given filename. If the extension is <tt>.sif</tt>, a
	 * {@link SIFReader} will be returned. All other extensions simply
	 * return an {@link EdgeListReader}.
	 * 
	 * @param filename   the name of the file for which we need a graph reader
	 */
	public static GraphReader fromFilename(String filename) {
		if (filename == null)
			return null;
		
		if (filename.endsWith(".sif"))
			return new SIFReader();
		
		return new EdgeListReader();
	}
}
