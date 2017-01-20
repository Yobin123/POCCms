package com.linj.album.view;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.linj.FileOperateUtil;
import com.linj.album.view.MatrixImageView.OnMovingListener;
import com.linj.album.view.MatrixImageView.OnSingleTapListener;
import com.linj.cameralibrary.R;
import com.linj.imageloader.DisplayImageOptions;
import com.linj.imageloader.ImageLoader;
import com.linj.imageloader.displayer.MatrixBitmapDisplayer;

import java.util.List;



public class AlbumViewPager extends ViewPager implements OnMovingListener {
	public final static String TAG="AlbumViewPager";

	private ImageLoader mImageLoader;

	private DisplayImageOptions mOptions;	


	private boolean mChildIsBeingDragged=false;

	private OnSingleTapListener onSingleTapListener;
	

	private OnPlayVideoListener onPlayVideoListener;
	public AlbumViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mImageLoader= ImageLoader.getInstance(context);

		DisplayImageOptions.Builder builder= new DisplayImageOptions.Builder();
		builder =builder
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.displayer(new MatrixBitmapDisplayer());
		mOptions=builder.build();
	}


	


	public String deleteCurrentPath(){
		return ((ViewPagerAdapter)getAdapter()).deleteCurrentItem(getCurrentItem());

	}
	public String detectCurrentPath(){
		return ((ViewPagerAdapter)getAdapter()).detectCurrentItem(getCurrentItem());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(mChildIsBeingDragged)
			return false;
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void startDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=true;
	}


	@Override
	public void stopDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=false;
	}

	public void setOnSingleTapListener(OnSingleTapListener onSingleTapListener) {
		this.onSingleTapListener = onSingleTapListener;
	}
	public void setOnPlayVideoListener(OnPlayVideoListener onPlayVideoListener) {
		this.onPlayVideoListener = onPlayVideoListener;
	}
	public interface OnPlayVideoListener{
		void onPlay(String path);
	}

	public class ViewPagerAdapter extends PagerAdapter {
		private List<String> paths;
		public ViewPagerAdapter(List<String> paths){
			this.paths=paths;
		}

		@Override
		public int getCount() {
			return paths.size();
		}

		@Override
		public Object instantiateItem(ViewGroup viewGroup, int position) {

			View imageLayout = inflate(getContext(),R.layout.item_album_pager, null);
			viewGroup.addView(imageLayout);
			assert imageLayout != null;
			MatrixImageView imageView = (MatrixImageView) imageLayout.findViewById(R.id.image);
			imageView.setOnMovingListener(AlbumViewPager.this);
			imageView.setOnSingleTapListener(onSingleTapListener);
			String path=paths.get(position);
			//final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			
			ImageButton videoIcon=(ImageButton)imageLayout.findViewById(R.id.videoicon);
			if(path.contains("video")){
				videoIcon.setVisibility(View.VISIBLE);
			}else {			
				videoIcon.setVisibility(View.GONE);
			}

//			videoIcon.setOnClickListener(playVideoListener);
			videoIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String path=v.getTag().toString();
					path=path.replace(getContext().getResources().getString(R.string.Thumbnail),
							getContext().getResources().getString(R.string.Video));
					path=path.replace(".jpg", ".3gp");
					Log.i(TAG, "--->>"+path.toString());
					if(onPlayVideoListener!=null)
						onPlayVideoListener.onPlay(path);
					else {
						Toast.makeText(getContext(), "listener is null", Toast.LENGTH_SHORT).show();
//					throw new RuntimeException("onPlayVideoListener is null");
					}
				}
			});

			videoIcon.setTag(path);
			imageLayout.setTag(path);

			mImageLoader.loadImage(path, imageView, mOptions);
			return imageLayout;
		}

		OnClickListener playVideoListener=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String path=v.getTag().toString();
				path=path.replace(getContext().getResources().getString(R.string.Thumbnail),
						getContext().getResources().getString(R.string.Video));
				path=path.replace(".jpg", ".3gp");
				Log.i(TAG, "--->>"+path.toString());
				if(onPlayVideoListener!=null)
					onPlayVideoListener.onPlay(path);
				else {
					Toast.makeText(getContext(), "listener is null", Toast.LENGTH_SHORT).show();
//					throw new RuntimeException("onPlayVideoListener is null");
				}
			}
		};

		@Override
		public int getItemPosition(Object object) {

			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int arg1, Object object) {
			((ViewPager) container).removeView((View) object);  
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;			
		}


		public String deleteCurrentItem(int position) {
			String path=paths.get(position);
			if(path!=null) {
				FileOperateUtil.deleteSourceFile(path, getContext());
				paths.remove(path);
				notifyDataSetChanged();
				if(paths.size()>0)
					return (getCurrentItem()+1)+"/"+paths.size();
				else {
					return "0/0";
				}
			}
			return null;
		}
		public String detectCurrentItem(int position){
			String path = paths.get(position);
			return path;
		}
	}



}
