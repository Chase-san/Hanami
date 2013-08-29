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
