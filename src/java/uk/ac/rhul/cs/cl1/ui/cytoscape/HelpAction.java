package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;

import cytoscape.util.CytoscapeAction;

/**
 * An action that shows the ClusterONE Help contents.
 * 
 * @author tamas
 */
public class HelpAction extends CytoscapeAction {
	private static HelpBroker helpBroker = null;
	private static HelpSet helpSet = null;
	
	private CSH.DisplayHelpFromSource csh;
	
	private String helpID = null;
	
	/**
	 * Constructs the action
	 */
	public HelpAction(String helpID, String label) {
		super(label);
		setPreferredMenu("Plugins.ClusterONE");
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_H);
		this.helpID = helpID;
	}
	
	public HelpAction(String helpID) {
		this(helpID, "Help...");
	}
	
	public HelpAction() {
		this("introduction");
	}
	
	protected void init() {
		if (csh != null)
			return;
		
		URL hsURL = getClass().getResource("help/cl1.hs");
		
		try {
			if (helpSet == null)
				helpSet = new HelpSet(null, hsURL);
			if (helpBroker == null)
				helpBroker = helpSet.createHelpBroker();
			helpBroker.setCurrentID(helpID);
			csh = new CSH.DisplayHelpFromSource(helpBroker);
		} catch (Exception ex) {
			CytoscapePlugin.showErrorMessage("ClusterONE Help cannot be started. Please see the ClusterONE website instead.");
			return;
		}
	}
	
	@Override
	public boolean isInToolBar() {
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		init();
		if (csh != null)
			csh.actionPerformed(event);
	}
}
