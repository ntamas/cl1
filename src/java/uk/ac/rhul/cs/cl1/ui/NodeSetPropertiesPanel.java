package uk.ac.rhul.cs.cl1.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * A panel that shows some properties of a {@link NodeSet}
 * @author tamas
 */
public class NodeSetPropertiesPanel extends JPanel {
	/** The nodeset for which we are showing the properties */
	protected NodeSet nodeSet = null;
	
	protected JLabel label = null;
	
	/** Constructor */
	public NodeSetPropertiesPanel() {
		super();
		label = new JLabel();
		this.add(label);
		updatePanel();
	}
	
	/** Updates the components of the panel when the nodeset changed */
	protected void updatePanel() {
		if (nodeSet == null) {
			label.setText("No nodeset selected");
			return;
		}
		
		label.setText(nodeSet.size()+" nodes selected");
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
