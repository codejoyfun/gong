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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.kids.commonframe.R;

public class AnimLoadingLayout extends LoadingLayout {
	private final AnimationDrawable mSetAnimation;
	private int[] animSet = new int[]{R.drawable.laohu02_00001,
			R.drawable.laohu02_00002,R.drawable.laohu02_00003,R.drawable.laohu02_00004,
			R.drawable.laohu02_00005,R.drawable.laohu02_00006,R.drawable.laohu02_00007,
			R.drawable.laohu02_00008,R.drawable.laohu02_00009,R.drawable.laohu02_00010,
			R.drawable.laohu02_00011,R.drawable.laohu02_00012,R.drawable.laohu02_00013,
			R.drawable.laohu02_00014,R.drawable.laohu02_00015,R.drawable.laohu02_00016,
			R.drawable.laohu02_00017,R.drawable.laohu02_00018,R.drawable.laohu02_00019,
			R.drawable.laohu02_00020,R.drawable.laohu02_00021,R.drawable.laohu02_00022,
			R.drawable.laohu02_00023,R.drawable.laohu02_00024,R.drawable.laohu02_00025,
			R.drawable.laohu02_00026,R.drawable.laohu02_00027};

	public AnimLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

		mHeaderImage.setScaleType(ScaleType.FIT_CENTER);
		mSetAnimation = (AnimationDrawable) context.getResources().getDrawable(R.anim.refresh_loading_setanim);

		setTextViewGone();
		setImageLayoutCenter();
	}

	public void onLoadingDrawableSet(Drawable imageDrawable) {
//		if (null != imageDrawable) {
//			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
//			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
//		}
	}

	protected void onPullImpl(float scaleOfLayout) {
		int mangle = (int)(scaleOfLayout * 80);
		if ( mangle >60 && mangle < 99 ) {
			int size = (int)((100 - mangle) * 27/40);
			size = 27 - size;
			mHeaderImage.setImageResource(animSet[size]);
		}
	}

	@Override
	protected void refreshingImpl() {
		mHeaderImage.setImageDrawable(mSetAnimation);
		mSetAnimation.start();
	}

	@Override
	protected void resetImpl() {
		mHeaderImage.clearAnimation();
		resetImageRotation();
	}

	private void resetImageRotation() {
		mHeaderImage.setImageResource(R.drawable.laohu02_00001);
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.laohu02_00001;
	}

}
