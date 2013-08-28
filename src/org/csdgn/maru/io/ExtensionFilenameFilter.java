package org.csdgn.maru.io;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

public class ExtensionFilenameFilter implements FilenameFilter {
	private final ExtensionFilenameFilter that = this;
	private String[] extensions;
	/**
	 * Extensions ending without a ., so png, txt, jpeg, and so on.
	 * @param ext
	 */
	public ExtensionFilenameFilter(String ... ext) {
		extensions = ext;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		int index = name.lastIndexOf('.');
		if(index == -1)
			return false;
		String ext = name.substring(index + 1);
		for(String ex : extensions) {
			if(ext.equalsIgnoreCase(ex)) {
				return true;
			}
		}
		return false;
	}
	
	public FileFilter getFileChooserFilter(final String desc) {
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				return that.accept(f.getParentFile(), f.getName());
			}

			@Override
			public String getDescription() {
				return desc;
			}
		};
	}

}
