/**
 * 
 */
package com.runwise.supply.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.runwise.supply.R;
import com.kids.commonframe.base.util.CommonUtils;


/**
 * 
 */
public class CustomInputMethodView extends TableLayout {
	public static final String BackKey = "<";
	public static final String XKey = "X";
	
	public static interface InputListener {
		public void onInputed(String content);
	}

	private TableRow row1;
	private TableRow row2;
	private TableRow row3;
	private TableRow row4;
	private Button input1;
	private Button input2;
	private Button input3;
	private Button input4;
	private Button input5;
	private Button input6;
	private Button input7;
	private Button input8;
	private Button input9;
	private Button input0;
	private Button inputx;
	private ImageButton inputb;

	private InputListener inputListener;

	public CustomInputMethodView(Context context) {
		super(context);
		init(context);
	}

	public CustomInputMethodView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setShowX(boolean show) {
		inputx.setEnabled(show);
	}

	private void init(Context context) {
		setBackgroundResource(R.drawable.keyboard_topbg);
		setStretchAllColumns(true);

		int btc = 0xffffffff;
		int bts = CommonUtils.dip2px(context, 14);
		int bh = CommonUtils.dip2px(context, 50);
		int bb = R.drawable.btn_keyboard;
		int xb = R.drawable.btn_keyboard_x;
		
		row1 = new TableRow(context);
		row2 = new TableRow(context);
		row3 = new TableRow(context);
		row4 = new TableRow(context);
		addView(row1);
		addView(row2);
		addView(row3);
		addView(row4);

		input1 = new Button(context);
		input2 = new Button(context);
		input3 = new Button(context);
		input1.setText("1");
		input1.setTextColor(btc);
		input1.setTextSize(bts);
		input1.setWidth(1);
		input1.setHeight(bh);
		input1.setBackgroundResource(bb);
		input2.setText("2");
		input2.setTextColor(btc);
		input2.setTextSize(bts);
		input2.setWidth(1);
		input2.setHeight(bh);
		input2.setBackgroundResource(bb);
		input3.setText("3");
		input3.setTextColor(btc);
		input3.setTextSize(bts);
		input3.setWidth(1);
		input3.setHeight(bh);
		input3.setBackgroundResource(bb);
		row1.addView(input1);
		row1.addView(input2);
		row1.addView(input3);

		input4 = new Button(context);
		input5 = new Button(context);
		input6 = new Button(context);
		input4.setText("4");
		input4.setTextColor(btc);
		input4.setTextSize(bts);
		input4.setWidth(1);
		input4.setHeight(bh);
		input4.setBackgroundResource(bb);
		input5.setText("5");
		input5.setTextColor(btc);
		input5.setTextSize(bts);
		input5.setWidth(1);
		input5.setHeight(bh);
		input5.setBackgroundResource(bb);
		input6.setText("6");
		input6.setTextColor(btc);
		input6.setTextSize(bts);
		input6.setWidth(1);
		input6.setHeight(bh);
		input6.setBackgroundResource(bb);
		row2.addView(input4);
		row2.addView(input5);
		row2.addView(input6);

		input7 = new Button(context);
		input8 = new Button(context);
		input9 = new Button(context);
		input7.setText("7");
		input7.setTextColor(btc);
		input7.setTextSize(bts);
		input7.setWidth(1);
		input7.setHeight(bh);
		input7.setBackgroundResource(bb);
		input8.setText("8");
		input8.setTextColor(btc);
		input8.setTextSize(bts);
		input8.setWidth(1);
		input8.setHeight(bh);
		input8.setBackgroundResource(bb);
		input9.setText("9");
		input9.setTextColor(btc);
		input9.setTextSize(bts);
		input9.setWidth(1);
		input9.setHeight(bh);
		input9.setBackgroundResource(bb);
		row3.addView(input7);
		row3.addView(input8);
		row3.addView(input9);

		inputx = new Button(context);
		input0 = new Button(context);
		inputb = new ImageButton(context);
		inputx.setText(XKey);
		inputx.setTextColor(0xff333333);
		inputx.setTextSize(bts);
		inputx.setWidth(1);
		inputx.setHeight(bh);
		inputx.setBackgroundResource(xb);
		input0.setText("0");
		input0.setTextColor(btc);
		input0.setTextSize(bts);
		input0.setWidth(1);
		input0.setHeight(bh);
		input0.setBackgroundResource(bb);
		inputb.setMaxHeight(bh);
		inputb.setMinimumHeight(bh);
		inputb.setBackgroundResource(xb);
		inputb.setImageResource(R.drawable.keyboard_cancel);
		row4.addView(inputx);
		row4.addView(input0);
		row4.addView(inputb);

		input1.setOnClickListener(inputClickListener);
		input2.setOnClickListener(inputClickListener);
		input3.setOnClickListener(inputClickListener);
		input4.setOnClickListener(inputClickListener);
		input5.setOnClickListener(inputClickListener);
		input6.setOnClickListener(inputClickListener);
		input7.setOnClickListener(inputClickListener);
		input8.setOnClickListener(inputClickListener);
		input9.setOnClickListener(inputClickListener);
		input0.setOnClickListener(inputClickListener);
		inputx.setOnClickListener(inputClickListener);
		inputb.setOnClickListener(inputClickListener);

		// 初始化不显示X
		setShowX(false);
	}

	private OnClickListener inputClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v instanceof ImageButton) {
				if (inputListener != null) inputListener.onInputed(BackKey);
			} else {
				Button b = (Button) v;
				String text = b.getText().toString().trim();
				if(XKey.equals(text)) text = "X";
				if (inputListener != null) inputListener.onInputed(text);
			}
		}
	};

	public void setInputListener(InputListener listener) {
		inputListener = listener;
	}
}
