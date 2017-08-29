package com.runwise.supply.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kids.commonframe.config.GlobalConstant;
import com.lidroid.xutils.util.LogUtils;
import com.runwise.supply.R;

/**
 * Created by myChaoFile on 2017/8/28.
 */

public class ObservableScrollView extends ScrollView {


    public ObservableScrollView(Context context) {
        super(context);
    }


    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private View mReferenceView;
    private View mTitleView;
    private int backgroundColor;
    private int[] backgroundColorRGB;
    private boolean isSlowlyChange = true;

    private ImageView leftView,rightView;
    private TextView titleTextView;


    @Override
    public void scrollTo(int x, int y) {
        if (x == 0 && y == 0 || y <= 0) {
            super.scrollTo(x, y);
        }
    }

    public void setSlowlyChange(boolean slowlyChange) {
        this.isSlowlyChange = slowlyChange;
    }

    /**
     * 初始化设置参数
     *
     * @param titleView
     * @param referenceView
     * @param backgroundColor
     * @param backgroundColorRGB
     */
    public void initAlphaTitle(View titleView, View referenceView, int backgroundColor, int[] backgroundColorRGB) {
        this.mTitleView = titleView;
        this.mReferenceView = referenceView;
        this.backgroundColor = backgroundColor;
        this.backgroundColorRGB = backgroundColorRGB;
    }
    public void setImageViews(ImageView leftView, ImageView rightView, TextView titleTextView) {
        this.leftView = leftView;
        this.rightView = rightView;
        this.titleTextView = titleTextView;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY >= mReferenceView.getTop() + mReferenceView.getMeasuredHeight()) {
            mTitleView.setBackgroundColor(backgroundColor);
        } else if (scrollY >= 0) {
            if (!isSlowlyChange) {
                mTitleView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                int maxScoll = (mReferenceView.getTop() + mReferenceView.getMeasuredHeight()) -  GlobalConstant.screenH ;
                float alpha = (float)scrollY / maxScoll * 255;
                if (alpha >=255){
                    alpha = 255;
                    leftView.setImageResource(R.drawable.nav_setting);
                    rightView.setImageResource(R.drawable.nav_service_message);
                    titleTextView.setVisibility(View.VISIBLE);
                }
                else{
                    leftView.setImageResource(R.drawable.nav_setting_w);
                    rightView.setImageResource(R.drawable.nav_service_message_w);
                    titleTextView.setVisibility(View.GONE);
                }
                LogUtils.e("-------------------"+alpha);
                mTitleView.setBackgroundColor(Color.argb((int) alpha, 0xff, 0xff, 0xff));//白色背景

//                int color = Color.argb(alpha, backgroundColorRGB[0], backgroundColorRGB[1], backgroundColorRGB[2]);
//                mTitleView.setBackgroundColor(color);
            }
        }

    }
}
