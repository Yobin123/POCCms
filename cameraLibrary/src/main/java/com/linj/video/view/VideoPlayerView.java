package com.linj.video.view;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


public class VideoPlayerView extends SurfaceView implements VideoPlayerOperation {
	private final static String TAG="VideoSurfaceView";
	private MediaPlayer mMediaPlayer;
	public VideoPlayerView(Context context){
		super(context);
		init();
	}
	public VideoPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	private void init() {
		mMediaPlayer=new MediaPlayer();

		getHolder().addCallback(callback);
	}

	protected void setPalyerListener(PlayerListener listener){
		mMediaPlayer.setOnCompletionListener(listener);
		mMediaPlayer.setOnSeekCompleteListener(listener);
		mMediaPlayer.setOnPreparedListener(listener);
	}

	@Override
	public boolean isPlaying(){
		return mMediaPlayer.isPlaying();
	}


	@Override
	public int getCurrentPosition(){
		if(isPlaying())
			return mMediaPlayer.getCurrentPosition();
		return 0;
	}



	@Override
	public void pausedPlay() {
		mMediaPlayer.pause();
	}
	@Override
	public void resumePlay() {
		// TODO Auto-generated method stub
		mMediaPlayer.start();
	}


	@Override
	public void seekPosition(int position){
		if(isPlaying())
			mMediaPlayer.pause();

		if(position<0||position>mMediaPlayer.getDuration()){
			mMediaPlayer.stop();
			return;
		}

		mMediaPlayer.seekTo(position);
	}


	@Override
	public void stopPlay() {
		mMediaPlayer.stop();
		mMediaPlayer.reset();
	}

	private SurfaceHolder.Callback callback=new SurfaceHolder.Callback() {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mMediaPlayer.setDisplay(getHolder());  		
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(mMediaPlayer.isPlaying())
				mMediaPlayer.stop();
			mMediaPlayer.reset();
		}
	};
	@Override
	public void playVideo(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{

		if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
		}
		mMediaPlayer.reset();
		mMediaPlayer.setDataSource(path);
		mMediaPlayer.prepare();
	}


	public interface PlayerListener extends  OnCompletionListener,
	OnSeekCompleteListener,OnPreparedListener{

	}
}
