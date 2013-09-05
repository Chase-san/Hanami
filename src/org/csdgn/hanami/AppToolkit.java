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
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

public class AppToolkit {
	
	private static File directory = null;
	private static ArrayList<Image> icons = null;
	
	/**
	 * Gets the directory for the program.
	 */
	public static File getLocalDirectory() {
		if (directory != null) {
			return directory;
		}
		//Determine our base directory
		String codePath = AppToolkit.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		File codeFile = new File(codePath);
		if (codeFile.exists() && codePath != null) {
			// So we have a predictable format
			codePath = codeFile.toString();
			int len = codePath.length();
			if (len > 3) {
				String last = codePath.substring(len - 3).toLowerCase();
				if (last.equals("jar") || last.equals("bin")) {
					// Executing from a jar or development path
					return directory = codeFile.getParentFile();
				}
			}
			if (directory == null) {
				directory = codeFile;
			}
			while (!directory.isDirectory()) {
				directory = directory.getParentFile();
			}
			return directory;
		}

		// I guess we can't do anything else...
		return directory = new File(".");
	}
	
	/**
	 * Get the program Icons
	 * 
	 * @return
	 */
	public static ArrayList<Image> getAppIconImages() {
		if (icons != null)
			return icons;

		icons = new ArrayList<Image>();

		for (int val : new int[] { 16, 32, 48 }) {
			String iconString = String.format("icon%d.png", val);

			InputStream stream = getLocalResource(iconString);
			if(stream != null) try {
				icons.add(ImageIO.read(stream));
			} catch (IOException e) {
				//If we fail, just show the default java icon
			}
		}

		return icons;
	}
	
	public static InputStream getLocalResource(String resource) {
		// check within current directory first
		try {
			File file = new File(getLocalDirectory(), resource);
			if (file.exists()) {
				return new FileInputStream(file);
			}
		} catch (Exception e) {
			//TODO
		}
		
		// fall back to in jar
		InputStream stream = ClassLoader.getSystemResourceAsStream(resource);
		if(stream != null)
			return stream;
		
		// if that fails, try development location
		try {
			File file = new File(getLocalDirectory(), "resource/" + resource);
			if (file.exists()) {
				return new FileInputStream(file);
			}
		} catch (Exception e) {
			//TODO
		}
		
		return null;
	}
	
	
	public static Dimension getAdjustedScaledImageSize(Dimension imageSize, Dimension maxSize, int scale) {
		Dimension scaled = getScaledImageSize(imageSize,maxSize,scale);
		if(scaled.height > maxSize.height) {
			Integer UIScrollBarWidth = (Integer) UIManager.get("ScrollBar.width");
			if(UIScrollBarWidth != null) {
				maxSize.width -= UIScrollBarWidth;
				scaled = getScaledImageSize(imageSize,maxSize,scale);
			}
		}
		return scaled;
	}
	
	private static Dimension getScaledImageSize(Dimension imageSize, Dimension maxContentSize, int scale) {
		if(scale == Options.SCALE_NONE) {
			return new Dimension(imageSize.width,imageSize.height);
		}
		
		double ratio = imageSize.width/(double)imageSize.height;
		
		double scaledWidth = maxContentSize.height * ratio;
		double scaledHeight = maxContentSize.width / ratio;
		
		switch(scale) {
		case Options.SCALE_FIT:
			if(scaledHeight <= maxContentSize.height) {
				scaledWidth = maxContentSize.width;
			} else {
				scaledHeight = maxContentSize.height;
			}
			break;
		case Options.SCALE_WIDTH:
			scaledWidth = maxContentSize.width;
			break;
		default:
		}
		
		return new Dimension((int)scaledWidth,(int)scaledHeight);
	}
	
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height, int interpolationType) {
		 int imageWidth = image.getWidth();
		 int imageHeight = image.getHeight();
		
		 double scaleX = (double) width / imageWidth;
		 double scaleY = (double) height / imageHeight;
		 
		 AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		 AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, interpolationType);
		 //We cannot set type to just image.getType() as that can cause the program to hang.
		 return bilinearScaleOp.filter(image, new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
	}
}
