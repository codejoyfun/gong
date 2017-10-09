package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.kids.commonframe.R;

/**
 * 自定义进度对话框
 * 
 * 
 */
public class CustomProgressDialog extends Dialog {
	
//	private AnimationDrawable anim; 
	private TextView tvMsg;
//	private HoldTimer holderTimer;
	public CustomProgressDialog(Context context) {
		super(context, R.style.DialogNoBg);
		setContentView(R.layout.dialog_custom_layout);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		this.setCanceledOnTouchOutside(false);
//		ImageView image = (ImageView) this.findViewById(R.id.loadingImageView);
//		anim = (AnimationDrawable) image.getBackground();
		tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
	}
	
	@Override
	public void show() {
//		this.setCancelable(false);
//		anim.start();
		super.show();
//		holderTimer = new HoldTimer(WAIT_TIME);
//		holderTimer.start();
	}

	@Override
	public void dismiss() {
		super.dismiss();
//		anim.stop();
//		if (holderTimer != null) {
//			holderTimer.cancel();
//		}
	}

	/**
	 * 提示内容
	 * @param strMessage
	 * @return
	 * 
	 */
	public void setMsg(String strMessage) {
		tvMsg.setText(strMessage);
	}
//	private class HoldTimer extends CountDownTimer implements Serializable {
//		private static final long serialVersionUID = 1L;
//		public HoldTimer(int second) {
//			super(second * 1000l, 1000l);
//		}
//		@Override
//		public void onFinish() {
//			CustomProgressDialog.this.setCancelable(true);
//			this.cancel();
//		}
//		@Override
//		public void onTick(long millisUntilFinished) {
//		}
//	}

}

