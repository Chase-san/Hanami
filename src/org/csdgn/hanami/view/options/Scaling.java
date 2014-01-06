package org.csdgn.hanami.view.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.csdgn.hanami.Options;

import java.awt.GridLayout;

public class Scaling extends JPanel implements OptionPanel, ActionListener {

	private static final long serialVersionUID = 1L;

	private Options tempOptions;

	private final ButtonGroup WDScalingGroup = new ButtonGroup();
	private final ButtonGroup FSScalingGroup = new ButtonGroup();

	private JRadioButton rdFSOriginalSize;
	private JRadioButton rdFSScaleWidth;
	private JRadioButton rdFSScaleFit;
	private JCheckBox cbFSScaleIfLarger;
	private JRadioButton rdWDOriginalSize;
	private JRadioButton rdWDScaleWidth;
	private JRadioButton rdWDScaleFit;
	private JRadioButton rdWDScaleWin;
	private JCheckBox cbWDScaleIfLarger;

	/**
	 * Create the panel.
	 */
	public Scaling() {
		setLayout(new GridLayout(2, 1, 4, 4));
		{
			JPanel panel = new JPanel();
			add(panel);
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Fullscreen Scaling", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
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
			add(panel);
			panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Windowed Scaling", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
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

	}

	@Override
	public void setOptions(Options options) {
		switch (options.fullScale) {
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
		switch (options.winScale) {
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
		if (options.fullScaleLarge) {
			cbFSScaleIfLarger.setSelected(true);
		}
		if (options.winScaleLarge) {
			cbWDScaleIfLarger.setSelected(true);
		}
		tempOptions = options;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "fs_scale_none":
			tempOptions.fullScale = Options.SCALE_NONE;
			return;
		case "fs_scale_width":
			tempOptions.fullScale = Options.SCALE_WIDTH;
			return;
		case "fs_scale_fit":
			tempOptions.fullScale = Options.SCALE_FIT;
			return;
		case "fs_scale_large":
			tempOptions.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			return;
		case "wd_scale_none":
			tempOptions.winScale = Options.SCALE_NONE;
			return;
		case "wd_scale_width":
			tempOptions.winScale = Options.SCALE_WIDTH;
			return;
		case "wd_scale_fit":
			tempOptions.winScale = Options.SCALE_FIT;
			return;
		case "wd_scale_window":
			tempOptions.winScale = Options.SCALE_WINDOW;
			return;
		case "wd_scale_large":
			tempOptions.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			return;
		}
		
	}
	
	public String toString() {
		return "Scaling";
	}

}
