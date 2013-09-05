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
package org.csdgn.hanami;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.csdgn.hanami.view.AboutDialog;
import org.csdgn.hanami.view.ImageBox;
import org.csdgn.hanami.view.OptionsDialog;
import org.csdgn.maru.image.AnimatedImage;
import org.csdgn.maru.image.builder.GIFFrameBuilder;
import org.csdgn.maru.io.DirectoryModel;
import org.csdgn.maru.io.ExtensionFilenameFilter;

/**
 * Combining all Lite functions into one program.<br>
 * Attempt #1
 * 
 * @author Chase
 * 
 */
public class Hanami extends KeyAdapter implements Runnable, ActionListener {

	/**
	 * TODO: find a way to extract this class
	 */
	class ImageLoader implements Runnable {
		File file;

		public ImageLoader(File file) {
			this.file = file;
		}

		// isBusy,rawImage,scaleAnimatedImage(),loadImage(),setOverlayText(),
		// setOverlayTextToFileData() are external

		@Override
		public void run() {
			try {
				setOverlayText("[Reading]");

				AnimatedImage image = null;
				if (file.getName().toLowerCase().endsWith("gif")) {
					image = new AnimatedImage(new GIFFrameBuilder(new FileInputStream(file), true));
				} else {
					image = new AnimatedImage(ImageIO.read(file));
				}

				setOverlayText("[Scaling]");
				rawImage = image;
				image = scaleAnimatedImage(image);

				setOverlayText("[Displaying]");
				loadImage(image);

				setOverlayTextToFileData(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO: find a way to extract this class
	 */
	class SimpleAnimator implements Runnable {
		AnimatedImage anim;
		int frameIndex = 0;

		public SimpleAnimator(AnimatedImage anim) {
			this.anim = anim;
		}

		@Override
		public void run() {
			view.setImage(anim.getFrames()[0]);
			tryResizeWindow();
			try {
				while (true) {
					Thread.sleep(anim.getDurations()[frameIndex]);
					++frameIndex;
					if (frameIndex >= anim.getFrameCount())
						frameIndex = 0;
					view.setImage(anim.getFrames()[frameIndex]);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	AnimatedImage rawImage;

	static File fileToLoad = null;

	public static void main(String[] args) throws IOException {
		// Set the UI to the native UI, fail silently if we can't
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// handle arguments
		if (args.length == 1) {
			fileToLoad = new File(args[0]);
			if (!fileToLoad.exists()) {
				fileToLoad = null;
			}
		} else if (args.length == 2 && "-x".equals(args[0])) {
			// Hex decode
			StringBuilder sb = new StringBuilder();

			for (String s : args[1].split("(?<=\\G....)")) {
				sb.append((char) Integer.parseInt(s, 16));
			}

			fileToLoad = new File(sb.toString());
			if (!fileToLoad.exists()) {
				fileToLoad = null;
			}
		}

		EventQueue.invokeLater(new Hanami());

		startTimerHack();
	}

	/**
	 * We need this hack to keep the timers for animated images synced. This
	 * hack uses practically no CPU/memory.
	 */
	static final Thread startTimerHack() {
		Thread timerHack = new Thread("Java Timer Hack") {
			@Override
			public void run() {
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		timerHack.setDaemon(true); // Very Important!
		timerHack.start();
		return timerHack;
	}

	JDialog full;
	JFrame window;
	ImageBox view;
	JScrollPane scrollPane;
	JLabel overlayText;
	DirectoryModel directory;
	int index = -1;
	boolean isFullscreen = false;
	Options options = new Options();
	OptionsDialog optdiag;
	JFileChooser openChooser;
	
	Future<?> animFuture = null;
	Future<?> loadFuture = null;
	ExecutorService loadpool;
	ExecutorService animpool;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch (cmd) {
		case "menu_open":
			if (openChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
				loadFile(openChooser.getSelectedFile());
			}
			break;
		case "menu_exit":
			System.exit(0);
			break;
		case "menu_options":
			optdiag.setOptions(options);
			if (optdiag.showOptionsDialog(window) == OptionsDialog.APPROVE_OPTION) {
				options = optdiag.getOptions();
				// Save options to file :)
				saveOptions();
			}
			break;
		case "menu_about":
			AboutDialog dlg = new AboutDialog();
			dlg.showAboutDialog(window);
		}
	}

	void saveOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.storeToFile(new File(local, OptionsFilename));
	}

	static final String OptionsFilename = "hanami.cfg";

	void loadOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.loadFromFile(new File(local, OptionsFilename));
	}

	public JMenuBar createMenu() {
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

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setActionCommand("menu_exit");
		mntmExit.setMnemonic('x');
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);

		JMenu mnOptions = new JMenu("View");
		mnOptions.setMnemonic('v');
		menuBar.add(mnOptions);

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
			if (scrollPane.getWidth() < view.getWidth()) {
				if (isFullscreen) {
					performScrollAction(scrollPane.getHorizontalScrollBar(), e);
				}
				canLeftRight = false;
			}
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			if (scrollPane.getHeight() < view.getHeight()) {
				if (isFullscreen) {
					performScrollAction(scrollPane.getVerticalScrollBar(), e);
				}
				// canUpDown = false;
			}
			break;
		}

		// we can't browse directories if we are busy 
		// or don't have one to browse
		if (index == -1 || (loadFuture != null && !loadFuture.isDone())) {
			return;
		}

		switch (code) {
		case KeyEvent.VK_LEFT:
			if (!canLeftRight) {
				break;
			}
		case KeyEvent.VK_PAGE_UP:
		case KeyEvent.VK_BACK_SPACE:
			if (--index < 0) {
				index = directory.size() - 1;
			}
			loadModelFile(directory.getFile(index));
			break;
		case KeyEvent.VK_RIGHT:
			if (!canLeftRight) {
				break;
			}
		case KeyEvent.VK_PAGE_DOWN:
		case KeyEvent.VK_SPACE:
			if (++index >= directory.size()) {
				index = 0;
			}
			loadModelFile(directory.getFile(index));
			break;
		case KeyEvent.VK_HOME:
			index = 0;
			loadModelFile(directory.getFile(index));
			break;
		case KeyEvent.VK_END:
			index = directory.size() - 1;
			loadModelFile(directory.getFile(index));
			break;
		}

	}

	void loadFile(File file) {
		try {
			directory.loadDirectory(file);
			index = directory.getFileIndex(file);
			loadModelFile(file);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/** Loads the given file without putting it in a thread */
	private void loadFileDirect(File file) {
		try {
			directory.loadDirectory(file);
			index = directory.getFileIndex(file);
			new ImageLoader(file).run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	void loadFile(String filename) {
		loadFile(new File(filename));
	}

	void loadImage(AnimatedImage image) {
		// stop old animation, if any
		if (animFuture != null) {
			if(!animFuture.isDone())
				animFuture.cancel(true);
			animFuture = null;
		}

		// load image into system
		if (image.getFrameCount() > 1) {
			//anim = ;
			//animpool.execute(anim);
			animFuture = animpool.submit(new SimpleAnimator(image));
			//anim.start();
		} else {
			loadSingleImage(image.getFrames()[0]);
		}

		resetScroll();
	}

	private void loadSingleImage(BufferedImage image) {
		view.setImage(image);
		tryResizeWindow();
	}

	synchronized void loadModelFile(File file) {
		loadFuture = loadpool.submit(new ImageLoader(file));
	}

	void resetScroll() {
		JViewport vp = scrollPane.getViewport();
		
		Point p = vp.getViewPosition();
		
		Dimension size = view.getPreferredSize();
		
		p.x = 0;
		p.y = 0;
		
		switch(options.startScrollX) {
		case SwingConstants.CENTER:
			if(size.width > vp.getWidth()) {
				p.x = (size.width >> 1) - (vp.getWidth() >> 1);
			}
			break;
		case SwingConstants.RIGHT:
			p.x = vp.getWidth();
			break;
		}
		
		switch(options.startScrollY) {
		case SwingConstants.CENTER:
			if(size.height > vp.getHeight()) {
				p.y = (size.height >> 1) - (vp.getHeight() >> 1);
			}
			break;
		case SwingConstants.BOTTOM:
			p.y = vp.getHeight();
			break;
		}
		
		vp.setViewPosition(p);
	}

	// for fullscreen, since we don't want to display the scrollbars
	void performScrollAction(JScrollBar bar, KeyEvent e) {
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

	void resizeWindow() {
		// Arguments: Window window, Dimension (image)size
		Dimension size = view.getPreferredSize();

		Rectangle max = WindowToolkit.getMaximumWindowBounds(window);
		Insets margin = WindowToolkit.getContentInsets(window, scrollPane);

		Rectangle bounds = window.getBounds();
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
		window.setBounds(bounds);
	}

	public void setupThreadPooling() {
		ThreadFactory tfactory = new ThreadFactory() {
		    public Thread newThread(Runnable r) {
		        Thread t = new Thread(r);
		        t.setDaemon(true);
		        return t;
		    }
		};
		loadpool = Executors.newSingleThreadExecutor(tfactory);
		animpool = Executors.newSingleThreadExecutor(tfactory);
	}
	
	@Override
	public void run() {
		loadOptions();
		
		window = new JFrame("Hanami");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, 240, 240);
		window.setLocationByPlatform(true);
		window.setIconImages(AppToolkit.getAppIconImages());

		setupThreadPooling();
		
		optdiag = new OptionsDialog();

		window.setJMenuBar(createMenu());

		full = new JDialog();
		full.setUndecorated(true);
		full.setAlwaysOnTop(true);

		// setup full screen glass pane
		overlayText = new JLabel();
		overlayText.setForeground(options.foreground);
		overlayText.setHorizontalAlignment(options.fullTextAlignX);
		overlayText.setVerticalAlignment(options.fullTextAlignY);
		full.setGlassPane(overlayText);
		overlayText.setVisible(true);

		directory = new DirectoryModel();

		ExtensionFilenameFilter eff = new ExtensionFilenameFilter("png", "gif", "jpg", "jpeg");

		directory.setDirectoryFilter(eff);

		// readOnly mode to prevent renaming files
		// when you click on them in the chooser
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		openChooser = new JFileChooser();
		openChooser.setAcceptAllFileFilterUsed(true);
		openChooser.setMultiSelectionEnabled(false);
		openChooser.setFileFilter(eff.getFileChooserFilter("Image File"));

		view = new ImageBox();

		JPanel wrapper = new JPanel(new java.awt.GridBagLayout());		
		wrapper.setBackground(options.background);
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.weightx = gbc.weighty = 1;
		gbc.anchor = options.imageAnchor;
		wrapper.add(view, gbc);

		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setViewportView(wrapper);

		setupScrollBars();

		window.addKeyListener(this);
		full.addKeyListener(this);

		window.add(scrollPane);
		window.setVisible(true);

		if (fileToLoad != null) {
			loadFileDirect(fileToLoad);
		}
	}

	AnimatedImage scaleAnimatedImage(AnimatedImage image) {
		BufferedImage[] imgs = new BufferedImage[image.getFrameCount()];
		for (int i = 0; i < imgs.length; ++i) {
			imgs[i] = scaleImage(image.getFrames()[i]);
		}
		return new AnimatedImage(imgs, image.getDurations());
	}

	BufferedImage scaleImage(BufferedImage image) {
		Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

		Dimension maxSize;
		if (isFullscreen) {
			Rectangle maxRect = WindowToolkit.getCurrentScreenBounds(full);
			maxSize = new Dimension(maxRect.width, maxRect.height);
		} else {
			Rectangle maxRect = WindowToolkit.getMaximumWindowBounds(window);
			Insets insets = WindowToolkit.getContentInsets(window, window.getContentPane());
			maxSize = new Dimension(maxRect.width - insets.left - insets.right, maxRect.height - insets.top - insets.bottom);
		}
		
		Dimension scaled = imageSize;

		boolean onlyScaleIfLarger = isFullscreen ? options.fullScaleLarge : options.winScaleLarge;
		int scale = isFullscreen ? options.fullScale : options.winScale;

		if (onlyScaleIfLarger) {
			if (imageSize.width > maxSize.width && (scale == Options.SCALE_FIT || scale == Options.SCALE_WIDTH)) {
				scaled = AppToolkit.getAdjustedScaledImageSize(imageSize, maxSize, scale);
			} else if (imageSize.height > maxSize.height && scale == Options.SCALE_FIT) {
				scaled = AppToolkit.getAdjustedScaledImageSize(imageSize, maxSize, scale);
			}
		} else {
			scaled = AppToolkit.getAdjustedScaledImageSize(imageSize, maxSize, scale);
		}

		if (scaled.width != imageSize.width || scaled.height != imageSize.height) {
			image = AppToolkit.getScaledImage(image, scaled.width, scaled.height, options.rescaleFilter);
		}

		return image;
	}

	void setFullscreen() {
		isFullscreen = true;

		Rectangle screen = WindowToolkit.getCurrentScreenBounds(window);

		window.remove(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		full.add(scrollPane);

		full.setVisible(true);
		full.setBounds(screen);
		full.requestFocus();
		full.toFront();

		loadImage(scaleAnimatedImage(rawImage));
	}

	synchronized void setOverlayText(String text) {
		overlayText.setText(text);
		window.setTitle(String.format("Hanami - %s", text));
	}

	synchronized void setOverlayTextToFileData(File file) {
		String path = file.getAbsolutePath();
		setOverlayText(String.format("[%d/%d] %s", index + 1, directory.size(), path));
	}

	void setScrollSize() {
		Dimension sSize = scrollPane.getSize();
		if (sSize.width == 1) {
			return;
		}

		Dimension iSize = view.getPreferredSize();

		// some nice scrolling dynamics
		int scroll = Math.min(sSize.height >> 1, (int) (iSize.height / 20.0));

		scrollPane.getVerticalScrollBar().setBlockIncrement(scroll);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Math.max(scroll >> 2, 1));

		scroll = Math.min(sSize.width >> 1, (int) (iSize.width / 20.0));

		scrollPane.getHorizontalScrollBar().setBlockIncrement(scroll);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(Math.max(scroll >> 2, 1));
	}

	void setupScrollBars() {
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

	void setWindowed() {
		isFullscreen = false;

		full.remove(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		window.add(scrollPane);

		full.setBounds(0, 0, 0, 0);
		full.setVisible(false);

		loadImage(scaleAnimatedImage(rawImage));

		window.validate();

		tryResizeWindow();
	}

	void tryResizeWindow() {
		if (!isFullscreen) {
			resizeWindow();
		}
	}
}
