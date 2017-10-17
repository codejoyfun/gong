package com.runwise.supply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferBatchLot;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferInRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入库页面
 *
 * Created by Dong on 2017/10/13.
 */

public class TransferInActivity extends NetWorkActivity {
    private static final int REQUEST_DETAIL = 0;
    private static final int REQUEST_TRANSFER_BATCH = 1;
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
    private TransferInProductAdapter mProductAdapter;

    private TransferEntity mTransferEntity;

    private Map<String,TransferDetailResponse.LinesBean> mBatchDataMap;//商品ID对应批次对象
    private Map<String,Integer> mTransferInMap = new HashMap<>();//商品ID对应入库数量

    private View dialogView;
    private PopupWindow mPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_in);
        setTitleText(true,"入库");
        showBackBtn();
        initPopWindow();
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_ENTITY);
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
        request.setPicking_id(mTransferEntity.getPickingID());
        //商品数量
        List<TransferInRequest.ProductData> productDataList = new ArrayList<>();
        for(String productId:mTransferInMap.keySet()){
            int count = mTransferInMap.get(productId);
            TransferInRequest.ProductData productData = new TransferInRequest.ProductData();
            productData.setProduct_id(Integer.valueOf(productId));
            productData.setQty(count);
            productDataList.add(productData);
        }
        request.setProducts(productDataList);
        sendConnection("/gongfu/shop/transfer/receive",request, REQUEST_TRANSFER_IN_ACTION,true,null);
    }

    /**
     * 设置底部数据
     */
    private void setupSummaryUI(){
        if(mTransferInMap==null)return;
        int total = 0;
        for(String id:mTransferInMap.keySet()){
            total = total + mTransferInMap.get(id);
        }
        mTvTransferInCount.setText(""+total);
        double totalMoney = 0;
        for(String productId:mTransferInMap.keySet()){
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(this).get(productId);
            totalMoney = totalMoney + basicBean.getPrice() * mTransferInMap.get(productId);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        mTvTransferInMoney.setText(decimalFormat.format(totalMoney));
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_DETAIL://调拨单详情 从中获取商品列表
                TransferDetailResponse transferDetailResponse = (TransferDetailResponse) result.getResult().getData();
                mProductAdapter.setProductList(transferDetailResponse.getLines());

                //批次信息
                List<TransferDetailResponse.LinesBean> lines = transferDetailResponse.getLines();
                mBatchDataMap = new HashMap<>();
                for(TransferDetailResponse.LinesBean line:lines){
                    mBatchDataMap.put(line.getProductID()+"",line);

                    //初始化接收信息，默认全部收到
                    int total = 0;

                    //TODO:测试
//                    TransferBatchLot lot = new TransferBatchLot();
//                    lot.setLotID("Z90909090");
//                    lot.setQuantQty(2);
//                    List<TransferBatchLot> lotList = new ArrayList<>();
//                    lotList.add(lot);
//                    TransferBatchLot lot2 = new TransferBatchLot();
//                    lot2.setLotID("Z90909091");
//                    lot2.setQuantQty(4);
//                    lotList.add(lot2);
//                    line.setProductLotInfo(lotList);

                    if(line.getProductLotInfo()==null){
                        //没有批次信息
                        mTransferInMap.put(line.getProductID()+"",(int)line.getProductUomQty());
                        continue;
                    }

                    //usedQty表示实际发送的批次
                    //根据批次信息设置初始收货数量,过滤掉usedQty为0的
                    for(TransferBatchLot transferBatchLot:line.getProductLotInfo()){
                        transferBatchLot.setActualQty(transferBatchLot.getQuantQty());
                        total = total + transferBatchLot.getQuantQty();
                    }
                    mTransferInMap.put(line.getProductID()+"",total);
                }
                setupSummaryUI();
                break;
            case REQUEST_TRANSFER_IN_ACTION:
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where){
            case REQUEST_TRANSFER_BATCH://出货批次信息
                TransferDetailResponse.LinesBean line2 = new TransferDetailResponse.LinesBean();
                TransferBatchLot lot = new TransferBatchLot();
                lot.setLotID("Z90909090");
                lot.setQuantQty(2);
                line2.setProductID(656);
                List<TransferBatchLot> lotList = new ArrayList<>();
                lotList.add(lot);
                line2.setProductLotInfo(lotList);

                TransferBatchLot lot2 = new TransferBatchLot();
                lot2.setLotID("Z90909091");
                lot2.setQuantQty(4);
                lotList.add(lot2);

                List<TransferDetailResponse.LinesBean> lines = new ArrayList<>();
                lines.add(line2);

                //批次信息
                mBatchDataMap = new HashMap<>();
                for(TransferDetailResponse.LinesBean line:lines){
                    mBatchDataMap.put(line.getProductID()+"",line);

                    //初始化接收信息，默认全部收到
                    int total = 0;
                    for(TransferBatchLot transferBatchLot:line.getProductLotInfo()){
                        transferBatchLot.setActualQty(transferBatchLot.getQuantQty());
                        total = total + transferBatchLot.getQuantQty();
                    }
                    mTransferInMap.put(line.getProductID()+"",total);
                }
                requestDetail();
                break;
            case REQUEST_DETAIL://调拨单详情 从中获取商品列表
                OrderResponse.ListBean.LinesBean linesBean = new OrderResponse.ListBean.LinesBean();
                linesBean.setProductID(656);
                linesBean.setProductUom("袋");
                linesBean.setProductUomQty(2.3);
                List<OrderResponse.ListBean.LinesBean> list = new ArrayList<>();
                list.add(linesBean);

                mProductAdapter.setProductList(list);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //设置批次返回
        if(resultCode==RESULT_OK && requestCode==ACT_REQ_TRANSFER_IN_BATCH){
            TransferDetailResponse.LinesBean batchLine = data.getParcelableExtra(TransferInBatchActivity.INTENT_KEY_TRANSFER_BATCH);
            mBatchDataMap.put(batchLine.getProductID()+"",batchLine);
            //cal total transfer in
            int total = 0;
            for(TransferBatchLot lot:batchLine.getProductLotInfo()){
                total = total + lot.getActualQty();
            }
            mTransferInMap.put(batchLine.getProductID()+"",total);

            setupSummaryUI();
            mProductAdapter.notifyDataSetChanged();
        }
    }

    private void initPopWindow() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transferin_set_count, null);
        mPopWindow = new PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(dialogView);
        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
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
        ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(linesBean.getProductID());
        if (listBean != null) {
            FrecoFactory.getInstance(getActivityContext()).disPlay(productImage, Constant.BASE_URL + listBean.getImage().getImageSmall());
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
        tvCount.setText(String.valueOf(linesBean.getProductUomQty()));
        tvActual.setText(String.valueOf(mTransferInMap.get(linesBean.getProductID()+"")));

        vSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTransferInMap.put(linesBean.getProductID()+"", Integer.valueOf(tvActual.getText().toString()));
                mProductAdapter.notifyDataSetChanged();
            }
        });

        vAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.valueOf(tvActual.getText().toString());
                if(current + 1 > linesBean.getProductUomQty()){
                    Toast.makeText(TransferInActivity.this,"不能超过发货数量",Toast.LENGTH_LONG).show();
                    return;
                }
                current = current + 1;
                tvActual.setText(String.valueOf(current));

            }
        });

        vSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current = Integer.valueOf(tvActual.getText().toString());
                if(current - 1 < 0 ){
                    Toast.makeText(TransferInActivity.this,"不能小于0",Toast.LENGTH_LONG).show();
                    return;
                }
                current = current - 1;
                tvActual.setText(String.valueOf(current));
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

    private class TransferInProductAdapter extends RecyclerView.Adapter<TransferInProductAdapter.ViewHolder>{
        private Context context;
        private boolean isTwoUnit;           //双单位,有值就显示

        public void setTwoUnit(boolean twoUnit) {
            isTwoUnit = twoUnit;
        }

        private List<OrderResponse.ListBean.LinesBean> productList = new ArrayList();

        public TransferInProductAdapter(Context context) {
            this.context = context;
        }

        public void setProductList(List<? extends OrderResponse.ListBean.LinesBean> productList) {
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
            final OrderResponse.ListBean.LinesBean bean = productList.get(position);
            final int pId = bean.getProductID();
            ViewHolder vh = holder;
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
            if (basicBean != null && basicBean.getImage() != null){
                FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+basicBean.getImage().getImageSmall());
            }

            if (basicBean != null){
                vh.name.setText(basicBean.getName());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                sb.append("  ").append(basicBean.getUnit()).append("\n")
                        .append("¥").append(basicBean.getPrice()).append("/").append(basicBean.getUom());
                vh.content.setText(sb.toString());

                vh.actualTransferIn.setText(""+mTransferInMap.get(bean.getProductID()+""));
                vh.heightTransferIn.setText("/"+bean.getProductUomQty()+bean.getProductUom());
                vh.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳去批次
                        if(mBatchDataMap==null){
                            return;
                        }
                        TransferDetailResponse.LinesBean transferBatchLine = mBatchDataMap.get(pId+"");
                        if(transferBatchLine.getProductLotInfo()==null){
                            //无批次信息，输入数量
                            showPopWindow(transferBatchLine);
                            return;
                        }
                        Intent intent = new Intent(TransferInActivity.this, TransferInBatchActivity.class);
                        intent.putExtra(TransferInBatchActivity.INTENT_KEY_PRODUCT,(Parcelable)bean);//传商品信息
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
}
