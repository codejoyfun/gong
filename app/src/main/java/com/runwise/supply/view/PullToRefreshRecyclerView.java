package com.runwise.supply.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

/**
 * Created by Dong on 2017/10/31.
 */

public class PullToRefreshRecyclerView  extends PullToRefreshBase<RecyclerView> {

    public PullToRefreshRecyclerView(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }


    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        return new RecyclerView(context, attrs);
    }

    @Override
    protected boolean isReadyForPullStart() {
        if (mRefreshableView.getChildCount() <= 0) {
            return true;
        }
        int firstVisiblePosition = mRefreshableView.getChildPosition(mRefreshableView.getChildAt(0));
        if (firstVisiblePosition == 0) {
            return mRefreshableView.getChildAt(0).getTop() == mRefreshableView.getPaddingTop();
        }
        return false;

    }

    @Override
    protected boolean isReadyForPullEnd() {
        int lastItemPosition = mRefreshableView.getChildCount() - 1;
        View lastItemView = mRefreshableView.getChildAt(lastItemPosition);
        int lastVisiblePosition = mRefreshableView.getChildPosition(lastItemView);
        // 是否是最后一个Item View
        if (lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount() - 1) {
            return mRefreshableView.getChildAt(lastItemPosition).getBottom() <= mRefreshableView.getBottom();
        }
        return false;
    }

}
