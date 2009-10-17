package uk.ac.rhul.cs.cl1.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


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
		
		item = new JMenuItem("Start");
		item.addActionListener(this);
		item.setActionCommand("start");
		menu.add(item);
		
		pluginsMenu.add(menu);
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		
		if (cmd.equals("about")) {
			AboutDialog dlg = new AboutDialog(Cytoscape.getDesktop());
			dlg.setVisible(true);
			return;
		}
		
		if (cmd.equals("start")) {
			JFrame frame = new JFrame();
			ClusterONEAlgorithmParametersPanel paramsPanel =
				new ClusterONEAlgorithmParametersPanel();
			paramsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			frame.setContentPane(paramsPanel);
			frame.pack();
			frame.setVisible(true);
			
			return;
		}
	}
}
