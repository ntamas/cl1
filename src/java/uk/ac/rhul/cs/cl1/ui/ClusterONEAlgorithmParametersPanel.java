package uk.ac.rhul.cs.cl1.ui;

import java.awt.Component;

import info.clearthought.layout.TableLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;

/**
 * Component that lets the user adjust the algorithm parameters of Cluster ONE
 * 
 * @author ntamas
 */
public class ClusterONEAlgorithmParametersPanel extends JPanel {
	/** Layout used in this panel */
	protected TableLayout layout;
	
	/** Spinner component for adjusting the minimum cluster size */
	protected JSpinner minimumClusterSizeSpinner;
	
	/** Spinner component for adjusting the minimum cluster density */
	protected JSpinner minimumClusterDensitySpinner;
	
	/** Combobox for selecting the cluster merging method */
	protected JComboBox mergingMethodCombo;
	
	/** Spinner component for adjusting the overlap threshold of the merging step */
	protected JSpinner overlapThresholdSpinner;
	
	/** Combobox for selecting the seeding method */
	protected JComboBox seedMethodCombo;
	
	/** Seeding methods */
	protected String[] seedMethods = {"From every node", "From unused nodes", "From every edge"};
	
	/** Merging methods */
	protected String[] mergingMethods = {"Match coefficient", "Meet/min coefficient"};
	
	public ClusterONEAlgorithmParametersPanel() {
		super();
		
		double sizes[][] = {
				{TableLayout.FILL, 10, TableLayout.PREFERRED},
				{TableLayout.PREFERRED}
		};
		layout = new TableLayout(sizes);
		setLayout(layout);
		
		JLabel label;
		
		/* Minimum cluster size spinner */
		label = new JLabel("Minimum size:");
		this.add(label, "0, 0, r, c");
		minimumClusterSizeSpinner = new JSpinner();
		minimumClusterSizeSpinner.setModel(
				new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1)
		);
		((JSpinner.NumberEditor)minimumClusterSizeSpinner.getEditor()).getTextField().setColumns(5);
		this.add(minimumClusterSizeSpinner, "2, 0, l, c");
		
		/* Minimum cluster density spinner */
		minimumClusterDensitySpinner = new JSpinner();
		minimumClusterDensitySpinner.setModel(
				new SpinnerNumberModel(0.2, 0.0, 1.0, 0.05)
		);
		((JSpinner.NumberEditor)minimumClusterDensitySpinner.getEditor()).getTextField().setColumns(5);
		this.addComponent("Minimum density:", minimumClusterDensitySpinner);
		
		/* Merging method combobox */
		mergingMethodCombo = new JComboBox(mergingMethods);
		this.addComponent("Merging method:", mergingMethodCombo);
		
		/* Overlap threshold spinner */
		overlapThresholdSpinner = new JSpinner();
		overlapThresholdSpinner.setModel(
				new SpinnerNumberModel(0.8, 0.0, 1.0, 0.05)
		);
		((JSpinner.NumberEditor)overlapThresholdSpinner.getEditor()).getTextField().setColumns(5);
		this.addComponent("Overlap threshold:", overlapThresholdSpinner);
		
		/* Seed selection method */
		seedMethodCombo = new JComboBox(seedMethods);
		this.addComponent("Seeding method:", seedMethodCombo);
	}

	/**
	 * Returns a {@link ClusterONEAlgorithmParameters} object from the current state
	 * of the panel
	 */
	public ClusterONEAlgorithmParameters getParameters() {
		ClusterONEAlgorithmParameters result = new ClusterONEAlgorithmParameters();
		
		result.setMinSize((Integer)minimumClusterSizeSpinner.getValue());
		result.setMinDensity((Double)minimumClusterDensitySpinner.getValue());
		result.setOverlapThreshold((Double)overlapThresholdSpinner.getValue());
		
		try {
			if (seedMethodCombo.getSelectedIndex() == 0)
				result.setSeedGenerator("nodes");
			else if (seedMethodCombo.getSelectedIndex() == 1)
				result.setSeedGenerator("unused_nodes");
			else if (seedMethodCombo.getSelectedIndex() == 2)
				result.setSeedGenerator("edges");
			else
				return null;
		} catch (InstantiationException ex) {
			return null;
		}
		
		if (mergingMethodCombo.getSelectedIndex() == 0)
			result.setMergingMethod("match");
		else if (mergingMethodCombo.getSelectedIndex() == 1)
			result.setMergingMethod("meet/min");
		else
			return null;

		return result;
	}

	/**
	 * Adds a new component to the end of the parameters panel
	 * @param caption    caption of the component in the left column
	 * @param component  the component itself that should go in the right column
	 */
	public void addComponent(String caption, Component component) {
		JLabel label = new JLabel(caption);
		int numRows = layout.getNumRow();
		layout.insertRow(numRows, TableLayout.PREFERRED);
		this.add(label, "0, "+numRows+", r, c");
		this.add(component, "2, "+numRows+", l, c");
	}
}
