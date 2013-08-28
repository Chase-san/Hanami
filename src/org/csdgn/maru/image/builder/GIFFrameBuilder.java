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
package org.csdgn.maru.image.builder;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

/**
 * Takes a given ImageInputStream and extracts the frames and durations from it.
 * @author Chase
 */
public class GIFFrameBuilder implements FrameBuilder {
	private ImageInputStream imgStream;
	private long[] durations;
	private BufferedImage[] frames;

	/**
	 * @param stream the ImageInputStream describing a possible Animated GIF.
	 * @throws IOException 
	 */
	public GIFFrameBuilder(InputStream stream) throws IOException {
		durations = null;
		frames = null;
		imgStream = ImageIO.createImageInputStream(stream);
	}
	
	/**
	 * @param stream the ImageInputStream describing a possible Animated GIF.
	 * @param process if true, it will process() the stream now.
	 * @throws IOException If the processing throws an exception.
	 */
	public GIFFrameBuilder(InputStream stream, boolean process) throws IOException {
		durations = null;
		frames = null;
		imgStream = ImageIO.createImageInputStream(stream);
		
		if(process) {
			process();
		}
	}
	
	/**
	 * How long the frame displays in milliseconds.
	 */
	public long[] getDurations() {
		return durations;
	}

	public BufferedImage[] getFrames() {
		return frames;
	}
	
	public int getFrameCount() {
		if(frames == null)
			return 0;
		return frames.length;
	}
	
	/**
	 * Call to process the ImageInputStream, if it has not already been processed.
	 */
	public void process() throws IOException {
		if(imgStream != null) {
			processImageStream(imgStream);
		}
		imgStream = null;
	}

	private void processImageStream(ImageInputStream stream) throws IOException {
		Iterator<ImageReader> it = ImageIO.getImageReaders(stream);
		ImageReader reader = null;
		while (it.hasNext()) {
			ImageReader r = it.next();
			String meta = r.getOriginatingProvider()
					.getNativeImageMetadataFormatName();
			if (!"javax_imageio_gif_image_1.0".equals(meta)) {
				continue;
			} else if ("gif".equalsIgnoreCase(r.getFormatName())) {
				reader = r;
			}
		}
		
		if (reader == null) {
			return;
		}

		ArrayList<BufferedImage> tImg = new ArrayList<BufferedImage>();
		ArrayList<int[]> tData = new ArrayList<int[]>();
		
		try {
			reader.setInput(stream);
	
			for (int i = 0;; ++i) try {
				IIOImage frame = reader.readAll(i, null);
				tImg.add((BufferedImage) frame.getRenderedImage());
				if(tData != null) {
					int[] data = processFrameMetadata(frame.getMetadata());
					if(data == null) {
						tData = null;
					} else {
						tData.add(data);
					}
				}
			} catch(IndexOutOfBoundsException ex) {
				break;
			}
		} finally {
			reader.dispose();
		}
		
		//Single Frame
		if(tImg.size() == 1 || tData == null) {
			frames = new BufferedImage[] { tImg.get(0) };
			durations = new long[] { 0 };
			return;
		}
		
		//Multiple Frames (sigh)
		//time,x,y,d
		
		//We couldn't do this till we knew how many frames there were.
		frames = new BufferedImage[tImg.size()];
		durations = new long[frames.length];
		int[] xO = new int[frames.length];
		int[] yO = new int[frames.length];
		int[] d = new int[frames.length];
		
		//Image Size
		int w = 0;
		int h = 0;
		for(int i=0; i < frames.length; ++i) {
			w = Math.max(w, tImg.get(i).getWidth() + (xO[i]=tData.get(i)[1]));
			h = Math.max(h, tImg.get(i).getHeight() + (yO[i]=tData.get(i)[2]));
			durations[i] = tData.get(i)[0] * 10L;
			d[i] = tData.get(i)[3];
		}
		tData.clear();
		tData = null;
		
		//Create Frames
		for(int i=0;i<frames.length;++i) {
			BufferedImage frame = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g = frame.createGraphics();
			
			//prepare the frame...
			if(i > 0) {
				if(d[i-1] == 1) {
					//do not dispose
					g.drawImage(frames[i-1],0,0,null);
				} else if(d[i-1] == 3) {
					//restore to previous
					for(int pIndex = i - 2; pIndex >= 0; --pIndex) {
						if(d[pIndex] <= 1) {
							g.drawImage(frames[pIndex], 0, 0, null);
							break;
						}
					}
				}
			}
			
			// draw the frame
			g.drawImage(tImg.get(i), xO[i], yO[i], null);
			g.dispose();
			
			frames[i] = frame;
		}
		
		xO = yO = d = null;
	}

	private int[] processFrameMetadata(IIOMetadata meta) {
		try {
			IIOMetadataNode imgRootNode = (IIOMetadataNode) meta
					.getAsTree("javax_imageio_gif_image_1.0");

			IIOMetadataNode gce = (IIOMetadataNode) imgRootNode
					.getElementsByTagName("GraphicControlExtension").item(0);
			int delay = Integer.parseInt(gce.getAttribute("delayTime"));
			int disposal = 0;
			switch (gce.getAttribute("disposalMethod")) {
			case "doNotDispose":
				disposal = 1;
				break;
			case "restoreToBackgroundColor":
				disposal = 2;
				break;
			case "restoreToPrevious":
				disposal = 3;
			}
			IIOMetadataNode id = (IIOMetadataNode) imgRootNode
					.getElementsByTagName("ImageDescriptor").item(0);
			int xOffset = Integer
					.parseInt(id.getAttribute("imageLeftPosition"));
			int yOffset = Integer.parseInt(id.getAttribute("imageTopPosition"));
			return new int[] { delay, xOffset, yOffset, disposal };
		} catch (IllegalArgumentException e) {
			//We have an unknown format, meaning we can't do anything with it.
			return null;
		}
	}
}
