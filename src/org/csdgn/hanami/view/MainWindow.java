/**
 * Copyright (c) 2013-2014 Robert Maupin
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.csdgn.hanami.AppToolkit;
import org.csdgn.hanami.Hanami;
import org.csdgn.hanami.Options;
import org.csdgn.hanami.WindowToolkit;
import org.csdgn.maru.image.AnimatedImage;

import com.mortennobel.imagescaling.ResampleFilters;

/**
 * This ought to be fun.
 * 
 * @author Chase
 * 
 */
public class MainWindow extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = -5687606517760941288L;

	class SimpleAnimator implements Runnable {
		AnimatedImage anim;
		int frameIndex = 0;

		public SimpleAnimator(AnimatedImage anim) {
			this.anim = anim;
		}

		@Override
		public void run() {
			image.setImage(anim.getFrames()[0]);
			tryResizeWindow();
			try {
				while (true) {
					Thread.sleep(anim.getDurations()[frameIndex]);
					++frameIndex;
					if (frameIndex >= anim.getFrameCount()) {
						frameIndex = 0;
					}
					image.setImage(anim.getFrames()[frameIndex]);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private Hanami model;

	private JDialog full;
	public ImageBox image;
	private JScrollPane scrollPane;
	private JLabel overlayText;
	private OptionsDialog optdiag;
	private JFileChooser openChooser;
	private AnimatedImage lastImage;
	public boolean isFullscreen = false;

	private Future<?> animationFuture;
	private ExecutorService callPool;
	private ExecutorService animationPool;
	
	private float lastZoom;

	public MainWindow(Hanami hanami) {
		super("Hanami");
		
		model = hanami;
		
		setupThreadPooling();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 240, 240);
		setLocationByPlatform(true);
		setIconImages(AppToolkit.getAppIconImages());

		optdiag = new OptionsDialog(this);

		setJMenuBar(createMenu());

		full = new JDialog();
		full.setUndecorated(true);
		full.setAlwaysOnTop(true);

		// setup full screen glass pane
		overlayText = new JLabel();
		overlayText.setForeground(model.options.foreground);
		overlayText.setHorizontalAlignment(model.options.getFullScreenCaptionHorizontal());
		overlayText.setVerticalAlignment(model.options.getFullScreenCaptionVertical());
		full.setGlassPane(overlayText);
		overlayText.setVisible(true);

		// readOnly mode to prevent renaming files
		// when you click on them in the chooser
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		openChooser = new JFileChooser();
		openChooser.setAcceptAllFileFilterUsed(true);
		openChooser.setMultiSelectionEnabled(false);
		openChooser.setFileFilter(model.fileFilter.getFileChooserFilter("Image File"));

		image = new ImageBox();

		JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
		wrapper.setBackground(model.options.background);
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.weightx = gbc.weighty = 1;
		gbc.anchor = model.options.getImageAnchorPosition();
		wrapper.add(image, gbc);

		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setViewportView(wrapper);

		setupScrollBars();

		addKeyListener(this);
		full.addKeyListener(this);

		add(scrollPane);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "menu_open":
			if (openChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				callLoadFile(openChooser.getSelectedFile());
			}
			break;
		case "menu_delete": {
			if (model.lastFile == null) {
				break;
			}
			if (model.options.askToDelete) {
				// show dialog
				Window owner = this;
				if (isFullscreen) {
					owner = full;
				}
				JPanel confirm = new JPanel(new BorderLayout(0, 4));
				confirm.add(new JLabel("Are you sure you want to delete this file?"), BorderLayout.CENTER);
				JCheckBox neverAskAgain = new JCheckBox("Never ask me again.");
				confirm.add(neverAskAgain, BorderLayout.SOUTH);

				if (JOptionPane.showConfirmDialog(owner, confirm, "Delete File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (neverAskAgain.isSelected()) {
						model.options.askToDelete = false;
					}
					model.deleteLastFile();
				}
			} else {
				model.deleteLastFile();
			}
			break;
		}
		case "menu_exit":
			System.exit(0);
			break;
		case "menu_refresh":
			model.reloadDirectory();
			setOverlayTextToFileData(model.lastFile);
			break;
		case "menu_options":
			optdiag.setOptions(model.options);
			if (optdiag.showDialog() == OptionsDialog.APPROVE_OPTION) {
				model.options = optdiag.getOptions();
				// Save options to file :)
				model.saveOptions();
			}
			break;
		case "menu_about":
			AboutDialog dlg = new AboutDialog();
			dlg.showAboutDialog(this);
		}
	}

	public void callLoadFile(final File file) {
		lastZoom = 1;
		callPool.submit(new Runnable() {
			@Override
			public void run() {
				if(model.loadFile(file)) {
					updateToNewImage();
					setOverlayTextToFileData(file);
				}
			}
		});
	}

	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.setActionCommand("menu_open");
		mntmOpen.setMnemonic('o');
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mntmOpen.addActionListener(this);
		mnFile.add(mntmOpen);

		mnFile.addSeparator();

		JMenuItem mntmDelete = new JMenuItem("Delete");
		mntmDelete.setActionCommand("menu_delete");
		mntmDelete.setMnemonic('d');
		mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mntmDelete.addActionListener(this);
		// mntmDelete.setEnabled(false);
		mnFile.add(mntmDelete);

		mnFile.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setActionCommand("menu_exit");
		mntmExit.setMnemonic('x');
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);

		JMenu mnOptions = new JMenu("View");
		mnOptions.setMnemonic('v');
		menuBar.add(mnOptions);

		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.setActionCommand("menu_refresh");
		mntmRefresh.setMnemonic('r');
		mntmRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mntmRefresh.addActionListener(this);
		mnOptions.add(mntmRefresh);

		mnOptions.addSeparator();

		JMenuItem mntmSettings = new JMenuItem("Options");
		mntmSettings.setActionCommand("menu_options");
		mntmSettings.setMnemonic('o');
		mntmSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
		mntmSettings.addActionListener(this);
		mnOptions.add(mntmSettings);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('h');
		mnHelp.addActionListener(this);
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About...");
		mntmAbout.setActionCommand("menu_about");
		mntmAbout.setMnemonic('a');
		mntmAbout.addActionListener(this);
		mnHelp.add(mntmAbout);

		return menuBar;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (isFullscreen) {
			switch (code) {
			// Exit full screen
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_ENTER:
				setWindowed();
				break;
			}
		} else {
			switch (code) {
			// Enter Full Screen
			case KeyEvent.VK_ENTER:
				setFullscreen();
				break;
			}
		}

		// boolean canUpDown = true;
		boolean canLeftRight = true;

		switch (code) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			if (scrollPane.getWidth() < image.getWidth()) {
				if (isFullscreen) {
					performScrollAction(scrollPane.getHorizontalScrollBar(), e);
				}
				canLeftRight = false;
			}
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			if (scrollPane.getHeight() < image.getHeight()) {
				if (isFullscreen) {
					performScrollAction(scrollPane.getVerticalScrollBar(), e);
				}
				// canUpDown = false;
			}
			break;
		}

		// we can't browse directories if we are busy
		// or don't have one to browse
		if (!model.canChangeIndex()) {
			return;
		}

		switch (code) {
		case KeyEvent.VK_LEFT:
			if (!canLeftRight) {
				break;
			}
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_BACK_SPACE:
			model.previousIndex();
			callLoadFile(model.getIndexedFile());
			break;
		case KeyEvent.VK_RIGHT:
			if (!canLeftRight) {
				break;
			}
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_SPACE:
			model.nextIndex();
			callLoadFile(model.getIndexedFile());
			break;
		case KeyEvent.VK_HOME:
			model.setIndex(0);
			callLoadFile(model.getIndexedFile());
			break;
		case KeyEvent.VK_END:
			model.setIndex(model.getSize() - 1);
			callLoadFile(model.getIndexedFile());
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_ADD:
			setImageZoom(lastZoom*1.5f);
			break;
		case KeyEvent.VK_SUBTRACT:
		case KeyEvent.VK_MINUS:
			setImageZoom(lastZoom/1.5f);
			break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	private void loadSingleImage(BufferedImage image) {
		this.image.setImage(image);
		tryResizeWindow();
	}

	// for fullscreen, since we don't want to display the scrollbars
	private void performScrollAction(JScrollBar bar, KeyEvent e) {
		InputMap imap = bar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		Object obj = imap.get(KeyStroke.getKeyStrokeForEvent(e));
		if (obj != null) {
			ActionMap amap = bar.getActionMap();
			Action a = amap.get(obj);
			if (a != null) {
				a.actionPerformed(new ActionEvent(bar, ActionEvent.ACTION_PERFORMED, null));
			}
		}
	}

	private void resetScroll() {
		JViewport vp = scrollPane.getViewport();

		Point p = vp.getViewPosition();

		Dimension size = image.getPreferredSize();

		p.x = 0;
		p.y = 0;

		switch (model.options.getScrollStartHorizontal()) {
		case SwingConstants.CENTER:
			if (size.width > vp.getWidth()) {
				p.x = (size.width >> 1) - (vp.getWidth() >> 1);
			}
			break;
		case SwingConstants.RIGHT:
			p.x = vp.getWidth();
			break;
		}

		switch (model.options.getScrollStartVertical()) {
		case SwingConstants.CENTER:
			if (size.height > vp.getHeight()) {
				p.y = (size.height >> 1) - (vp.getHeight() >> 1);
			}
			break;
		case SwingConstants.BOTTOM:
			p.y = vp.getHeight();
			break;
		}

		vp.setViewPosition(p);
	}

	private void resizeWindow() {
		// Arguments: Window window, Dimension (image)size
		Dimension size = image.getPreferredSize();

		Rectangle max = WindowToolkit.getMaximumWindowBounds(this);
		Insets margin = WindowToolkit.getContentInsets(this, scrollPane);

		Rectangle bounds = getBounds();
		bounds.width = size.width + margin.left + margin.right;
		bounds.height = size.height + margin.top + margin.bottom;

		// Height first
		if (bounds.height > max.height) {
			bounds.height = max.height;
			bounds.y = max.y;
			// add scrollbar adjustment to width
			Integer UIScrollBarWidth = (Integer) UIManager.get("ScrollBar.width");
			if (UIScrollBarWidth != null) {
				bounds.width += UIScrollBarWidth;
			}
		} else if (bounds.y + bounds.height > max.y + max.height) {
			bounds.y = max.y + max.height - bounds.height;
		}

		if (bounds.width > max.width) {
			bounds.width = max.width;
			bounds.x = max.x;
		} else if (bounds.x + bounds.width > max.x + max.width) {
			bounds.x = max.x + max.width - bounds.width;
		}

		// TODO only resize if new dimensions are larger?
		setBounds(bounds);
	}

	private AnimatedImage scaleAnimatedImage(AnimatedImage image) {
		BufferedImage[] imgs = new BufferedImage[image.getFrameCount()];
		for (int i = 0; i < imgs.length; ++i) {
			imgs[i] = scaleImage(image.getFrames()[i]);
		}
		return new AnimatedImage(imgs, image.getDurations());
	}

	private BufferedImage scaleImage(BufferedImage image) {
		Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

		Dimension scaled = imageSize;
		boolean onlyScaleIfLarger = isFullscreen ? model.options.fullScaleLarge : model.options.winScaleLarge;
		Options.Scale scale = isFullscreen ? model.options.fullScale : model.options.winScale;

		Dimension maxSize;
		if (isFullscreen) {
			Rectangle maxRect = WindowToolkit.getCurrentScreenBounds(full);
			maxSize = new Dimension(maxRect.width, maxRect.height);
		} else if (scale == Options.Scale.Window) {
			Rectangle maxRect = scrollPane.getBounds();
			maxSize = new Dimension(maxRect.width, maxRect.height);
			scale = Options.Scale.Fit;
		} else {
			Rectangle maxRect = WindowToolkit.getMaximumWindowBounds(this);
			Insets insets = WindowToolkit.getContentInsets(this, getContentPane());
			maxSize = new Dimension(maxRect.width - insets.left - insets.right, maxRect.height - insets.top - insets.bottom);
		}

		if (onlyScaleIfLarger) {
			if (imageSize.width > maxSize.width && (scale == Options.Scale.Fit || scale == Options.Scale.Width)
					|| imageSize.height > maxSize.height && scale == Options.Scale.Fit) {
				scaled = AppToolkit.getAdjustedScaledImageSize(imageSize, maxSize, scale);
			}
		} else {
			scaled = AppToolkit.getAdjustedScaledImageSize(imageSize, maxSize, scale);
		}

		if (scaled.width != imageSize.width || scaled.height != imageSize.height) {
			image = AppToolkit.getScaledImage(image, scaled.width, scaled.height, model.options.resizeFilter);
		}

		return image;
	}

	public void setFullscreen() {
		isFullscreen = true;

		Rectangle screen = WindowToolkit.getCurrentScreenBounds(this);

		remove(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		full.add(scrollPane);

		full.setVisible(true);
		full.setBounds(screen);
		full.requestFocus();
		full.toFront();

		//updateToNewImage();
		resetScroll();
	}

	public synchronized void setOverlayText(String text) {
		overlayText.setText(text);
		setTitle(String.format("Hanami - %s", text));
	}

	public synchronized void setOverlayTextToFileData(File file) {
		String path = file.getAbsolutePath();
		setOverlayText(String.format("[%d/%d] %s", model.getIndex() + 1, model.getSize(), path));
	}

	private void setScrollSize() {
		Dimension sSize = scrollPane.getSize();
		if (sSize.width == 1) {
			return;
		}

		Dimension iSize = image.getPreferredSize();

		// some nice scrolling dynamics
		int scroll = Math.min(sSize.height >> 1, (int) (iSize.height / 20.0));

		scrollPane.getVerticalScrollBar().setBlockIncrement(scroll);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Math.max(scroll >> 2, 1));

		scroll = Math.min(sSize.width >> 1, (int) (iSize.width / 20.0));

		scrollPane.getHorizontalScrollBar().setBlockIncrement(scroll);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(Math.max(scroll >> 2, 1));
	}

	private void setupScrollBars() {
		JScrollBar vert = scrollPane.getVerticalScrollBar();
		vert.setUnitIncrement(10);
		InputMap map = vert.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke("DOWN"), "positiveBlockIncrement");
		map.put(KeyStroke.getKeyStroke("UP"), "negativeBlockIncrement");

		JScrollBar horz = scrollPane.getHorizontalScrollBar();
		horz.setUnitIncrement(10);
		map = horz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke("RIGHT"), "positiveBlockIncrement");
		map.put(KeyStroke.getKeyStroke("LEFT"), "negativeBlockIncrement");

		scrollPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setScrollSize();
			}
		});
	}

	private void setupThreadPooling() {
		ThreadFactory tfactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		};
		callPool = Executors.newSingleThreadExecutor(tfactory);
		animationPool = Executors.newSingleThreadExecutor(tfactory);
	}

	public void setWindowed() {
		isFullscreen = false;

		full.remove(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane);

		full.setBounds(0, 0, 0, 0);
		full.setVisible(false);

		validate();

		//updateToNewImage();
		//setImageZoom(lastZoom);
		resetScroll();

		validate();

		tryResizeWindow();
	}

	private void tryResizeWindow() {
		if (!isFullscreen && model.options.winScale != Options.Scale.Window) {
			resizeWindow();
		}
	}
	
	private void setImageZoom(float zoom) {
		if(lastZoom == zoom) {
			updateImage(lastImage);
			return;
		}
		
		AnimatedImage rawImage = model.getImage();
		
		int nw = (int) (rawImage.getWidth() * zoom);
		int nh = (int) (rawImage.getHeight() * zoom);
		
		//get center of visible rect
		Rectangle rect = image.getVisibleRect();
		rect.x = (int) (rect.getCenterX() / lastZoom * zoom - rect.width / 2);
		rect.y = (int) (rect.getCenterY() / lastZoom * zoom - rect.height / 2);
		
		AnimatedImage zoomedImage = AppToolkit.getScaledAnimatedImage(rawImage, nw, nh, ResampleFilters.getLanczos3Filter());
		updateImage(zoomedImage);
		
		//recenter position (we need two of these, for some reason)
		image.scrollRectToVisible(rect);
		image.scrollRectToVisible(rect);
		
		lastZoom = zoom;
	}
	
	private void updateToNewImage() {
		updateImage(scaleAnimatedImage(model.getImage()));
	}

	private void updateImage(AnimatedImage image) {
		if (lastImage != null) {
			lastImage.flush();
		}

		// stop old animation, if any
		if (animationFuture != null) {
			if (!animationFuture.isDone()) {
				animationFuture.cancel(true);
			}
			animationFuture = null;
		}

		// load image into system
		if (image.getFrameCount() > 1) {
			// anim = ;
			// animpool.execute(anim);
			animationFuture = animationPool.submit(new SimpleAnimator(image));
			// anim.start();
		} else {
			loadSingleImage(image.getFrames()[0]);
		}

	}
}