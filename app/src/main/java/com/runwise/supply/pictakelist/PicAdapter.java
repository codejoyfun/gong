package com.runwise.supply.pictakelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runwise.supply.R;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;

import java.util.ArrayList;
import java.util.List;


public class PicAdapter extends RecyclerView.Adapter {
    private List<PicTake> takeList;
    private int itemHeight;
    private ResizeOptions imageSize;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private SelectPictureActivity mActivity;

    public PicAdapter(Context context) {
        mContext = context;
        mActivity = (SelectPictureActivity)context;
        takeList = new ArrayList<PicTake>();
        int spacing = (int) mContext.getResources().getDimension(R.dimen.verticalSpacing);
        itemHeight = (GlobalConstant.screenW - spacing * 3) / 4;
        imageSize = new ResizeOptions(itemHeight / 2, itemHeight / 2);
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<PicTake> list) {
        if (list == null) {
            return;
        }
        takeList.clear();
        takeList.addAll(list);
        addFirstItem();
        this.notifyDataSetChanged();
    }

    public List<PicTake> getList() {
        List<PicTake> selectList = new ArrayList<PicTake>();
        for (int i = 1; i < takeList.size(); i++) {
            selectList.add(takeList.get(i));
        }
        return selectList;
    }

    private void addFirstItem() {
        PicTake add = new PicTake();
        takeList.add(0, add);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.photo_grid_item, null);
        return new PicHolder(mContext, itemView, imageSize, Constant.STYLE_CAMERA);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PicHolder picHolder = (PicHolder) holder;
        PicTake bean = takeList.get(position);
        picHolder.onBind(bean, position);
    }

    @Override
    public int getItemCount() {
        return takeList.size();
    }
}