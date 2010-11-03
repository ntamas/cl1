package uk.ac.rhul.cs.cl1.ui.cytoscape;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import uk.ac.rhul.cs.cl1.io.ClusteringWriterFactory;
import uk.ac.rhul.cs.utils.StringUtils;

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
	 * Writes the names of the given nodes to the given PrintWriter
	 */
	protected void writeNodesToFile(PrintWriter wr, List<Node> nodes) {
		List<String> nodeNames = new ArrayList<String>();
		for (Node node: nodes) {
			nodeNames.add(node.getIdentifier());
		}
		wr.println(StringUtils.join(nodeNames.iterator(), ' '));
	}
	
	/**
	 * Writes the names of the nodes in the given clusters to the given PrintWriter
	 */
	protected void writeNodeListsToFile(PrintWriter wr, List<List<Node>> nodeLists,
			ClusteringWriterFactory.Format format)
			throws IOException {
		switch (format) {
		case PLAIN:
			for (List<Node> nodes: nodeLists) {
				writeNodesToFile(wr, nodes);
			}
			break;
			
		case DETAILED:
			// TODO
			for (List<Node> nodes: nodeLists) {
				writeNodesToFile(wr, nodes);
			}
			break;
			
		case GENEPRO:
			int index = 0;
			
			wr.println("CID\tORF");
			for (List<Node> nodes: nodeLists) {
				String clusterName = "Complex "+index;
				for (Node node: nodes) {
					wr.printf("%s\t%s\n", clusterName, node.getIdentifier());
				}
				index++;
			}
			break;
		}
	}
	
	/**
	 * Returns the list of nodes that should be saved
	 */
	protected List<List<Node>> getNodeListsToBeSaved() {
		return this.resultViewer.getSelectedCytoscapeNodeSets();
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
		
		PrintWriter wr = null;
		
		try {
			wr = new PrintWriter(file);
			writeNodeListsToFile(wr, this.getNodeListsToBeSaved(), format);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			CytoscapePlugin.showErrorMessage("I/O error while trying to save the selected clusters to\n"+
					file.getAbsolutePath());
		} finally {
			if (wr != null)
				wr.close();
		}
	}
}
