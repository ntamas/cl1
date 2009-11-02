package uk.ac.rhul.cs.cl1.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;

/**
 * A Swing panel that shows the results of a Cluster ONE run
 * 
 * @author tamas
 */
public class ResultViewerPanel extends JPanel {
	/**
	 * Information label showing the number of elements in the resulting nodeset
	 */
	protected JLabel countLabel;
	
	/**
	 * The table shown within the panel
	 */
	protected JTable table;
	
	/**
	 * Constructor
	 */
	public ResultViewerPanel() {
		this.setLayout(new BorderLayout());
		
		/* Add the label showing the number of clusters */
		countLabel = new JLabel();
		countLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		this.add(countLabel, BorderLayout.NORTH);
		
		/* Add the result table */
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setIntercellSpacing(new Dimension(0, 4));

		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);	
	}
	
	/**
	 * Retrieves the selected {@link NodeSet}
	 */
	public NodeSet getSelectedNodeSet() {
		Integer selectedIndex = this.getSelectedNodeSetIndex();
		if (selectedIndex == null)
			return null;
		
		return this.getTableModel().getNodeSetByIndex(selectedIndex);
	}
	
	/**
	 * Retrieves the selected {@link NodeSet}s
	 */
	public List<NodeSet> getSelectedNodeSets() {
		NodeSetTableModel model = this.getTableModel();
		List<NodeSet> result = new ArrayList<NodeSet>();
		for (int idx: this.getSelectedNodeSetIndices()) {
			result.add(model.getNodeSetByIndex(idx));
		}
		return result;
	}
	
	/**
	 * Retrieves the index of the selected {@link NodeSet} in the original result set.
	 */
	public Integer getSelectedNodeSetIndex() {
		int selectedRow = this.table.getSelectedRow();
		if (selectedRow == -1)
			return null;
		return this.table.convertRowIndexToModel(selectedRow);
	}
	
	/**
	 * Retrieves the indices of the selected {@link NodeSet}s in the original result set.
	 */
	public int[] getSelectedNodeSetIndices() {
		int[] selectedRows = this.table.getSelectedRows();
		int[] selectedRowsInModel = new int[selectedRows.length];
		int i = 0;
		for (int idx: selectedRows) {
			selectedRowsInModel[i] = this.table.convertRowIndexToModel(idx);
			i++;
		}
		return selectedRowsInModel;
	}
	
	/**
	 * Gets the table shown within the panel
	 */
	public JTable getTable() {
		return table;
	}
	
	/**
	 * Gets the table model of the panel
	 */
	public NodeSetTableModel getTableModel() {
		return (NodeSetTableModel)this.table.getModel();
	}
	
	/**
	 * Sets the list of nodesets to be shown in this result viewer panel
	 */
	public void setNodeSets(List<NodeSet> set) {
		int n = set.size();
		NodeSetTableModel model = new NodeSetTableModel(set);
		
		table.setModel(model);
		table.getColumnModel().getColumn(1).setCellRenderer(new JTextAreaRenderer(10));
		
		if (n == 0)
			countLabel.setText("No clusters detected");
		else if (n == 1)
			countLabel.setText("1 cluster detected");
		else
			countLabel.setText(set.size()+" clusters detected");
		
		/* Try to make the table sortable. If the JRE is too old, simply leave it as is */
		try {
			TableRowSorter<NodeSetTableModel> rowSorter;
			rowSorter = new TableRowSorter<NodeSetTableModel>(model);
			/* The cluster column is never sortable */
			rowSorter.setSortable(0, false);
			/* Sort on the details column by default if not in detailed mode */
			if (!model.isInDetailedMode()) {
				List<RowSorter.SortKey> list = new ArrayList<RowSorter.SortKey>();
				list.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
				rowSorter.setSortKeys(list);
			}
			table.setRowSorter(rowSorter);
		} catch (Exception ex) {
			/* well, meh */
		}
	}
	
	/**
	 * Adjusts the selection by selecting the given indices and deselecting everything else
	 */
	public void setSelectedNodeSetIndices(int[] indices) {
		ListSelectionModel model = this.table.getSelectionModel();
		
		model.setValueIsAdjusting(true);
		model.clearSelection();
		for (int i: indices)
			model.addSelectionInterval(i, i);
		model.setValueIsAdjusting(false);
	}

	/**
	 * Adjusts the selection by selecting the given indices and deselecting everything else
	 */
	public void setSelectedNodeSetIndices(List<Integer> indices) {
		ListSelectionModel model = this.table.getSelectionModel();
		
		model.setValueIsAdjusting(true);
		model.clearSelection();
		for (int i: indices) {
			int j = this.table.convertRowIndexToView(i);
			model.addSelectionInterval(j, j);
		}
		model.setValueIsAdjusting(false);
	}
}
