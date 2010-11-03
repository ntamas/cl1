package uk.ac.rhul.cs.cl1.io;

import java.io.File;

import uk.ac.rhul.cs.utils.StringUtils;

/**
 * Factory class that constructs {@link ClusteringWriter} objects
 * 
 * @author ntamas
 */
public class ClusteringWriterFactory {
	public enum Format {
		PLAIN("Cluster list", "txt"),
		CSV("CSV-formatted detailed cluster list", "csv"),
		GENEPRO("GenePro formatted cluster list", "tab");
		
		private String extension;
		private String name;

		Format(String name, String extension) {
			this.name = name;
			this.extension = extension;
		}
		
		public static Format forFile(File file) {
			String extension = StringUtils.getFileExtension(file);
			
			for (Format format: Format.values()) {
				if (format.extension.equals(extension))
					return format;
			}
			
			return null;
		}
		
		public String getExtension() {
			return extension;
		}
		
		public String getName() {
			return name;
		}
	}
	
	/**
	 * Constructs a {@link ClusteringWriter} from the given {@link Format}
	 * 
	 * @param format   the format for which we need a {@link ClusteringWriter}
	 */
	public static ClusteringWriter fromFormat(Format format) {
		switch (format) {
		case PLAIN:
			return new PlainTextClusteringWriter();
		case CSV:
			return new CSVClusteringWriter();
		case GENEPRO:
			return new GeneProClusteringWriter();
		default:
			return null;
		}
	}
}
