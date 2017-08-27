package com.kids.commonframe.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.CustomProgressDialog;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.ViewUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 基类activity 每一个acitivty都要继承该类
 * @date 2013-12-12
 */
public abstract class BaseActivity extends FragmentActivity{
	//修改字体颜色
	protected final static int LEFTANDRIGHT = 0;
	protected final static int LEFTTEXT = 1;
	protected final static int RIGHTTEXT = 2;
	protected Context mContext;
	//通用对话框子类可以直接使用
	protected CustomDialog dialog;
	private CustomProgressDialog progressDialog;
	private List<BroadcastReceiver> receiverList;

	private boolean login,logout;
	private UserLoginEvent userLoginEvent;
	private boolean isResume;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( GlobalConstant.screenW == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			GlobalConstant.screenW = metrics.widthPixels;
			GlobalConstant.screenH = metrics.heightPixels;
		}
		BaseManager.getInstance().addActivity(this);
		mContext = this;
		dialog = new CustomDialog( mContext );
//				getWindow().setWindowAnimations(R.style.ActivityAnimation);
		EventBus.getDefault().register(this);
//		if (AndroidWorkaround.checkDeviceHasNavigationBar(this)){
//			AndroidWorkaround.assistActivity(findViewById(android.R.id.content));
//		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("screenW",GlobalConstant.screenW);
		outState.putInt("screenH",GlobalConstant.screenH);
		outState.putString("baseUrl", Constant.BASE_URL);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		GlobalConstant.screenW = savedInstanceState.getInt("screenW");
		GlobalConstant.screenH = savedInstanceState.getInt("screenH");
		Constant.BASE_URL = savedInstanceState.getString("baseUrl");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventUserlogin(UserLoginEvent userLoginEvent) {
			this.userLoginEvent = userLoginEvent;
			login = true;
			logout = false;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventUserlogout(UserLogoutEvent userLogoutEvent) {
			login = false;
			logout = true;
	}

	//用户登入，登出调用*该方法只有在界面可见时调用
	public void onUserLogin(UserLoginEvent userLoginEvent) {

	}
	public void onUserLoginout() {

	}
	/**
	 * 显示网络请求对话框
	 */
	public void showIProgressDialog () {
		if ( progressDialog == null ) {
			progressDialog =  new CustomProgressDialog( this);
		}
		if ( !progressDialog.isShowing() && !this.isFinishing()) {
			progressDialog.show();
		}
	}
	/**
	 * 隐藏网络请求对话框
	 */
	public void dismissIProgressDialog() {
		if ( progressDialog != null && !this.isFinishing()&&progressDialog.isShowing() ) {
			progressDialog.dismiss();
		}
	}
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ViewUtils.inject(this);
	}

	/**
	 * 设置为沉浸式标题栏状态
	 */
	public void setStatusBarEnabled() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			setTranslucentStatus(true);
		}
//		SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
//		systemBarTintManager.setStatusBarTintEnabled(true);
	}

	@TargetApi(19)
	protected  void setTranslucentStatus(boolean on){
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	@Override
	protected void onPause() {
		super.onPause();
		isResume = false;
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		isResume = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.e("login", "onResume----" +  BaseActivity.this.getClass().getSimpleName());
		//友盟数据统计
		MobclickAgent.onResume(this);
		//jpush推送统计
		//		JPushInterface.onResume(this);
		if (logout) {
			onUserLoginout();
			logout = false;
		}
		else if (login) {
			onUserLogin(userLoginEvent);
			login = false;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseManager.getInstance().removeActivity(this);
		if (receiverList != null ) {
			for ( BroadcastReceiver receiver : receiverList) {
				this.unregisterReceiver(receiver);
			}
		}
		EventBus.getDefault().unregister(this);
	}
	/**
	 * 添加一个广播接收器
	 * 可以快速注册接收器并不必担心注销问题
	 * @param action
	 * @param receiver
	 */
	public void addBroadReceiver(String action,BroadcastReceiver receiver){
		if ( receiverList == null ) {
			receiverList = new ArrayList<BroadcastReceiver>();
		}
		IntentFilter intentFilter = new IntentFilter(action);
		registerReceiver(receiver, intentFilter);
		receiverList.add(receiver);
	}
	//--------------------------通用方法设置标题栏－－－－－－

	protected void setTitleLeftIcon(boolean show, int resid) {
		ImageView view = (ImageView) findViewById(R.id.title_iv_left);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setImageResource(resid);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected void showBackBtn(){
		ImageView view = (ImageView) findViewById(R.id.title_iv_left);
			view.setVisibility(View.VISIBLE);
			view.setImageResource(R.drawable.back_btn);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void setTitleRigthIcon(boolean show, int resid) {
		ImageView view = (ImageView) findViewById(R.id.title_iv_rigth);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setImageResource(resid);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected  void setTitleRightIcon2(boolean show,int resid){
		ImageView view = (ImageView) findViewById(R.id.title_iv_rigth2);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setImageResource(resid);
		} else {
			view.setVisibility(View.GONE);
		}

	}
	protected void setTitleLeftText(boolean show, String text) {
		TextView view = (TextView) findViewById(R.id.title_tv_left);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected void setTitleRightText(boolean show, String text) {
		TextView view = (TextView) findViewById(R.id.title_tv_rigth);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected void setTitleTextColor(int isLeftOrRight,String color){
		TextView rightTV = (TextView) findViewById(R.id.title_tv_rigth);
		TextView leftTV = (TextView) findViewById(R.id.title_tv_left);
		switch (isLeftOrRight){
			case LEFTANDRIGHT:
				rightTV.setTextColor(Color.parseColor(color));
				leftTV.setTextColor(Color.parseColor(color));
				break;
			case LEFTTEXT:
				leftTV.setTextColor(Color.parseColor(color));
				break;
			case RIGHTTEXT:
				rightTV.setTextColor(Color.parseColor(color));
				break;
			default:
				break;
		}
	}
	protected void setTitleText(boolean show, String text) {
		TextView view = (TextView) findViewById(R.id.title_tv_title);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected void setTitleTitleBg(int color) {
		View view = findViewById(R.id.title_bg);
		view.setBackgroundColor(color);
	}

	protected static ViewGroup getRootView(Activity context)
	{
		return ((ViewGroup)context.findViewById(android.R.id.content));
	}
}
