package com.runwise.supply.tools;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View.OnClickListener;

import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

public class ShareUtil {
	  public static final String HTTP_SCHEME = "http";
	  public static final String HTTPS_SCHEME = "https";

     //测试代码
	public static void ShareQQ(Context context) {
//		ShareParams sp = new ShareParams();
//		sp.title = "测试分享的标题";
//		sp.titleUrl = "http://sharesdk.cn"; // 标题的超链接
//		sp.text = "测试分享的文本";
//		sp.imageUrl = "http://www.someserver.com/测试图片网络地址.jpg";
//		sp.site = "发布分享的网站名称";
//		sp.siteUrl = "发布分享网站的地址";
		
//		Platform qzone = ShareSDK.getPlatform(context, SinaWeibo.NAME);
//		Platform qzone = ShareSDK.getPlatform(context, QZone.NAME);
//		Platform qzone = ShareSDK.getPlatform(context, Wechat.NAME);
//		Platform qzone = ShareSDK.getPlatform(context, WechatMoments.NAME);
//		Platform qzone = ShareSDK.getPlatform(context, QQ.NAME);
		
//		qzone.setPlatformActionListener(new PlatformActionListener() {
//			@Override
//			public void onError(Platform arg0, int arg1, Throwable arg2) {
//				// TODO Auto-generated method stub
//				Log.e("qq", "onError：" + arg2.getMsg());
//
//			}
//			@Override
//			public void onComplete(Platform arg0, int arg1,
//					HashMap<String, Object> arg2) {
//				// TODO Auto-generated method stub
//				Log.e("qq", "onComplete");
//
//			}
//			@Override
//			public void onCancel(Platform arg0, int arg1) {
//				// TODO Auto-generated method stub
//				Log.e("qq", "onCancel");
//			}
//		}); // 设置分享事件回调
		// 执行图文分享
//		qzone.share(sp);
	}

	public static void showShare(Context context, String title,String text, String imageUrl,String linkUrl) {
		showShareView(context,null,title,text,imageUrl,linkUrl,null,null,false);
	}
	public static void showShare(Activity context, String title,String text, String imageUrl,String linkUrl,boolean onlyWechat) {
		showShareView(context,null,title,text,imageUrl,linkUrl,null,null,onlyWechat);
	}
	public static void showShare(Context context, String title,String text, String imageUrl,String linkUrl,String localPath) {
		showShareView(context,null,title,text,imageUrl,linkUrl,localPath,null,false);
	}
	public static void showShare(Context context, String title,String text, String imageUrl,String linkUrl,OnClickListener onClickListener) {
		showShareView(context,null,title,text,imageUrl,linkUrl,null,onClickListener,false);
	}
	public static void showShareView(Context context,String platform, String title,String text, String imageUrl,String linkUrl,String localPath,OnClickListener onClickListener,boolean onlyWechat) {
		final OnekeyShare oks = new OnekeyShare();
//		Log.e("info","platform:"+platform+";title:"+title+";imageUrl:"+imageUrl+";linkUrl:"+linkUrl+";localPath:"+localPath);
//		oks.addHiddenPlatform(Wechat.NAME);
//		oks.addHiddenPlatform(QQ.NAME);
//		oks.setNotification(R.drawable.ic_launcher, "来这游");
//		oks.setAddress("12345678901");
		if(onlyWechat){
			oks.addHiddenPlatform(QQ.NAME);
			oks.addHiddenPlatform(SinaWeibo.NAME);
		}
		oks.setTitle(title);
		if(isNetWorkUrl(linkUrl)){
			oks.setTitleUrl(linkUrl);
			oks.setText(text/*+linkUrl*/);
		}
		else {
			oks.setText(text);
		}
		oks.setImageUrl(imageUrl);
		if (!TextUtils.isEmpty(localPath)) {
		    oks.setImagePath(localPath);
		}
		oks.setUrl(linkUrl);
//		oks.setComment("");
//		oks.setSite("中国联通互动宝宝");
//		oks.setSiteUrl("http://unicom.3ikids.com");
//		oks.setVenueName("ShareSDK");
//		oks.setVenueDescription("This is a beautiful place!");
//		oks.setLatitude(23.056081f);
//		oks.setLongitude(113.385708f);
		if (!TextUtils.isEmpty(platform)) {
			oks.setPlatform(platform);
		}
		oks.setDialogMode();
		oks.setSilent(false);
//		if (onClickListener != null) {
//			Bitmap enableLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_download);
////			Bitmap disableLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_download);
//			String label = context.getResources().getString(R.string.down_load_img);
//			oks.setCustomerLogo(enableLogo, enableLogo, label, onClickListener);
//		}
		oks.show(context);
	}

	public static boolean isNetWorkUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		return url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME);
	}

}
