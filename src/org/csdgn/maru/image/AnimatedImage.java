package org.csdgn.maru.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.csdgn.maru.image.builder.FrameBuilder;

public class AnimatedImage implements ImageFrames {
	private BufferedImage[] frames;
	private long[] durations;
	
	public AnimatedImage(BufferedImage image) {
		frames = new BufferedImage[1];
		frames[0] = image;
		durations = new long[1];
		durations[0] = 0;
	}
	
	public AnimatedImage(FrameBuilder builder) {
		if(builder.getFrameCount() == 0) try {
			builder.process();
		} catch(IOException e) {
			e.printStackTrace();
		}
		frames = builder.getFrames();
		durations = builder.getDurations();
	}
	
	public AnimatedImage(BufferedImage[] frames, long[] durations) {
		this.frames = frames;
		this.durations = durations;
	}
	
	public int getWidth() {
		return frames[0].getWidth();
	}
	
	public int getHeight() {
		return frames[0].getHeight();
	}

	@Override
	public long[] getDurations() {
		return durations;
	}

	@Override
	public BufferedImage[] getFrames() {
		return frames;
	}

	@Override
	public int getFrameCount() {
		return frames.length;
	}
}
