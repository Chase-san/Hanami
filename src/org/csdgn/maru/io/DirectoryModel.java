/**
 * Copyright (c) 2011-2013 Robert Maupin
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
package org.csdgn.maru.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Observable;

/**
 * Handles the directories and their sorting. Be sure to set the filter before loading the directory!
 * 
 * @author Robert Maupin
 * @version 1.0
 */
public class DirectoryModel extends Observable {
	private File directory;
	private FilenameFilter filter = null;
	private File[] files;

	/**
	 * Just checks if the directory has been set, if not, it throws an
	 * exception.
	 */
	private void checkDirectoryState() {
		if (files == null) {
			throw new IllegalStateException("A directory has not been set.");
		}
	}

	private void doLoadDirectory(File dir) throws FileNotFoundException {
		if (!dir.exists()) {
			throw new FileNotFoundException(
					"The directory to load was not found.");
		}
		directory = dir;
		if (filter != null) {
			files = dir.listFiles(filter);
		} else {
			files = dir.listFiles();
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * Returns the file in the directory for the given index.
	 * 
	 * @param index
	 *            The index of the file in the directory
	 * @return the file of that index
	 */
	public File getFile(int index) {
		checkDirectoryState();
		return files[index];
	}

	/**
	 * Returns the given index of the indicated file in the directory under the
	 * current sorting.
	 * 
	 * @param file
	 *            The file for which to find the index of.
	 * @return the index of the file in the directory, or -1 if the file was not
	 *         found in the model. Just because the model cannot find it does
	 *         not mean it is not within the directory the model loaded, but
	 *         other factors may have prevented its inclusion in the model. Such
	 *         as the FileFilter.
	 * @see #setDirectoryFilter(FileFilter)
	 */
	public int getFileIndex(File file) {
		checkDirectoryState();
		// NOTE A hashmap could make this faster
		for (int i = 0; i < files.length; ++i) {
			if (files[i].equals(file)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets the directory to the given directory
	 * 
	 * @param filedir
	 *            The directory or a file within the desired directory to load
	 *            into the model.
	 * @throws FileNotFoundException
	 *             If the directory does not exist.
	 */
	public void loadDirectory(File filedir) throws FileNotFoundException {
		if (filedir == null) {
			throw new IllegalArgumentException("Argument must not be null.");
		}
		if (filedir.isDirectory()) {
			if(filedir.equals(directory))
				return;
			doLoadDirectory(filedir);
		} else {
			if(filedir.getParentFile().equals(directory))
				return;
			doLoadDirectory(filedir.getParentFile());
		}
	}

	/**
	 * Reloads the directory from the filesystem.
	 * 
	 * @throws FileNotFoundException
	 *             If the file does not exist.
	 */
	public void reloadDirectory() throws FileNotFoundException {
		if (!directory.exists()) {
			throw new FileNotFoundException(
					"The directory to reload was not found.");
		}
		doLoadDirectory(directory);
	}

	/**
	 * Sets the filter for this Directory Model to use. This will filter the
	 * files in the directory model. Possibly altering the index of files within
	 * the model.
	 * 
	 * @param filter
	 *            the filter to use for getting a list of files.
	 */
	public void setDirectoryFilter(FilenameFilter filter) {
		this.filter = filter;
	}

	/**
	 * Returns the number of currently indexed files in this directory model.
	 * 
	 * @return number of (possibly filtered) files in the directory
	 */
	public int size() {
		checkDirectoryState();
		return files.length;
	}

	/**
	 * Sorts the model via the given directory model sorter, which gets direct
	 * access to the internal file array for sorting.
	 * 
	 * @param sorter
	 *            A directory model sorter to use to sort this directory model.
	 */
	public void sort(DirectoryModelSorter sorter) {
		checkDirectoryState();
		sorter.sortFileArray(files);
	}
}
