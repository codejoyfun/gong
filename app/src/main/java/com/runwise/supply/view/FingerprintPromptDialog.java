package com.runwise.supply.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.mine.SettingActivity;

import java.io.Serializable;

/**
 * 指纹对话框
 */
public class FingerprintPromptDialog extends Dialog {

    private Button okBtn;
    private Button cancle;
    private Activity activity;

	public FingerprintPromptDialog(Activity context) {
		super(context, R.style.CustomProgressDialog);
		this.activity = context;
		setContentView(R.layout.dialog_fingerprint_prompt);
		Window window = getWindow();
		window.getAttributes().gravity = Gravity.CENTER;
 		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(false);
        cancle = (Button) this.findViewById(R.id.cancle);
		okBtn = (Button) this.findViewById(R.id.ok);
		//马上设置
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), SettingActivity.class);
				activity.startActivity(intent);
			}
		});
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}
