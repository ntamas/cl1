package uk.ac.rhul.cs.cl1.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Detailed statistics about a nodeset.
 * 
 * Instances of this class are used in the result viewer table. Instances
 * are immutable and the properties are pre-calculated - either in the
 * {@link NodeSet} itself or here in {@link NodeSetDetails}.
 * 
 * @author ntamas
 */
public class NodeSetDetails implements Comparable<NodeSetDetails> {
	/** Formatter used to format fractional numbers */
	private static final NumberFormat fractionalFormat = new DecimalFormat("0.000");
	
	/**
	 * The associated nodeset
	 */
	protected final NodeSet nodeSet;
	
	/**
	 * Cached string representation
	 */
	protected String stringRep;
	
	/**
	 * Constructs a NodeSetDetails object corresponding to the given {@link NodeSet}
	 */
	public NodeSetDetails(NodeSet nodeSet) {
		this.nodeSet = nodeSet;
	}
	
	/**
	 * Converts this object to a string representation
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html>Nodes: ");
		sb.append(nodeSet.size());
		sb.append("<br>");
		
		sb.append("Density: ");
		sb.append(fractionalFormat.format(nodeSet.getDensity()));
		sb.append("<br>");
		
		sb.append("Quality: ");
		sb.append(fractionalFormat.format(nodeSet.getQuality()));
		sb.append("<br>");
		
		double significance = nodeSet.getSignificance();
		sb.append("P-value: <font color=\"");
		sb.append(PValueRenderer.getColorCodeForValue(significance));
		sb.append("\">");
		sb.append(PValueRenderer.formatValue(significance, false));
		sb.append("</font>");
		
		sb.append("</html>");

		return sb.toString();
	}
	
	/**
	 * Compares this object to another (used to sort the result viewer table)
	 * 
	 * @param  other   the other object
	 * @throws  NullPointerException  if the other object is null
	 */
	public int compareTo(NodeSetDetails other) {
		final int BEFORE = -1;
		final int EQUAL  = 0;
		final int AFTER  = 1;
		
		if (this == other)
			return EQUAL;
		
		if (this.nodeSet.equals(other.nodeSet))
			return EQUAL;
		
		double sigThis = this.nodeSet.getSignificance();
		double sigThat = other.nodeSet.getSignificance();
		
		if (sigThis < sigThat)
			return AFTER;
		if (sigThis > sigThat)
			return BEFORE;
		
		double qThis = this.nodeSet.getQuality();
		double qThat = other.nodeSet.getQuality();
		int sizeThis = this.nodeSet.size();
		int sizeThat = other.nodeSet.size();
				
		if (qThis*sizeThis > qThat*sizeThat)
			return AFTER;
		if (qThis*sizeThis < qThat*sizeThat)
			return BEFORE;
		
		double densityThis = this.nodeSet.getDensity();
		double densityThat = other.nodeSet.getDensity();
		
		if (densityThis > densityThat)
			return AFTER;
		if (densityThis < densityThat)
			return BEFORE;
		
		return this.nodeSet.hashCode() - other.nodeSet.hashCode();
	}
}
