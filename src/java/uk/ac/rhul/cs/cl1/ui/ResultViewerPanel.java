package uk.ac.rhul.cs.cl1.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import uk.ac.rhul.cs.cl1.NodeSet;

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
		
		countLabel = new JLabel();
		countLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		
		this.add(countLabel, BorderLayout.NORTH);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Gets the table shown within the panel
	 */
	public JTable getTable() {
		return table;
	}
	
	/**
	 * Sets the list of nodesets to be shown in this result viewer panel
	 */
	public void setNodeSets(List<NodeSet> set) {
		int n = set.size();
		
		table.setModel(new NodeSetTableModel(set));
		table.getColumnModel().getColumn(1).setCellRenderer(new JTextAreaRenderer(10));
		
		if (n == 0)
			countLabel.setText("No clusters detected");
		else if (n == 1)
			countLabel.setText("1 cluster detected");
		else
			countLabel.setText(set.size()+" clusters detected");
	}
}
