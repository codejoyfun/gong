package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kids.commonframe.R;

import java.io.Serializable;


public class CustomUploadDialog extends Dialog {
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	public final static int BOTH = 2;
	public final static int NONE = 3;
//	private RelativeLayout dialogLayout;
	private TextView context, hint;
	private ProgressBar progress_Bar;

	public CustomUploadDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		setContentView(R.layout.dialog_upload_layout);
		Window window = getWindow();
		window.getAttributes().gravity = Gravity.CENTER;
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(false);
//		dialogLayout = (RelativeLayout) this
//				.findViewById(R.id.dialog_upload_layout);
		this.progress_Bar = (ProgressBar) this.findViewById(R.id.progress_Bar);
		this.context = (TextView) this.findViewById(R.id.context);
		this.hint = (TextView) this.findViewById(R.id.hint);
	}

	/**
	 * 设置消息提示
	 */
	public void setMessage(String strMessage) {
		context.setText(strMessage);
	}

	/**
	 * 设置消息提示
	 */
	public void setHint(String strMessage) {
		hint.setText(strMessage);
	}

//	public void setLayoutBgColor(int resid) {
//		dialogLayout.setBackgroundResource(resid);
//	}

	/**
	 * 隐藏，显示view
	 * 
	 * @param view
	 * @param visib
	 */
	public void setViewVisibility(View view, boolean visib) {
		int vis = visib == true ? View.VISIBLE : View.INVISIBLE;
		view.setVisibility(vis);
	}

	public void setViewVisibility(boolean view, boolean view1) {
		int vis = view == true ? View.VISIBLE : View.INVISIBLE;
		int vis1 = view1 == true ? View.VISIBLE : View.INVISIBLE;
		context.setVisibility(vis);
		progress_Bar.setVisibility(vis);
		hint.setVisibility(vis1);
	}


	/**
	 * 延迟2s消失
	 */
	public void holdDismiss(int sec) {
		this.show();
		new HoldTimer(sec).start();
	}

	private class HoldTimer extends CountDownTimer implements Serializable {
		private static final long serialVersionUID = 1L;

		public HoldTimer(int second) {
			super(second * 1000l, 1000l);
		}

		@Override
		public void onFinish() {
			this.cancel();
			CustomUploadDialog.this.dismiss();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
}
