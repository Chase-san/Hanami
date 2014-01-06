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
package org.csdgn.hanami;

import java.awt.EventQueue;
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
import javax.swing.UIManager;

import org.csdgn.hanami.view.MainWindow;
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
public class Hanami implements Runnable {

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
				view.setOverlayText("[Reading]");

				if (rawImage != null) {
					rawImage.flush();
				}

				AnimatedImage image = null;
				if (file.getName().toLowerCase().endsWith("gif")) {
					image = new AnimatedImage(new GIFFrameBuilder(new FileInputStream(file), true));
				} else {
					image = new AnimatedImage(ImageIO.read(file));
				}

				view.setOverlayText("[Scaling]");
				rawImage = image;
				image = view.scaleAnimatedImage(image);

				view.setOverlayText("[Displaying]");
				loadImage(image, true);

				view.setOverlayTextToFileData(file);
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
			view.view.setImage(anim.getFrames()[0]);
			view.tryResizeWindow();
			try {
				while (true) {
					Thread.sleep(anim.getDurations()[frameIndex]);
					++frameIndex;
					if (frameIndex >= anim.getFrameCount()) {
						frameIndex = 0;
					}
					view.view.setImage(anim.getFrames()[frameIndex]);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public AnimatedImage rawImage;

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

	public DirectoryModel directory;
	public int index = -1;
	
	public Options options = new Options();

	Future<?> animFuture = null;
	public Future<?> loadFuture = null;
	ExecutorService loadpool;
	ExecutorService animpool;

	static final String OptionsFilename = "hanami.cfg";

	public File lastFile;

	AnimatedImage lastImage;
	
	public void deleteLastFile() {
		lastFile.delete();
		lastFile = null;
		try {
			directory.reloadDirectory();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadFile(File file) {
		try {
			directory.loadDirectory(file);
			index = directory.getFileIndex(file);
			loadModelFile(file);
			lastFile = file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void loadFile(String filename) {
		loadFile(new File(filename));
	}

	/** Loads the given file without putting it in a thread */
	private void loadFileDirect(File file) {
		try {
			directory.loadDirectory(file);
			index = directory.getFileIndex(file);
			new ImageLoader(file).run();
			lastFile = file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void loadImage(AnimatedImage image, boolean flushOld) {
		if (flushOld && lastImage != null) {
			lastImage.flush();
		}

		// stop old animation, if any
		if (animFuture != null) {
			if (!animFuture.isDone()) {
				animFuture.cancel(true);
			}
			animFuture = null;
		}

		// load image into system
		if (image.getFrameCount() > 1) {
			// anim = ;
			// animpool.execute(anim);
			animFuture = animpool.submit(new SimpleAnimator(image));
			// anim.start();
		} else {
			loadSingleImage(image.getFrames()[0]);
		}

		view.resetScroll();
	}

	public synchronized void loadModelFile(File file) {
		loadFuture = loadpool.submit(new ImageLoader(file));
		lastFile = file;
	}

	void loadOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.loadFromFile(new File(local, OptionsFilename));
	}

	private void loadSingleImage(BufferedImage image) {
		view.view.setImage(image);
		view.tryResizeWindow();
	}
	
	public ExtensionFilenameFilter fileFilter;

	public void reloadDirectory() {
		try {
			if(lastFile == null)
				return;
			
			directory.reloadDirectory();
			index = directory.getFileIndex(lastFile);

			// update title / caption
			view.setOverlayTextToFileData(lastFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private MainWindow view;

	@Override
	public void run() {
		loadOptions();

		setupThreadPooling();
		
		fileFilter = new ExtensionFilenameFilter("png", "gif", "jpg", "jpeg");
		directory = new DirectoryModel();
		directory.setDirectoryFilter(fileFilter);

		view = new MainWindow(this);

		if (fileToLoad != null) {
			loadFileDirect(fileToLoad);
		}
	}

	public void saveOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.storeToFile(new File(local, OptionsFilename));
	}

	public void setupThreadPooling() {
		ThreadFactory tfactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		};
		loadpool = Executors.newSingleThreadExecutor(tfactory);
		animpool = Executors.newSingleThreadExecutor(tfactory);
	}
}