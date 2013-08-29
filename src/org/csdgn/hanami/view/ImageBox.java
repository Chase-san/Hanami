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
package org.csdgn.hanami.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class ImageBox extends JComponent {
	private static final long serialVersionUID = -558558369159449435L;
	
	private BufferedImage image;
	
	public ImageBox() {
		setOpaque(false);
		setBackground(Color.WHITE);
	}

	public BufferedImage getImage() {
		return image;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	public void setImage(BufferedImage img) {
		image = img;

		Dimension size = new Dimension(img.getWidth(), img.getHeight());
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);

		revalidate();
		repaint();
	}
}
