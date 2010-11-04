package uk.ac.rhul.cs.cl1.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import uk.ac.rhul.cs.cl1.DummyQualityFunction;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.QualityFunction;
import uk.ac.rhul.cs.utils.StringUtils;

/**
 * Writes a clustering to a stream in CSV format.
 * 
 * The table will contain some basic statistics about the clusters such as
 * their size, density, internal and external weight etc.
 * 
 * @author ntamas
 */
public class CSVClusteringWriter extends AbstractClusteringWriter {
	private String columnSep;
	private String doubleQuoteChar;
	private String quoteChar;
	private String quoteTriggers;
	
	private QualityFunction qualityFunction = null;
	
	public CSVClusteringWriter() {
		this(",", "\"");
	}
	
	public CSVClusteringWriter(String columnSep, String quoteChar) {
		super();
		this.setColumnSeparator(columnSep);
		this.setQuoteChar(quoteChar);
		this.setQualityFunction(null);
	}
	
	public void setColumnSeparator(String columnSep) {
		this.columnSep = columnSep;
		this.quoteTriggers = " " + this.columnSep + this.quoteChar;
	}
	
	public void setQualityFunction(QualityFunction qualityFunction) {
		if (qualityFunction == null)
			qualityFunction = new DummyQualityFunction();
		this.qualityFunction = qualityFunction;
	}
	
	public void setQuoteChar(String quoteChar) {
		this.quoteChar = quoteChar;
		this.quoteTriggers = " " + this.columnSep + this.quoteChar;
		this.doubleQuoteChar = this.quoteChar + this.quoteChar;
	}
	
	public void writeClustering(List<? extends NodeSet> clustering,
			OutputStream stream) throws IOException {
		PrintWriter wr = new PrintWriter(stream);
		String[] parts = {
				"Cluster", "Size", "Density", "Internal weight",
				"External weight", "Quality", "P-value", "Members"
		};
		
		int clusterIndex = 0;
		
		wr.println(StringUtils.join(parts, columnSep));
		
		for (NodeSet nodeSet: clustering) {
			clusterIndex++;
			
			parts[0] = Integer.toString(clusterIndex);
			parts[1] = Integer.toString(nodeSet.size());
			parts[2] = quote(String.format("%.4g", nodeSet.getDensity()));
			parts[3] = quote(String.format("%.4g", nodeSet.getTotalInternalEdgeWeight()));
			parts[4] = quote(String.format("%.4g", nodeSet.getTotalBoundaryEdgeWeight()));
			parts[5] = quote(String.format("%.4g", qualityFunction.calculate(nodeSet)));
			parts[6] = quote(String.format("%g", nodeSet.getSignificance()));
			parts[7] = quote(nodeSet.toString(" "));
			
			wr.println(StringUtils.join(parts, columnSep));
		}
		
		wr.flush();
	}
	
	private String quote(String str) {
		if (!StringUtils.containsAny(str, quoteTriggers))
			return str;
		
		return quoteChar + str.replace(quoteChar, doubleQuoteChar) + quoteChar;
	}
}
