package com.kids.commonframe.config;

/**
 * 项目地址 3.0+
 */
public interface Constant {
    //正式环境地址
//   public final static String RELEASE_URL = "http://develop.runwise.cn";
//   public final static String RELEASE_URL = "http://test.runwise.cn";
    boolean test = false;
    String RELEASE_URL = test?"http://test.runwise.cn":"http://develop.runwise.cn";
    //测试环境地址
    String TEST_URL = "http://114.215.40.244:8083/";
    //七牛Url
    String QINIU_URL = "http://ofwp5weyr.bkt.clouddn.com/";
    //用于请求的URL
    String BASE_URL = RELEASE_URL;

    //相册相关
    String ALBUM_PICTAKES = "CLASSCIRCLE_PICTAKES";
    String ALBUM_PICTAKE = "CLASSCIRCLE_PICTAKE";
    int PIC_MAX_COUNT = 9;// 最多上传张数

    int STYLE_CAMERA = 0;
    int STYLE_TIMELINE = 1;

    int TYPE_DATA = 1;
    int TYPE_SECTION = 2;
    int EDITTYPE_TEXT = 4;

    int PIC_SELECT_TYPE_ALBUM = 1;
    int PIC_SELECT_TYPE_CAMERA = 2;

    /**
     * 订单状态
     */
    String ORDER_STATE_DRAFT = "draft";
    String ORDER_STATE_SALE = "sale";
    String ORDER_STATE_PEISONG = "peisong";
    String ORDER_STATE_DONE = "done";
    String ORDER_STATE_RATED = "rated";

}
