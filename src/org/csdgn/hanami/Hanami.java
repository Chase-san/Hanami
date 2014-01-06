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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
public class Hanami {
	private static final String OptionsFilename = "hanami.cfg";

	public static void main(String[] args) throws IOException {
		// Set the UI to the native UI, fail silently if we can't
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		File fileToLoad = null;

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

		Hanami model = new Hanami();
		MainWindow view = new MainWindow(model);

		if (fileToLoad != null) {
			// view.l
			view.callLoadFile(fileToLoad);
		}

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

	private AnimatedImage rawImage;
	private DirectoryModel directory;
	private int index = -1;
	public Options options = new Options();
	public ExtensionFilenameFilter fileFilter;
	public File lastFile;

	public Hanami() {
		loadOptions();

		fileFilter = new ExtensionFilenameFilter("png", "gif", "jpg", "jpeg");
		directory = new DirectoryModel();
		directory.setDirectoryFilter(fileFilter);
	}

	public boolean canChangeIndex() {
		return directory.size() != 0;
	}

	public void deleteLastFile() {
		lastFile.delete();
		lastFile = null;
		try {
			directory.reloadDirectory();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public AnimatedImage getImage() {
		return rawImage;
	}

	public int getIndex() {
		return index;
	}

	public File getIndexedFile() {
		return directory.getFile(index);
	}

	public int getSize() {
		return directory.size();
	}

	public void loadFile(File file) {
		try {
			directory.loadDirectory(file);
			index = directory.getFileIndex(file);

			if (rawImage != null) {
				rawImage.flush();
			}
			AnimatedImage image = null;
			if (file.getName().toLowerCase().endsWith("gif")) {
				image = new AnimatedImage(new GIFFrameBuilder(new FileInputStream(file), true));
			} else {
				image = new AnimatedImage(ImageIO.read(file));
			}
			rawImage = image;

			lastFile = file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		lastFile = file;
	}

	private void loadOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.loadFromFile(new File(local, OptionsFilename));
	}

	public void nextIndex() {
		if (++index >= directory.size()) {
			index = 0;
		}
	}

	public void previousIndex() {
		if (--index < 0) {
			index = directory.size() - 1;
		}
	}

	public void reloadDirectory() {
		try {
			if (lastFile == null) {
				return;
			}
			directory.reloadDirectory();
			index = directory.getFileIndex(lastFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveOptions() {
		File local = AppToolkit.getLocalDirectory();
		options.storeToFile(new File(local, OptionsFilename));
	}

	public void setIndex(int index) {
		this.index = index;
	}
}