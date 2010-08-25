package uk.ac.rhul.cs.cl1.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import uk.ac.rhul.cs.cl1.CohesivenessFunction;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.QualityFunction;

/**
 * A panel that shows some properties of a {@link NodeSet}
 * @author tamas
 */
public class NodeSetPropertiesPanel extends JPanel {
	/** The nodeset for which we are showing the properties */
	protected NodeSet nodeSet = null;
	
	/**
	 * The quality function we are working with
	 * 
	 * @todo  Fix it, it should not be hardwired here
	 */
	protected final QualityFunction qualityFunc = new CohesivenessFunction();
	
	/** An information label */
	protected JLabel label = null;
	
	/** Table containing the details */
	protected JTable detailsTable = null;
	
	/** Table model */
	protected DefaultTableModel model = null;
	
	/** Table column headers */
	protected static String[] columnNames = {"Property", "Value"};
	
	class RightAlignedLabelRenderer extends JLabel implements TableCellRenderer {
		public RightAlignedLabelRenderer() {
			super("", SwingConstants.RIGHT);
		}
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value == null)
				this.setText("");
			else
				this.setText(value.toString());
			return this;
		}
	};
	
	/** Constructor */
	public NodeSetPropertiesPanel() {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		label = new JLabel("", SwingConstants.CENTER);
		label.setMaximumSize(null);
		this.add(label);
		
		detailsTable = new JTable(5, 2);
		// detailsTable.setBorder(BorderFactory.createEtchedBorder());
		detailsTable.setRowSelectionAllowed(false);
		detailsTable.setColumnSelectionAllowed(false);
		detailsTable.setCellSelectionEnabled(false);
		detailsTable.setFocusable(false);
		detailsTable.setAutoCreateColumnsFromModel(true);
		detailsTable.setBackground(this.getBackground());
		detailsTable.setGridColor(this.getBackground());
		detailsTable.setFont(this.getFont());
		detailsTable.setIntercellSpacing(new Dimension(5, 0));
		this.add(detailsTable);
		
		setupTableModel();
		updatePanel();
	}
	
	/** Sets up the table model */
	protected void setupTableModel() {
		model = new DefaultTableModel(columnNames, 0);
		String[] rowNames = {"Number of nodes:", "In-weight:", "Out-weight:", "Density:", "Quality:", "P-value:"};
		Object[] row = new Object[2];
		
		for (String rowName: rowNames) {
			row[0] = rowName; row[1] = null;
			model.addRow(row);
		}
		
		detailsTable.setModel(model);
		detailsTable.getColumn(detailsTable.getColumnName(0)).setCellRenderer(new RightAlignedLabelRenderer());
	}
	
	/** Updates the components of the panel when the nodeset changed */
	protected void updatePanel() {
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
					PValueRenderer.formatValue(qualityFunc.calculate(this.nodeSet), false),
					4, 1);
			model.setValueAt(
					PValueRenderer.formatValue(this.nodeSet.getSignificance(), false),
					5, 1);
		} else {
			for (int i = 0; i < model.getRowCount(); i++)
				model.setValueAt(null, i, 1);
		}
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
