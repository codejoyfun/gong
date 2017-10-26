package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.R;
import com.kids.commonframe.base.BaseManager;
import com.kids.commonframe.base.CheckVersionManager;
import com.kids.commonframe.base.VersionUpdateResponse;
import com.kids.commonframe.config.Constant;


/**
 * 自动更新对话框,作了主要逻辑处理
 */
public class CustomUpdateDialog extends Dialog {
	private Context context;
	private TextView title;
	private TextView updateContext;
	private Button okBtn;
	private Button cancle;
	private ImageView icon;
	private TextView mTvVersion;
	private View mViewClose;
	private View mLayoutBtns;
	private View mBtnMandatory;

	private UpdateDialogOkListener listener;
	private UpdateDialogCancelListener cancelListener;
	private CheckVersionManager checkVersionManager;
	public CustomUpdateDialog(final Context context, final VersionUpdateResponse bean, CheckVersionManager mCheckVersionManager) {
	super(context, R.style.CustomProgressDialog);
		this.context = context;
		this.checkVersionManager = mCheckVersionManager;
		setContentView(R.layout.update_dialog_layout);
		//setContentView(R.layout.dialog_update_tip);
		this.setCanceledOnTouchOutside(false);
		getWindow().getAttributes().gravity = Gravity.CENTER;
		this.setCancelable(true);
		title = (TextView) this.findViewById(R.id.update_title);
		updateContext = (TextView) this.findViewById(R.id.update_context);
		cancle = (Button) this.findViewById(R.id.update_cancle);
		okBtn = (Button) this.findViewById(R.id.update_ok);
		icon = (ImageView) this.findViewById(R.id.iv_update);
		mTvVersion = (TextView) this.findViewById(R.id.update_version);
		mViewClose = findViewById(R.id.iv_update_close);
		mBtnMandatory = (findViewById(R.id.btn_mandatory_update));
		mLayoutBtns = findViewById(R.id.ll_not_mandatory_update);

		if (!bean.isMandatory()) {
			//title.setText("发现新版本，更新内容为：");
			okBtn.setText("立即更新");
			icon.setImageResource(R.drawable.check_update_icon);
			//cancle.setText("取消");
			mBtnMandatory.setVisibility(View.GONE);
		}
		else{
			//title.setText("发现新版本，需要更新才能继续使用：");
			okBtn.setText("立即更新");
			//cancle.setText("退出");
			this.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					BaseManager.getInstance().finishAll();
				}
			});
			mBtnMandatory.setVisibility(View.VISIBLE);
			mLayoutBtns.setVisibility(View.GONE);
			mBtnMandatory.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					checkVersionManager.startDownloadFile(Constant.BASE_URL+bean.getUrl());
				}
			});
		}

		mTvVersion.setText("最新版本（"+bean.getVersionName()+"）");
		updateContext.setText("更新内容：\n"+bean.getDescription());
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomUpdateDialog.this.dismiss();
				if (CustomUpdateDialog.this.cancelListener != null) {
					CustomUpdateDialog.this.cancelListener.doCancelButton(
							cancle, CustomUpdateDialog.this);

				}
			}
		});
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				checkVersionManager.startDownloadFile(Constant.BASE_URL+bean.getUrl());
//				Uri updateUrl = Uri.parse(bean.getUrl());
//				Intent intent = new Intent(Intent.ACTION_VIEW, updateUrl);
//				if (bean.getUrl().startsWith("www.")) {
//					intent.setClassName("com.android.browser",
//							"com.android.browser.BrowserActivity");
//				}
//				CustomUpdateDialog.this.context.startActivity(intent);
			}
		});
		mViewClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomUpdateDialog.this.dismiss();
				if (CustomUpdateDialog.this.cancelListener != null) {
					CustomUpdateDialog.this.cancelListener.doCancelButton(
							cancle, CustomUpdateDialog.this);

				}
			}
		});
	}
	public void setChanceBtnListener(UpdateDialogCancelListener listener) {
		this.cancelListener = listener;
	}

	public void setBtnListener(UpdateDialogOkListener listener) {
		this.listener = listener;
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CustomUpdateDialog.this.listener != null) {
					CustomUpdateDialog.this.listener.doUpdateButton(okBtn,
							CustomUpdateDialog.this);

				}
			}
		});
	}

	public void setCancelBtnListener(UpdateDialogCancelListener listener) {
		this.cancelListener = listener;
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (CustomUpdateDialog.this.cancelListener != null) {
					CustomUpdateDialog.this.cancelListener.doCancelButton(
							cancle, CustomUpdateDialog.this);

				}
			}
		});
	}

	public void setUpdateText(String text) {
		okBtn.setText(text);
	}

	public void setChannelText(String text) {
		cancle.setText(text);
	}

	/**
	 * 设置消息提示
	 */
	public void setMessage(String strMessage) {
		title.setText(strMessage);
	}

	public interface UpdateDialogOkListener {
		public void doUpdateButton(Button btn, CustomUpdateDialog dialog);
	}

	public interface UpdateDialogCancelListener {
		public void doCancelButton(Button btn, CustomUpdateDialog dialog);
	}


}
