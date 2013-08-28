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
