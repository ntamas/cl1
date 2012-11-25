package uk.ac.rhul.cs.cl1.ui;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;

/**
 * Component that lets the user adjust the algorithm parameters of ClusterONE
 * 
 * @author ntamas
 */
public class ClusterONEAlgorithmParametersPanel extends JPanel {
	private static final String AUTO = "Auto";
	
	/** Sections used in this panel */
	public enum Section {
		BASIC("Basic parameters", true),
		ADVANCED("Advanced parameters", false);
		
		/** Title of the section */
		protected String title;
		
		/** Whether the section should be expanded by default */
		protected boolean expanded;
		
		Section(String title, boolean expanded) {
			this.title = title;
			this.expanded = expanded;
		}
		
		/** Returns the title of the section */
		public String getTitle() { return this.title; }
		
		/** Returns whether the section is expanded by default */
		public boolean isExpanded() { return this.expanded; }
	}

	/** Subpanel components corresponding to each section */
	protected TreeMap<Section, JPanel> subpanels = null;
	
	/** Layouts of the subpanel components corresponding to each section */
	protected TreeMap<Section, TableLayout> layouts = null;
	
	/** Spinner component for adjusting the minimum cluster size */
	protected JSpinner minimumClusterSizeSpinner;
	
	/** Spinner component for adjusting the minimum cluster density */
	protected JSpinner minimumClusterDensitySpinner;
	
	/** Spinner component for selecting the amount of node penalty */
	protected JSpinner nodePenaltySpinner;
	
	/** Spinner component for selecting the haircut threshold */
	protected JSpinner haircutThresholdSpinner;
	
	/** Combobox for selecting the cluster merging method */
	protected JComboBox mergingMethodCombo;
	
	/** Spinner component for adjusting the overlap threshold of the merging step */
	protected JSpinner overlapThresholdSpinner;
	
	/** Combobox for selecting the seeding method */
	protected JComboBox seedMethodCombo;
	
	/** Combobox for selecting the similarity function */
	protected JComboBox similarityCombo;
	
	/** Merging methods */
	protected String[] mergingMethods = {"Single-pass", "Multi-pass"};
	
	/** Seeding methods */
	protected String[] seedMethods = {"From unused nodes", "From every node", "From every edge"};
	
	/** Similarity functions */
	protected String[] similarityFunctions = {"Match coefficient",
			"Simpson coefficient", "Jaccard similarity", "Dice similarity"};
	
	/** Internal class to provide a number formatter that does not freak out from strings */
	private class LenientNumberFormatter extends JFormattedTextField.AbstractFormatter {
		private DecimalFormat format = new DecimalFormat("0.##");
		
		public Object stringToValue(String text) throws ParseException {
			try {
				 return Double.parseDouble(text);
			} catch (NumberFormatException ex) {
				return text;
			}
		}

		public String valueToString(Object value) throws ParseException {
			try {
				return format.format(value);
			} catch (IllegalArgumentException ex) {
				return value.toString();
			}
		}
		
	}
	
	/** Internal class to listen for change and action events from subcomponents */
	private class PropertyChangeManager extends PropertyChangeSupport
		implements ActionListener, ChangeListener {
		public PropertyChangeManager(Object sourceBean) {
			super(sourceBean);
		}

		/**
		 * Called when one of the comboboxes in the panel change their values.
		 * 
		 * @param event  the actual event which describes what changed exactly.
		 *               Unfortunately we cannot simply forward it outside as
		 *               we don't want to expose the internal widgets directly,
		 *               so we simply call {@link fire()}.
		 */
		public void actionPerformed(ActionEvent event) {
			fireParametersChanged();
		}

		/**
		 * Called when one of the spinners in the panel change their values.
		 * 
		 * @param event  the actual event which describes what changed exactly.
		 *               Unfortunately we cannot simply forward it outside as
		 *               we don't want to expose the internal widgets directly,
		 *               so we simply call {@link fire()}.
		 */
		public void stateChanged(ChangeEvent event) {
			fireParametersChanged();
		}
	}
	
	/** Private class to ease the firing of PropertyChangeEvents when something changed */
	private final PropertyChangeManager changeManager = new PropertyChangeManager(this);
	
	public ClusterONEAlgorithmParametersPanel() {
		super();
		
		ClusterONEAlgorithmParameters defaultParams =
			new ClusterONEAlgorithmParameters();
		
		subpanels = new TreeMap<Section, JPanel>();
		layouts = new TreeMap<Section, TableLayout>();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		/* Minimum cluster size spinner */	
		minimumClusterSizeSpinner = addSpinner(Section.BASIC, "Minimum size:",
			new SpinnerNumberModel(defaultParams.getMinSize(), 1, Integer.MAX_VALUE, 1)
		);
		
		/* Minimum cluster density spinner */
		Object minDensity = defaultParams.getMinDensity();
		if (minDensity == null)
			minDensity = AUTO;
		minimumClusterDensitySpinner = addSpinner(Section.BASIC, "Minimum density:",
			new ExtendedSpinnerNumberModel(minDensity, 0.0, 1.0, 0.05, AUTO)
		);
		((JSpinner.DefaultEditor)minimumClusterDensitySpinner.getEditor()).getTextField().setFormatterFactory(
				new JFormattedTextField.AbstractFormatterFactory() {
					private AbstractFormatter formatter = new LenientNumberFormatter();
					
					public AbstractFormatter getFormatter(JFormattedTextField tf) {
						return formatter;
					}
				}
		);
		
		/* Node penalty spinner */
		nodePenaltySpinner = addSpinner(Section.ADVANCED, "Node penalty:",
			new SpinnerNumberModel(defaultParams.getNodePenalty(), 0.0, 10.0, 0.2)
		);
		
		/* Haircut threshold spinner */
		haircutThresholdSpinner = addSpinner(Section.ADVANCED, "Haircut threshold:",
			new SpinnerNumberModel(defaultParams.getHaircutThreshold(), 0.0, 1.0, 0.05)
		);
		
		/* Merging method combobox */
		mergingMethodCombo = addComboBox(Section.ADVANCED, "Merging method:", mergingMethods);
		
		/* Similarity function combobox */
		similarityCombo = addComboBox(Section.ADVANCED, "Similarity:", similarityFunctions);
		
		/* Overlap threshold spinner */
		overlapThresholdSpinner = addSpinner(Section.ADVANCED, "Overlap threshold:",
			new SpinnerNumberModel(defaultParams.getOverlapThreshold(), 0.0, 1.0, 0.05)
		);
		
		/* Seed selection method */
		seedMethodCombo = addComboBox(Section.ADVANCED, "Seeding method:", seedMethods);
	}

	/**
	 * Expands all the panels.
	 */
	public void expandAll() {
		for (JPanel panel: subpanels.values()) {
			if (panel instanceof CollapsiblePanel) {
				((CollapsiblePanel)panel).setExpanded(true);
			}
		}
	}
	
	/**
	 * Returns a {@link ClusterONEAlgorithmParameters} object from the current state
	 * of the panel
	 */
	public ClusterONEAlgorithmParameters getParameters() {
		ClusterONEAlgorithmParameters result = new ClusterONEAlgorithmParameters();
		Object minimumDensity = minimumClusterDensitySpinner.getValue();
		
		if (minimumDensity.equals(AUTO))
			result.setMinDensity(null);
		else
			result.setMinDensity((Double)minimumDensity);
			
		result.setMinSize((Integer)minimumClusterSizeSpinner.getValue());
		result.setHaircutThreshold((Double)haircutThresholdSpinner.getValue());
		result.setOverlapThreshold((Double)overlapThresholdSpinner.getValue());
		result.setNodePenalty((Double)nodePenaltySpinner.getValue());
		
		try {
			if (seedMethodCombo.getSelectedIndex() == 0)
				result.setSeedGenerator("unused_nodes");
			else if (seedMethodCombo.getSelectedIndex() == 1)
				result.setSeedGenerator("nodes");
			else if (seedMethodCombo.getSelectedIndex() == 2)
				result.setSeedGenerator("edges");
			else
				return null;
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			return null;
		}
		
		try {
			if (similarityCombo.getSelectedIndex() == 0)
				result.setSimilarityFunction("match");
			else if (similarityCombo.getSelectedIndex() == 1)
				result.setSimilarityFunction("simpson");
			else if (similarityCombo.getSelectedIndex() == 2)
				result.setSimilarityFunction("jaccard");
			else if (similarityCombo.getSelectedIndex() == 3)
				result.setSimilarityFunction("dice");
			else
				return null;
		} catch (InstantiationException ex) {
			return null;
		}
		
		if (mergingMethodCombo.getSelectedIndex() == 0)
			result.setMergingMethodName("single");
		else if (mergingMethodCombo.getSelectedIndex() == 1)
			result.setMergingMethodName("multi");
		
		return result;
	}
	
	/**
	 * Returns the subpanel corresponding to the given section
	 * 
	 * If the section has not been used yet, this method also creates the
	 * corresponding section and sets an appropriate layout on it.
	 */
	public JPanel getSubpanel(Section section) {
		if (subpanels.containsKey(section))
			return subpanels.get(section);
		
		CollapsiblePanel newPanel = constructNewSubpanel(section.getTitle());
		newPanel.setExpanded(section.isExpanded());
		double sizes[][] = {
				{TableLayout.PREFERRED, 10, TableLayout.PREFERRED},
				{TableLayout.PREFERRED}
		};
		layouts.put(section, new TableLayout(sizes));
		newPanel.setLayout(layouts.get(section));
		
		this.add(newPanel);
		this.add(Box.createVerticalStrut(10));
		subpanels.put(section, newPanel);
		
		return newPanel;
	}
	
	/**
	 * Constructs a new subpanel with the given title
	 */
	protected CollapsiblePanel constructNewSubpanel(String title) {
		CollapsiblePanel newPanel = new CollapsiblePanel(title);
		return newPanel;
	}
	
	/**
	 * Adds a new component to the end of the parameters panel.
	 * 
	 * The panel will <em>not</em> register itself to the component to listen to
	 * change events, you have to do it manually from the caller.
	 * 
	 * @param section    the section to add the component to
	 * @param caption    caption of the component in the left column
	 * @param component  the component itself that should go in the right column
	 */
	public void addComponent(Section section, String caption, Component component) {
		JLabel label = new JLabel(caption);
		JPanel subpanel = this.getSubpanel(section);
		TableLayout layout = this.layouts.get(section);
		
		int numRows = layout.getNumRow();
		layout.insertRow(numRows, TableLayout.PREFERRED);
		subpanel.add(label, "0, "+numRows+", r, c");
		subpanel.add(component, "2, "+numRows+", l, c");
	}
	
	/**
	 * Adds a combobox to the parameters panel.
	 * 
	 * This method will also register the panel to listen for changes of the
	 * selected item in the combobox and fire a {@link PropertyChangeEvent}
	 * if needed.
	 * 
	 * @param section    the section to add the component to
	 * @param caption    caption of the component in the left column
	 * @param items      the array of items in the combo box
	 */
	public JComboBox addComboBox(Section section, String caption, String[] items) {
		JComboBox combo = new JComboBox(items);
		this.addComponent(section, caption, combo);
		combo.addActionListener(changeManager);
		return combo;
	}
	
	/**
	 * Adds a spinner to the parameters panel.
	 * 
	 * This method will also register the panel to listen for changes of the spinner
	 * value and fire a {@link PropertyChangeEvent} if needed.
	 * 
	 * @param section    the section to add the component to
	 * @param caption    caption of the component in the left column
	 * @param model      the model of the spinner value
	 */
	public JSpinner addSpinner(Section section, String caption, SpinnerModel model) {
		JSpinner spinner = new JSpinner();
		JSpinner.DefaultEditor editor;
		
		spinner.setModel(model);
		editor = (JSpinner.DefaultEditor)spinner.getEditor();
		editor.getTextField().setColumns(5);
		editor.getTextField().setHorizontalAlignment(JTextField.RIGHT);
		this.addComponent(section, caption, spinner);
		spinner.addChangeListener(changeManager);
		return spinner;
	}
	
	/**
	 * Notifies registered {@link PropertyChangeListener}s that one of the algorithm
	 * properties have changed.
	 * 
	 * Currently this is a very dumb method, it will fire a simple
	 * {@link PropertyChangeEvent} with null old and new values and
	 * "algorithm_parameters" as source, so the caller would not know which
	 * property changed exactly. However, this is enough for our purposes for
	 * the time being.
	 */
	protected void fireParametersChanged() {
		firePropertyChange("parameters", null, null);
	}
	
	/**
	 * Registers a component to be monitored by the panel.
	 * 
	 * Whenever the component fires an {@link ActionEvent} or a {@link ChangeEvent},
	 * the algorithm panel will assume that the algorithm parameters depend on the
	 * value of that component and therefore will fire a {@link PropertyChangeEvent}.
	 * This method is used by {@link addComponent}.
	 * 
	 * @param  component  the component to be monitored. We will use reflection to
	 *                    figure out whether the component supports the following
	 *                    methods (in the following order of priority):
	 *                    <tt>addChangeListener</tt>, <tt>addActionListener</tt>.
	 */
	public void monitorComponent(Component component)
	       throws IllegalArgumentException, IllegalAccessException,
	              InvocationTargetException, NoSuchMethodException {
		Class<?> cls = component.getClass();
		Method method = null;
		
		// First, try with addChangeListener()
		try {
			method = cls.getMethod("addChangeListener", ChangeListener.class);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		
		// If unsuccessful, try with addActionListener()
		if (method == null) {
			try {
				method = cls.getMethod("addActionListener", ActionListener.class);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
		}
		
		// If we found at least one suitable method, invoke it
		if (method != null) {
			method.invoke(component, changeManager);
		} else {
			// No suitable method was found, throw an InvocationTargetException
			throw new NoSuchMethodException();
		}
	}
}
