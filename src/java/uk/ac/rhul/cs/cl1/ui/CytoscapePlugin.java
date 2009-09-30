package uk.ac.rhul.cs.cl1.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import uk.ac.rhul.cs.cl1.AboutDialog;

import cytoscape.Cytoscape;

public class CytoscapePlugin extends cytoscape.plugin.CytoscapePlugin implements ActionListener {
	public CytoscapePlugin() {
		JMenu pluginsMenu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
		JMenu menu = new JMenu("Cluster ONE");
		JMenuItem item;
		
		item = new JMenuItem("About");
		item.addActionListener(this);
		item.setActionCommand("about");
		menu.add(item);
		
		pluginsMenu.add(menu);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("about")) {
			AboutDialog dlg = new AboutDialog(Cytoscape.getDesktop());
			dlg.setVisible(true);
		}
	}
}
