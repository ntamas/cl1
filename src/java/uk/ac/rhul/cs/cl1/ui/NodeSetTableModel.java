package uk.ac.rhul.cs.cl1.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.sosnoski.util.hashmap.ObjectIntHashMap;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.FruchtermanReingoldLayoutAlgorithm;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.GraphLayoutAlgorithm;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;

/**
 * Table model that can be used to show a list of {@link NodeSet} objects
 * in a JTable.
 * 
 * @author tamas
 */
public class NodeSetTableModel extends AbstractTableModel {
	private static final Color DARK_RED = new Color(128, 0, 0);
	
	/** Column headers for the simple mode */
	String[] simpleHeaders = { "Cluster", "Details" };
	
	/** Column classes for the simple mode */
	Class<?>[] simpleClasses = { ImageIcon.class, NodeSetDetails.class };
	
	/** Column headers for the detailed mode */
	String[] detailedHeaders = { "Cluster", "Nodes", "Density",
			"In-weight", "Out-weight", "Quality", "P-value" };
	
	/** Column classes for the detailed mode */
	Class<?>[] detailedClasses = {
		ImageIcon.class, Integer.class, Double.class, Double.class, Double.class, Double.class,
		Double.class
	};
	
	/** Column headers for the current mode */
	String[] currentHeaders = null;
	
	/** Column classes for the current mode */
	Class<?>[] currentClasses = null;
	
	/**
	 * The list of {@link NodeSet} objects shown in this model
	 */
	protected List<ValuedNodeSet> nodeSets = null;
	
	/**
	 * The list of rendered cluster graphs for all the {@link NodeSet} objects shown in this model
	 */
	protected List<Future<Icon>> nodeSetIcons = new ArrayList<Future<Icon>>();
	
	/**
	 * The list of {@link NodeSetDetails} objects to avoid having to calculate
	 * the Details column all the time
	 */
	protected List<NodeSetDetails> nodeSetDetails = new ArrayList<NodeSetDetails>();
	
	/**
	 * Whether the model is in detailed mode or simple mode
	 * 
	 * In the simple mode, only two columns are shown: the cluster members
	 * and some basic properties (in a single column). In the detailed mode,
	 * each property has its own column
	 */
	boolean detailedMode = true;
	
	/**
	 * Icon showing a circular progress indicator. Loaded on demand from resources.
	 */
	private Icon progressIcon = null;
	
	/**
	 * Internal class that represents the task that renders the cluster in the result table
	 */
	private class RendererTask extends FutureTask<Icon> {
		int rowIndex;
		
		public RendererTask(int rowIndex, Graph subgraph, GraphLayoutAlgorithm algorithm,
				HashMap<Integer, Color> colorMapping) {
			super(new GraphRenderer(subgraph, algorithm, colorMapping));
			this.rowIndex = rowIndex;
		}
		
		protected void done() {
			fireTableCellUpdated(rowIndex, 0);
		}
	}
	
	/**
	 * Constructs a new table model backed by the given list of nodesets
	 */
	public NodeSetTableModel(List<ValuedNodeSet> nodeSets) {
		this.nodeSets = new ArrayList<ValuedNodeSet>(nodeSets);
		updateNodeSetDetails();
		this.setDetailedMode(false);
	}

	public int getColumnCount() {
		return currentHeaders.length;
	}

	public int getRowCount() {
		return nodeSets.size();
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return currentClasses[col];
	}
	
	@Override
	public String getColumnName(int col) {
		return currentHeaders[col];
	}
	
	/**
	 * Returns the names of members in a given row.
	 * 
	 * @param row  the index of the row for which we need the list
	 * @return  an array containing the names of the members
	 */
	public String[] getMemberNames(int row) {
		NodeSet nodeSet = this.nodeSets.get(row);
		if (nodeSet == null)
			return new String[0];
		
		return nodeSet.getMemberNames();
	}
	
	public Object getValueAt(int row, int col) {
		NodeSet nodeSet = this.nodeSets.get(row);
		if (nodeSet == null)
			return null;
		
		if (col == 0) {
			/* Check whether we have a rendered image or not */
			try {
				Future<Icon> icon = nodeSetIcons.get(row);
				if (icon != null && icon.isDone())
					return icon.get();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} catch (ExecutionException ex) {
				ex.printStackTrace();
			}
			return this.getProgressIcon();
		}
		
		if (!detailedMode) {
			/* Simple mode, column 1 */
			return this.nodeSetDetails.get(row);
		}
		
		/* Detailed mode */
		if (col == 1)
			return nodeSet.size();
		if (col == 2)
			return nodeSet.getDensity();
		if (col == 3)
			return nodeSet.getTotalInternalEdgeWeight();
		if (col == 4)
			return nodeSet.getTotalBoundaryEdgeWeight();
		if (col == 5)
			return nodeSet.getQuality();
		if (col == 6)
			return nodeSet.getSignificance();
		
		return "TODO";
	}
	
	/**
	 * Returns an icon showing a progress indicator
	 */
	private Icon getProgressIcon() {
		if (this.progressIcon == null) {
			this.progressIcon = new ImageIcon(this.getClass().getResource("../resources/wait.jpg"));
		}
		return this.progressIcon;
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
	 * Returns whether the table model is in detailed mode
	 */
	public boolean isInDetailedMode() {
		return detailedMode;
	}
	
	/**
	 * Returns whether the table model is in detailed mode
	 */
	public void setDetailedMode(boolean mode) {
		if (mode == detailedMode)
			return;
		
		detailedMode = mode;
		currentHeaders = detailedMode ? detailedHeaders : simpleHeaders;
		currentClasses = detailedMode ? detailedClasses : simpleClasses;
		this.fireTableStructureChanged();
	}
	
	private void updateNodeSetDetails() {
		Executor threadPool = ClusterONE.getThreadPool();
		int i = 0;
		
		nodeSetDetails.clear();
		nodeSetIcons.clear();
		for (ValuedNodeSet nodeSet: nodeSets) {
			HashMap<Integer, Color> subgraphColorMap = new HashMap<Integer, Color>();
			Graph subgraph = nodeSet.getSubgraph();
			ObjectIntHashMap nodeNameIndex = subgraph.getNodeNameHashMap();
			
			boolean onlyCores = true;
			for (int nodeIndex: nodeSet)
				if (nodeSet.getValue(nodeIndex) > 1) {
					onlyCores = false;
					break;
				}
			
			if (!onlyCores) {
				for (int nodeIndex: nodeSet) {
					if (nodeSet.getValue(nodeIndex) == 1) {
						int id = nodeNameIndex.get(Integer.toString(nodeIndex));
						if (id != ObjectIntHashMap.DEFAULT_NOT_FOUND)
							subgraphColorMap.put(id, DARK_RED);
					}
				}
			}
						
			RendererTask rendererTask = new RendererTask(i, subgraph,
					new FruchtermanReingoldLayoutAlgorithm(), subgraphColorMap);
			threadPool.execute(rendererTask);
			nodeSetIcons.add(rendererTask);
			nodeSetDetails.add(new NodeSetDetails(nodeSet));
			i++;
		}
	}
}
