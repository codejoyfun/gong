package com.kids.commonframe.base.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
	private static final String FILENAME_SIGN = "sign_ctcar.png";
	public static final String MONEY_VALUE = "money";
	public static final String MONEY_UNIT = "unit";


	public static boolean isAppAlive(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null || appProcesses.isEmpty()) {
			return false;
		}
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	private static Random random = new Random();
	/**
	 * 根据手机分辨率从dp转成px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static  int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 */
	public static  int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f)-15;
	}
	/**
	 * 判断是否有网络连接
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断当前网络是否是wifi网络  
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
	/**
	 * 拨打电话
	 *
	 * @param context
	 */
	public static void callNumber(Context context, String phoneNumber) {
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phoneNumber));
			context.startActivity(intent);
		} catch (Exception e) {
			ToastUtil.show(context, "您的设备不支持拨打电话");
		}
	}

	/**
	 * 判断当前网络是否是3G网络  
	 *
	 GPRS       2G(2.5) General Packet Radia Service 114kbps
	 EDGE       2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps

	 CDMA     2G 电信 Code Division Multiple Access 码分多址
	 1xRTT      2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
	 IDEN      2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）

	 EVDO_0   3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
	 EVDO_A  3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
	 UMTS      3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
	 HSDPA    3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
	 HSUPA    3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps

	 HSPA      3G (分HSDPA,HSUPA) High Speed Packet Access
	 EVDO_B 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
	 EHRPD  3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
	 HSPAP  3G HSPAP 比 HSDPA 快些

	 LTE        4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G

	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		TelephonyManager telephoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final int type = telephoneManager.getNetworkType();
		//联通3g
		if (type == TelephonyManager.NETWORK_TYPE_UMTS
				|| type == TelephonyManager.NETWORK_TYPE_HSDPA
				||type == TelephonyManager.NETWORK_TYPE_HSUPA) {
			return true;
		}
		//电信3g
		else if (type==TelephonyManager.NETWORK_TYPE_EVDO_0
				||type==TelephonyManager.NETWORK_TYPE_EVDO_A) {
			return true;
		}
		//其他3g网络
		else if ( type == TelephonyManager.NETWORK_TYPE_HSPA
				||type==TelephonyManager.NETWORK_TYPE_EVDO_B
				||type==TelephonyManager.NETWORK_TYPE_EHRPD
				||type==TelephonyManager.NETWORK_TYPE_HSPAP) {
			return true;
		}
		//4g网络
		else	if (type == TelephonyManager.NETWORK_TYPE_LTE) {
			return true;
		}
		return false;
	}
	/**
	 *获取IMEI
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context){
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId();
	}
	/**
	 * 获取手机IMSI
	 * @return
	 */
	public static final String createIMSI() {
		StringBuffer strbuf = new StringBuffer();
		for(int i = 0; i < 15; i++) strbuf.append(random.nextInt(10));
		return strbuf.toString();
	}
	/**
	 * 返回电话的IMEI:手机的唯一标识码
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String imei = "";
		TelephonyManager tm =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			imei = tm.getDeviceId();
			if(isEmpty(imei)) {
				imei = createIMSI();
			}
		}
		return imei;
	}

	/**
	 * 手机制造商
	 * @return
	 */
	public static String getProduct(){

		return android.os.Build.PRODUCT;
	}
	/**
	 * 手机系统版本
	 * @return
	 */
	public static String getOsVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
	/**
	 * 获取当前的版本号
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context){
		String version = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取应用名称
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName =
				(String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}
	/**
	 * 获取当前的版本号
	 * @return
	 * @throws Exception
	 */
	public static String getVersionCode(Context context){
		String version = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			version = String.valueOf(packInfo.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	/**
	 * 通过Wifi获取MAC ADDRESS作为DEVICE ID
	 * 缺点：如果Wifi关闭的时候，硬件设备可能无法返回MAC ADDRESS.。
	 * @return
	 */
	public static String getMacAddress() {
		String macSerial = null;
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}
	/**
	 * 判断是否为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return (s == null || "".equals(s) || "null".equals(s.toLowerCase()));
	}

	/**
	 * 得到手机屏幕的宽
	 * @param mActivity
	 * @return
	 */
	public static int getScreenWidth(Activity mActivity){
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	/**
	 * 得到手机屏幕的高
	 * @param mActivity
	 * @return
	 */
	public static int getScreenHeight(Activity mActivity){
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 取得文件大小
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSizes(File f) throws Exception{
		long s=0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s= fis.available();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}
	/**
	 *  递归  //取得文件夹大小
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileFolderSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++){
			if (flist[i].isDirectory()){
				size = size + getFileFolderSize(flist[i]);
			} else{
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 处理手机好加上星星
	 * @return
	 */
	public static String heandlerMobel(String source) {
		if(TextUtils.isEmpty(source)) {
			return "";
		}
		String start = source.substring(0,3);
		String end = source.substring(source.length()-4);
		return start+"****"+end;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			//递归删除目录中的子目录下
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}
	/**
	 * 转换文件大小
	 * @param fileS
	 * @return
	 */
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.##");
		String fileSizeString = "";
		if(fileS == 0 || fileS < 350) return "0M";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/** 根据key获取metadata中的值
	 * @param context
	 * @param channelKey
	 * @return
	 */
	public static int getMetaDataIntValue(Context context , String channelKey) {
		int msg = 0;
		try {
			ApplicationInfo appInfo = context.getPackageManager() .getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			msg=appInfo.metaData.getInt(channelKey);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * 根据key获取metadata中的值
	 * @param context
	 * @param channelKey
	 * @return
	 */
	public static String getMetaDataStringValue(Context context , String channelKey) {
		String msg = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager() .getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			msg=appInfo.metaData.getString(channelKey);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * 判断字符串是否为纯数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}

	/**
	 * is has sd
	 * @return
	 */
	public static boolean isSDCard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 600);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		return intent;
	}
	/**
	 * 返回保存图片名字
	 * @return
	 */
	public static String getPicName() {
		SimpleDateFormat fat = new SimpleDateFormat("yyyyMMdd_hhmmss");
		return fat.format(new Date())+".jpg";
	}


	public static Bitmap convertViewToBitmap(View view){
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	public static long getTimelong(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat(str);
		Calendar mCalendar = formatter.getCalendar();
		return mCalendar.getTimeInMillis();
	}
	/**
	 * 判断字符串是否超过 指定的长度
	 * @param name
	 * @param length
	 * @return
	 */
	public static boolean isTooLong(String name,int length){
		String gbkStr = null;
		if(name.contains(" ")) name = name.replace(" ", "");
		if(name.contains("."))  name = name.replace(".", "");
		if(name.contains("·"))  name = name.replace("·", "");
		if(name.contains("。"))  name = name.replace("。", "");
		byte[] b = name.getBytes();
		try {
			//转为GBK编码
			gbkStr = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(!isEmpty(gbkStr)){
			b = gbkStr.getBytes();
		}

		if(b.length <= length){
			return true;
		}
		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 *
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
	/**
	 * 转bitmap -> drawable
	 */
	public static Drawable convertBitmap2Drawable(Context context,Bitmap bitmap) {
		BitmapDrawable td = new BitmapDrawable(context.getResources(),bitmap);
		return td;
	}
	//获取渠道
	public static String getChannel(Context context , String channelKey) {
		String msg = null;
		try {
			ApplicationInfo appInfo = context.getPackageManager() .getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			msg=appInfo.metaData.getString(channelKey);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * 获取缓存目录
	 * @param context
	 * @return
	 */
	public static String getCachePath(Context context) {
		File cacheDir = StorageUtils.getCacheDirectory(context);
		//设置视频的缓冲目录
		cacheDir = new File(cacheDir , "tempCache");
		if(!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir.getAbsolutePath();
	}

	public static String getImagePath (Context context) {
		File imagePath = generateImagePath(context);
		if(!imagePath.exists()) {
			imagePath.mkdirs();
		}
		return imagePath.getAbsolutePath();
	}

	private static File generateImagePath(Context context) {
		return new File(getStorageDir(context), "/image/");
	}

	private static File storageDir = null;
	private static File getStorageDir(Context context) {
		if(storageDir == null) {
			File fileCache = StorageUtils.getCacheDirectory(context);
			if(fileCache.exists() && fileCache.canWrite()) {
				return fileCache;
			}
			storageDir = context.getFilesDir();
		}
		return storageDir;
	}

	static String getString(Context context, int resId){
		return context.getResources().getString(resId);
	}
	/**
	 * 去掉参数中的< >括号
	 * */
	public static String handlerParams(String params) {
		if ( !TextUtils.isEmpty(params) ) {
			params = params.replaceAll("<", "*").replaceAll(">", "*");
		}
		return params;
	}


	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * @param context
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Context context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	/**
	 * 用来判断服务是否运行.
	 * @param mContext
	 * @param className 判断的服务名字（例如：net.loonggg.testbackstage.TestService）
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext,String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(Integer.MAX_VALUE);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	/* 安装apk */
	public static void installApk(Context context, String filePath) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + filePath),"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/* 卸载apk */
	public static void uninstallApk(Context context, String packageName) {
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}
	/* 得到APK版本信息   */
	public static String getApkVersionCode(Context context, String filePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			return String.valueOf(info.versionCode);       //得到版本信息
		}
		return null;
	}
	public static long getStorageSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long totalSize = totalBlocks * blockSize;

		return totalSize;
	}

	public static long getStorageAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long availSize = availableBlocks * blockSize;

		return availSize;
	}

	public static boolean isFileExist(String path) {
		File file = new File(path);
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}
	/**文件重命名
	 * @param path 文件目录
	 * @param oldname  原来的文件名
	 * @param newname 新文件名
	 */
	public static void renameFile(String path,String oldname,String newname) {
		if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			if (!oldfile.exists()) {
				return;//重命名文件不存在
			}
			if (newfile.exists()){
				//若在该目录下已经有一个文件和新文件名相同，则删除已经存在的
				newfile.delete();
			}
			oldfile.renameTo(newfile);
		}
	}

	/**
	 * 设置状态栏颜色
	 * @param activity
	 * @param colorResId
	 */
	public static void setWindowStatusBarColor(Activity activity, int colorResId) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = activity.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(activity.getResources().getColor(colorResId));

				//底部导航栏
				//window.setNavigationBarColor(activity.getResources().getColor(colorResId));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String generateImgFileName() {
		return UUID.randomUUID().toString()+".png";
	}

	public static Bitmap drawTextToBitmap(Context mContext,  int resourceId,  String mText) {
		try {
			Resources resources = mContext.getResources();
			float scale = resources.getDisplayMetrics().density;
			Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

			android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
			// set default bitmap config if none
			if(bitmapConfig == null) {
				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			}
			// resource bitmaps are imutable,
			// so we need to convert it to mutable one
			bitmap = bitmap.copy(bitmapConfig, true);

			Canvas canvas = new Canvas(bitmap);
			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paint.setColor(Color.WHITE);
			// text size in pixels
			paint.setTextSize((int) (12 * scale));
			// text shadow
			paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width())/6;
			int y = (bitmap.getHeight() - bounds.height())/3;

			canvas.drawText(mText, x * scale, y * scale, paint);

			return bitmap;
		} catch (Exception e) {
			// TODO: handle exception



			return null;
		}

	}
	//filename可以为空，则给一个默认
	public static String saveSignBitmapToFile(Context context,Bitmap bitmap,String fileName) throws FileNotFoundException {
		if (TextUtils.isEmpty(fileName)) {
			fileName = FILENAME_SIGN;
		}
		File signPath = new File(CommonUtils.getCachePath(context),fileName);
		FileOutputStream fos = new FileOutputStream(signPath.getPath());
//		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] b = baos.toByteArray();
			if (b != null) {
				fos.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return signPath.getAbsolutePath();
	}

	//如果不传，直接默认名称
	public static Bitmap getSignBitmap(Context context,String fileName){
		if (fileName == null) {
			fileName = FILENAME_SIGN;
		}
		FileInputStream fis = null;
		try {
			fis = context.openFileInput(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return BitmapFactory.decodeStream(fis);
	}
	/**
	 * 使用java正则表达式去掉多余的.与0
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s){
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 对于钱数，根据位数，返回以万或元为单位的字符串
 	 * @param money，比如19800.12
	 * @return unit="万"，money="1.9"
     */
	public static HashMap<String,String> formatMoney(String money){
		HashMap returnMap = new HashMap();
		if (TextUtils.isEmpty(money)){
			returnMap.put(MONEY_UNIT,"元");
			returnMap.put(MONEY_VALUE,"0");
			return returnMap;
		}
		StringBuffer formatSb = new StringBuffer();
		DecimalFormat df = new DecimalFormat("#.00");
		float iMoney = Float.valueOf(money).floatValue();
		if (iMoney >= 10000){
			formatSb.append(subZeroAndDot(df.format(iMoney / 10000.0)));
//			formatSb.append("万");
			returnMap.put(MONEY_UNIT,"万");
		}else {
			formatSb.append(subZeroAndDot(df.format(iMoney)));
//			formatSb.append("元");
			returnMap.put(MONEY_UNIT,"元");
		}
		returnMap.put(MONEY_VALUE,formatSb.toString());
		return returnMap;

	}
	/**
	 * 得到资源文件中图片的Uri
	 * @param context 上下文对象
	 * @param id 资源id
	 * @return Uri
	 */
	public static Uri getUriFromDrawableRes(Context context, int id) {
		Resources resources = context.getResources();
		String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ resources.getResourcePackageName(id) + "/"
				+ resources.getResourceTypeName(id) + "/"
				+ resources.getResourceEntryName(id);
		return Uri.parse(path);
	}
}
