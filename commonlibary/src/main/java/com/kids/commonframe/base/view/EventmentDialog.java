package com.kids.commonframe.base.view;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kids.commonframe.R;

import java.io.Serializable;

/**
 * 选择环境
 * @author chao
 */
public class EventmentDialog extends Dialog {
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	public final static int BOTH = 2;
	public final static int NONE = 3;
    private TextView context;
    private Button okBtn;
    private Button cancle;
    private View hLine;
    private View vLine;

    private LinearLayout btnLayout;

    private TextView title;
	private RadioGroup chooseButton;
	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;
	public EventmentDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		setContentView(R.layout.eventment_dialog_layout);
		Window window = getWindow();
		window.getAttributes().gravity = Gravity.CENTER;
 		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(false);
		this.context = (TextView) this.findViewById(R.id.context);
        cancle = (Button) this.findViewById(R.id.cancle);
		okBtn = (Button) this.findViewById(R.id.ok);
		vLine = this.findViewById(R.id.vline);
		hLine = this.findViewById(R.id.hline);
		chooseButton = (RadioGroup) this.findViewById(R.id.chooseButton);
		title = (TextView) this.findViewById(R.id.title);
		btnLayout = (LinearLayout) this.findViewById(R.id.dialog_btn_layout);
		setLeftBtnListener("取消",null);
		chooseButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
              if (onCheckedChangeListener != null ) {
				  onCheckedChangeListener.onCheckedChanged(group,checkedId);
			  }
			}
		});
	}

	public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
		this.onCheckedChangeListener = onCheckedChangeListener;
	}
	public void setCheckButton (int id) {
		chooseButton.check(id);
	}
	/**
	 * 设置对话框样式，现实一个按钮，还是现实两个按钮
	 * @param model default BOTH
	 */
	public void setModel (int model) {
		switch (model) {
		case EventmentDialog.LEFT:
			okBtn.setVisibility(View.GONE);
			cancle.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.GONE);
			hLine.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.VISIBLE);
			break;
		case EventmentDialog.RIGHT:
			cancle.setVisibility(View.GONE);
			okBtn.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.GONE);
			hLine.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.VISIBLE);
			break;
		case EventmentDialog.BOTH:
			cancle.setVisibility(View.VISIBLE);
			okBtn.setVisibility(View.VISIBLE);
			vLine.setVisibility(View.VISIBLE);
			hLine.setVisibility(View.VISIBLE);
			btnLayout.setVisibility(View.VISIBLE);
			break;
		case EventmentDialog.NONE:
			cancle.setVisibility(View.GONE);
			okBtn.setVisibility(View.GONE);
			vLine.setVisibility(View.GONE);
			hLine.setVisibility(View.GONE);
			btnLayout.setVisibility(View.GONE);
			break;
		}
	}
	/**
	 * 设置消息提示
	 */
	public void setMessage(String strMessage) {
		context.setText(strMessage);
	}
	/**
	 * 设置标题
	 */
	public void setTitle (String title) {
		this.title.setText(title);
	}
	/**
	 * 设置左右按钮多标签以及监听起
	 * @param text
	 * @param listener
	 */
	public void setLeftBtnListener (String text ,final  DialogListener listener) {
		cancle.setText(text);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null) {
				  listener.doClickButton(okBtn, EventmentDialog.this);
				}
				EventmentDialog.this.dismiss();
			}
		});
	}
	public void setRightBtnListener (String text , final DialogListener listener) {
		okBtn.setText(text);
		okBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listener != null) {
				  listener.doClickButton(okBtn, EventmentDialog.this);
				}
				EventmentDialog.this.dismiss();
			}
		});
	}
	
	/**
	 * 设置左右按钮颜色
	 * @param color
	 */
	public void setLeftBtnColor (int color) {
		cancle.setTextColor(color);
	}
	public void setRightBtnColor (int color) {
		okBtn.setTextColor(color);
	}
	/**
	 * 延迟2s消失
	 */
	public void holdDismiss (int sec) {
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
		    EventmentDialog.this.dismiss();
		}
		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

   public interface DialogListener {
	   public void doClickButton(Button btn, EventmentDialog dialog);
   }
}
