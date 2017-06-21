package com.runwise.supply.pictakelist;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.runwise.supply.R;


public class PinnedTitleHolder extends PinnedRecyclerViewHolder {

    private Context context;
    private TextView tvPinnedTitle;

    public PinnedTitleHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvPinnedTitle = (TextView) itemView.findViewById(R.id.tv_pinned_time);
    }
    @Override
    public void onBind(final PicTake bean, final int position) {
        tvPinnedTitle.setText(bean.getPinnedTitle());
    }

}
