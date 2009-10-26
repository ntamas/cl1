package uk.ac.rhul.cs.cl1.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Table model that can be used to show a list of {@link NodeSet} objects
 * in a JTable
 * 
 * @author tamas
 */
public class NodeSetTableModel extends AbstractTableModel {
	/** Column headers for the simple mode */
	String[] simpleHeaders = { "Cluster", "Details" };
	
	/** Column headers for the detailed mode */
	String[] detailedHeaders = { "Cluster", "Nodes", "Density" };
	
	/** Column headers for the current mode */
	String[] currentHeaders = null;
	
	/** Formatter used to format fractional numbers */
	NumberFormat fractionalFormat = new DecimalFormat("0.000");
	
	/**
	 * The list of {@link NodeSet} objects shown in this model
	 */
	protected List<NodeSet> nodeSets;
	
	/**
	 * Whether the model is in detailed mode or simple mode
	 * 
	 * In the simple mode, only two columns are shown: the cluster members
	 * and some basic properties (in a single column). In the detailed mode,
	 * each property has its own column
	 */
	boolean detailedMode;
	
	/**
	 * Constructs a new table model backed by the given list of nodesets
	 */
	public NodeSetTableModel(List<NodeSet> nodeSets) {
		this.nodeSets = new ArrayList<NodeSet>(nodeSets);
		this.setDetailedMode(false);
	}

	@Override
	public int getColumnCount() {
		return currentHeaders.length;
	}

	@Override
	public int getRowCount() {
		return nodeSets.size();
	}
	
	/**
	 * Returns whether the table model is in detailed mode
	 */
	public boolean getDetailedMode() {
		return detailedMode;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		return String.class;
	}
	
	@Override
	public String getColumnName(int col) {
		return currentHeaders[col];
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		NodeSet nodeSet = this.nodeSets.get(row);
		if (nodeSet == null)
			return null;
		
		if (col == 0)
			return nodeSet.toString();
		
		if (!detailedMode) {
			/* Simple mode, column 1 */
			return getClusterDetails(nodeSet);
		}
		
		/* Detailed mode */
		return "TODO";
	}
	
	/**
	 * Returns the {@link NodeSet} shown in the given row.
	 * 
	 * @param row   the row index
	 * @return   the corresponding {@link NodeSet}
	 */
	public NodeSet getNodeSetByIndex(int row) {
		return nodeSets.get(row);
	}
	
	/**
	 * Returns a detailed description of a cluster in the Details column
	 */
	public String getClusterDetails(NodeSet nodeSet) {
		StringBuilder sb = new StringBuilder();
		sb.append("Nodes: "); sb.append(nodeSet.size()); sb.append("\n");
		sb.append("Density: "); sb.append(fractionalFormat.format(nodeSet.getDensity()));
		return sb.toString();
	}
	
	/**
	 * Returns whether the table model is in detailed mode
	 */
	public void setDetailedMode(boolean mode) {
		currentHeaders = detailedMode ? detailedHeaders : simpleHeaders;
		
		if (mode == detailedMode)
			return;
		
		detailedMode = mode;
		this.fireTableStructureChanged();
	}
	
}
