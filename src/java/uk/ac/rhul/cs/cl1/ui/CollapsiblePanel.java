package uk.ac.rhul.cs.cl1.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.lowagie.text.Font;

/**
 * Collapsible panel for the GUI.
 * 
 * This panel consists of a header label and a main area. When the user
 * clicks on the heading label, the main area is toggled (i.e. hidden if
 * it was shown, shown if it was hidden).
 * 
 * @author tamas
 */
public class CollapsiblePanel extends JPanel {
	/**
	 * The label in the header of the panel
	 */
	protected JLabel headerLabel = null;
	
	/**
	 * The main component of the panel
	 */
	protected JComponent component = null;
	
	/**
	 * Icon to be used in the collapsed state of the panel
	 */
	private static ImageIcon collapsedIcon = loadIcon("plus.gif");
		
	/**
	 * Icon to be used in the expanded state of the panel
	 */
	private static ImageIcon expandedIcon = loadIcon("minus.gif");
	
	/**
	 * Constructs a new collapsible panel with an empty title and a
	 * default JPanel component being wrapped.
	 */
	public CollapsiblePanel() {
		this("");
	}
	
	/**
	 * Constructs a new collapsible panel with the given title and a
	 * default JPanel component being wrapped.
	 * 
	 * @param  title  the title of the panel
	 */
	public CollapsiblePanel(String title) {
		this(title, true);
	}
	
	/**
	 * Constructs a new collapsible panel with the given title and a
	 * default JPanel component being wrapped.
	 * 
	 * @param  title  the title of the panel
	 * @param  expanded   whether the panel is expanded
	 */
	public CollapsiblePanel(String title, boolean expanded) {
		this(new JPanel(), title, expanded);
	}
	
	/**
	 * Constructs a new collapsible panel with the given title and the
	 * given component as contents.
	 * 
	 * @param  component  the component being wrapped by the panel
	 * @param  title      the title of the panel
	 */
	public CollapsiblePanel(JComponent component, String title) {
		this(component, title, true);
	}
	
	/**
	 * Constructs a new collapsible panel with the given title and the
	 * given component as contents.
	 * 
	 * @param  component  the component being wrapped by the panel
	 * @param  title      the title of the panel
	 * @param  expanded   whether the panel is expanded
	 */
	public CollapsiblePanel(JComponent component, String title, boolean expanded) {
		super();
		
		super.setLayout(new BorderLayout());
		
		headerLabel = new JLabel(title);
		headerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		super.add(headerLabel, BorderLayout.NORTH);
		
		this.setWrappedComponent(component);
		this.setExpanded(expanded);
		
		headerLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setExpanded(!isExpanded());
			}
		});
	}
	
	/**
	 * Dispatches the method call to the wrapped component
	 */
	@Override
	public Component add(Component comp) {
		return this.component.add(comp);
	}
	
	/**
	 * Dispatches the method call to the wrapped component
	 */
	@Override
	public Component add(Component comp, int index) {
		return this.component.add(comp, index);
	}
	
	/**
	 * Dispatches the method call to the wrapped component
	 */
	@Override
	public void add(Component comp, Object constraints) {
		this.component.add(comp, constraints);
	}
	
	/**
	 * Dispatches the method call to the wrapped component
	 */
	@Override
	public void add(Component comp, Object constraints, int index) {
		this.component.add(comp, constraints, index);
	}
	
	/**
	 * Returns the component wrapped by this panel
	 */
	public JComponent getWrappedComponent() {
		return this.component;
	}
	
	/**
	 * Loads an icon from the resources corresponding to the panel
	 */
	private static ImageIcon loadIcon(String name) {
		URL url;
		
		url = CollapsiblePanel.class.getResource("resources/"+name);
		if (url == null)
			url = CollapsiblePanel.class.getResource("../resources/"+name);
		if (url == null)
			return null;
		
		return new ImageIcon(url);
	}
	
	/**
	 * Returns whether the panel is expanded
	 * 
	 * @return  whether the panel is expanded
	 */
	public boolean isExpanded() {
		if (component == null)
			return false;
		
		return component.isVisible();
	}
	
	/**
	 * Sets the border of the internally wrapped component
	 */
	public void setBorder(Border border) {
		if (component != null)
			component.setBorder(border);
	}
	
	/**
	 * Sets the layout of the internally wrapped component
	 */
	public void setLayout(LayoutManager mgr) {
		if (component != null)
			component.setLayout(mgr);
	}
	
	/**
	 * Sets the central component of the panel
	 */
	public void setWrappedComponent(JComponent component) {
		this.component = component;
		super.add(component, BorderLayout.CENTER);
	}
	
	/**
	 * Sets whether the panel is expanded
	 * 
	 * In collapsed state, the main component of the panel is not shown,
	 * only the label. In expanded state, both the main component and
	 * the header are shown.
	 * 
	 * @param  expanded  whether the panel is expanded
	 */
	public void setExpanded(boolean expanded) {
		if (component != null)
			component.setVisible(expanded);
		headerLabel.setIcon(expanded ? expandedIcon : collapsedIcon);
	}
}
