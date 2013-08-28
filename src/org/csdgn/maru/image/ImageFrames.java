package org.csdgn.maru.image;

import java.awt.image.BufferedImage;

public interface ImageFrames {
	public long[] getDurations();
	public BufferedImage[] getFrames();
	public int getFrameCount();
}
