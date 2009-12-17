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

import uk.ac.rhul.cs.utils.StringUtils;

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
	protected void writeNodesToFile(PrintWriter wr, List<Node> nodes) throws IOException {
		List<String> nodeNames = new ArrayList<String>();
		for (Node node: nodes) {
			nodeNames.add(node.getIdentifier());
		}
		wr.println(StringUtils.join(nodeNames.iterator(), ' '));
		if (wr.checkError())
			throw new IOException();
	}
	
	/**
	 * Writes the names of the nodes in the given clusters to the given PrintWriter
	 */
	protected void writeNodeListsToFile(PrintWriter wr, List<List<Node>> nodeLists) throws IOException {
		for (List<Node> nodes: nodeLists) {
			writeNodesToFile(wr, nodes);
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
		File file = FileUtil.getFile(this.getFileDialogTitle(), FileUtil.SAVE);
		PrintWriter wr = null;
		
		try {
			wr = new PrintWriter(file);
			writeNodeListsToFile(wr, this.getNodeListsToBeSaved());
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
