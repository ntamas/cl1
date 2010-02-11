package uk.ac.rhul.cs.cl1.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer that renders the representation of a p-value using a JLabel
 */
public class PValueRenderer extends JLabel implements TableCellRenderer {
	/** Formatter used to format fractional numbers larger than 1e-3 */
	private static final NumberFormat fractionalFormat = new DecimalFormat("0.000");
	/** Formatter used to format fractional numbers smaller than 1e-3 */
	private static final NumberFormat scientificFormat = new DecimalFormat("0.000E0");
	
	public PValueRenderer() {
		super("", SwingConstants.RIGHT);
		this.setOpaque(true);
	}
	
	public Component getTableCellRendererComponent(JTable table,
			Object value0, boolean isSelected, boolean hasFocus, int row,
			int column) {
		Double value = (Double)value0;
		
		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
		} else {
			this.setBackground(table.getBackground());
		}
		
		this.setForeground(getColorForValue(value));
		this.setText(formatValue(value));
		
		return this;
	}
	
	public static Color getColorForValue(Double value) {
		if (value.isNaN()) {
			return Color.LIGHT_GRAY;
		} else if (value <= 0.05) {
			return Color.RED;
		} else if (value <= 0.1) {
			return Color.ORANGE;
		}
		return Color.BLACK;
	}

	public static Object getColorCodeForValue(Double value) {
		if (value.isNaN()) {
			return "#888888";
		} else if (value <= 0.05) {
			return "#ff0000";
		} else if (value <= 0.1) {
			return "#ff8000";
		}
		return "#000000";
	}
	
	public static String formatValue(Double value) {
		return formatValue(value, true);
	}
	
	public static String formatValue(Double value, boolean allowHTML) {
		if (value.isNaN())
			return "NA";
		
		if (value.isInfinite()) {
			if (value < 0)
				return "-inf";
			return "+inf";
		}
		
		if (value == 0 || Math.abs(value) >= 1e-3)
			return fractionalFormat.format(value);
		else if (allowHTML) {
			String s = scientificFormat.format(value);
			int ePos = s.indexOf('E');
			if (ePos == -1)
				ePos = s.indexOf('e');
			if (ePos == -1)
				return s;
			return "<html>" + s.substring(0, ePos) + " x 10<sup>" + s.substring(ePos+1) + "</sup></html>";
		} else
			return scientificFormat.format(value);
	}
}
