package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.ui.ClusterONEAlgorithmParametersPanel;
import uk.ac.rhul.cs.cl1.ui.ClusterONEAlgorithmParametersPanel.Section;
import uk.ac.rhul.cs.cl1.ui.EmptyIcon;

/**
 * Cytoscape control panel for ClusterONE
 * 
 * @author tamas
 */
public class ControlPanel extends JPanel implements CytoPanelComponent, PropertyChangeListener,
	SetCurrentNetworkListener {
	/** Algorithm parameters panel embedded inside the control panel */
	protected ClusterONEAlgorithmParametersPanel algorithmParametersPanel;
	
	/** Selection info panel embedded inside the control panel */
//	protected SelectionPropertiesPanel selectionInfoPanel;
	
	/** Combobox for selecting the appropriate weight attribute */
	protected JComboBox weightAttributeCombo;
	
	/** Button for refreshing the list of weight attributes */
	protected JButton weightAttributeRefreshButton;
	
	/** The ClusterONE Cytoscape app in which this control panel lives */
	protected ClusterONECytoscapeApp app;
	
	public ControlPanel(ClusterONECytoscapeApp app) {
		super();
		this.app = app;
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel algorithmParametersPanel = constructAlgorithmParametersPanel();
		
		Dimension d = algorithmParametersPanel.getPreferredSize();
		d.width = Integer.MAX_VALUE;
		algorithmParametersPanel.setMaximumSize(d);
		
		this.add(algorithmParametersPanel);
//		this.add(constructSelectionInfoPanel());
		this.add(constructButtonPanel());
		
		this.add(Box.createVerticalGlue());
	}
	
	protected JPanel constructAlgorithmParametersPanel() {
		URL url = this.getClass().getResource("../../resources/refresh.png");
		
		/* Algorithm parameters panel */
		algorithmParametersPanel = new ClusterONEAlgorithmParametersPanel();
		
		/* Weight attribute name method */
		JPanel weightPanel = new JPanel();
		weightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		weightAttributeCombo = new JComboBox();
		updateWeightAttributeCombo();
		weightAttributeRefreshButton = new JButton(url == null ? new EmptyIcon() : new ImageIcon(url));
		weightAttributeRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateWeightAttributeCombo();
			}
		});
		if (ClusterONE.isRunningOnMac()) {
			weightAttributeRefreshButton.putClientProperty("JButton.buttonType", "square");
		}
		weightPanel.add(weightAttributeCombo);
		weightPanel.add(Box.createHorizontalStrut(3));
		weightPanel.add(weightAttributeRefreshButton);
		algorithmParametersPanel.addComponent(Section.BASIC, "Edge weights:", weightPanel);
		
		try {
			algorithmParametersPanel.monitorComponent(weightAttributeCombo);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		algorithmParametersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		algorithmParametersPanel.addPropertyChangeListener(
				"parameters", this);
		
		return algorithmParametersPanel;
	}
	
//	protected JPanel constructSelectionInfoPanel() {
//		selectionInfoPanel = new SelectionPropertiesPanel(this);
//		return new CollapsiblePanel(selectionInfoPanel, "Selection info");
//	}
	
	protected JPanel constructButtonPanel() {
		/* Button panel */
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout());
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new StartAction(app));
		buttonPanel.add(startButton);
		JButton closeButton = new JButton("Close panel");
		closeButton.addActionListener(new CloseControlPanelAction(this));
		buttonPanel.add(closeButton);
		JButton helpButton = new JButton(new HelpAction(app, "control-panel"));
		if (ClusterONE.isRunningOnMac()) {
			helpButton.putClientProperty("JButton.buttonType", "help");
			helpButton.setText("");
		}
		buttonPanel.add(helpButton);
		
		return buttonPanel;
	}
	
	/**
	 * Returns a {@link ClusterONEAlgorithmParameters} object from the current state
	 * of the panel
	 */
	ClusterONEAlgorithmParameters getParameters() {
		return algorithmParametersPanel.getParameters();
	}
	
	/**
	 * Retrieves the {@link ControlPanel} instance that is shown on the Cytoscape
	 * control panel
	 * 
	 * @param   app  the Cytoscape desktop application instance
	 * @return  the {@link ControlPanel} instance or null if no control panel is open
	 */
	public static ControlPanel getShownInstance(CySwingApplication app) {
		CytoPanel panel = app.getCytoPanel(CytoPanelName.WEST);
		
		for (int i = 0; i < panel.getCytoPanelComponentCount(); i++) {
			Component c = panel.getComponentAt(i);
			if (c instanceof ControlPanel)
				return (ControlPanel)c;
		}
		
		return null;
	}
	
	/**
	 * Returns the selected weight attribute name
	 * 
	 * @return   the selected weight attribute name or null if no weights should be used
	 */
	public String getWeightAttributeName() {
		if (weightAttributeCombo.getSelectedIndex() == 0)
			return null;
		if (weightAttributeCombo.getSelectedItem() == null)
			return null;
		return weightAttributeCombo.getSelectedItem().toString();
	}
	
	/** Updates the weight attribute combo box to contain the suitable edge attribute
	 * names from the current Cytoscape network
	 */
	public void updateWeightAttributeCombo() {
		Object currentItem = weightAttributeCombo.getSelectedItem();
		
		weightAttributeCombo.removeAllItems();
		weightAttributeCombo.addItem("[unweighted]");
		
		CyNetwork network = app.getCurrentNetwork();
		if (network == null)
			return;
		
		CyTable nodeTable = network.getDefaultEdgeTable();
		
		ArrayList<String> names = new ArrayList<String>();
		for (CyColumn column: nodeTable.getColumns()) {
			Class<?> type = column.getType();
			
			// Ignore non-numeric columns
			if (type != Integer.class && type != Long.class &&
					type != Float.class && type != Double.class)
				continue;
			
			// Ignore the SUID column
			if ("SUID".equals(column.getName()))
				continue;
			
			names.add(column.getName());
		}
		Collections.sort(names);
		
		Collections.sort(names);
		for (String name: names)
			weightAttributeCombo.addItem(name);
		if (currentItem != null)
			weightAttributeCombo.setSelectedItem(currentItem);
	}
	
	/**
	 * Called when the algorithm parameters have changed.
	 * 
	 * This method will update the properties of the current selection in the
	 * info panel accordingly.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == algorithmParametersPanel) {
//			selectionInfoPanel.setQualityFunction(
//				getParameters().getQualityFunction()
//			);
//			selectionInfoPanel.updateNodeSetFromSelection();
		}
	}

	/**
	 * Activates and shows the control panel.
	 */
	public void activate() {
		app.registerService(this, CytoPanelComponent.class);
		app.registerService(this, SetCurrentNetworkListener.class);
		
		CytoPanel cytoPanel = app.getCySwingApplication().getCytoPanel(getCytoPanelName());
		
		/* Ensure that the panel is visible */
		if (cytoPanel.getState() == CytoPanelState.HIDE) {
			cytoPanel.setState(CytoPanelState.DOCK);
		}
		setVisible(true);
		
		/* Activate the panel */
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(getComponent()));
		
		/* Update the weight attribute combo */
		updateWeightAttributeCombo();
	}
	
	/**
	 * Deactivates and hides the control panel.
	 */
	public void deactivate() {
		app.unregisterService(this, CytoPanelComponent.class);
		app.unregisterService(this, SetCurrentNetworkListener.class);
	}
	
	// --------------------------------------------------------------------
	// SetCurrentNetworkListener implementation
	// --------------------------------------------------------------------
	
	/**
	 * Called when the current network changes in the application.
	 */
	public void handleEvent(SetCurrentNetworkEvent event) {
		updateWeightAttributeCombo();
	}
	
	// --------------------------------------------------------------------
	// CytoPanelComponent implementation
	// --------------------------------------------------------------------
	
	/**
	 * Returns the component to be placed in Cytoscape.
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * Return the name of the Cytoscape panel in which this component will be placed.
	 */
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	/**
	 * Returns the icon of the panel.
	 */
	public Icon getIcon() {
		// TODO: use the ClusterONE logo?
		return null;
	}

	/**
	 * Returns the title of this panel.
	 */
	public String getTitle() {
		return ClusterONE.applicationName;
	}
}
