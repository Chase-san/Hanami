package org.csdgn.hanami.view.options;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.csdgn.hanami.Options;
import org.csdgn.maru.util.HashBiMap;

public class Anchors extends JPanel implements OptionPanel, ActionListener {
	private static final long serialVersionUID = 1L;

	private Options tempOptions;

	private static HashBiMap<String, Integer> ANCHOR_DATA = new HashBiMap<String, Integer>(new String[] { "anchor_tl", "anchor_tc",
			"anchor_tr", "anchor_ml", "anchor_mc", "anchor_mr", "anchor_bl", "anchor_bc", "anchor_br" }, new Integer[] {
			GridBagConstraints.NORTHWEST, GridBagConstraints.NORTH, GridBagConstraints.NORTHEAST, GridBagConstraints.WEST,
			GridBagConstraints.CENTER, GridBagConstraints.EAST, GridBagConstraints.SOUTHWEST, GridBagConstraints.SOUTH,
			GridBagConstraints.SOUTHEAST });

	private final ButtonGroup AnchorGroup = new ButtonGroup();
	private final ButtonGroup ScrollGroup = new ButtonGroup();

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

	public Anchors() {
		setLayout(new GridLayout(2, 1, 4, 4));
		{
			JPanel panel = new JPanel();
			add(panel);
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Image Anchor", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			panel.setToolTipText("Determines how the image will be anchored to the frame.");
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
			add(panel);
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Initial Scroll Position", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			panel.setToolTipText("Determines the image will be scrolled when it first loaded.");
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
	public void setOptions(Options options) {
		switch (options.imageAnchor) {
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

		// scroll
		{
			switch (options.startScrollX) {
			case SwingConstants.LEFT:
				switch (options.startScrollY) {
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
				switch (options.startScrollY) {
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
				switch (options.startScrollY) {
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
		tempOptions = options;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.startsWith("anchor_")) {
			tempOptions.imageAnchor = ANCHOR_DATA.get(cmd);
		}
		if(cmd.startsWith("scroll_")) {
			switch (cmd) {
			case "scroll_tl":
			case "scroll_tc":
			case "scroll_tr":
				tempOptions.startScrollY = SwingConstants.TOP;
				break;
			case "scroll_ml":
			case "scroll_mc":
			case "scroll_mr":
				tempOptions.startScrollY = SwingConstants.CENTER;
				break;
			case "scroll_bl":
			case "scroll_bc":
			case "scroll_br":
				tempOptions.startScrollY = SwingConstants.BOTTOM;
				break;
			}
			// scroll x
			switch (cmd) {
			case "scroll_tl":
			case "scroll_ml":
			case "scroll_bl":
				tempOptions.startScrollX = SwingConstants.LEFT;
				return;
			case "scroll_tc":
			case "scroll_mc":
			case "scroll_bc":
				tempOptions.startScrollX = SwingConstants.CENTER;
				return;
			case "scroll_tr":
			case "scroll_mr":
			case "scroll_br":
				tempOptions.startScrollX = SwingConstants.RIGHT;
				return;
			}
		}
	}
	
	public String toString() {
		return "Anchors";
	}
}
