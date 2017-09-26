package com.runwise.supply.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.runwise.supply.tools.DensityUtil;

/**
 * Created by mike on 2017/9/5.
 */

public class YourScrollableViewPager extends ViewPager {

    private static final int MATCH_PARENT = 1073742592;

    private int currentPageNumber;
    private int pageCount;

    public YourScrollableViewPager(Context context) {
        super(context);
        prepareUI();
    }

    public YourScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareUI();
    }
    int mDefaultPageHeight;
    private void prepareUI() {
        setOffscreenPageLimit(pageCount);
        mDefaultPageHeight = DensityUtil.dip2px(getContext(),200);
        maxHeight = mDefaultPageHeight;
    }

    int maxHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() != 0) {
            View child = getChildAt(currentPageNumber);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h>maxHeight){
                maxHeight = h;
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    public void onPrevPage() {
        measure(MATCH_PARENT, 0);
    }

    public void onNextPage() {
        measure(MATCH_PARENT, 0);
    }}
