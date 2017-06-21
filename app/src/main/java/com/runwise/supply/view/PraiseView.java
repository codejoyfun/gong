package com.runwise.supply.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.FrameLayout;

import com.runwise.supply.R;


public class PraiseView extends FrameLayout implements Checkable{
	protected OnPraisCheckedListener praiseCheckedListener;
	protected CheckedImageView imageView;
	public PraiseView(Context context) {
		super(context);
		initalize(context,null,-1);
	}

	public PraiseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initalize(context,attrs,-1);
	}
	public PraiseView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initalize(context, attrs,defStyle);
	}

	protected void initalize(Context context, AttributeSet attrs, int defStyle) {
		setClickable(true);
		imageView = new CheckedImageView(getContext());
		if ( attrs != null) {
			final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PraiseView);
			int resId = typedArray.getResourceId(R.styleable.PraiseView_defaultSrc,-1);
			if ( resId != -1) {
				imageView.setImageResource(resId);
			}
			typedArray.recycle();
//			imageView.setImageResource(R.drawable.praise_selector);
		}
		LayoutParams flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.CENTER);
		addView(imageView, flp);
	}

	@Override
	public void toggle() {
		checkChange();
	}

	public void setChecked(boolean isCheacked) {
		setCheckedAnim(isCheacked,false);
	}
	public void setCheckedAnim(boolean isCheacked) {
		setCheckedAnim(isCheacked,true);
	}
	public void setCheckedAnim(boolean isCheacked,boolean anim) {
		imageView.setChecked(isCheacked);
		if (isCheacked && anim) {
			AnimHelper.with(new PulseAnimator()).duration(800).playOn(this);
		}
	}

	public void checkChange() {
		if (imageView.isChecked) {
			imageView.setChecked(false);
		} else {
			imageView.setChecked(true);
			AnimHelper.with(new PulseAnimator()).duration(600).playOn(this);
		}
//		if (praiseCheckedListener != null) {
//			praiseCheckedListener.onPraisChecked(imageView.isChecked);
//		}
	}
	
	//初始化点赞图标
	public void defalutCheck() {
		if (!imageView.isChecked) {
			imageView.setChecked(true);
		}
	}

	public boolean isChecked() {
		return imageView.isChecked;
	}

	public void setOnPraisCheckedListener(OnPraisCheckedListener praiseCheckedListener) {
		this.praiseCheckedListener = praiseCheckedListener;
	}
	
	public interface OnPraisCheckedListener{
		void onPraisChecked(boolean isChecked);
	}
}

