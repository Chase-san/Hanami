package org.csdgn.hanami.view;

import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.Font;

import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;

import org.csdgn.hanami.AppToolkit;
import org.csdgn.hanami.Version;

import javax.swing.BoxLayout;

public class AboutDialog extends JPanel {
	private static final long serialVersionUID = -2028002813135850360L;

	public AboutDialog() {
		
		setBorder(new EmptyBorder(2,2,2,2));
		setLayout(new BorderLayout(0, 8));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout(8, 0));
		add(titlePanel, BorderLayout.NORTH);
		
		JLabel logoLabel = new JLabel(new ImageIcon(AppToolkit.getAppIconImages().get(2)));
		titlePanel.add(logoLabel, BorderLayout.WEST);
		
		JPanel subTitlePanel = new JPanel();
		subTitlePanel.setLayout(new BoxLayout(subTitlePanel, BoxLayout.Y_AXIS));
		titlePanel.add(subTitlePanel, BorderLayout.CENTER);
		
		JLabel titleLabel = new JLabel(Version.NAME);
		titleLabel.setForeground(Color.decode("#da819a"));
		titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
		subTitlePanel.add(titleLabel);
		
		JLabel buildLabel = new JLabel(Version.getVersionString());
		buildLabel.setBorder(new EmptyBorder(0, 2, 0, 0));
		buildLabel.setForeground(SystemColor.textInactiveText);
		buildLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
		subTitlePanel.add(buildLabel);
		
		JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setOpaque(false);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textArea.setFocusable(false);
		textArea.setEditable(false);
		textArea.setPreferredSize(new Dimension(150,100));
		textArea.setText("Copyright© 2012-2013 Robert Maupin\nAll Rights Reserved.\n\nCreated because IrfanView can't be run natively on linux.\n");
		add(textArea);
	}
	
	public void showAboutDialog(Window owner) {
		JOptionPane pane = new JOptionPane(this,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION);
		JDialog dialog = pane.createDialog(owner,"About");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.pack();
		
		dialog.setVisible(true);
		
	}

}
