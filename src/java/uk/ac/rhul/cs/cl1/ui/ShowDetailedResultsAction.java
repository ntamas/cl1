package uk.ac.rhul.cs.cl1.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class ShowDetailedResultsAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected ResultViewerPanel resultViewer;

	/**
	 * Constructor
	 */
	public ShowDetailedResultsAction(ResultViewerPanel panel) {
		super();
		this.resultViewer = panel;
		this.putValue(AbstractAction.SMALL_ICON,
				new ImageIcon(this.getClass().getResource("../resources/details.png"))
		);
		this.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Shows or hides the details of each cluster");
	}
	
	public void actionPerformed(ActionEvent event) {
		NodeSetTableModel model = resultViewer.getTableModel();
		if (model.isInDetailedMode())
			model.setDetailedMode(false);
		else
			model.setDetailedMode(true);
	}
}
