package com.kids.commonframe.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils {
	//---------------图片压缩与图片旋转处理
	public static final int SCALE_IMAGE_WIDTH = 740;
	public static final int SCALE_IMAGE_HEIGHT = 1260;

	/**
	 * 返回视频缩略图
	 * @param kind could be MINI_KIND or MICRO_KIND
	 */
	public static Bitmap getVideoThumbnail(String filePath, int width, int height, int kind)
	{
		Bitmap localBitmap = null;
		localBitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
		localBitmap = ThumbnailUtils.extractThumbnail(localBitmap, width, height, 2);
		return localBitmap;
	}

	/**
	 * 根据宽高路径返回图片
	 */
	public static Bitmap decodeScaleImage(String paramString, int width, int height)
	{
		BitmapFactory.Options localOptions = getBitmapOptions(paramString);
		int i = calculateInSampleSize(localOptions, width, height);
		localOptions.inSampleSize = i;
		localOptions.inJustDecodeBounds = false;
		Bitmap localBitmap1= null;
		try{
			localBitmap1 = BitmapFactory.decodeFile(paramString, localOptions);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		int j = readPictureDegree(paramString); //获取旋转角度
		Bitmap localBitmap2 = null;
		if ((localBitmap1 != null) && (j != 0))
		{
			localBitmap2 = rotaingImageView(j, localBitmap1);
			localBitmap1.recycle();
			localBitmap1 = null;
			return localBitmap2;
		}
		return localBitmap1;
	}

	public static Bitmap decodeScaleImage(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
	{
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
		int i = calculateInSampleSize(localOptions, paramInt2, paramInt3);
		localOptions.inSampleSize = i;
		localOptions.inJustDecodeBounds = false;
		Bitmap localBitmap = BitmapFactory.decodeResource(paramContext.getResources(), paramInt1, localOptions);
		return localBitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options paramOptions, int paramInt1, int paramInt2)
	{
		int i = paramOptions.outHeight;
		int j = paramOptions.outWidth;
		int k = 1;
		if ((i > paramInt2) || (j > paramInt1))
		{
			int m = Math.round(i / paramInt2);
			int n = Math.round(j / paramInt1);
			k = m > n ? m : n;
		}
		return k;
	}
	/**
	 * 指定宽高返回图片缩略图，返回图片路径
	 */
	public static String getThumbnailImage(String paramString, int paramInt)
	{
		Bitmap localBitmap = decodeScaleImage(paramString, paramInt, paramInt);
		try
		{
			File localFile = File.createTempFile("image", ".jpg");
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
			localFileOutputStream.close();
			return localFile.getAbsolutePath();
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		return paramString;
	}

	/**
	 * 拷贝文件到指点目录
	 * @param sourceFilePath 源文件
	 * @param descFilePath    目标文件
	 */
	public static void copyFile (File sourceFilePath, File descFilePath) {
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new FileInputStream(sourceFilePath);
			out = new BufferedOutputStream(new FileOutputStream(descFilePath));
			int len = 0 ;
			byte buff[] = new byte[1024];
			while (( len = in.read(buff)) >0) {
				out.write(buff, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 如果图片大于500kb 压缩图片，返回缩放路径
	 * */
	public static String getScaledImage(Context context, String imagePath, int width, int height){
		File localFile1 = new File(imagePath);
		if (!localFile1.exists())
			return imagePath;
		long l = localFile1.length();
		if (l <= 102400L*5)
		{
			return imagePath;
		}
		Bitmap localBitmap = decodeScaleImage(imagePath, width, height);
		try
		{
			File localFile2 = File.createTempFile("image", ".jpg", context.getFilesDir());
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
			localFileOutputStream.close();
			return localFile2.getAbsolutePath();
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		finally{
			if ( localBitmap != null && !localBitmap.isRecycled()) {
				localBitmap.recycle();
				localBitmap = null ;
			}
		}
		return imagePath;
	}
	/**
	 * 缩放图片并覆盖原图片路径
	 */
	public static File getScaledImage(Context paramContext, File paramString,int width,int height)
	{
		if (!paramString.exists())
			return paramString;
		long l = paramString.length();
		if (l <= 102400L*5)
		{
			return paramString;
		}
		Bitmap localBitmap = decodeScaleImage(paramString.getAbsolutePath(), width, height);
		try
		{
			FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 90, localFileOutputStream);
			localFileOutputStream.close();
			return paramString;
		}
		catch (Exception localException)
		{
			localException.printStackTrace();
		}
		finally{
			if ( localBitmap != null && !localBitmap.isRecycled()) {
				localBitmap.recycle();
				localBitmap = null ;
			}
		}
		return paramString;
	}

	/**
	 * 压缩图片 使用默认大小，返回缩放路径
	 */
	public static String getScaledImage(Context context,String imagePath) {
		return getScaledImage( context ,imagePath , SCALE_IMAGE_WIDTH,SCALE_IMAGE_HEIGHT);
	}
	/**
	 * 压缩图片，使用默认大小，覆盖原路径
	 */
	public static File getScaledImage(Context context,File filePath) {
		return getScaledImage( context ,filePath , SCALE_IMAGE_WIDTH,SCALE_IMAGE_HEIGHT);
	}

	public static String getScaledImage(Context paramContext, String paramString, int paramInt)
	{
		File localFile1 = new File(paramString);
		if (localFile1.exists())
		{
			long l = localFile1.length();
			if (l > 102400L*5)
			{
				Bitmap localBitmap = decodeScaleImage(paramString, SCALE_IMAGE_WIDTH, SCALE_IMAGE_HEIGHT);
				try
				{
					File localFile2 = new File(paramContext.getExternalCacheDir(), "eaemobTemp" + paramInt + ".jpg");
					FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
					localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, localFileOutputStream);
					localFileOutputStream.close();
					return localFile2.getAbsolutePath();
				}
				catch (Exception localException)
				{
					localException.printStackTrace();
				}
				finally{
					if ( localBitmap != null && !localBitmap.isRecycled()) {
						localBitmap.recycle();
						localBitmap = null ;
					}
				}
			}
		}
		return paramString;
	}



	public static int readPictureDegree(String paramString)
	{
		int i = 0;
		try
		{
			ExifInterface localExifInterface = new ExifInterface(paramString);
			int j = localExifInterface.getAttributeInt("Orientation", 1);
			switch (j)
			{
			case 6:
				i = 90;
				break;
			case 3:
				i = 180;
				break;
			case 8:
				i = 270;
			case 4:
			case 5:
			case 7:
			}
		}
		catch (IOException localIOException)
		{
			localIOException.printStackTrace();
		}
		return i;
	}

	public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap)
	{
		Matrix localMatrix = new Matrix();
		localMatrix.postRotate(paramInt);
		Bitmap localBitmap = null;
		try{
		   localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
		}
		catch (Exception e){
			e.printStackTrace();
			return paramBitmap;
		}
		if ( paramBitmap != null && !paramBitmap.isRecycled()) {
			paramBitmap.recycle();
		}
		return localBitmap;
	}

	public static BitmapFactory.Options getBitmapOptions(String paramString)
	{
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(paramString, localOptions);
		return localOptions;
	}

	/**
	 * 根据七牛API 处理图片，返回指定大小的图片URL
	 * @param sourceUrl
	 * @param width
	 * @param height
     * @return
     */
	public static String getScaleURL(String sourceUrl,String width,String height) {
		if ( TextUtils.isEmpty(sourceUrl) ) {
			return "";
		}
		Uri uri = Uri.parse(sourceUrl);
		if("file".equals(uri.getScheme())|| "content".equals(uri.getScheme())){
			return  sourceUrl;
		}
        return new StringBuffer(sourceUrl).append("?imageMogr2/thumbnail/" + width +"x" + height).toString();
	}
	/**
	 * 写文件
	 *
	 * @param inputStream
	 * @return
	 */
	public static File writeToFile(File file, InputStream inputStream) {
		File folder = new File(file.getParentFile().getPath());
		if (!folder.exists()) {
			folder.mkdir();
		}
		OutputStream outStream = null;
		try {
			file.createNewFile();
			outStream = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			int count;
			while ((count = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, count);
			}
			outStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
