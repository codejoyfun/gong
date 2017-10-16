package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.TransferOutBatchAdapter;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.TransferOutDetailResponse;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferOutActivity extends NetWorkActivity {

    private static final int REQUEST_DETAIL = 1 << 0;
    @BindView(R.id.tv_call_in_store_tag)
    TextView mTvCallInStoreTag;
    @BindView(R.id.tv_call_in_store)
    TextView mTvCallInStore;
    @BindView(R.id.tv_call_out_store_tag)
    TextView mTvCallOutStoreTag;
    @BindView(R.id.tv_call_out_store)
    TextView mTvCallOutStore;
    @BindView(R.id.tv_allocation)
    TextView mTvAllocation;
    @BindView(R.id.rl_allocation)
    RelativeLayout mRlAllocation;
    @BindView(R.id.v_line1)
    View mVLine1;
    @BindView(R.id.lv_product)
    ListView mLvProduct;
    @BindView(R.id.tv_submit)
    TextView mTvSubmit;

    public static final String INTENT_KEY_TRANSFER_ENTITY = "intent_key_transfer_entity";
    TransferEntity mTransferEntity;
    ProductAdapter mProductAdapter;

    private PopupWindow mPopWindow;     //底部弹出
    private View dialogView;            //弹窗的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out);
        ButterKnife.bind(this);
        showBackBtn();
        setTitleText(true,"出库");
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_ENTITY);
        requestData();
        mLvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopWindow(mTransferOutDetailResponse.getLines().get(position));
            }
        });
        initPopWindow();
    }

    TransferOutDetailResponse mTransferOutDetailResponse;

    public void simulationData(){
        mTransferOutDetailResponse = new TransferOutDetailResponse();
        TransferOutDetailResponse.Info info = new TransferOutDetailResponse.Info();
        info.setLocationDestName("东川店");
        info.setLocationName("中大店");
        mTransferOutDetailResponse.setInfo(info);//720 745 734

        TransferOutDetailResponse.TransferBatchLine transferBatchLine1 = newTransferBatchLine("674");
        TransferOutDetailResponse.TransferBatchLine transferBatchLine2 = newTransferBatchLine("720");
        TransferOutDetailResponse.TransferBatchLine transferBatchLine3 = newTransferBatchLine("745");
        TransferOutDetailResponse.TransferBatchLine transferBatchLine4 = newTransferBatchLine("734");
        ArrayList<TransferOutDetailResponse.TransferBatchLine> transferBatchLines = new ArrayList<>();
        transferBatchLines.add(transferBatchLine1);
        transferBatchLines.add(transferBatchLine2);
        transferBatchLines.add(transferBatchLine3);
        transferBatchLines.add(transferBatchLine4);
        mTransferOutDetailResponse.setLines(transferBatchLines);
    }

    private TransferOutDetailResponse.TransferBatchLine newTransferBatchLine(String productId){
        TransferOutDetailResponse.TransferBatchLine transferBatchLine = new TransferOutDetailResponse.TransferBatchLine();
        transferBatchLine.setProductID(productId);
        transferBatchLine.setProductImage("");
        transferBatchLine.setPriceUnit(10);
        transferBatchLine.setProductUnit("产品单位");
        transferBatchLine.setProductUom("库存单位");
        transferBatchLine.setProductUomQty(11);
        ArrayList<TransferOutDetailResponse.TransferBatchLot> transferBatchLots = new ArrayList<TransferOutDetailResponse.TransferBatchLot>();
        for (int i = 0;i<3;i++){
            TransferOutDetailResponse.TransferBatchLot transferBatchLot = new TransferOutDetailResponse.TransferBatchLot();
            transferBatchLot.setLotID("Z2017092400002");
            transferBatchLot.setQuantQty(5);
            transferBatchLots.add(transferBatchLot);
        }
        transferBatchLine.setProductInfo(transferBatchLots);
        return transferBatchLine;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_DETAIL:
                mTransferOutDetailResponse = (TransferOutDetailResponse) result.getResult().getData();
                simulationData();
                setUpData();
                break;
        }
    }

    private void setUpData() {
        mTvCallInStore.setText(mTransferOutDetailResponse.getInfo().getLocationDestName());
        mTvCallOutStore.setText(mTransferOutDetailResponse.getInfo().getLocationName());
        mProductAdapter = new ProductAdapter();
        mProductAdapter.setData(mTransferOutDetailResponse.getLines());
        mLvProduct.setAdapter(mProductAdapter);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        simulationData();
        setUpData();
    }

    public static Intent getStartIntent(Context context, TransferEntity transferEntity) {
        Intent intent = new Intent(context, TransferOutActivity.class);
        intent.putExtra(INTENT_KEY_TRANSFER_ENTITY, transferEntity);
        return intent;
    }

    /**
     * 请求订单详情，拿商品列表
     */
    private void requestData() {
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output/" + mTransferEntity.getPickingID(), request, REQUEST_DETAIL, true, TransferOutDetailResponse.class);
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        startActivity(TransferOutSuccessActivity.getStartIntent(getActivityContext()));
    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transferout_batch_list, null);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(dialogView);
        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
    }

    private void showPopWindow(TransferOutDetailResponse.TransferBatchLine transferBatchLine){
        SimpleDraweeView productImage = (SimpleDraweeView) dialogView.findViewById(R.id.productImage);
        TextView name = (TextView) dialogView.findViewById(R.id.name);
        TextView content = (TextView) dialogView.findViewById(R.id.content);
        TextView tv_count = (TextView) dialogView.findViewById(R.id.tv_count);
        ListView lv_batch = (ListView) dialogView.findViewById(R.id.lv_batch);
        FrecoFactory.getInstance(getActivityContext()).disPlay(productImage, Constant.BASE_URL + transferBatchLine.getProductImage());
        ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(transferBatchLine.getProductID());
        if (listBean != null) {
            name.setText(listBean.getName());
            StringBuffer sb = new StringBuffer(listBean.getDefaultCode());
            sb.append("  ").append(listBean.getUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice) {
                if (listBean.isTwoUnit()) {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getSettlePrice()))).append("元/").append(listBean.getSettleUomId());
                } else {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getPrice()))).append("元/").append(listBean.getProductUom());
                }
            }
            content.setText(sb.toString());
        }
//        tv_count.setText(transferBatchLine.get);
        TransferOutBatchAdapter transferOutBatchAdapter = new TransferOutBatchAdapter();
        transferOutBatchAdapter.setData(transferBatchLine.getProductInfo());
        lv_batch.setAdapter(transferOutBatchAdapter);
        mPopWindow.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.4f);
        dialogView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                backgroundAlpha(1);
            }
        });
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    public class ProductAdapter extends IBaseAdapter {

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivityContext()).inflate(R.layout.item_transferout_product, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            TransferOutDetailResponse.TransferBatchLine transferBatchLine = (TransferOutDetailResponse.TransferBatchLine) mList.get(position);
            viewHolder = (ViewHolder) convertView.getTag();
            FrecoFactory.getInstance(getActivityContext()).disPlay(viewHolder.mProductImage, Constant.BASE_URL + transferBatchLine.getProductImage());
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(transferBatchLine.getProductID());
            if (listBean != null) {
                viewHolder.mName.setText(listBean.getName());
                StringBuffer sb = new StringBuffer(listBean.getDefaultCode());
                sb.append("  |  ").append(listBean.getUnit());
                boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
                if (canSeePrice) {
                    if (listBean.isTwoUnit()) {
                        sb.append("\n").append("¥").append(UserUtils.formatPrice(String.valueOf(listBean.getSettlePrice()))).append("元/").append(listBean.getSettleUomId());
                    } else {
                        sb.append("\n").append("¥").append(UserUtils.formatPrice(String.valueOf(listBean.getPrice()))).append("元/").append(listBean.getProductUom());
                    }
                }
                viewHolder.mContent.setText(sb.toString());
            }
            viewHolder.mUnit1.setText("/" + transferBatchLine.getProductUomQty() + transferBatchLine.getProductUom());
            viewHolder.mTvCount.setText(String.valueOf(transferBatchLine.getPriceUnit()));
            viewHolder.mLlBatch.removeAllViews();
            if(transferBatchLine.getProductInfo().size() == 0){
                viewHolder.mLlBatch.setVisibility(View.GONE);
                viewHolder.mVLine1.setVisibility(View.GONE);
            }else{
                viewHolder.mLlBatch.setVisibility(View.VISIBLE);
                viewHolder.mVLine1.setVisibility(View.VISIBLE);
            }
            for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductInfo()) {
                View view = LayoutInflater.from(getActivityContext()).inflate(R.layout.item_batch, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_batch_name);
                tvName.setText(transferBatchLot.getLotID());
                TextView tvCount = (TextView) view.findViewById(R.id.tv_batch_count);
                tvCount.setText(String.valueOf(transferBatchLot.getQuantQty()));
                viewHolder.mLlBatch.addView(view);
            }
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.productImage)
            SimpleDraweeView mProductImage;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.content)
            TextView mContent;
            @BindView(R.id.tv_price)
            TextView mTvPrice;
            @BindView(R.id.unit1)
            TextView mUnit1;
            @BindView(R.id.tv_count)
            TextView mTvCount;
            @BindView(R.id.v_line1)
            View mVLine1;
            @BindView(R.id.ll_batch)
            LinearLayout mLlBatch;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
