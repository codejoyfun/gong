package com.runwise.supply;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferBatchLot;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.view.NoWatchEditText;

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
    private TransferDetailResponse.LinesBean mTransferBatchLine;//批次信息
    private BatchListAdapter mBatchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_in_batch);
        mTransferBatchLine = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_BATCH);
        mLinesBean = getIntent().getParcelableExtra(INTENT_KEY_PRODUCT);
        mBatchListAdapter = new BatchListAdapter();
        mLvBatch.setAdapter(mBatchListAdapter);
        initViews();
    }

    private void initViews(){
        ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(this).get(mLinesBean.getProductID()+"");
        FrecoFactory.getInstance(this).disPlay(mSdvProductImg, Constant.BASE_URL+listBean.getImage().getImageSmall());
        mTvProductName.setText(listBean.getName());
        mTvProductContent.setText(listBean.getDefaultCode()+" "+listBean.getUnit());
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @OnClick({R.id.btn_confirm,R.id.iv_cancle})
    public void onBtnClick(View v){
        switch (v.getId()){
            case R.id.btn_confirm:
                Intent data = new Intent();
                data.putExtra(INTENT_KEY_TRANSFER_BATCH, (Parcelable) mTransferBatchLine);
                setResult(RESULT_OK,data);
                finish();
                break;
            case R.id.iv_cancle:
                finish();
                break;
        }
    }

    private class BatchListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTransferBatchLine==null?0:mTransferBatchLine.getProductLotInfo().size();
        }

        @Override
        public TransferBatchLot getItem(int position) {
            return mTransferBatchLine.getProductLotInfo().get(position);
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
            final TransferBatchLot lot = getItem(position);
            viewHolder.tvBatch.setText(lot.getLotID());
            viewHolder.tvDeliverCount.setText((int)lot.getUsedQty()+"");
            viewHolder.etProductCount.setText(lot.getActualQty()+"");
            viewHolder.tvProductDateValue.setText(lot.getLotDate());
            //增加
            viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int current = lot.getActualQty();
                    lot.setActualQty(current+1);
                    if(!checkTotal()){
                        Toast.makeText(TransferInBatchActivity.this,"总数量不能超过订单量",Toast.LENGTH_LONG).show();
                        lot.setActualQty(current);
                        return;
                    }
                    notifyDataSetChanged();
                }
            });
            //减少
            viewHolder.btnSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int current = lot.getActualQty();
                    if(current-1>=0)lot.setActualQty(current-1);
                    if(!checkTotal()){
                        Toast.makeText(TransferInBatchActivity.this,"总数量不能超过订单量",Toast.LENGTH_LONG).show();
                        lot.setActualQty(current);
                        return;
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    /**
     * 总数量是否超过订单量
     * @return
     */
    private boolean checkTotal(){
        int totalActual = 0;//实际数量
        int totalExpected = 0;//订单量
        for(TransferBatchLot lot:mTransferBatchLine.getProductLotInfo()){
            totalActual = totalActual + lot.getActualQty();
            totalExpected = totalExpected + (int)lot.getUsedQty();
        }
        return totalActual <= totalExpected;
    }

    static class ViewHolder {
        @BindView(R.id.tv_batch_value)
        TextView tvBatch;
        @BindView(R.id.tv_product_date_value)
        TextView tvProductDateValue;
        @BindView(R.id.et_deliver_count)
        TextView tvDeliverCount;
        @BindView(R.id.et_product_count)
        NoWatchEditText etProductCount;
        @BindView(R.id.btn_transfer_in_add)
        Button btnAdd;
        @BindView(R.id.btn_transfer_in_subtract)
        Button btnSubtract;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
