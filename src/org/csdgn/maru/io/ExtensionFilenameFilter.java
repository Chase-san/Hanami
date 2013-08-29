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
