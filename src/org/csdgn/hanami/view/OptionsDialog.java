/**
 * Copyright (c) 2013 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.hanami.view;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

import org.csdgn.hanami.Options;
import org.csdgn.hanami.view.options.Anchors;
import org.csdgn.hanami.view.options.OptionPanel;
import org.csdgn.hanami.view.options.Scaling;

import javax.swing.JScrollPane;
import javax.swing.JList;

import java.awt.Dimension;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class OptionsDialog extends JPanel {
	public static final int CANCEL_OPTION = 1;
	public static final int APPROVE_OPTION = 0;

	private static final long serialVersionUID = -517146768561794643L;
	private final JPanel contentPanel = new JPanel();
	
	private Options tempOptions = new Options();
	
	private JPanel content;
	private JScrollPane scrollPane;
	private DefaultListModel<JPanel> listModel;

	/**
	 * Create the dialog.
	 */
	public OptionsDialog() {
		setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel);
		
		contentPanel.setLayout(new BorderLayout(4, 0));
		
		listModel = new DefaultListModel<JPanel>();
		
		final JList<JPanel> list = new JList<JPanel>(listModel);
		listModel.addElement(new Scaling());
		listModel.addElement(new Anchors());
		
		list.setSelectedIndex(0);
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				content.removeAll();
				content.add(listModel.get(list.getSelectedIndex()));
				content.revalidate();
			}
		});
		
		scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(new Dimension(120, 130));
		contentPanel.add(scrollPane, BorderLayout.WEST);
		
		content = new JPanel();
		contentPanel.add(content);
		
		content.add(listModel.get(0));
		
	}

	public Options getOptions() {
		return tempOptions;
	}

	public void setOptions(Options options) {
		for(int i = 0; i < listModel.size(); ++i) {
			((OptionPanel)listModel.get(i)).setOptions(options);
		}
		tempOptions = options.copy();
	}

	public int showOptionsDialog(Window owner) {
		JOptionPane pane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		pane.setOptions(new String[] { "Save", "Cancel" });
		JDialog dialog = pane.createDialog(owner, "Options");
		
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setVisible(true);

		return "Save".equals(pane.getValue()) ? APPROVE_OPTION : CANCEL_OPTION;
	}
}