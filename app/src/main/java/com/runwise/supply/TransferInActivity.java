package com.runwise.supply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferBatchLot;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferInRequest;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.ProductBasicHelper;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;
import com.umeng.analytics.MobclickAgent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.TransferSuccessActivity.INTENT_KEY_TRANSFER_ID;

/**
 * 入库页面
 *
 * Created by Dong on 2017/10/13.
 */

public class TransferInActivity extends NetWorkActivity {
    private static final int REQUEST_DETAIL = 0;
    private static final int REQUEST_PRODUCT_INFO = 1;
    private static final int REQUEST_TRANSFER_IN_ACTION = 2;
    private static final int ACT_REQ_TRANSFER_IN_BATCH = 0x123;

    public static final String INTENT_KEY_TRANSFER_ENTITY = "transfer_entity";
    @ViewInject(R.id.tv_transfer_to)
    private TextView mTvLocation;
    @ViewInject(R.id.tv_transfer_from)
    private TextView mTvDestLocation;
    @ViewInject(R.id.rv_transfer_in_products)
    private RecyclerView mRvProducts;
    @ViewInject(R.id.tv_transfer_in_count)
    private TextView mTvTransferInCount;//入库商品
    @ViewInject(R.id.tv_transfer_in_money)
    private TextView mTvTransferInMoney;//价格
    @ViewInject(R.id.tv_no_price_count)
    private TextView mTvNoPriceCount;
    private TransferInProductAdapter mProductAdapter;

    private TransferEntity mTransferEntity;

    private Map<String,TransferDetailResponse.LinesBean> mBatchDataMap;//商品ID对应批次对象
    private Map<String,Double> mTransferCountMap = new HashMap<>();//商品ID对应入库数量

    private View dialogView;
    private PopupWindow mPopWindow;
    private TransferDetailResponse mTransferDetailResponse;
    private boolean canSeePrice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_in);
        setTitleText(true,"入库");
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        showBackBtn();
        initPopWindow();
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_ENTITY);
        productBasicHelper = new ProductBasicHelper(this,netWorkHelper);
        initViews();
        requestDetail();//如果从列表过来，没有详情信息，要查
    }

    private void initViews(){
        mTvLocation.setText(mTransferEntity.getLocationName());
        mTvDestLocation.setText(mTransferEntity.getLocationDestName());
        mRvProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mProductAdapter = new TransferInProductAdapter(this);
        mRvProducts.setAdapter(mProductAdapter);
    }

    @OnClick({R.id.tv_do_transfer_in})
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.tv_do_transfer_in:
                requestDoTransferIn();
                break;
        }
    }

    /**
     * 从调拨单详情 获取商品列表
     */
    private void requestDetail(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/"+mTransferEntity.getPickingID(),request,REQUEST_DETAIL,true, TransferDetailResponse.class);
    }

    /**
     * 入库
     */
    private void requestDoTransferIn(){
        TransferInRequest request = new TransferInRequest();
        request.setPickingID(mTransferEntity.getPickingID());

        //商品数量
        List<TransferInRequest.IProductData> reqProductDataList = new ArrayList<>();
        request.setProducts(reqProductDataList);
        //遍历每件商品
        for(TransferDetailResponse.LinesBean linesBean:mTransferDetailResponse.getLines()){
            //批次
            if(linesBean.isLotTracking()){
                TransferInRequest.ProductData reqProductData = new TransferInRequest.ProductData();
                reqProductData.setProductID(linesBean.getProductID());
                List<TransferInRequest.ProductLotData> reqLotDataList = new ArrayList<>();
                //实际数据在map中
                TransferDetailResponse.LinesBean batchLine = mBatchDataMap.get(linesBean.getProductID()+"");
                if(batchLine!=null){
                    for(TransferBatchLot lot:batchLine.getProductLotInfo()){
                        TransferInRequest.ProductLotData reqLotData = new TransferInRequest.ProductLotData();
                        reqLotData.setQtyDone(lot.getActualQty());
                        reqLotData.setLotIDID(lot.getLotIDID());
                        reqLotDataList.add(reqLotData);
                    }
                    reqProductData.setLotsInfo(reqLotDataList);//批次lotsInfo为数组
                }
                reqProductDataList.add(reqProductData);
            }else{//非批次
                double qty = mTransferCountMap.get(linesBean.getProductID()+"");
                if(qty<=0)continue;
                TransferInRequest.ProductDataNoLot reqProductData = new TransferInRequest.ProductDataNoLot();
                reqProductData.setProductID(linesBean.getProductID());
                TransferInRequest.ProductLotData reqLotData = new TransferInRequest.ProductLotData();
                reqLotData.setQtyDone(qty);
                reqProductData.setLotsInfo(reqLotData);//批次lotsInfo为单个对象
                reqProductDataList.add(reqProductData);
            }
        }

        sendConnection("/gongfu/shop/transfer/receive",request, REQUEST_TRANSFER_IN_ACTION,true,null);
    }

    /**
     * 设置底部数据
     */
    private void setupSummaryUI(){
        if(mTransferCountMap ==null)return;
        double total = 0;
        for(String id: mTransferCountMap.keySet()){
            total = total + mTransferCountMap.get(id);
        }
        mTvTransferInCount.setText(NumberUtil.getIOrD(total));
        double totalMoney = 0;
        for(String productId: mTransferCountMap.keySet()){
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(productId);
            totalMoney = totalMoney + basicBean.getPrice() * mTransferCountMap.get(productId);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        mTvTransferInMoney.setText(decimalFormat.format(totalMoney));
        if(!canSeePrice){//简单粗暴直接盖着价格
            mTvNoPriceCount.setText(NumberUtil.getIOrD(total)+"件");
            mTvNoPriceCount.setVisibility(View.VISIBLE);
        }
    }

    ProductBasicHelper productBasicHelper;
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_DETAIL://调拨单详情 从中获取商品列表
                mTransferDetailResponse = (TransferDetailResponse) result.getResult().getData();

                //对每个商品，设置批次信息，初始收货信息
                List<TransferDetailResponse.LinesBean> lines = mTransferDetailResponse.getLines();
                mBatchDataMap = new HashMap<>();
                for(TransferDetailResponse.LinesBean line:lines){

                    //TODO:测试
//                    TransferBatchLot lot = new TransferBatchLot();
//                    lot.setLotIDID("Z90909090");
//                    lot.setQuantQty(2);
//                    List<TransferBatchLot> lotList = new ArrayList<>();
//                    lotList.add(lot);
//                    TransferBatchLot lot2 = new TransferBatchLot();
//                    lot2.setLotIDID("Z90909091");
//                    lot2.setQuantQty(4);
//                    lotList.add(lot2);
//                    line.setProductLotInfo(lotList);

                    if(!line.isLotTracking()){
                        //不是批次商品
                        mTransferCountMap.put(line.getProductID()+"",line.getActualOutputNum());
                        continue;
                    }

                    mBatchDataMap.put(line.getProductID()+"",line);
                    //初始化接收信息，默认全部收到
                    double total = 0;
                    //usedQty表示实际发送的批次
                    //根据批次信息设置初始收货数量,过滤掉usedQty为0的,0为未发出批次
                    List<TransferBatchLot> actualTransferOutList = new ArrayList<>();
                    if(line.getProductLotInfo()!=null){
                        for(TransferBatchLot transferBatchLot:line.getProductLotInfo()){
                            if(transferBatchLot.getUsedQty()==0)continue;
                            transferBatchLot.setActualQty(transferBatchLot.getUsedQty());
                            total = total + transferBatchLot.getActualQty();
                            actualTransferOutList.add(transferBatchLot);
                        }
                    }
                    line.setProductLotInfo(actualTransferOutList);//重设批次信息,只保留有发出的批次
                    mTransferCountMap.put(line.getProductID()+"",total);//设置初始收货数量
                }

                //商品信息不全，还要在查
                if(!productBasicHelper.checkTransfer(mTransferDetailResponse.getLines())){
                    productBasicHelper.requestDetail(REQUEST_PRODUCT_INFO);
                    return;
                }else{
                    setupSummaryUI();
                    mProductAdapter.setProductList(mTransferDetailResponse.getLines());
                }
                break;
            case REQUEST_PRODUCT_INFO:
                if(productBasicHelper.onSuccess(result)){
                    setupSummaryUI();
                    mProductAdapter.setProductList(mTransferDetailResponse.getLines());
                }
                break;
            case REQUEST_TRANSFER_IN_ACTION:
                Intent intent = new Intent(this,TransferSuccessActivity.class);
                intent.putExtra(INTENT_KEY_TRANSFER_ID,mTransferEntity.getPickingID());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        Toast.makeText(this,errMsg,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置批次返回
        if(resultCode==RESULT_OK && requestCode==ACT_REQ_TRANSFER_IN_BATCH){
            TransferDetailResponse.LinesBean batchLine = data.getParcelableExtra(TransferInBatchActivity.INTENT_KEY_TRANSFER_BATCH);
            mBatchDataMap.put(batchLine.getProductID()+"",batchLine);
            //cal total transfer in
            double total = 0;
            for(TransferBatchLot lot:batchLine.getProductLotInfo()){
                total = total + lot.getActualQty();
            }
            mTransferCountMap.put(batchLine.getProductID()+"",total);

            setupSummaryUI();
            mProductAdapter.notifyDataSetChanged();
        }
    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transferin_set_count, null);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(dialogView);
//        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setAnimationStyle(R.style.MyPopwindow_anim_style);
    }

    private void showPopWindow(final TransferDetailResponse.LinesBean linesBean) {
        SimpleDraweeView productImage = (SimpleDraweeView) dialogView.findViewById(R.id.iv_transfer_in_product);
        TextView name = (TextView) dialogView.findViewById(R.id.tv_name);
        TextView content = (TextView) dialogView.findViewById(R.id.tv_content);
        TextView tvCount = (TextView) dialogView.findViewById(R.id.et_deliver_count);
        View vAdd = dialogView.findViewById(R.id.btn_transfer_in_add);
        View vSubtract = dialogView.findViewById(R.id.btn_transfer_in_subtract);
        final TextView tvActual = (TextView) dialogView.findViewById(R.id.et_product_count);
        View vSubmit = dialogView.findViewById(R.id.btn_confirm);
        FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(productImage, linesBean.getProductImage());
        name.setText(linesBean.getProductName());
        StringBuffer sb = new StringBuffer(linesBean.getProductCode());
        sb.append("  ").append(linesBean.getProductUnit());

        boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(linesBean.getProductID()+"");
        if (listBean != null && canSeePrice) {
            if (listBean.isTwoUnit()) {
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getSettlePrice()))).append("元/").append(listBean.getSettleUomId());
            } else {
                sb.append("\n").append(UserUtils.formatPrice(String.valueOf(listBean.getPrice()))).append("元/").append(listBean.getProductUom());
            }
        }
        content.setText(sb.toString());

        tvCount.setText(NumberUtil.getIOrD(linesBean.getActualOutputNum()));
        tvActual.setText(NumberUtil.getIOrD(mTransferCountMap.get(linesBean.getProductID()+"")));

        vSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTransferCountMap.put(linesBean.getProductID()+"",
                        new BigDecimal(tvActual.getText().toString()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                mProductAdapter.notifyDataSetChanged();
                mPopWindow.dismiss();
                setupSummaryUI();
            }
        });

        vAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double current = Double.valueOf(tvActual.getText().toString());
                if(current + 1 > linesBean.getActualOutputNum()){
                    Toast.makeText(TransferInActivity.this,"不能超过发货数量",Toast.LENGTH_LONG).show();
                    return;
                }
                current = current + 1;
                tvActual.setText(NumberUtil.getIOrD(current));

            }
        });

        vSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double current = Double.valueOf(tvActual.getText().toString());
                if(current - 1 < 0 ){
                    Toast.makeText(TransferInActivity.this,"不能小于0",Toast.LENGTH_LONG).show();
                    return;
                }
                current = current - 1;
                tvActual.setText(NumberUtil.getIOrD(current));
            }
        });

        tvActual.addTextChangedListener(new TextWatcher() {
            String previous;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previous = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    double value = new BigDecimal(s.toString()).setScale(2,RoundingMode.HALF_UP).doubleValue();
                    if(value > linesBean.getActualOutputNum()){
                        Toast.makeText(TransferInActivity.this,"不能超过发货数量",Toast.LENGTH_LONG).show();
                        tvActual.setText(previous);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPopWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.4f);
        dialogView.findViewById(R.id.iv_cancle).setOnClickListener(new View.OnClickListener() {
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

    private class TransferInProductAdapter extends RecyclerView.Adapter<TransferInProductAdapter.ViewHolder>{
        private Context context;
        private boolean isTwoUnit;           //双单位,有值就显示

        public void setTwoUnit(boolean twoUnit) {
            isTwoUnit = twoUnit;
        }

        private List<TransferDetailResponse.LinesBean> productList = new ArrayList();

        public TransferInProductAdapter(Context context) {
            this.context = context;
        }

        public void setProductList(List<TransferDetailResponse.LinesBean> productList) {
            this.productList.clear();
            if (productList != null && productList.size() > 0){
                this.productList.addAll(productList);
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer_in_product,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TransferDetailResponse.LinesBean bean = productList.get(position);
            final int pId = bean.getProductID();
            ViewHolder vh = holder;
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
            FrecoFactory.getInstance(context).displayWithoutHost(vh.productImage, bean.getProductImage());

            if (basicBean != null){
                vh.name.setText(bean.getProductName());
                StringBuffer sb = new StringBuffer(bean.getProductCode());
                sb.append(" | ").append(bean.getProductUnit()).append("\n");
                if(canSeePrice) sb.append("¥").append(basicBean.getPrice()).append("/").append(bean.getProductUom());
                vh.content.setText(sb.toString());

                vh.actualTransferIn.setText(NumberUtil.getIOrD(mTransferCountMap.get(bean.getProductID()+"")));
                vh.heightTransferIn.setText("/"+NumberUtil.getIOrD(bean.getActualOutputNum())+bean.getProductUom());
                vh.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //设置收货数量，分批次和非批次
                        if(mBatchDataMap==null){
                            return;
                        }
                        TransferDetailResponse.LinesBean transferBatchLine = mBatchDataMap.get(pId+"");
                        if(!bean.isLotTracking()){
                            //无批次信息，输入数量
                            showPopWindow(bean);
                            return;
                        }
                        Intent intent = new Intent(TransferInActivity.this, TransferInBatchActivity.class);
                        //intent.putExtra(TransferInBatchActivity.INTENT_KEY_PRODUCT,(Parcelable)bean);//传商品信息
                        intent.putExtra(TransferInBatchActivity.INTENT_KEY_TRANSFER_BATCH,(Parcelable) transferBatchLine);//传批次信息
                        startActivityForResult(intent,ACT_REQ_TRANSFER_IN_BATCH);
                    }
                });
            }else{
                vh.name.setText("");
            }

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public SimpleDraweeView productImage;
            public TextView name;
            public TextView content;
            public View rootView;
            public TextView actualTransferIn;//实际收到
            public TextView heightTransferIn;//预计收到
            public ViewHolder(View itemView) {
                super(itemView);
                rootView = itemView;
                productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
                name = (TextView) itemView.findViewById(R.id.name);
                content = (TextView)itemView.findViewById(R.id.content);
                actualTransferIn = (TextView)itemView.findViewById(R.id.tv_transfer_in_actual_num);
                heightTransferIn = (TextView)itemView.findViewById(R.id.tv_transfer_in_height);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("入库");
        MobclickAgent.onResume(this);          //统计时长
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("入库");
        MobclickAgent.onPause(this);          //统计时长
    }
}
