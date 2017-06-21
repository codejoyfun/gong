package com.kids.commonframe.config;


import com.android.volley.VolleyLog;
import com.kids.commonframe.base.util.CommonUtils;
import com.lidroid.xutils.util.LogUtils;


public class GlobalConstant {
     //基本访问地址
	public static String BASE_URL = "http://114.215.40.244:8083/";     //apptest
	public static String QINIU_RUL = "http://kbtcimage.3ikids.com/";
	//屏幕宽高
	public static int screenH;
	public static int screenW;
	//log开关
	/**以后log输出以(LogUtils.*)方式输出**/
	public static boolean LOG_OPEN = true;
	/**缓存图片路径*/
	public static final String CACHE_PATH = "kbtc_image";
	public static final String CACHE_BIG_PATH = "kbtc_other_image";

    //客户端类型
	public static  String CLIENT_TYPE = "1";
	//App版本号
	public static  String APP_VERSION_CODE;
	public static  String APP_VERSION_NAME;
	//客户端系统版本号
	public static  String SYS_VERSION = CommonUtils.getOsVersion();
	//硬件型号
	public static  String HWARE_VERSION = CommonUtils.getProduct();
//-------------------------------一些变量---------------------------------
	 public static String PERSION_ID;
	 public static String CLASSSHOW_LOGO;
//	 public static String ENVIRONMENT;//环境
	 public static String IPPORT;//IP地址及端口号
//	 public static String NETWORKOPERATOR;//网络运营商
//	 public static String URL_GROWTHRECORD;//成长档案URL
//	 public static String URL_TEACHERRESOURCE;//教师资源URL
//	 public static String URL_CANDYHOUSERULE;//糖果屋规则URL
	 
	public static void logOpen () {
		LogUtils.allowD = true;
		LogUtils.allowE = true;
		LogUtils.allowI = true;
		LogUtils.allowV = true;
		LogUtils.allowW = true;
		LogUtils.allowWtf = true;
		VolleyLog.DEBUG = true;
	}
	public static void logClose () {
		LogUtils.allowD = false;
		LogUtils.allowE = false;
		LogUtils.allowI = false;
		LogUtils.allowV = false;
		LogUtils.allowW = false;
		LogUtils.allowWtf = false;
		VolleyLog.DEBUG = false;
	}

	//环信配置
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";

	//下载配置
	public static final String KEY_DOWNLOAD_ENTRY = "key_download_entry";
	public static final String KEY_DOWNLOAD_ACTION = "key_download_action";

	public static final int KEY_DOWNLOAD_ACTION_ADD = 0;
	public static final int KEY_DOWNLOAD_ACTION_PAUSE = 1;
	public static final int KEY_DOWNLOAD_ACTION_RESUME = 2;
	public static final int KEY_DOWNLOAD_ACTION_CANCEL = 3;
	public static final int KEY_DOWNLOAD_ACTION_PAUSE_ALL = 4;
	public static final int KEY_DOWNLOAD_ACTION_RECOVER_ALL = 5;

	public static final int CONNECT_TIME = 10 * 1000;
	public static final int READ_TIME = 10 * 1000;
}
