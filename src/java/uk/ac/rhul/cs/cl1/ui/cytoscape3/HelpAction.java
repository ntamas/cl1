package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;

/**
 * An action that shows the ClusterONE Help contents.
 * 
 * @author tamas
 */
public class HelpAction extends AbstractClusterONEAction {
	private static HelpBroker helpBroker = null;
	private static HelpSet helpSet = null;
	
	private CSH.DisplayHelpFromSource csh;
	
	private String helpID = null;
	
	// --------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------

	/**
	 * Constructs the action
	 */
	public HelpAction(ClusterONECytoscapeApp app, String helpID, String label) {
		super(app, label);
		installInMenu();
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_H);
		this.helpID = helpID;
	}
	
	public HelpAction(ClusterONECytoscapeApp app, String helpID) {
		this(app, helpID, "Help...");
	}
	
	public HelpAction(ClusterONECytoscapeApp app) {
		this(app, "introduction");
	}
	
	protected void init() {
		if (csh != null)
			return;
		
		URL hsURL = app.getResource(getClass().getPackage().getName().replace(".", "/") + "/help/cl1.hs");
		
		try {
			if (helpSet == null)
				helpSet = new HelpSet(null, hsURL);
			if (helpBroker == null)
				helpBroker = helpSet.createHelpBroker();
			helpBroker.setCurrentID(helpID);
			csh = new CSH.DisplayHelpFromSource(helpBroker);
		} catch (Exception ex) {
			app.showErrorMessage("ClusterONE Help cannot be started. Please see the ClusterONE website instead.");
			ex.printStackTrace();
			return;
		}
	}
	
	// --------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Query methods
	// --------------------------------------------------------------------

	// --------------------------------------------------------------------
	// Manipulation methods
	// --------------------------------------------------------------------

	public void actionPerformed(ActionEvent event) {
		init();
		if (csh != null) {
			// Set the help ID on the event source to ensure that the proper topic is shown
			Object source = event.getSource();
			if (source != null) {
				try {
					CSH.setHelpIDString((Component)source, helpID);
				} catch (ClassCastException ex) {
					// meh.
				}
			}
			csh.actionPerformed(event);
		}
	}
}
