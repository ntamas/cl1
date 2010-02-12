package uk.ac.rhul.cs.cl1.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * A panel that shows some properties of a {@link NodeSet}
 * @author tamas
 */
public class NodeSetPropertiesPanel extends JPanel {
	/** The nodeset for which we are showing the properties */
	protected NodeSet nodeSet = null;
	
	/** An information label */
	protected JLabel label = null;
	
	/** Table containing the details */
	protected JTable detailsTable = null;
	
	/** Table column headers */
	protected static String[] columnNames = {"Property", "Value"};
	
	/** Constructor */
	public NodeSetPropertiesPanel() {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		label = new JLabel("", SwingConstants.CENTER);
		label.setMaximumSize(null);
		this.add(label);
		
		detailsTable = new JTable(5, 2);
		detailsTable.setBorder(BorderFactory.createEtchedBorder());
		detailsTable.setRowSelectionAllowed(false);
		detailsTable.setColumnSelectionAllowed(false);
		detailsTable.setCellSelectionEnabled(false);
		detailsTable.setAutoCreateColumnsFromModel(true);
		this.add(detailsTable);
		
		updatePanel();
	}
	
	/** Updates the components of the panel when the nodeset changed */
	protected void updatePanel() {
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		String[] rowNames = {"Number of nodes", "In-weight", "Out-weight", "Density", "Quality", "P-value"};
		Object[] row = new Object[2];
		
		for (String rowName: rowNames) {
			row[0] = rowName; row[1] = null;
			model.addRow(row);
		}
		
		if (nodeSet != null) {
			model.setValueAt(this.nodeSet.size(), 0, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getTotalInternalEdgeWeight(), false),
					1, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getTotalBoundaryEdgeWeight(), false),
					2, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getDensity(), false),
					3, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getQuality(), false),
					4, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getSignificance(), false),
					5, 1);
		}
		
		detailsTable.setModel(model);
	}
	
	/**
	 * Returns the {@link NodeSet} whose properties are shown
	 * @return the node set
	 */
	public NodeSet getNodeSet() {
		return nodeSet;
	}

	/**
	 * Sets the {@link NodeSet} whose properties are shown
	 * @param nodeSet the nodeSet to show
	 */
	public void setNodeSet(NodeSet nodeSet) {
		this.nodeSet = nodeSet;
		updatePanel();
	}
}
