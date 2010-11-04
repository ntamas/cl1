package uk.ac.rhul.cs.cl1.ui.cytoscape;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.io.ClusteringWriter;
import uk.ac.rhul.cs.cl1.io.ClusteringWriterFactory;

import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/**
 * Action that saves the names of the members of the selected clusters
 * to a file on the disk, separated by spaces, one per line
 * 
 * @author ntamas
 */
public class SaveClusterAction extends AbstractAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;
	
	/**
	 * Constructor
	 */
	public SaveClusterAction(CytoscapeResultViewerPanel panel) {
		super("Save selected cluster(s)...");
		this.resultViewer = panel;
		this.putValue(AbstractAction.MNEMONIC_KEY, KeyEvent.VK_S);
	}
	
	/**
	 * Returns the list of nodes that should be saved
	 */
	protected List<NodeSet> getNodeListsToBeSaved() {
		return this.resultViewer.getSelectedNodeSets();
	}
	
	/**
	 * Returns the title of the dialog box where the destination file will be selected
	 */
	protected String getFileDialogTitle() {
		return "Select the file to save the selected clusters to";
	}
	
	public void actionPerformed(ActionEvent arg0) {
		ClusteringWriterFactory.Format[] formats =
			ClusteringWriterFactory.Format.values();
		CyFileFilter[] filters = new CyFileFilter[formats.length];
		for (int i = 0; i < formats.length; i++)
			filters[formats.length - i - 1] = new CyFileFilter(
					formats[i].getExtension(),
					formats[i].getName()
			);
		File file = FileUtil.getFile(this.getFileDialogTitle(), FileUtil.SAVE, filters);
		
		if (file == null)
			return;
		
		ClusteringWriterFactory.Format format =
			ClusteringWriterFactory.Format.forFile(file);
		if (format == null) {
			CytoscapePlugin.showErrorMessage("The extension of the given filename does not correspond to\n"+
					"any of the known formats. Please use one of the default\n"+
					"extensions (.tab for GenePro files, .txt for cluster lists, "+
					".csv for CSV cluster lists).");
			return;
		}
		
		ClusteringWriter wr = ClusteringWriterFactory.fromFormat(format);
		try {
			wr.writeClustering(this.getNodeListsToBeSaved(), file);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			CytoscapePlugin.showErrorMessage("I/O error while trying to save the selected clusters to\n"+
					file.getAbsolutePath());
		}
	}
}
