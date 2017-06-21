package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.kids.commonframe.R;


/**
 * 自定义进度对话框
 * 
 * 
 */
public class CustomSavePicDialog extends Dialog {
	SaveInterface inter;
	public CustomSavePicDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		setContentView(R.layout.save_photo_layout);
		Window window = getWindow();
		window.getAttributes().gravity = Gravity.BOTTOM;
		window.setWindowAnimations(R.style.MyPopwindow_anim_style);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
		View save = this.findViewById(R.id.select_layout);
		View chance = this.findViewById(R.id.cancle_button);
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( inter != null ) {
					inter.doSave();
				}
				dismiss();
			}
		});
		chance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	public void setOnSaveClickItem(SaveInterface inter){
		this.inter = inter;
	}
	public interface SaveInterface{
		void doSave();
	}
}
