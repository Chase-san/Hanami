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

import java.awt.Color;
import java.awt.image.AffineTransformOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.swing.SwingConstants;

public class Options implements Cloneable {
	public static final int SCALE_NONE = 0;
	public static final int SCALE_WIDTH = 1;
	public static final int SCALE_FIT = 2;
	
	public int fullScale = SCALE_WIDTH;
	public int winScale = SCALE_FIT;
	public boolean fullScaleLarge = true;
	public boolean winScaleLarge = true;
	public int fullTextAlignX = SwingConstants.LEFT;
	public int fullTextAlignY = SwingConstants.TOP;
	public int imageAnchor = java.awt.GridBagConstraints.CENTER;
	public int startScrollX = SwingConstants.LEFT;
	public int startScrollY = SwingConstants.TOP;
	public int rescaleFilter = AffineTransformOp.TYPE_BILINEAR;
	public Color background = Color.BLACK;
	public Color foreground = Color.MAGENTA;
	
	public Options copy() {
		//as this class gets larger, I don't want to have to expand a copy constructor/method...
		//so I use ~REFLECTION~ instead (clone() might work, but its very close to heavy wizardry).
		Options settings = new Options();
		try {
			for(Field f : Options.class.getFields()) {
				if(isValidProperty(f)) {
					f.set(settings, f.get(this));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}
	
	public void loadFromFile(File file) {
		if(!file.exists())
			return;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(file));
			for(Field f : Options.class.getFields()) {
				if(isValidProperty(f)) {
					Object value = loadObject(f.get(this), properties.getProperty(f.getName()));
					//use default if not in settings file
					if(value != null) {
						//FOR DEBUGGING
						//System.out.println("Setting " + f.getName() + " to " + value);
						f.set(this, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isValidProperty(Field f) {
		int mods = f.getModifiers();
		if(Modifier.isPublic(mods)
		&& !Modifier.isFinal(mods)
		&& !Modifier.isStatic(mods))
			return true;
		return false;
	}
	
	public void storeToFile(File file) {
		Properties properties = new Properties();
		try {
			for(Field f : Options.class.getFields()) {
				if(isValidProperty(f)) {
					String name = f.getName();
					Object value = f.get(this);
					properties.setProperty(name, storeObject(value));
				}
			}
			properties.store(new FileOutputStream(file), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Object loadObject(Object obj, String read) {
		if(obj == null || read == null)
			return null;
		if(obj instanceof Color) {
			return Color.decode(read);
		}
		if(obj instanceof Integer) {
			return Integer.parseInt(read);
		}
		if(obj instanceof Boolean) {
			int a = read.indexOf(0);
			if(a == '0' || a == 'f' || a == 'n')
				return false;
			return true;
		}
		return null;
	}
	
	private static String storeObject(Object obj) {
		if(obj == null)
			return "null";
		if(obj instanceof Color) {
			Color c = (Color)obj;
			return String.format("#%02x%02x%02x", c.getRed(),c.getGreen(),c.getBlue());
		}
		return obj.toString();
	}
}
