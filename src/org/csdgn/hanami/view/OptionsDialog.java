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

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;

import org.csdgn.hanami.Options;
import org.csdgn.maru.util.HashBiMap;

import javax.swing.JToggleButton;

public class OptionsDialog extends JPanel implements ActionListener {
	public static final int CANCEL_OPTION = 1;
	public static final int APPROVE_OPTION = 0;

	private static HashBiMap<String, Integer> ANCHOR_DATA = new HashBiMap<String, Integer>(
			new String[] { "anchor_tl", "anchor_tc", "anchor_tr", "anchor_ml", "anchor_mc", "anchor_mr", "anchor_bl", "anchor_bc", "anchor_br" },
			new Integer[] { GridBagConstraints.NORTHWEST, GridBagConstraints.NORTH, GridBagConstraints.NORTHEAST,
					GridBagConstraints.WEST, GridBagConstraints.CENTER, GridBagConstraints.EAST,
					GridBagConstraints.SOUTHWEST, GridBagConstraints.SOUTH, GridBagConstraints.SOUTHEAST });

	private static final long serialVersionUID = -517146768561794643L;
	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup WDScalingGroup = new ButtonGroup();
	private final ButtonGroup FSScalingGroup = new ButtonGroup();
	private final ButtonGroup AnchorGroup = new ButtonGroup();
	private final ButtonGroup ScrollGroup = new ButtonGroup();
	private Options tempSettings = new Options();

	private JRadioButton rdFSOriginalSize;

	private JRadioButton rdFSScaleWidth;
	private JRadioButton rdFSScaleFit;
	private JCheckBox cbFSScaleIfLarger;
	private JRadioButton rdWDOriginalSize;
	private JRadioButton rdWDScaleWidth;
	private JRadioButton rdWDScaleFit;
	private JRadioButton rdWDScaleWin;
	private JCheckBox cbWDScaleIfLarger;

	private JToggleButton tglAnchorTL;
	private JToggleButton tglAnchorTC;
	private JToggleButton tglAnchorTR;
	private JToggleButton tglAnchorML;
	private JToggleButton tglAnchorMC;
	private JToggleButton tglAnchorMR;
	private JToggleButton tglAnchorBL;
	private JToggleButton tglAnchorBC;
	private JToggleButton tglAnchorBR;

	private JToggleButton tglScrollTL;
	private JToggleButton tglScrollTC;
	private JToggleButton tglScrollTR;
	private JToggleButton tglScrollML;
	private JToggleButton tglScrollMC;
	private JToggleButton tglScrollMR;
	private JToggleButton tglScrollBL;
	private JToggleButton tglScrollBC;
	private JToggleButton tglScrollBR;

	/**
	 * Create the dialog.
	 */
	public OptionsDialog() {
		setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel);
		contentPanel.setLayout(new GridLayout(2, 1, 0, 0));
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Fullscreen Scaling", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			rdFSOriginalSize = new JRadioButton("Do not scale images");
			rdFSOriginalSize.setActionCommand("fs_scale_none");
			rdFSOriginalSize.setToolTipText("When set to this images are never scaled.");
			FSScalingGroup.add(rdFSOriginalSize);
			rdFSOriginalSize.addActionListener(this);
			panel.add(rdFSOriginalSize);
			rdFSScaleWidth = new JRadioButton("Scale images to width");
			rdFSScaleWidth.setActionCommand("fs_scale_width");
			rdFSScaleWidth.setToolTipText("When set to this the images are scaled to the screen width.");
			FSScalingGroup.add(rdFSScaleWidth);
			rdFSScaleWidth.addActionListener(this);
			panel.add(rdFSScaleWidth);
			rdFSScaleFit = new JRadioButton("Scale images to fit");
			rdFSScaleFit.setActionCommand("fs_scale_fit");
			rdFSScaleFit.setToolTipText("When set to this images are scaled to fit on the screen.");
			FSScalingGroup.add(rdFSScaleFit);
			rdFSScaleFit.addActionListener(this);
			panel.add(rdFSScaleFit);
			cbFSScaleIfLarger = new JCheckBox("Only scale large images");
			cbFSScaleIfLarger.setActionCommand("fs_scale_large");
			cbFSScaleIfLarger.setToolTipText("When this is checked only images larger then the screen are resized.");
			cbFSScaleIfLarger.addActionListener(this);
			panel.add(cbFSScaleIfLarger);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Image Anchor", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			panel.setToolTipText("Determines how the image will be anchored to the frame.");

			contentPanel.add(panel);
			panel.setLayout(new GridLayout(0, 3, 0, 0));

			tglAnchorTL = new JToggleButton("↖");
			tglAnchorTL.setActionCommand("anchor_tl");
			tglAnchorTL.setToolTipText("This anchors the image to the top left side of the frame.");
			tglAnchorTL.setFocusable(false);
			AnchorGroup.add(tglAnchorTL);
			panel.add(tglAnchorTL);

			tglAnchorTC = new JToggleButton("↑");
			tglAnchorTC.setActionCommand("anchor_tc");
			tglAnchorTC.setToolTipText("This anchors the image to the top of the frame.");
			tglAnchorTC.setFocusable(false);
			AnchorGroup.add(tglAnchorTC);
			panel.add(tglAnchorTC);

			tglAnchorTR = new JToggleButton("↗");
			tglAnchorTR.setActionCommand("anchor_tr");
			tglAnchorTR.setToolTipText("This anchors the image to the top right side of the frame.");
			tglAnchorTR.setFocusable(false);
			AnchorGroup.add(tglAnchorTR);
			panel.add(tglAnchorTR);

			tglAnchorML = new JToggleButton("←");
			tglAnchorML.setActionCommand("anchor_ml");
			tglAnchorML.setToolTipText("This anchors the image to the left side of the frame.");
			tglAnchorML.setFocusable(false);
			AnchorGroup.add(tglAnchorML);
			panel.add(tglAnchorML);

			tglAnchorMC = new JToggleButton();
			tglAnchorMC.setActionCommand("anchor_mc");
			tglAnchorMC.setToolTipText("This anchors the image to the central part of the frame.");
			tglAnchorMC.setFocusable(false);
			AnchorGroup.add(tglAnchorMC);
			panel.add(tglAnchorMC);

			tglAnchorMR = new JToggleButton("→");
			tglAnchorMR.setActionCommand("anchor_mr");
			tglAnchorMR.setToolTipText("This anchors the image to the right side of the frame.");
			tglAnchorMR.setFocusable(false);
			AnchorGroup.add(tglAnchorMR);
			panel.add(tglAnchorMR);

			tglAnchorBL = new JToggleButton("↙");
			tglAnchorBL.setActionCommand("anchor_bl");
			tglAnchorBL.setToolTipText("This anchors the image to the bottom left side of the frame.");
			tglAnchorBL.setFocusable(false);
			AnchorGroup.add(tglAnchorBL);
			panel.add(tglAnchorBL);

			tglAnchorBC = new JToggleButton("↓");
			tglAnchorBC.setActionCommand("anchor_bc");
			tglAnchorBC.setToolTipText("This anchors the image to the bottom of the frame.");
			tglAnchorBC.setFocusable(false);
			AnchorGroup.add(tglAnchorBC);
			panel.add(tglAnchorBC);

			tglAnchorBR = new JToggleButton("↘");
			tglAnchorBR.setActionCommand("anchor_br");
			tglAnchorBR.setToolTipText("This anchors the image to the bottom right side of the frame.");
			tglAnchorBR.setFocusable(false);
			AnchorGroup.add(tglAnchorBR);
			panel.add(tglAnchorBR);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Windowed Scaling", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panel);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			rdWDOriginalSize = new JRadioButton("Do not scale images");
			rdWDOriginalSize.setActionCommand("wd_scale_none");
			rdWDOriginalSize.setToolTipText("When set to this images are never scaled.");
			WDScalingGroup.add(rdWDOriginalSize);
			rdWDOriginalSize.addActionListener(this);
			panel.add(rdWDOriginalSize);
			rdWDScaleFit = new JRadioButton("Scale images to desktop");
			rdWDScaleFit.setActionCommand("wd_scale_fit");
			rdWDScaleFit
					.setToolTipText("When set to this images are scaled to fit within the maximum content size, based on the desktop size.");
			WDScalingGroup.add(rdWDScaleFit);
			rdWDScaleFit.addActionListener(this);
			panel.add(rdWDScaleFit);
			rdWDScaleWidth = new JRadioButton("Scale images to desktop width");
			rdWDScaleWidth.setActionCommand("wd_scale_width");
			rdWDScaleWidth.setToolTipText("When set to this images are scaled to the maximum content width, based on the desktop width.");
			WDScalingGroup.add(rdWDScaleWidth);
			rdWDScaleWidth.addActionListener(this);
			panel.add(rdWDScaleWidth);
			rdWDScaleWin = new JRadioButton("Scale images to window");
			rdWDScaleWin.setActionCommand("wd_scale_window");
			rdWDScaleWin.setToolTipText("When set to this, images are scaled to fit within the current window.");
			WDScalingGroup.add(rdWDScaleWin);
			rdWDScaleWin.addActionListener(this);
			panel.add(rdWDScaleWin);
			cbWDScaleIfLarger = new JCheckBox("Only scale large images");
			cbWDScaleIfLarger.setActionCommand("wd_scale_large");
			cbWDScaleIfLarger.setToolTipText("When this is checked only images larger then the maximum window size are resized.");
			cbWDScaleIfLarger.addActionListener(this);
			panel.add(cbWDScaleIfLarger);
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Initial Scroll Position", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			panel.setToolTipText("Determines the image will be scrolled when it first loaded.");

			contentPanel.add(panel);
			panel.setLayout(new GridLayout(0, 3, 0, 0));

			tglScrollTL = new JToggleButton("↖");
			tglScrollTL.setActionCommand("scroll_tl");
			tglScrollTL.setToolTipText("This scrolls to the top left side of the frame.");
			tglScrollTL.setFocusable(false);
			ScrollGroup.add(tglScrollTL);
			panel.add(tglScrollTL);

			tglScrollTC = new JToggleButton("↑");
			tglScrollTC.setActionCommand("scroll_tc");
			tglScrollTC.setToolTipText("This scrolls to the top of the frame.");
			tglScrollTC.setFocusable(false);
			ScrollGroup.add(tglScrollTC);
			panel.add(tglScrollTC);

			tglScrollTR = new JToggleButton("↗");
			tglScrollTR.setActionCommand("scroll_tr");
			tglScrollTR.setToolTipText("This scrolls to the top right side of the frame.");
			tglScrollTR.setFocusable(false);
			ScrollGroup.add(tglScrollTR);
			panel.add(tglScrollTR);

			tglScrollML = new JToggleButton("←");
			tglScrollML.setActionCommand("scroll_ml");
			tglScrollML.setToolTipText("This scrolls to the left side of the frame.");
			tglScrollML.setFocusable(false);
			ScrollGroup.add(tglScrollML);
			panel.add(tglScrollML);

			tglScrollMC = new JToggleButton();
			tglScrollMC.setActionCommand("scroll_mc");
			tglScrollMC.setToolTipText("This scrolls to the central part of the frame.");
			tglScrollMC.setFocusable(false);
			ScrollGroup.add(tglScrollMC);
			panel.add(tglScrollMC);

			tglScrollMR = new JToggleButton("→");
			tglScrollMR.setActionCommand("scroll_mr");
			tglScrollMR.setToolTipText("This scrolls to the right side of the frame.");
			tglScrollMR.setFocusable(false);
			ScrollGroup.add(tglScrollMR);
			panel.add(tglScrollMR);

			tglScrollBL = new JToggleButton("↙");
			tglScrollBL.setActionCommand("scroll_bl");
			tglScrollBL.setToolTipText("This scrolls to the bottom left side of the frame.");
			tglScrollBL.setFocusable(false);
			ScrollGroup.add(tglScrollBL);
			panel.add(tglScrollBL);

			tglScrollBC = new JToggleButton("↓");
			tglScrollBC.setActionCommand("scroll_bc");
			tglScrollBC.setToolTipText("This scrolls to the bottom of the frame.");
			tglScrollBC.setFocusable(false);
			ScrollGroup.add(tglScrollBC);
			panel.add(tglScrollBC);

			tglScrollBR = new JToggleButton("↘");
			tglScrollBR.setActionCommand("scroll_br");
			tglScrollBR.setToolTipText("This scrolls to the bottom right side of the frame.");
			tglScrollBR.setFocusable(false);
			ScrollGroup.add(tglScrollBR);
			panel.add(tglScrollBR);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "fs_scale_none":
			tempSettings.fullScale = Options.SCALE_NONE;
			return;
		case "fs_scale_width":
			tempSettings.fullScale = Options.SCALE_WIDTH;
			return;
		case "fs_scale_fit":
			tempSettings.fullScale = Options.SCALE_FIT;
			return;
		case "fs_scale_large":
			tempSettings.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			return;
		case "wd_scale_none":
			tempSettings.winScale = Options.SCALE_NONE;
			return;
		case "wd_scale_width":
			tempSettings.winScale = Options.SCALE_WIDTH;
			return;
		case "wd_scale_fit":
			tempSettings.winScale = Options.SCALE_FIT;
			return;
		case "wd_scale_window":
			tempSettings.winScale = Options.SCALE_WINDOW;
			return;
		case "wd_scale_large":
			tempSettings.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			return;
		}
		if(cmd.startsWith("anchor_")) {
			tempSettings.imageAnchor = ANCHOR_DATA.get(cmd);
		}
		if(cmd.startsWith("scroll_")) {
			switch (cmd) {
			case "scroll_tl":
			case "scroll_tc":
			case "scroll_tr":
				tempSettings.startScrollY = SwingConstants.TOP;
				break;
			case "scroll_ml":
			case "scroll_mc":
			case "scroll_mr":
				tempSettings.startScrollY = SwingConstants.CENTER;
				break;
			case "scroll_bl":
			case "scroll_bc":
			case "scroll_br":
				tempSettings.startScrollY = SwingConstants.BOTTOM;
				break;
			}
			// scroll x
			switch (cmd) {
			case "scroll_tl":
			case "scroll_ml":
			case "scroll_bl":
				tempSettings.startScrollX = SwingConstants.LEFT;
				return;
			case "scroll_tc":
			case "scroll_mc":
			case "scroll_bc":
				tempSettings.startScrollX = SwingConstants.CENTER;
				return;
			case "scroll_tr":
			case "scroll_mr":
			case "scroll_br":
				tempSettings.startScrollX = SwingConstants.RIGHT;
				return;
			}
		}
	}

	public Options getOptions() {
		return tempSettings;
	}

	public void setOptions(Options settings) {
		switch (settings.fullScale) {
		case Options.SCALE_NONE:
			rdFSOriginalSize.setSelected(true);
			break;
		case Options.SCALE_WIDTH:
			rdFSScaleWidth.setSelected(true);
			break;
		case Options.SCALE_FIT:
			rdFSScaleFit.setSelected(true);
			break;
		}
		switch (settings.winScale) {
		case Options.SCALE_NONE:
			rdWDOriginalSize.setSelected(true);
			break;
		case Options.SCALE_WIDTH:
			rdWDScaleWidth.setSelected(true);
			break;
		case Options.SCALE_FIT:
			rdWDScaleFit.setSelected(true);
			break;
		case Options.SCALE_WINDOW:
			rdWDScaleWin.setSelected(true);
			break;
		}
		if (settings.fullScaleLarge) {
			cbFSScaleIfLarger.setSelected(true);
		}
		if (settings.winScaleLarge) {
			cbWDScaleIfLarger.setSelected(true);
		}
		
		switch (settings.imageAnchor) {
		case GridBagConstraints.NORTHWEST:
			tglAnchorTL.setSelected(true);
			break;
		case GridBagConstraints.NORTH:
			tglAnchorTC.setSelected(true);
			break;
		case GridBagConstraints.NORTHEAST:
			tglAnchorTR.setSelected(true);
			break;
		case GridBagConstraints.WEST:
			tglAnchorML.setSelected(true);
			break;
		case GridBagConstraints.CENTER:
			tglAnchorMC.setSelected(true);
			break;
		case GridBagConstraints.EAST:
			tglAnchorMR.setSelected(true);
			break;
		case GridBagConstraints.SOUTHWEST:
			tglAnchorBL.setSelected(true);
			break;
		case GridBagConstraints.SOUTH:
			tglAnchorBC.setSelected(true);
			break;
		case GridBagConstraints.SOUTHEAST:
			tglAnchorBR.setSelected(true);
			break;
		}
		
		//scroll
		{
			switch(settings.startScrollX) {
			case SwingConstants.LEFT:
				switch(settings.startScrollY) {
				case SwingConstants.TOP:
					tglScrollTL.setSelected(true);
					break;
				case SwingConstants.CENTER:
					tglScrollML.setSelected(true);
					break;
				case SwingConstants.BOTTOM:
					tglScrollBL.setSelected(true);
					break;
				}
				break;
			case SwingConstants.CENTER:
				switch(settings.startScrollY) {
				case SwingConstants.TOP:
					tglScrollTC.setSelected(true);
					break;
				case SwingConstants.CENTER:
					tglScrollMC.setSelected(true);
					break;
				case SwingConstants.BOTTOM:
					tglScrollBC.setSelected(true);
					break;
				}
				break;
			case SwingConstants.RIGHT:
				switch(settings.startScrollY) {
				case SwingConstants.TOP:
					tglScrollTR.setSelected(true);
					break;
				case SwingConstants.CENTER:
					tglScrollMR.setSelected(true);
					break;
				case SwingConstants.BOTTOM:
					tglScrollBR.setSelected(true);
					break;
				}
				break;
			}
			
			
		}
		tempSettings = settings.copy();
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