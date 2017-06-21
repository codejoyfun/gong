package com.runwise.supply.pictakelist;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.kids.commonframe.config.Constant;
import com.kids.commonframe.config.GlobalConstant;
import com.runwise.supply.R;

import java.util.ArrayList;
import java.util.List;


public class PinnedRecyclerAdapter extends RecyclerView.Adapter<PinnedRecyclerViewHolder>
        implements PinnedHeaderNotifyer<String> {

    private ArrayList<PicTake> mData;

    private int itemHeight;
    private ResizeOptions imageSize;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private int styleType ;

    public PinnedRecyclerAdapter(Context context,int styleType){
        mContext = context;
        this.styleType = styleType;
        mData = new ArrayList<>();
        int spacing = (int) context.getResources().getDimension(R.dimen.verticalSpacing);
        itemHeight = (GlobalConstant.screenW - spacing * 3) / 4;
        imageSize = new ResizeOptions(itemHeight / 2, itemHeight / 2);
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<PicTake> list) {
        if (list == null) {
            return;
        }
        mData.clear();
        mData.addAll(list);
        if(styleType == Constant.STYLE_CAMERA){
            addFirstItem();
        }
    }

    private void addFirstItem() {
        PicTake add = new PicTake();
        add.setPinnedType(Constant.TYPE_DATA);
        mData.add(0, add);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup oldSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == Constant.TYPE_SECTION) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (oldSizeLookup != null) {
                        return oldSizeLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(PinnedRecyclerViewHolder holder) {
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            final StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) lp;
            slp.setFullSpan(getItemViewType(holder.getLayoutPosition()) == Constant.TYPE_SECTION);
        }
    }

    @Override
    public PinnedRecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View  itemView = LayoutInflater.from(parent.getContext()).inflate(getItemLayoutId(viewType), parent, false);
        PinnedRecyclerViewHolder holder = getItemViewHolder(viewType,itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(PinnedRecyclerViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case Constant.TYPE_SECTION:
                holder.itemView.setTag(position);
                if(holder instanceof PinnedTitleHolder){
                    PinnedTitleHolder picTitleHolder = (PinnedTitleHolder) holder;
                    picTitleHolder.onBind(getData().get(position),position);
                }
                break;
            case Constant.TYPE_DATA:
                holder.itemView.setTag(position);
                if(holder instanceof PicHolder){
                    PicHolder picHolder = (PicHolder) holder;
                    picHolder.onBind(getData().get(position), position);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getPinnedType();
    }

    @Override
    public boolean isPinnedHeaderType(int viewType) {
        return viewType == Constant.TYPE_SECTION;
    }

    @Override
    public String getPinnedHeaderInfo(int position) {
        return mData == null ? null : mData.get(position).getCreateTime();
    }

    public int getItemLayoutId(int viewType){
        switch (viewType) {
            case Constant.TYPE_SECTION:
                return R.layout.item_pinned_header;
            case Constant.TYPE_DATA:
                return R.layout.photo_grid_item;
        }
        return R.layout.photo_grid_item;
    }

    public void add(int pos, PicTake item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addMoreData(List<PicTake> data) {
        int startPos = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(startPos, data.size());
    }

    public ArrayList<PicTake> getData() {
        return mData;
    }

    public PinnedRecyclerViewHolder getItemViewHolder(int viewType,View itemView){
        switch (viewType) {
            case Constant.TYPE_SECTION:
                return new PinnedTitleHolder(mContext,itemView);
            case Constant.TYPE_DATA:
                return new PicHolder(mContext,itemView,imageSize,styleType);
        }
        return new PicHolder(mContext,itemView,imageSize,styleType);
    }

    public List<PicTake> getList() {
        List<PicTake> selectList = new ArrayList<>();
        int i = 0;
        if(styleType == Constant.STYLE_CAMERA){
            i = 1;
        }
        for (; i < mData.size(); i++) {
            if(mData.get(i).getPinnedType()!= Constant.TYPE_SECTION){
                selectList.add(mData.get(i));
            }
        }
        return selectList;
    }
}

