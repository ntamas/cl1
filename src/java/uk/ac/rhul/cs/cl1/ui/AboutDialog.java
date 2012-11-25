package uk.ac.rhul.cs.cl1.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.text.html.HTMLEditorKit;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.utils.StringUtils;

/**
 * The about dialog box for ClusterONE.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class AboutDialog extends JDialog implements ActionListener {
	/**
	 * Stores whether the contents of the dialog box have been constructed or not.
	 */
	private boolean constructed = false;
	
	/**
	 * Constructs an about dialog with the given owner and modality.
	 * 
	 * @param owner   the owner of the dialog box
	 * @param modal   the modality of the dialog box
	 */
	public AboutDialog(Frame owner, boolean modal) {
		super(owner, "About ClusterONE", modal);
	}
	
	/**
	 * Constructs an about dialog with the given owner.
	 * 
	 * @param owner   the owner of the dialog box
	 */
	public AboutDialog(Frame owner) {
		this(owner, true);
	}

	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
	
	/**
	 * Constructs the dialog contents.
	 */
	protected void construct() {
		setResizable(false);
		
        JPanel contentPane = new JPanel();
        
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setEditorKit(new HTMLEditorKit());
		editorPane.setBackground(contentPane.getBackground());
        
		URL logoURL = getLogoResourceURL();
		String logoCode = "";
		if (logoURL != null) {
			logoCode = "<center><img src=\""+logoURL+"\" /></center>";
		}
        
        String textTemplate = null;
        try {
        	InputStream stream = getAboutTextResourceAsStream();
        	if (stream != null) {
	        	textTemplate = StringUtils.readInputStream(stream);
        	} else {
        		textTemplate = "";
        	}
        } catch (IOException ex) {
        	textTemplate = "";
        }
        
        String text = textTemplate.replaceAll("%logo%", logoCode)
                                  .replaceAll("%appname%", ClusterONE.applicationName)
                                  .replaceAll("%version%", ClusterONE.version);
        editorPane.setText(text);
        editorPane.setBackground(Color.white);
        editorPane.setAlignmentX(0.5f);
        editorPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        
        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(this);
        closeButton.setAlignmentX(0.5f);
        
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBackground(Color.white);
        contentPane.setOpaque(true);
        contentPane.add(editorPane);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(closeButton);
        
        setContentPane(contentPane);
        pack();
        
        setSize(420, 320);
        
        constructed = true;
	}
	
	/**
	 * Returns the text of the About box as a resource stream.
	 */
	protected InputStream getAboutTextResourceAsStream() throws IOException {
		return this.getClass().getResourceAsStream("../resources/about_dialog.txt");
	}
	
	/**
	 * Returns the URL of the resource containing the ClusterONE logo.
	 */
	protected URL getLogoResourceURL() {
		return this.getClass().getResource("../resources/logo.png");
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible && !constructed)
			construct();
		
		super.setVisible(visible);
	}
	
}
