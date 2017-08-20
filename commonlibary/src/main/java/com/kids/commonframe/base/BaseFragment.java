package com.kids.commonframe.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.LogUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.ViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 每一个fragment都继承该类
 *
 */
public abstract class BaseFragment extends Fragment {
	protected Activity mContext;
	protected CustomDialog dialog;
	protected View mainView;

	private boolean login,logout;
	private UserLoginEvent userLoginEvent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		if( GlobalConstant.screenH == 0 ) {
			DisplayMetrics metrics = new DisplayMetrics();
			mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			GlobalConstant.screenW = metrics.widthPixels;
			GlobalConstant.screenH = metrics.heightPixels;
		}
		dialog = new CustomDialog( mContext );
		LayoutInflater inflater = LayoutInflater.from( mContext );
		int layoutId = createViewByLayoutId();
		if ( layoutId == 0 ) {
			throw new IllegalArgumentException("你必须再createViewByLayoutId方法中返回主视图");
		}
		mainView = inflater.inflate(layoutId, null);
		ViewUtils.inject(this , mainView);
		EventBus.getDefault().register(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventUserlogin(UserLoginEvent userLoginEvent) {
			this.userLoginEvent = userLoginEvent;
			login = true;
			logout = false;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventUserlogout(UserLogoutEvent userLogoutEvent) {
			logout = true;
			login = false;
	}

	//用户登入，登出调用*该方法只有在界面可见时调用
	public void onUserLogin(UserLoginEvent userLoginEvent) {

	}
	public void onUserLoginout() {

	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.e("login", "onResume----" + BaseFragment.this.getClass().getSimpleName());
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
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			ViewGroup parent = (ViewGroup) mainView.getParent();
			if (parent != null) {
				parent.removeView(mainView);
			}
			return  mainView ;
	}

//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		ViewUtils.inject(this , view);
//		afterCreate(savedInstanceState);
//		System.out.println("onViewCreated");
//	}
//
//	/**
//	 * 设置数据
//	 * @param savedInstanceState
//     */
//	protected abstract void afterCreate(Bundle savedInstanceState);
	/**
	 * 返回fragemtn要创建的View
	 * 不必重写 onCreateView 只需从写改方法
	 * */
	protected abstract int createViewByLayoutId();

	public View findViewById (int resId) {
		if (mainView == null) {
			return null;
		}
		return mainView.findViewById(resId);
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
	protected void setTitleRigthIcon(boolean show, int resid) {
		ImageView view = (ImageView) findViewById(R.id.title_iv_rigth);
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
	protected void setTitleText(boolean show, String text) {
		TextView view = (TextView) findViewById(R.id.title_tv_title);
		if (show) {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		} else {
			view.setVisibility(View.GONE);
		}
	}
	protected void setTitleTextColor(int color){
		TextView view = (TextView) findViewById(R.id.title_tv_title);
		view.setTextColor(color);
	}
	protected void setTitleTitleBg(int color) {
		FrameLayout view = (FrameLayout) findViewById(R.id.title_bg);
		view.setBackgroundColor(color);
	}

}
