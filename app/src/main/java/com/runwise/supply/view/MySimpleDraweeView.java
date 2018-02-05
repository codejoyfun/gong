package com.runwise.supply.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by mike on 2018/2/4.
 */

public class MySimpleDraweeView extends SimpleDraweeView {
    public MySimpleDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public MySimpleDraweeView(Context context) {
        super(context);
    }

    public MySimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MySimpleDraweeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            System.out.println("MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }
}
