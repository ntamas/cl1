package uk.ac.rhul.cs.cl1.io;

import java.util.Arrays;
import java.util.TreeSet;

import uk.ac.rhul.cs.utils.StringUtils;

/**
 * Factory class that constructs {@link GraphReader} objects
 * 
 * @author ntamas
 */
public class GraphReaderFactory {
	public enum Format {
		EDGE_LIST(), SIF("sif");
		
		TreeSet<String> extensions = null;
		
		Format() {}
		
		Format(String extension) {
			this.extensions = new TreeSet<String>();			
			this.extensions.add(extension);
		}
		
		Format(String[] extensions) {
			this.extensions = new TreeSet<String>(Arrays.asList(extensions));
		}
		
		public static Format fromFilename(String filename) {
			String ext = StringUtils.getFileExtension(filename);
			for (Format format: Format.values()) {
				if (format.extensions != null && format.extensions.contains(ext))
					return format;
			}
			
			return Format.EDGE_LIST;
		}
	}
	
	/**
	 * Constructs a {@link GraphReader} from the given {@link Format}
	 * 
	 * @param format   the format for which we need a {@link GraphReader}
	 */
	public static GraphReader fromFormat(Format format) {
		switch (format) {
		case SIF:
			return new SIFReader();
		case EDGE_LIST:
			return new EdgeListReader();
		default:
			return null;
		}
	}
	
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
		return fromFormat(Format.fromFilename(filename));
	}
}
