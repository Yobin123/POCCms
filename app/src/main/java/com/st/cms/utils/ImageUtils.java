package com.st.cms.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
	
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	// 第一：质量压缩方法：
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		Log.e("TAG", "压缩时图片大小：" + baos.toByteArray().length);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);

		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		System.out.println(w + "----" + h + "----------------------" + be);
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩

		// 注意：
		// 图片比例压缩时， 我看到一个算法，说比较快。。
		// be = (int) ((w / STANDARD_WIDTH + h/ STANDARD_HEIGHT) / 2);
		// 结论二：图片比例压缩倍数 就是 （宽度压缩倍数+高度压缩倍数）/2.
	}
	// 第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
	public static Bitmap getimage(String srcPath, int reqWidth, int reqHeight) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inPreferredConfig = Config.RGB_565;
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		if(reqWidth > 0 && reqHeight > 0)
		{
			ww = reqWidth;
			hh = reqHeight;
		}
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
			
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if(w == h)
		{
			be = (int)(ww > hh ? (newOpts.outWidth / ww) : (newOpts.outHeight / hh)); 
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		Log.e("TAG", ww + "---:" + hh + "+++" + w + "----" + h + "----------------------" + be);
		if(baos.toByteArray().length >= 64 * 1024)
		{
			Log.e("TAG", "一次压缩大小：" + baos.toByteArray().length);
			int multiple = (int) Math.ceil((float)baos.toByteArray().length / (64 * 1024));
			newOpts.inSampleSize = multiple;
			Log.e("TAG", "二次压缩大小为：" + multiple);
			bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, newOpts);
		}
		baos.reset();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		Log.e("TAG", "大小为：" + baos.toByteArray().length);
//		do
//		{
//			baos.reset();
//			be += 1;
//			newOpts.inSampleSize = be;
//			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//			Log.e("TAG", "大小为：" + baos.toByteArray().length);
//		}while(baos.toByteArray().length > 100 * 1024);
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		//compressimage方法其实是无效的，它适合用来进行质量压缩，然后进行网络传输，
		//然而通过bitmapfactory进行解码时会将文件读到内存中，压缩前多大，解码后还是多大，不会减少像素。
		return bitmap;
		// 注意：
		// 图片比例压缩时， 我看到一个算法，说比较快。。
		// be = (int) ((w / STANDARD_WIDTH + h/ STANDARD_HEIGHT) / 2);
		// 结论二：图片比例压缩倍数 就是 （宽度压缩倍数+高度压缩倍数）/2.
	}

	// 第三：图片按比例大小压缩方法（根据Bitmap图片压缩）：
	public static Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}



	/**
	 * 通过uri获取图片并进行压缩
	 *
	 * @param uri
	 */
	public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
		InputStream input = ac.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		int originalWidth = onlyBoundsOptions.outWidth;
		int originalHeight = onlyBoundsOptions.outHeight;
		if ((originalWidth == -1) || (originalHeight == -1))
			return null;
		//图片分辨率以480x800为标准
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (originalWidth / ww);
		} else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (originalHeight / hh);
		}
		if (be <= 0)
			be = 1;
		//比例压缩
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = be;//设置缩放比例
		bitmapOptions.inDither = true;//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		input = ac.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return compressImage(bitmap);//再进行质量压缩
	}

	//    Bitmap对象保存味图片文件
	public static void saveBitmapFile(Bitmap bitmap,String filePath){
		File file=new File(filePath);//将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Bitmap drawCircleView01(Bitmap bitmap) {
		//这里可能需要调整一下图片的大小来让你的图片能在圆里面充分显示
		bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
		//构建一个位图对象，画布绘制出来的图片将会绘制到此bitmap对象上
		Bitmap bm = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
		//构建一个画布,
		Canvas canvas = new Canvas(bm);
		//获得一个画笔对象，并设置为抗锯齿
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		//获得一种渲染方式对象
		//BitmapShader的作用是使用一张位图作为纹理来对某一区域进行填充。
		//可以想象成在一块区域内铺瓷砖，只是这里的瓷砖是一张张位图而已。
		Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		//设置画笔的渲染方式
		paint.setShader(shader);
		//通过画布的画圆方法将渲染后的图片绘制出来
		canvas.drawCircle(100, 100, 100, paint);
		//返回的就是一个圆形的bitmap对象
		return bm;
	}

	/**
	 * 一种挺有效的方法，规避BitmapFactory.decodeStream或者decodeFile函数，使用BitmapFactory.decodeFileDescriptor
	 * @param path
	 * @return
	 */
	public static  Bitmap readBitmapByPath(String path)   {
		BitmapFactory.Options bfOptions=new BitmapFactory.Options();
		bfOptions.inDither=false;
		bfOptions.inPurgeable=true;
		bfOptions.inInputShareable=true;
		bfOptions.inTempStorage=new byte[32 * 1024];

		File file=new File(path);
		FileInputStream fs=null;
		try {
			fs = new FileInputStream(file);
			if(fs!=null)
				return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// 第二：图片按比例大小压缩方法（根据路径获取图片并压缩）：
	public static void setCompressImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);

		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		try {
			FileInputStream fis = new FileInputStream(new File(srcPath));
			bitmap = BitmapFactory.decodeFileDescriptor(fis.getFD(),null,newOpts);
			newOpts.inJustDecodeBounds = false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	   ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.size()/ 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		Log.i("---->>", "----->>>" + baos.size());
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		byteArryToFile(srcPath, baos);
//		readBitmapByPath(srcPath);

//		return bitmap;
	}


	private static void byteArryToFile(String srcPath, ByteArrayOutputStream baos) {
		//将压缩好的字节流再次写入到文件
		byte[] bytes = null;
		BufferedOutputStream bos = null;
		try {
			//将压缩好的字节流再次写入到文件
			bytes= baos.toByteArray();
			 bos = new BufferedOutputStream(new FileOutputStream(srcPath));
			if(bytes!=null){
				bos.write(bytes);
				bos.flush();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bos!=null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
