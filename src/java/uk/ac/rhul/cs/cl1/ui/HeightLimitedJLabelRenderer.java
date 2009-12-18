/**
 * This file is taken from the MCODE plugin with some slight modifications.
 * The lines below are the original license of MCODE:
 * 
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package uk.ac.rhul.cs.cl1.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * A text area renderer that creates a line wrapped, non-editable text area.
 */
public class HeightLimitedJLabelRenderer extends JLabel implements
		TableCellRenderer {
	/**
	 * Minimum height of the text area renderer
	 */
	int minHeight;

	/**
	 * Constructor
	 * 
	 * @param minHeight
	 *            The minimum height of the row, either the size of the
	 *            graph picture or zero
	 */
	public HeightLimitedJLabelRenderer(int minHeight) {
		this.setFont(this.getFont().deriveFont(11.0f));
		this.setOpaque(true);
		this.minHeight = minHeight;
	}

	/**
	 * Used to render a table cell. Handles selection color and cell height
	 * and width. Note: Be careful changing this code as there could easily
	 * be infinite loops created when calculating preferred cell size as the
	 * user changes the dialog box size.
	 * 
	 * @param table
	 *            Parent table of cell
	 * @param value
	 *            Value of cell
	 * @param isSelected
	 *            True if cell is selected
	 * @param hasFocus
	 *            True if cell has focus
	 * @param row
	 *            The row of this cell
	 * @param column
	 *            The column of this cell
	 * @return The cell to be rendered by the calling code
	 */
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		this.setText(value.toString());

		if (isSelected) {
			this.setBackground(table.getSelectionBackground());
			this.setForeground(table.getSelectionForeground());
		} else {
			this.setBackground(table.getBackground());
			this.setForeground(table.getForeground());
		}
		
		// row height calculations
		int currentRowHeight = table.getRowHeight(row);
		int rowMargin = table.getRowMargin();
		this.setSize(table.getColumnModel().getColumn(column).getWidth(),
				currentRowHeight - (2 * rowMargin));
		int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();
		
		// JLabel can grow and shrink here
		if (currentRowHeight != Math.max(textAreaPreferredHeight
				+ (2 * rowMargin), minHeight + (2 * rowMargin))) {
			table.setRowHeight(row, Math.max(textAreaPreferredHeight
					+ (2 * rowMargin), minHeight + (2 * rowMargin)));
		}
		
		return this;
	}
}
