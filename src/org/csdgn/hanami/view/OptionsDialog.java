package org.csdgn.hanami.view;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;

import org.csdgn.hanami.options.Options;

public class OptionsDialog extends JPanel implements ActionListener {
	public static final int CANCEL_OPTION = 1;
	public static final int APPROVE_OPTION = 0;

	private static final long serialVersionUID = -517146768561794643L;
	private final JPanel contentPanel = new JPanel();
	private final ButtonGroup WDScalingGroup = new ButtonGroup();
	private final ButtonGroup FSScalingGroup = new ButtonGroup();
	private Options tempSettings = new Options();

	private JRadioButton rdFSOriginalSize;

	private JRadioButton rdFSScaleWidth;
	private JRadioButton rdFSScaleFit;
	private JCheckBox cbFSScaleIfLarger;
	private JRadioButton rdWDOriginalSize;
	private JRadioButton rdWDScaleWidth;
	private JRadioButton rdWDScaleFit;
	private JCheckBox cbWDScaleIfLarger;

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
			// cbFSScaleIfLarger.addItemListener(this);
			cbFSScaleIfLarger.setActionCommand("fs_scale_large");
			cbFSScaleIfLarger.setToolTipText("When this is checked only images larger then the screen are resized.");
			cbFSScaleIfLarger.addActionListener(this);
			panel.add(cbFSScaleIfLarger);
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
			rdWDScaleWidth = new JRadioButton("Scale images to width");
			rdWDScaleWidth.setActionCommand("wd_scale_width");
			rdWDScaleWidth.setToolTipText("When set to this images are scaled to the maximum window width.");
			WDScalingGroup.add(rdWDScaleWidth);
			rdWDScaleWidth.addActionListener(this);
			panel.add(rdWDScaleWidth);
			rdWDScaleFit = new JRadioButton("Scale images to fit");
			rdWDScaleFit.setActionCommand("wd_scale_fit");
			rdWDScaleFit.setToolTipText("When set to this images are scaled to fit within the maximum window size.");
			WDScalingGroup.add(rdWDScaleFit);
			rdWDScaleFit.addActionListener(this);
			panel.add(rdWDScaleFit);
			cbWDScaleIfLarger = new JCheckBox("Only scale large images");
			cbWDScaleIfLarger.setActionCommand("wd_scale_large");
			cbWDScaleIfLarger.setToolTipText("When this is checked only images larger then the maximum window size are resized.");
			cbWDScaleIfLarger.addActionListener(this);
			panel.add(cbWDScaleIfLarger);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
//		case "save":
//			approve = true;
//			setVisible(false);
//			break;
//		case "discard":
//			setVisible(false);
//			break;
		case "fs_scale_none":
			tempSettings.fullScale = Options.SCALE_NONE;
			break;
		case "fs_scale_width":
			tempSettings.fullScale = Options.SCALE_WIDTH;
			break;
		case "fs_scale_fit":
			tempSettings.fullScale = Options.SCALE_FIT;
			break;
		case "fs_scale_large":
			tempSettings.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			break;
		case "wd_scale_none":
			tempSettings.winScale = Options.SCALE_NONE;
			break;
		case "wd_scale_width":
			tempSettings.winScale = Options.SCALE_WIDTH;
			break;
		case "wd_scale_fit":
			tempSettings.winScale = Options.SCALE_FIT;
			break;
		case "wd_scale_large":
			tempSettings.fullScaleLarge = ((JCheckBox) e.getSource()).isSelected();
			break;
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
		}
		if (settings.fullScaleLarge) {
			cbFSScaleIfLarger.setSelected(true);
		}
		if (settings.winScaleLarge) {
			cbWDScaleIfLarger.setSelected(true);
		}
		tempSettings = settings.copy();
	}

	public int showOptionsDialog(Window owner) {
		JOptionPane pane = new JOptionPane(this,JOptionPane.PLAIN_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
		pane.setOptions(new String[] {"Save","Cancel"});
		JDialog dialog = pane.createDialog(owner,"Options");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		return "Save".equals(pane.getValue()) ? APPROVE_OPTION : CANCEL_OPTION;
	}
}
