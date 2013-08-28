package org.csdgn.maru.image.builder;

import java.io.IOException;

import org.csdgn.maru.image.ImageFrames;

public interface FrameBuilder extends ImageFrames {
	public void process() throws IOException;
}
