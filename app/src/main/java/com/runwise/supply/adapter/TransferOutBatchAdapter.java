package com.runwise.supply.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.TransferOutDetailResponse;
import com.runwise.supply.view.NoWatchEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mike on 2017/10/15.
 */

public class TransferOutBatchAdapter extends IBaseAdapter {
    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        final TransferOutDetailResponse.TransferBatchLot transferBatchLot = (TransferOutDetailResponse.TransferBatchLot) mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(convertView.getContext()).inflate(R.layout.item_tranferout_batch, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.mEtCount.setText(String.valueOf(transferBatchLot.getQuantQty()));
        viewHolder.mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantQty = transferBatchLot.getQuantQty()+1;
                transferBatchLot.setQuantQty(quantQty);
                notifyDataSetChanged();
            }
        });
        viewHolder.mIvReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantQty = transferBatchLot.getQuantQty()-1;
                if (quantQty == 0){
                    return;
                }
                transferBatchLot.setQuantQty(quantQty);
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
                int quantQty = Integer.parseInt(s.toString());
                transferBatchLot.setQuantQty(quantQty);
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
