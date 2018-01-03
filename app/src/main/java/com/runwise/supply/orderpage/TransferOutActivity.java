package com.runwise.supply.orderpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.runwise.supply.orderpage.entity.TransferOutRequest;
import com.runwise.supply.tools.UserUtils;
import com.runwise.supply.view.MaxHeightListView;
import com.runwise.supply.view.NoWatchEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.R.id.tv_submit;


public class TransferOutActivity extends NetWorkActivity {

    private static final int REQUEST_DETAIL = 1 << 0;
    private static final int REQUEST_TRANSFEROUT = 1 << 1;
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
    @BindView(tv_submit)
    TextView mTvSubmit;

    public static final String INTENT_KEY_TRANSFER_ENTITY = "intent_key_transfer_entity";
    TransferEntity mTransferEntity;
    ProductAdapter mProductAdapter;

    private PopupWindow mPopWindow;     //底部弹出
    private View dialogView;            //弹窗的view
    private PopupWindow mPopWindowNoBatch;     //底部弹出
    private View dialogViewNoBatch;            //弹窗的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_out);
        ButterKnife.bind(this);
        showBackBtn();
        setTitleText(true, "出库");
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_ENTITY);
        requestData();
        mLvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransferOutDetailResponse.TransferBatchLine transferBatchLine = mTransferOutDetailResponse.getLines().get(position);
                if (transferBatchLine.getProductLotInfo() == null || transferBatchLine.getProductLotInfo().size() == 0) {
                    showPopWindowNoBatch(mTransferOutDetailResponse.getLines().get(position));
                } else {
                    showPopWindow(mTransferOutDetailResponse.getLines().get(position));
                }

            }
        });
        initPopWindow();
        initPopWindowNoBatch();
    }

    TransferOutDetailResponse mTransferOutDetailResponse;

    public void simulationData() {
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

    private TransferOutDetailResponse.TransferBatchLine newTransferBatchLine(String productId) {
        TransferOutDetailResponse.TransferBatchLine transferBatchLine = new TransferOutDetailResponse.TransferBatchLine();
        transferBatchLine.setProductID(productId);
        transferBatchLine.setProductImage("");
        transferBatchLine.setPriceUnit(10);
        transferBatchLine.setProductUnit("产品单位");
        transferBatchLine.setProductUom("库存单位");
        transferBatchLine.setProductUomQty(15);
        ArrayList<TransferOutDetailResponse.TransferBatchLot> transferBatchLots = new ArrayList<TransferOutDetailResponse.TransferBatchLot>();
        if (!productId.equals("674")) {
            for (int i = 0; i < 3; i++) {
                TransferOutDetailResponse.TransferBatchLot transferBatchLot = new TransferOutDetailResponse.TransferBatchLot();
                transferBatchLot.setLotID("" + i);
                transferBatchLot.setQuantQty(10);
                transferBatchLot.setUsedQty(5);
                transferBatchLots.add(transferBatchLot);
            }
        }
        transferBatchLine.setProductLotInfo(transferBatchLots);
        return transferBatchLine;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_DETAIL:
                mTransferOutDetailResponse = (TransferOutDetailResponse) result.getResult().getData();
//                simulationData();
                processData();
                setUpData();
                break;
            case REQUEST_TRANSFEROUT:
                finish();
                startActivity(TransferOutSuccessActivity.getStartIntent(getActivityContext(), mTransferEntity));
                break;
        }
    }

    private void processData() {
        for (TransferOutDetailResponse.TransferBatchLine transferBatchLine : mTransferOutDetailResponse.getLines()) {
            HashMap<String, TransferOutDetailResponse.TransferBatchLot> transferBatchLotHashMap = new HashMap<>();
            List<TransferOutDetailResponse.TransferBatchLot> transferBatchLots = new ArrayList<>();
            for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
                transferBatchLotHashMap.put(transferBatchLot.getLotIDID(), transferBatchLot);
            }
            Iterator iter = transferBatchLotHashMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                transferBatchLots.add((TransferOutDetailResponse.TransferBatchLot) entry.getValue());
            }
            transferBatchLine.setProductLotInfo(transferBatchLots);
        }
    }

    private void setUpData() {
        mTvCallInStore.setText(mTransferOutDetailResponse.getInfo().getLocationDestName());
        mTvCallOutStore.setText(mTransferOutDetailResponse.getInfo().getLocationName());
        mProductAdapter = new ProductAdapter();
        for (TransferOutDetailResponse.TransferBatchLine transferBatchLine : mTransferOutDetailResponse.getLines()) {
            transferBatchLine.setActualQty(transferBatchLine.getProductUomQty());
        }
        mProductAdapter.setData(mTransferOutDetailResponse.getLines());
        mLvProduct.setAdapter(mProductAdapter);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
//        simulationData();
//        setUpData();
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
        sendConnection("/gongfu/shop/transfer/" + mTransferEntity.getPickingID(), request, REQUEST_DETAIL, true, TransferOutDetailResponse.class);
    }

    private void submit() {
        TransferOutRequest transferOutRequest = new TransferOutRequest();
        transferOutRequest.setPickingID(mTransferEntity.getPickingID());
        List<TransferOutRequest.IProduct> products = new ArrayList<>();
        for (int i = 0; i < mProductAdapter.getList().size(); i++) {
            TransferOutDetailResponse.TransferBatchLine transferBatchLine = (TransferOutDetailResponse.TransferBatchLine) mProductAdapter.getList().get(i);

            if (transferBatchLine.getProductLotInfo() != null && transferBatchLine.getProductLotInfo().size() > 0 ) {
                TransferOutRequest.Product product = new TransferOutRequest.Product();
                product.setProductID(Integer.parseInt(transferBatchLine.getProductID()));
                List<TransferOutRequest.Lot> lots = new ArrayList<>();
                for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
                    if (transferBatchLot.getUsedQty() == 0){
                        continue;
                    }
                    TransferOutRequest.Lot lot = new TransferOutRequest.Lot();
                    lot.setLotIDID(transferBatchLot.getLotIDID());
                    lot.setQty(NumberUtil.getIOrD(transferBatchLot.getUsedQty()));
                    lots.add(lot);
                    product.setLotsInfo(lots);
                }
                products.add(product);
            } else {
                TransferOutRequest.ProductNoLot product = new TransferOutRequest.ProductNoLot();
                product.setProductID(Integer.parseInt(transferBatchLine.getProductID()));
                TransferOutRequest.Lot lot = new TransferOutRequest.Lot();
                lot.setLotIDID("");
                lot.setQty(NumberUtil.getIOrD(transferBatchLine.getActualQty()));
                product.setLotsInfo(lot);
                products.add(product);
            }
        }
        transferOutRequest.setProducts(products);
        sendConnection("/gongfu/shop/transfer/confirm/" + mTransferEntity.getPickingID(), transferOutRequest, REQUEST_TRANSFEROUT, true, null);
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        submit();
    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transferout_batch_list, null);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(dialogView);
//        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
    }

    private void initPopWindowNoBatch() {
        dialogViewNoBatch = LayoutInflater.from(this).inflate(R.layout.dialog_transferout_list, null);
        mPopWindowNoBatch = new PopupWindow(dialogViewNoBatch, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindowNoBatch.setContentView(dialogViewNoBatch);
//        mPopWindowNoBatch.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindowNoBatch.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindowNoBatch.setFocusable(true);
        mPopWindowNoBatch.setOutsideTouchable(true);
    }

    TransferOutDetailResponse.TransferBatchLine copyTransferBatchLine(TransferOutDetailResponse.TransferBatchLine transferBatchLine) {
        TransferOutDetailResponse.TransferBatchLine copyTransferBatchLine = new TransferOutDetailResponse.TransferBatchLine();
        copyTransferBatchLine.setPriceUnit(transferBatchLine.getPriceUnit());
        copyTransferBatchLine.setProductUomQty(transferBatchLine.getProductUomQty());
        copyTransferBatchLine.setProductUom(transferBatchLine.getProductUom());
        copyTransferBatchLine.setProductID(transferBatchLine.getProductID());
        copyTransferBatchLine.setProductImage(transferBatchLine.getProductImage());
        copyTransferBatchLine.setProductUnit(transferBatchLine.getProductUnit());

        ArrayList<TransferOutDetailResponse.TransferBatchLot> transferBatchLots = new ArrayList<>();
        for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
            TransferOutDetailResponse.TransferBatchLot copyTransferBatchLot = new TransferOutDetailResponse.TransferBatchLot();
            copyTransferBatchLot.setQuantQty(transferBatchLot.getQuantQty());
            copyTransferBatchLot.setLotID(transferBatchLot.getLotID());
            copyTransferBatchLot.setLotIDID(transferBatchLot.getLotIDID());
            copyTransferBatchLot.setUsedQty(transferBatchLot.getUsedQty());
            transferBatchLots.add(copyTransferBatchLot);
        }
        copyTransferBatchLine.setProductLotInfo(transferBatchLots);
        return copyTransferBatchLine;
    }

    private void showPopWindowNoBatch(final TransferOutDetailResponse.TransferBatchLine transferBatchLine) {
        SimpleDraweeView productImage = (SimpleDraweeView) dialogViewNoBatch.findViewById(R.id.productImage);
        TextView name = (TextView) dialogViewNoBatch.findViewById(R.id.name);
        TextView content = (TextView) dialogViewNoBatch.findViewById(R.id.content);
        TextView tv_count = (TextView) dialogViewNoBatch.findViewById(R.id.tv_count);
        TextView tv_submit = (TextView) dialogViewNoBatch.findViewById(R.id.tv_submit);
        ImageView iv_reduce = (ImageView) dialogViewNoBatch.findViewById(R.id.iv_reduce);
        ImageView iv_add = (ImageView) dialogViewNoBatch.findViewById(R.id.iv_add);
        final NoWatchEditText et_count = (NoWatchEditText) dialogViewNoBatch.findViewById(R.id.et_count);
        FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(productImage, transferBatchLine.getProductImage());
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

        tv_count.setText(NumberUtil.getIOrD(transferBatchLine.getProductUomQty()));
        et_count.setText(NumberUtil.getIOrD(transferBatchLine.getActualQty()));

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_count.getText().toString())) {
                    toast("输入的数量不能为空");
                    return;
                }
                double actualQty = Double.parseDouble(et_count.getText().toString());
                actualQty += 1;
                if (actualQty > transferBatchLine.getProductUomQty()) {
                    toast("总数量不能超过订单量");
                    return;
                }
                et_count.setText(NumberUtil.getIOrD(actualQty));
            }
        });
        iv_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_count.getText().toString())) {
                    toast("输入的数量不能为空");
                    return;
                }
                double actualQty = Double.parseDouble(et_count.getText().toString());
                actualQty -= 1;
                if (actualQty < 0) {
                    actualQty = 0;
                }
                et_count.setText(NumberUtil.getIOrD(actualQty));
            }
        });

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_count.getText().toString())) {
                    toast("输入的数量不能为空");
                    return;
                }
                double actualQty = new BigDecimal(et_count.getText().toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
                if (actualQty > transferBatchLine.getProductUomQty()) {
                    toast("总数量不能超过订单量");
                    return;
                }
                transferBatchLine.setActualQty(actualQty);
                mProductAdapter.notifyDataSetChanged();
                mPopWindowNoBatch.dismiss();

            }
        });

        mPopWindowNoBatch.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.4f);
        dialogViewNoBatch.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindowNoBatch.dismiss();
            }
        });
        mPopWindowNoBatch.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
    }

    private void showPopWindow(final TransferOutDetailResponse.TransferBatchLine sourceTransferBatchLine) {
        final TransferOutDetailResponse.TransferBatchLine transferBatchLine = copyTransferBatchLine(sourceTransferBatchLine);
        SimpleDraweeView productImage = (SimpleDraweeView) dialogView.findViewById(R.id.productImage);
        TextView name = (TextView) dialogView.findViewById(R.id.name);
        TextView content = (TextView) dialogView.findViewById(R.id.content);
        TextView tv_count = (TextView) dialogView.findViewById(R.id.tv_count);
        TextView tv_submit = (TextView) dialogView.findViewById(R.id.tv_submit);
        MaxHeightListView lv_batch = (MaxHeightListView) dialogView.findViewById(R.id.lv_batch);
        FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(productImage, transferBatchLine.getProductImage());
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
        tv_count.setText(NumberUtil.getIOrD(transferBatchLine.getProductUomQty()));
        TransferOutBatchAdapter transferOutBatchAdapter = new TransferOutBatchAdapter();
        transferOutBatchAdapter.setTotalCount(transferBatchLine.getProductUomQty());
        transferOutBatchAdapter.setData(transferBatchLine.getProductLotInfo());
        lv_batch.setAdapter(transferOutBatchAdapter);

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalCount = 0;
                for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
                    totalCount += transferBatchLot.getUsedQty();
                }
                if (totalCount > transferBatchLine.getProductUomQty()) {
                    toast("总数量不能超过订单量");
                    return;
                }
                int totalActualQty = 0;
                sourceTransferBatchLine.setProductLotInfo(new ArrayList<TransferOutDetailResponse.TransferBatchLot>());
                for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
                    totalActualQty += transferBatchLot.getUsedQty();
                    sourceTransferBatchLine.getProductLotInfo().add(transferBatchLot);
                }
                sourceTransferBatchLine.setActualQty(totalActualQty);
                mProductAdapter.notifyDataSetChanged();
                mPopWindow.dismiss();
            }
        });

        mPopWindow.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.4f);
        dialogView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
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
            FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(viewHolder.mProductImage, transferBatchLine.getProductImage());
            viewHolder.mName.setText(transferBatchLine.getProductName());
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(transferBatchLine.getProductID());
            if (listBean != null) {
                StringBuffer sb = new StringBuffer(transferBatchLine.getProductCode());
                sb.append("  |  ").append(transferBatchLine.getProductUnit());
                boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
                if (canSeePrice) {
                    if (listBean.isTwoUnit()) {
                        sb.append("\n").append("¥").append(UserUtils.formatPrice(String.valueOf(listBean.getSettlePrice()))).append("元/").append(listBean.getSettleUomId());
                    } else {
                        sb.append("\n").append("¥").append(UserUtils.formatPrice(String.valueOf(listBean.getPrice()))).append("元/").append(transferBatchLine.getProductUom());
                    }
                }
                viewHolder.mContent.setText(sb.toString());
            }
            viewHolder.mUnit1.setText("/" + NumberUtil.getIOrD(transferBatchLine.getProductUomQty()) + transferBatchLine.getProductUom());
            viewHolder.mTvCount.setText(NumberUtil.getIOrD(transferBatchLine.getActualQty()));
            viewHolder.mLlBatch.removeAllViews();
            if (transferBatchLine.getProductLotInfo() == null || transferBatchLine.getProductLotInfo().size() == 0) {
                viewHolder.mLlBatch.setVisibility(View.GONE);
                viewHolder.mVLine1.setVisibility(View.GONE);
            } else {
                viewHolder.mLlBatch.setVisibility(View.VISIBLE);
                viewHolder.mVLine1.setVisibility(View.VISIBLE);
                for (TransferOutDetailResponse.TransferBatchLot transferBatchLot : transferBatchLine.getProductLotInfo()) {
                    if (transferBatchLot.getUsedQty() == 0){
                        continue;
                    }
                    View view = LayoutInflater.from(getActivityContext()).inflate(R.layout.item_batch, null);
                    TextView tvName = (TextView) view.findViewById(R.id.tv_batch_name);
                    tvName.setText(transferBatchLot.getLotID());
                    TextView tvCount = (TextView) view.findViewById(R.id.tv_batch_count);
                    tvCount.setText(NumberUtil.getIOrD(transferBatchLot.getUsedQty()));
                    viewHolder.mLlBatch.addView(view);
                }
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
