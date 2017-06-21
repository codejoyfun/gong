package com.runwise.supply.pictakelist;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class PinnedRecyclerViewHolder extends RecyclerView.ViewHolder {

    public PinnedRecyclerViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void onBind(final PicTake bean, final int position);
}