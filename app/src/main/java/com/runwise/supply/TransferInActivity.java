package com.runwise.supply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferBatchResponse;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferInRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;

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

    private Map<String,TransferBatchResponse.TransferBatchLine> mBatchDataMap;//商品ID对应批次对象
    private Map<String,Integer> mTransferInMap = new HashMap<>();//商品ID对应入库数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_in);
        setTitleText(true,"入库");
        setTitleLeftIcon(true, R.drawable.nav_back);
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER_ENTITY);
        initViews();
        requestData();
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
     * 获取出货列表,批次信息
     */
    private void requestData(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output/"+mTransferEntity.getPickingID(),REQUEST_TRANSFER_BATCH,true,TransferBatchResponse.class);
    }

    /**
     * 从调拨单详情 获取商品列表
     */
    private void requestProductList(){
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
        DecimalFormat decimalFormat = new DecimalFormat("#.$$");
        mTvTransferInMoney.setText("¥"+decimalFormat.format(totalMoney));
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_TRANSFER_BATCH://出货批次信息
                TransferBatchResponse transferBatchResponse = (TransferBatchResponse) result.getResult().getData();
                //批次信息
                List<TransferBatchResponse.TransferBatchLine> lines = transferBatchResponse.getLines();
                mBatchDataMap = new HashMap<>();
                for(TransferBatchResponse.TransferBatchLine line:lines){
                    mBatchDataMap.put(line.getProductID(),line);

                    //初始化接收信息，默认全部收到
                    int total = 0;
                    for(TransferBatchResponse.TransferBatchLot transferBatchLot:line.getProductInfo()){
                        transferBatchLot.setActualQty(transferBatchLot.getQuantQty());
                        total = total + transferBatchLot.getQuantQty();
                    }
                    mTransferInMap.put(line.getProductID(),total);
                }
                setupSummaryUI();
                requestProductList();
                break;
            case REQUEST_DETAIL://调拨单详情 从中获取商品列表
                TransferDetailResponse transferDetailResponse = (TransferDetailResponse) result.getResult().getData();
                mProductAdapter.setProductList(transferDetailResponse.getLines());
                break;
            case REQUEST_TRANSFER_IN_ACTION:
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where){
            case REQUEST_TRANSFER_BATCH://出货批次信息
                TransferBatchResponse.TransferBatchLine line2 = new TransferBatchResponse.TransferBatchLine();
                TransferBatchResponse.TransferBatchLot lot = new TransferBatchResponse.TransferBatchLot();
                lot.setLotID("Z90909090");
                lot.setQuantQty(2);
                line2.setProductID("656");
                List<TransferBatchResponse.TransferBatchLot> lotList = new ArrayList<>();
                lotList.add(lot);
                line2.setProductInfo(lotList);

                TransferBatchResponse.TransferBatchLot lot2 = new TransferBatchResponse.TransferBatchLot();
                lot2.setLotID("Z90909090");
                lot2.setQuantQty(4);
                lotList.add(lot2);

                List<TransferBatchResponse.TransferBatchLine> lines = new ArrayList<>();
                lines.add(line2);

                //批次信息
                mBatchDataMap = new HashMap<>();
                for(TransferBatchResponse.TransferBatchLine line:lines){
                    mBatchDataMap.put(line.getProductID(),line);

                    //初始化接收信息，默认全部收到
                    int total = 0;
                    for(TransferBatchResponse.TransferBatchLot transferBatchLot:line.getProductInfo()){
                        transferBatchLot.setActualQty(transferBatchLot.getQuantQty());
                        total = total + transferBatchLot.getQuantQty();
                    }
                    mTransferInMap.put(line.getProductID(),total);
                }
                requestProductList();
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
            TransferBatchResponse.TransferBatchLine batchLine = data.getParcelableExtra(TransferInBatchActivity.INTENT_KEY_TRANSFER_BATCH);
            mBatchDataMap.put(batchLine.getProductID(),batchLine);
            //cal total transfer in
            int total = 0;
            for(TransferBatchResponse.TransferBatchLot lot:batchLine.getProductInfo()){
                total = total + lot.getActualQty();
            }
            mTransferInMap.put(batchLine.getProductID(),total);

            setupSummaryUI();
            mProductAdapter.notifyDataSetChanged();
        }
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

        public void setProductList(List<OrderResponse.ListBean.LinesBean> productList) {
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
                        if(mBatchDataMap==null)return;
                        TransferBatchResponse.TransferBatchLine transferBatchLine = mBatchDataMap.get(pId+"");
                        Intent intent = new Intent(TransferInActivity.this, TransferInBatchActivity.class);
                        intent.putExtra(TransferInBatchActivity.INTENT_KEY_PRODUCT,(Parcelable)bean);//传商品信息
                        intent.putExtra(TransferInBatchActivity.INTENT_KEY_TRANSFER_BATCH,transferBatchLine);//传批次信息
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
