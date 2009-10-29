package uk.ac.rhul.cs.cl1.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 * Mouse adapter subclass that pops up a given popup menu
 * @author tamas
 */
public class PopupMenuTrigger extends MouseAdapter {
	/** The menu associated to this trigger */
	private JPopupMenu menu;

	/**
	 * Constructor.
	 * 
	 * @param  popupMenu  sets the menu to be popped up when the appropriate events are trieggered
	 */
	public PopupMenuTrigger(JPopupMenu menu) {
		this.menu = menu;
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		maybePopup(event);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		maybePopup(event);
	}
	
	protected boolean maybePopup(MouseEvent event) {
		if (!event.isPopupTrigger())
			return false;
		
		/* If we are right-clicking over a JTable, make sure that the row is selected */
		Component c = event.getComponent();
		if (c instanceof JTable) {
			Point p = event.getPoint();
			JTable table = (JTable)c;
			int row = table.rowAtPoint(p);
			ListSelectionModel model = table.getSelectionModel();
			if (!model.isSelectedIndex(row))
				model.setSelectionInterval(row, row);
		}
		
		/* Show the popup menu */
		menu.show(c, event.getX(), event.getY());
		return true;
	}
}
