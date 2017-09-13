package com.kids.commonframe.config;

/**
 * 项目地址 3.0+
 */
public class Constant {
   //正式环境地址
//   public final static String RELEASE_URL = "http://develop.runwise.cn";
   public final static String RELEASE_URL = "http://test.runwise.cn";
   //测试环境地址
   public final static String TEST_URL = "http://114.215.40.244:8083/";
   //七牛Url
   public final static String QINIU_URL = "http://ofwp5weyr.bkt.clouddn.com/";
   //用于请求的URL
   public static String BASE_URL = RELEASE_URL;

   //相册相关
   public static final String ALBUM_PICTAKES = "CLASSCIRCLE_PICTAKES";
   public static final String ALBUM_PICTAKE = "CLASSCIRCLE_PICTAKE";
   public static final int PIC_MAX_COUNT = 9;// 最多上传张数

   public static final int  STYLE_CAMERA = 0;
   public static final int  STYLE_TIMELINE = 1;

   public final static int TYPE_DATA = 1;
   public final static int TYPE_SECTION = 2;
   public final static int EDITTYPE_TEXT = 4;

   public final static int PIC_SELECT_TYPE_ALBUM = 1;
   public final static int PIC_SELECT_TYPE_CAMERA = 2;

}
