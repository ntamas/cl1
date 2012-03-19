package uk.ac.rhul.cs.cl1.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;

/**
 * A dialog embedding a {@link ClusterONEAlgorithmParametersPanel}
 * 
 * @author ntamas
 */
public class ClusterONEAlgorithmParametersDialog extends JDialog implements ActionListener {
	/**
	 * The parameter panel embedded within the dialog
	 */
	protected ClusterONEAlgorithmParametersPanel paramsPanel;
	
	/**
	 * Result code that shows how the window was closed
	 */
	protected int resultCode;
	
	public ClusterONEAlgorithmParametersDialog() {
		super((Dialog)null, "ClusterONE algorithm parameters", true);
		setLocationRelativeTo(null);
		initialize();
	}
	
	public ClusterONEAlgorithmParametersDialog(Dialog owner) {
		super(owner, "ClusterONE algorithm parameters", true);
		initialize();
	}
	
	public ClusterONEAlgorithmParametersDialog(Frame owner) {
		super(owner, "ClusterONE algorithm parameters", true);
		initialize();
	}
	
	public ClusterONEAlgorithmParameters getParameters() {
		return paramsPanel.getParameters();
	}
	
	public ClusterONEAlgorithmParametersPanel getParametersPanel() {
		return paramsPanel;
	}
	
	protected void initialize() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		paramsPanel = new ClusterONEAlgorithmParametersPanel();
		mainPanel.add(paramsPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton startButton = new JButton("Start");
		startButton.setMnemonic('s');
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		buttonPanel.add(startButton);
		
		JButton closeButton = new JButton("Close");
		closeButton.setMnemonic('c');
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		
		mainPanel.add(buttonPanel);
		
		setContentPane(mainPanel);
		
		pack();
	}

	public boolean execute() {
		this.resultCode = JOptionPane.CLOSED_OPTION;
		this.setVisible(true);
		return (this.resultCode == JOptionPane.YES_OPTION);
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null)
			return;
		
		if (cmd.equals("start")) {
			this.resultCode = JOptionPane.YES_OPTION;
			this.setVisible(false);
		} else if (cmd.equals("close")) {
			this.resultCode = JOptionPane.CLOSED_OPTION;
			this.setVisible(false);
		}
	}
}
