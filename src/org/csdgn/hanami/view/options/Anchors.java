package org.csdgn.hanami.view.options;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.csdgn.hanami.Options;

public class Anchors extends JPanel implements OptionPanel, ActionListener {
	private static final long serialVersionUID = 1L;

	private Options tempOptions;

	
	private static HashMap<String,Options.Position> POSITION_DATA = new HashMap<String,Options.Position>();
	static {
		//new String[] { "tl", "tc", "tr", "ml", "mc", "mr", "bl", "bc", "br" };
		//new Options.Position[] {};
	}

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
		setLayout(new GridLayout(1, 2, 4, 4));
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
		switch (options.imageAnchorPosition) {
		case TopLeft:
			tglAnchorTL.setSelected(true);
			break;
		case TopCenter:
			tglAnchorTC.setSelected(true);
			break;
		case TopRight:
			tglAnchorTR.setSelected(true);
			break;
		case Left:
			tglAnchorML.setSelected(true);
			break;
		case Center:
			tglAnchorMC.setSelected(true);
			break;
		case Right:
			tglAnchorMR.setSelected(true);
			break;
		case BottomLeft:
			tglAnchorBL.setSelected(true);
			break;
		case BottomCenter:
			tglAnchorBC.setSelected(true);
			break;
		case BottomRight:
			tglAnchorBR.setSelected(true);
			break;
		}

		// scroll
		switch (options.scrollStartPosition) {
		case TopLeft:
			tglScrollTL.setSelected(true);
			break;
		case TopCenter:
			tglScrollTC.setSelected(true);
			break;
		case TopRight:
			tglScrollTR.setSelected(true);
			break;
		case Left:
			tglScrollML.setSelected(true);
			break;
		case Center:
			tglScrollMC.setSelected(true);
			break;
		case Right:
			tglScrollMR.setSelected(true);
			break;
		case BottomLeft:
			tglScrollBL.setSelected(true);
			break;
		case BottomCenter:
			tglScrollBC.setSelected(true);
			break;
		case BottomRight:
			tglScrollBR.setSelected(true);
			break;
		}
		tempOptions = options;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.startsWith("anchor_")) {
			tempOptions.imageAnchorPosition = Options.Position.getPosition(cmd.substring(7));
		} else if(cmd.startsWith("scroll_")) {
			tempOptions.scrollStartPosition = Options.Position.getPosition(cmd.substring(7));
		}
	}
	
	public String toString() {
		return "Anchors";
	}
}
