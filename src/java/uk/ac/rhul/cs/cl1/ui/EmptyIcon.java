package uk.ac.rhul.cs.cl1.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An empty icon.
 * 
 * @author ntamas
 */
public final class EmptyIcon implements Icon {

	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	public EmptyIcon() {
		this(0, 0);
	}
	
	public EmptyIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	/**
	 * The height of the icon.
	 */
	int height;
	
	/**
	 * The width of the icon.
	 */
	int width;
	
	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	/**
	 * Returns the height of the icon.
	 */
	public int getIconHeight() {
		return height;
	}

	/**
	 * Returns the width of the icon.
	 */
	public int getIconWidth() {
		return width;
	}
	
	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	/**
	 * Paints the icon by doing nothing.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
	}

	// --------------------------------------------------------------------
	// Private methods
	// --------------------------------------------------------------------

}
