package com.runwise.supply.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.TransferOutDetailResponse;
import com.runwise.supply.view.NoWatchEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mike on 2017/10/15.
 */

public class TransferOutBatchAdapter extends IBaseAdapter {

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    int mTotalCount = 0;

    boolean checkCount(Context context) {
        int actualCount = 0;
        for (Object o : mList) {
            TransferOutDetailResponse.TransferBatchLot transferBatchLot = (TransferOutDetailResponse.TransferBatchLot) o;
            actualCount += transferBatchLot.getQuantQty();
        }
        if (actualCount == mTotalCount) {
            ToastUtil.show(context, "总数量不能超过订单量");
            return false;
        }
        return true;
    }

    @Override
    protected View getExView(int position, View convertView, final ViewGroup parent) {
        final TransferOutDetailResponse.TransferBatchLot transferBatchLot = (TransferOutDetailResponse.TransferBatchLot) mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tranferout_batch, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mEtCount.setText(String.valueOf(transferBatchLot.getActualQty()));
        viewHolder.mTvBatchCount.setText(String.valueOf(transferBatchLot.getQuantQty()));
        viewHolder.mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCount(v.getContext());
                int actualQty = transferBatchLot.getActualQty() + 1;
                if (actualQty >transferBatchLot.getActualQty()){
                    ToastUtil.show(parent.getContext(),"超过库存数量");
                    return;
                }
                transferBatchLot.setActualQty(actualQty);
                notifyDataSetChanged();
            }
        });
        viewHolder.mIvReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actualQty = transferBatchLot.getActualQty() - 1;
                if (actualQty == 0) {
                    return;
                }
                transferBatchLot.setActualQty(actualQty);
                notifyDataSetChanged();
            }
        });
        viewHolder.mEtCount.removeTextChangedListener();
        viewHolder.mEtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int actualQty = Integer.parseInt(s.toString());
                int lastActualQty = transferBatchLot.getActualQty();
                transferBatchLot.setActualQty(actualQty);
                if(actualQty>transferBatchLot.getQuantQty()){
                    ToastUtil.show(parent.getContext(),"超过库存数量");
                    return;
                }
                if (!checkCount(parent.getContext())) {
                }

            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_batch_name)
        TextView mTvBatchName;
        @BindView(R.id.tv_batch_count)
        TextView mTvBatchCount;
        @BindView(R.id.iv_add)
        ImageView mIvAdd;
        @BindView(R.id.et_count)
        NoWatchEditText mEtCount;
        @BindView(R.id.iv_reduce)
        ImageView mIvReduce;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
