package com.linj.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.linj.imageloader.ImageSizeUtil.ImageSize;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;



public class ImageLoader {
	private static final String TAG = "ImageLoader";

	private static ImageLoader mInstance;

	private LinkedBlockingDeque<Runnable> mTaskQueue;


	private LruCache<String, Bitmap> mLruCache;

	private ExecutorService mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 1;

	private Type mType = Type.LIFO;


	private Thread mPoolThread;

	private Handler mUIHandler;


	private Semaphore mSemaphoreThreadPool;

	private Context mContext;

	public enum Type
	{
		FIFO, LIFO;
	}
	public static ImageLoader getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO,context);
				}
			}
		}
		return mInstance;
	}
	private ImageLoader(int threadCount, Type type,Context context)
	{
		init(threadCount, type,context);
	}
	public static ImageLoader getInstance(int threadCount, Type type,Context context)
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{

					mInstance = new ImageLoader(threadCount, type,context);
				}
			}
		}
		return mInstance;
	}


	private void init(int threadCount, Type type,Context context)
	{

		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mContext=context.getApplicationContext();
		mLruCache = new LruCache<String, Bitmap>(cacheMemory){
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				//				return value.getAllocationByteCount();
				return value.getRowBytes() * value.getHeight();
			}

		};


		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount,true);
		mTaskQueue = new LinkedBlockingDeque<Runnable>();	
		initBackThread();
	}

	private void initBackThread()
	{

		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				while(true){
					try {
						mSemaphoreThreadPool.acquire();
						Runnable runnable=getTask();
						mThreadPool.execute(runnable);	
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


			};
		};

		mPoolThread.start();
	}


	public void loadImage( String path,  ImageView imageView, DisplayImageOptions options) 
	{
		// 得到图片的旋转角度
		int degree = getBitmapDegree(path);
		// 根据旋转角度，生成旋转矩阵
		final Matrix matrix = new Matrix();
		matrix.postRotate(degree+90);

		options.displayer.display(options.imageResOnLoading, imageView);
		if (mUIHandler == null)
		{
			mUIHandler = new Handler(mContext.getMainLooper())
			{
				public void handleMessage(Message msg)
				{

					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					Bitmap returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
					ImageView view = holder.imageView;
					DisplayImageOptions options=holder.options;
					if(returnBm!=null){
						options.displayer.display(returnBm, view);
					}
					else {
						options.displayer.display(options.imageResOnFail, view);
					}
				};
			};
		}


		Bitmap bm = getBitmapFromLruCache(path);

		if (bm != null)
		{
			refreashBitmap(path, imageView, bm,options);

		} else{
			addTask(buildTask(path, imageView,options));
		}

	}

	/**
	 * ���ݴ���Ĳ������½�һ������
	 * 
	 * @param path
	 * @param imageView
	 * @return
	 */
	private Runnable buildTask(final String path, final ImageView imageView,
			final DisplayImageOptions options)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap bm = null;
				if (options.fromNet)
				{
					File file = getDiskCacheDir(imageView.getContext(),
							md5(path));
					if (file.exists()){
						bm = loadImageFromLocal(file.getAbsolutePath(),
								imageView);
					} else{
						if (options.cacheOnDisk){
							boolean downloadState = DownloadImgUtils
									.downloadImgByUrl(path, file);
							if (downloadState){
								bm = loadImageFromLocal(file.getAbsolutePath(),
										imageView);
							}
						} else{
							bm = DownloadImgUtils.downloadImgByUrl(path,
									imageView);
						}
					}
				} else{
					bm = loadImageFromLocal(path, imageView);
				}

				if (options.cacheInMemory) {
					addBitmapToLruCache(path, bm);
				}

				refreashBitmap(path, imageView, bm,options);
				//�ͷ��ź�
				mSemaphoreThreadPool.release();
			}


		};
	}

	private Bitmap loadImageFromLocal(final String path,
			final ImageView imageView)
	{
		Bitmap bm;

		ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);

		bm = decodeSampledBitmapFromPath(path, imageSize.width,
				imageSize.height);
		return bm;
	}


	/**
	 * ����ǩ�������࣬���ַ����ֽ�����
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str)
	{
		byte[] digest = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ʽ��
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes)
		{
			// ��ÿ���ֽ���0xFF���������㣬Ȼ��ת��Ϊ10���ƣ�Ȼ�������Integer��ת��Ϊ16����
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// ÿ���ֽ�8Ϊ��תΪ16���Ʊ�־��2��16����λ
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	private void refreashBitmap(String path, ImageView imageView,
			Bitmap bm,DisplayImageOptions options){
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		holder.options=options;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * ��ͼƬ����LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm)
	{
		if (getBitmapFromLruCache(path) == null){
			if (bm != null)
				mLruCache.put(path, bm);
		}
	}

	/**
	 * ����ͼƬ��Ҫ��ʾ�Ŀ�͸߶�ͼƬ����ѹ��
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,
			int height)
	{

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}



	/**
	 * ��û���ͼƬ�ĵ�ַ
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName)
	{
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
		{
			cachePath = context.getExternalCacheDir().getPath();
		} else
		{
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * ����path�ڻ����л�ȡbitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		return mLruCache.get(key);
	}


	/**
	 * ���������ȡ��һ��������������Ϊ��ʱ���������÷���
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	private Runnable getTask() throws InterruptedException
	{
		if (mType == Type.FIFO)
		{
			return mTaskQueue.takeFirst();
		} else 
		{
			return mTaskQueue.takeLast();
		}
	}
	/**
	 * ��������������
	 * @param runnable
	 * @throws InterruptedException
	 */
	private  void addTask(Runnable runnable)
	{
		try {
			mTaskQueue.put(runnable);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}

	}
	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
		DisplayImageOptions options;
	}

	/**
	 * 获取原始图片的角度（解决三星手机拍照后图片是横着的问题）
	 * @param path 图片的绝对路径
	 * @return 原始图片的角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return degree;
	}
}
