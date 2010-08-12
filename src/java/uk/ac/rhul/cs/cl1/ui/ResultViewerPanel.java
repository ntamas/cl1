package uk.ac.rhul.cs.cl1.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;
import uk.ac.rhul.cs.cl1.ui.NodeSetTableModel;
import uk.ac.rhul.cs.utils.StringUtils;

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
	 * The top toolbar
	 */
	protected JToolBar topToolBar;
	
	/**
	 * The table shown within the panel
	 */
	protected JTable table;
	
	/**
	 * The scroll pane encapsulating the table
	 */
	protected JScrollPane scrollPane;
	
	/**
	 * Constructor
	 */
	public ResultViewerPanel() {
		this(null);
	}
	
	/**
	 * Constructor
	 */
	public ResultViewerPanel(List<ValuedNodeSet> nodeSets) {
		this.setLayout(new BorderLayout());
		
		/* Create the label showing the number of clusters */
		countLabel = new JLabel();
		countLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		
		/* Add the result table */
		table = new JTable() {
			@Override
			public JToolTip createToolTip() {
				JMultiLineToolTip toolTip = new JMultiLineToolTip();
				toolTip.setFixedWidth(300);
				return toolTip;
			}
			@Override
			public String getToolTipText(MouseEvent e) {
				TableModel model = getTableModel();
				if (!(model instanceof NodeSetTableModel))
					return "";
				
				Point p = e.getPoint();
				int rowIndex = convertRowIndexToModel(rowAtPoint(p));
				return StringUtils.join(
						((NodeSetTableModel)model).getMemberNames(rowIndex), ", ");
			}
		};

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setIntercellSpacing(new Dimension(0, 4));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		scrollPane = new JScrollPane(table);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER);
		
		if (nodeSets != null)
			this.setNodeSets(nodeSets);
		
		/* Add a toolbar to the top */
		topToolBar = new JToolBar();
		topToolBar.add(countLabel);
		topToolBar.add(Box.createHorizontalGlue());
		topToolBar.add(new JToggleButton(new ShowDetailedResultsAction(this)));
		topToolBar.setFloatable(false);
		topToolBar.setRollover(false);
		topToolBar.setBorderPainted(false);
		topToolBar.setOpaque(false);
		this.add(topToolBar, BorderLayout.NORTH);
	}
	
	/**
	 * Adds a new action to the toolbar
	 */
	public JButton addAction(Action action) {
		JButton button = topToolBar.add(action);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setRolloverEnabled(true);
		return button;
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
	 * Gets the scroll pane shown within the panel
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
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
	public void setNodeSets(List<ValuedNodeSet> set) {
		int n = set.size();
		
		/* Set up the table model, ensure that the table's columns are reformatted when
		 * the model is updated (i.e. when switching detailed mode on/off)
		 */
		NodeSetTableModel model = new NodeSetTableModel(set);
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent arg0) {
				setupTableColumnModel();
			}
		});
		table.setModel(model);
		
		setupTableColumnModel();
		
		scrollPane.setPreferredSize(table.getPreferredSize());
		
		if (n == 0)
			countLabel.setText("No clusters");
		else if (n == 1)
			countLabel.setText("1 cluster");
		else
			countLabel.setText(set.size()+" clusters");
		
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
	 * Makes some necessary adjustments to a freshy created column model of the table
	 */
	private void setupTableColumnModel() {
		NodeSetTableModel model = (NodeSetTableModel)table.getModel();
		TableColumnModel colModel = table.getColumnModel();
		
		/* First column: preferred width and height is 60 pixels */
		colModel.getColumn(0).setPreferredWidth(60);
		colModel.getColumn(0).setMaxWidth(60);
		
		if (!model.isInDetailedMode()) {
			/* Don't set row heights, let the cell renderer for the details column do it */
			colModel.getColumn(1).setPreferredWidth(120);
			colModel.getColumn(1).setCellRenderer(new HeightLimitedJLabelRenderer(50));
		} else {
			/* Set a unique row height */
			table.setRowHeight(60);
			/* Set a special renderer for P-values */
			colModel.getColumn(6).setCellRenderer(new PValueRenderer());
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
