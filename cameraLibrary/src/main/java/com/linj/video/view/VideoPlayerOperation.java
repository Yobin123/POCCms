package com.linj.video.view;

import java.io.IOException;


public interface VideoPlayerOperation {

	boolean isPlaying();

	int getCurrentPosition();


	void seekPosition(int position);

	void stopPlay();

	void playVideo(String path) throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException;

	void pausedPlay();
	void resumePlay();

}
