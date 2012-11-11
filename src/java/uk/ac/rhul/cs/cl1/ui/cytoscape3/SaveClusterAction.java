package uk.ac.rhul.cs.cl1.ui.cytoscape3;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;

import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.io.ClusteringWriter;
import uk.ac.rhul.cs.cl1.io.ClusteringWriterFactory;

/**
 * Action that saves the names of the members of the selected clusters
 * to a file on the disk, separated by spaces, one per line
 * 
 * @author ntamas
 */
public class SaveClusterAction extends AbstractClusterONEAction {
	/**
	 * Result viewer panel associated to the action
	 */
	protected CytoscapeResultViewerPanel resultViewer;
	
	/**
	 * Constructor
	 */
	public SaveClusterAction(CytoscapeResultViewerPanel panel) {
		super(panel.getCytoscapeApp(), "Save selected cluster(s)...");
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
		FileChooserFilter[] filters = new FileChooserFilter[formats.length];
		
		for (int i = 0; i < formats.length; i++) {
			filters[formats.length - i - 1] = new FileChooserFilter(
					formats[i].getName(), formats[i].getExtension());
		}
		
		FileUtil fileUtil = app.getService(FileUtil.class);
		if (fileUtil == null)
			return;
		
		File file = fileUtil.getFile(app.getCySwingApplication().getJFrame(),
				this.getFileDialogTitle(), FileUtil.SAVE, Arrays.asList(filters));
		
		if (file == null)
			return;
		
		ClusteringWriterFactory.Format format =
			ClusteringWriterFactory.Format.forFile(file);
		if (format == null) {
			app.showErrorMessage("The extension of the given filename does not correspond to\n"+
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
			app.showErrorMessage("I/O error while trying to save the selected clusters to\n"+
					file.getAbsolutePath());
		}
	}
}
