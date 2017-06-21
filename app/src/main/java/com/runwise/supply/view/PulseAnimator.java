package com.runwise.supply.view;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

public class PulseAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleY", 1, 1.5f, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1, 1.5f, 1)
        );
    }
}
