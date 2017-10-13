package com.runwise.supply;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.entity.TransferBatchResponse;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.NoWatchEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 入库 批次页
 *
 * Created by Dong on 2017/10/13.
 */

public class TransferInBatchActivity extends NetWorkActivity {

    public static final String INTENT_KEY_TRANSFER_BATCH = "transfer_batch";
    public static final String INTENT_KEY_PRODUCT = "lines_bean";
    @ViewInject(R.id.lv_batch)
    private ListView mLvBatch;
    @ViewInject(R.id.iv_transfer_in_product)
    private SimpleDraweeView mSdvProductImg;
    @ViewInject(R.id.tv_name)
    private TextView mTvProductName;
    @ViewInject(R.id.tv_content)
    private TextView mTvProductContent;

    private OrderResponse.ListBean.LinesBean mLinesBean;//商品基本信息
    private TransferBatchResponse.TransferBatchLine mTransferBatchLine;//批次信息
    private BatchListAdapter mBatchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_in_batch);
        mTransferBatchLine = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_BATCH);
        mLinesBean = getIntent().getParcelableExtra(INTENT_KEY_PRODUCT);
        mBatchListAdapter = new BatchListAdapter();
        mLvBatch.setAdapter(mBatchListAdapter);
        initViews();
    }

    private void initViews(){
        ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(this).get(mLinesBean.getProductID());
        //TODO:fresco
        mTvProductName.setText(listBean.getName());
        mTvProductContent.setText(listBean.getDefaultCode()+" "+listBean.getUnit());
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    private class BatchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTransferBatchLine==null?0:mTransferBatchLine.getProductInfo().size();
        }

        @Override
        public TransferBatchResponse.TransferBatchLot getItem(int position) {
            return mTransferBatchLine.getProductInfo().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_transfer_in_batch, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            TransferBatchResponse.TransferBatchLot lot = getItem(position);
            viewHolder.tvBatch.setText(lot.getLotID());
            viewHolder.tvDeliverCount.setText(lot.getQuantQty()+"");
            return convertView;
        }


    }

    static class ViewHolder {
        @BindView(R.id.tv_batch_value)
        TextView tvBatch;
        @BindView(R.id.tv_product_date_value)
        TextView tvProductDateValue;
        @BindView(R.id.tv_deliver_count)
        TextView tvDeliverCount;
        @BindView(R.id.et_product_count)
        NoWatchEditText etProductCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
