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

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.image.BufferedImage;

public class WindowToolkit {
	public static Rectangle getCurrentScreenBounds(Window win) {
		return win.getGraphicsConfiguration().getDevice()
				.getDefaultConfiguration().getBounds();
	}

	public static Rectangle getMaximumWindowBounds(Window win) {
		Rectangle bound = getCurrentScreenBounds(win);
		Insets insets = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(
				win.getGraphicsConfiguration());
		bound.x += insets.left;
		bound.y += insets.top;
		bound.width -= insets.right + insets.left;
		bound.height -= insets.top + insets.bottom;
		return bound;
	}
	
	public static Insets getContentInsets(Window win, Component com) {
		Point winP = win.getLocationOnScreen();
		Point comP = com.getLocationOnScreen();
		return new Insets(
				comP.y - winP.y,
				comP.x - winP.x,
				winP.y + win.getHeight() - (comP.y + com.getHeight()),
				winP.x + win.getWidth() - (comP.x + com.getWidth()));
	}
	
	public static BufferedImage createCompatibleImage(int width, int height) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gs.getDefaultConfiguration();
		return gc.createCompatibleImage(width, height, Transparency.OPAQUE);
	}
}
