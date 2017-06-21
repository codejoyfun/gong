/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.kids.commonframe.R;


@SuppressLint("ViewConstructor")
public class IndicatorArrow extends FrameLayout implements AnimationListener {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private Animation mInAnim, mOutAnim;
	private ImageView mArrowImageView;

	private Animation mRotateAnimation, mResetRotateAnimation;

	public IndicatorArrow(Context context) {
		super(context);
		init(context);
	}

	public IndicatorArrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private void init (Context context) {
		mArrowImageView = new ImageView(context);

		Drawable arrowD = getResources().getDrawable(R.drawable.indicator_arrow);
		mArrowImageView.setImageDrawable(arrowD);

		final int padding = getResources().getDimensionPixelSize(R.dimen.indicator_internal_padding);
		mArrowImageView.setPadding(padding, padding, padding, padding);
		addView(mArrowImageView);

		int inAnimResId, outAnimResId;
		inAnimResId = R.anim.slide_in_from_bottom;
		outAnimResId = R.anim.slide_out_to_bottom;
		setBackgroundResource(R.drawable.indicator_bg_bottom);

		// Rotate Arrow so it's pointing the correct way
		mArrowImageView.setScaleType(ScaleType.MATRIX);
		Matrix matrix = new Matrix();
		matrix.setRotate(180f, arrowD.getIntrinsicWidth() / 2f, arrowD.getIntrinsicHeight() / 2f);
		mArrowImageView.setImageMatrix(matrix);

		mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
		mInAnim.setAnimationListener(this);

		mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
		mOutAnim.setAnimationListener(this);

		final Interpolator interpolator = new LinearInterpolator();
		mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(interpolator);
		mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setFillAfter(true);

		mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mResetRotateAnimation.setInterpolator(interpolator);
		mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		mResetRotateAnimation.setFillAfter(true);
	}
	

	public final boolean isVisible() {
		Animation currentAnim = getAnimation();
		if (null != currentAnim) {
			return mInAnim == currentAnim;
		}

		return getVisibility() == View.VISIBLE;
	}

	public void hide() {
		startAnimation(mOutAnim);
	}

	public void show() {
		mArrowImageView.clearAnimation();
		startAnimation(mInAnim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == mOutAnim) {
			mArrowImageView.clearAnimation();
			setVisibility(View.GONE);
		} else if (animation == mInAnim) {
			setVisibility(View.VISIBLE);
		}

		clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// NO-OP
	}

	@Override
	public void onAnimationStart(Animation animation) {
		setVisibility(View.VISIBLE);
	}

	public void releaseToRefresh() {
		mArrowImageView.startAnimation(mRotateAnimation);
	}

	public void pullToRefresh() {
		mArrowImageView.startAnimation(mResetRotateAnimation);
	}

}
