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
