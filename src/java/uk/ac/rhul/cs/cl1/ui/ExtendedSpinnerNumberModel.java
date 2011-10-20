package uk.ac.rhul.cs.cl1.ui;

import javax.swing.AbstractSpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Spinner number model with an extra item.
 * 
 * This spinner model works like the <code>SpinnerNumberModel</code>,
 * but getting the previous value of the lowest number of the model
 * yields a special item that can be specified at construction
 * time or with the <code>setExtraItem()</code> method.
 * 
 * @author tamas
 *
 */
public class ExtendedSpinnerNumberModel extends AbstractSpinnerModel {

	class MyChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			fireStateChanged();
		}
	}
	
	/**
	 * The extra item in the spinner model.
	 */
	private Object extraItem = null;
	
	/**
	 * The linked number model.
	 */
	private SpinnerNumberModel model;
	
	/**
	 * Whether the extra item is the selected one at the moment.
	 */
	private boolean showingExtraItem = false;
	
	public ExtendedSpinnerNumberModel() {
		model = new SpinnerNumberModel();
		model.addChangeListener(new MyChangeListener());
	}

	public ExtendedSpinnerNumberModel(Object value, double minimum,
			double maximum, double stepSize, Object extraItem) {
		model = new SpinnerNumberModel(minimum, minimum, maximum, stepSize);
		model.addChangeListener(new MyChangeListener());
		
		this.setExtraItem(extraItem);
		this.setValue(value);
	}

	public ExtendedSpinnerNumberModel(int value, int minimum, int maximum,
			int stepSize, Object extraItem) {
		model = new SpinnerNumberModel(value, minimum, maximum, stepSize);
		model.addChangeListener(new MyChangeListener());
		
		this.setExtraItem(extraItem);
	}

	@SuppressWarnings("rawtypes")
	public ExtendedSpinnerNumberModel(Number value, Comparable minimum,
			Comparable maximum, Number stepSize, Object extraItem) {
		model = new SpinnerNumberModel(value, minimum, maximum, stepSize);
		model.addChangeListener(new MyChangeListener());
		
		this.setExtraItem(extraItem);
	}
	
	/**
	 * Returns the extra item of the spinner model.
	 */
	public Object getExtraItem() {
		return this.extraItem;
	}
	
	public Object getNextValue() {
		Object result;
		
		if (showingExtraItem) {
			// We are at the extra item, which means that the superclass is
			// at the first item. Simply return the current value of the
			// superclass and clear the flag.
			showingExtraItem = false;
			result = model.getValue();
			fireStateChanged();
		} else {
			result = model.getNextValue();
		}
		
		return result;
	}
	
	public Object getPreviousValue() {
		if (showingExtraItem) {
			// We are at the extra item and there is no previous one, so just
			// return null.
			return null;
		}
		
		Object result = model.getPreviousValue();
		if (result == null && !showingExtraItem) {
			// We have to show the extra item now
			showingExtraItem = true;
			fireStateChanged();
			return extraItem;
		}
		
		return result;
	}
	
	public Object getValue() {
		return showingExtraItem ? extraItem : model.getValue();
	}
	
	/**
	 * Sets the extra item of the spinner model.
	 */
	public void setExtraItem(Object extraItem) {
		this.extraItem = extraItem;
		if (showingExtraItem) {
			this.setValue(extraItem == null ? model.getValue() : extraItem);
		}
	}
	
	public void setValue(Object value) {
		showingExtraItem = (value == extraItem);
		if (showingExtraItem) {
			model.setValue(model.getMinimum());
		} else {
			model.setValue(value);
		}
	}
}
