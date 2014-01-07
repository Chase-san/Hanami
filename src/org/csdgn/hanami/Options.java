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
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.swing.SwingConstants;

import com.mortennobel.imagescaling.ResampleFilter;
import com.mortennobel.imagescaling.ResampleFilters;

public class Options implements Cloneable {
	public static enum Scale {
		None,Width,Fit,Window
	}
	
	public static enum Position {
		TopLeft("tl"),TopCenter("tc"),TopRight("tr"),
		Left("ml"),Center("mc"),Right("mr"),
		BottomLeft("bl"),BottomCenter("bc"),BottomRight("br");
		
		public final String position;
		private Position(String data) {
			position = data;
		}
		public static Position getPosition(String data) {
			for(Position p : Position.values())
				if(p.position.equals(data))
					return p;
			return null;
		}
	}
	
	public Scale fullScale = Scale.Width;
	public Scale winScale = Scale.Fit;
	public boolean fullScaleLarge = true;
	public boolean winScaleLarge = true;
	public boolean askToDelete = true;
	public Position fullScreenCaptionPosition = Position.TopLeft;
	public Position scrollStartPosition = Position.TopLeft;
	public Position imageAnchorPosition = Position.Center;
	public ResampleFilter resizeFilter = ResampleFilters.getLanczos3Filter();
	public Color background = Color.BLACK;
	public Color foreground = Color.MAGENTA;
	
	public int getScrollStartHorizontal() {
		switch(fullScreenCaptionPosition) {
		case TopLeft: case Left: case BottomLeft:
			return SwingConstants.LEFT;
		case TopCenter: case Center: case BottomCenter:
			return SwingConstants.CENTER;
		case TopRight: case Right: case BottomRight:
			return SwingConstants.RIGHT;
		}
		return 0;
	}
	public int getScrollStartVertical() {
		switch(fullScreenCaptionPosition) {
		case TopLeft: case TopCenter: case TopRight:
			return SwingConstants.TOP;
		case Left:  case Center: case Right:
			return SwingConstants.CENTER;
		case BottomLeft: case BottomCenter: case BottomRight:
			return SwingConstants.BOTTOM;
		}
		return 0;
	}
	public int getFullScreenCaptionHorizontal() {
		switch(fullScreenCaptionPosition) {
		case TopLeft: case Left: case BottomLeft:
			return SwingConstants.LEFT;
		case TopCenter: case Center: case BottomCenter:
			return SwingConstants.CENTER;
		case TopRight: case Right: case BottomRight:
			return SwingConstants.RIGHT;
		}
		return 0;
	}
	public int getFullScreenCaptionVertical() {
		switch(fullScreenCaptionPosition) {
		case TopLeft: case TopCenter: case TopRight:
			return SwingConstants.TOP;
		case Left:  case Center: case Right:
			return SwingConstants.CENTER;
		case BottomLeft: case BottomCenter: case BottomRight:
			return SwingConstants.BOTTOM;
		}
		return 0;
	}
	public int getImageAnchorPosition() {
		switch(fullScreenCaptionPosition) {
		case TopLeft:
			return GridBagConstraints.NORTHWEST;
		case TopCenter:
			return GridBagConstraints.NORTH;
		case TopRight:
			return GridBagConstraints.NORTHEAST;
		case Left:
			return GridBagConstraints.WEST;
		case Center:
			return GridBagConstraints.CENTER;
		case Right:
			return GridBagConstraints.EAST;
		case BottomLeft:
			return GridBagConstraints.SOUTHWEST;
		case BottomCenter:
			return GridBagConstraints.SOUTH;
		case BottomRight:
			return GridBagConstraints.SOUTHEAST;
		}
		return 0;
	}
	
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
						f.set(this, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final ResampleFilter[] filterTable = new ResampleFilter[] {
		ResampleFilters.getBellFilter(),
		ResampleFilters.getBiCubicFilter(),
		ResampleFilters.getBoxFilter(),
		ResampleFilters.getBSplineFilter(),
		ResampleFilters.getHermiteFilter(),
		ResampleFilters.getLanczos3Filter(),
		ResampleFilters.getMitchellFilter(),
		ResampleFilters.getTriangleFilter()
	};
	
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
		if(obj.getClass().isEnum()) {
			//hrmm, something like this I think
			for(Object tObj : obj.getClass().getEnumConstants())
				if(tObj.toString().equals(read))
					return tObj;
		}
		if(obj instanceof Color) {
			return Color.decode("#"+read);
		}
		if(obj instanceof Integer) {
			return Integer.parseInt(read);
		}
		if(obj instanceof ResampleFilter) {
			for(int i = 0; i < filterTable.length; ++i) {
				if(filterTable[i].getName().equals(read)) {
					return filterTable[i];
				}
			}
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
		if(obj instanceof ResampleFilter) {
			return ((ResampleFilter)obj).getName();
		}
		if(obj instanceof Color) {
			Color c = (Color)obj;
			return String.format("%02x%02x%02x", c.getRed(),c.getGreen(),c.getBlue());
		}
		return obj.toString();
	}
}
