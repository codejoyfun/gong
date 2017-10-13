package com.runwise.supply;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.entity.TransferBatchResponse;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.entity.TransferEntity;
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

    private Map<String,TransferBatchResponse.TransferBatchLine> mBatchDataMap;//商品ID对应批次
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

    /**
     * 获取出货列表,批次信息
     */
    private void requestData(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output/"+mTransferEntity.getPickingID(),REQUEST_TRANSFER_BATCH,true,TransferBatchResponse.class);
    }

    /**
     * 商品列表
     */
    private void requestProductList(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/"+mTransferEntity.getPickingID(),request,REQUEST_DETAIL,true, TransferDetailResponse.class);

    }

    /**
     * 设置底部数据
     */
    private void setupSummaryUI(){
        if(mTransferInMap==null)return;
        mTvTransferInCount.setText(mTransferInMap.size()+"");
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
                }
                requestProductList();
                break;
            case REQUEST_DETAIL://商品列表
                TransferDetailResponse transferDetailResponse = (TransferDetailResponse) result.getResult().getData();
                //初始化入库数据，默认为全部收到
                for(OrderResponse.ListBean.LinesBean linesBean:transferDetailResponse.getLines()){
                    mTransferInMap.put(linesBean.getProductID()+"",(int)linesBean.getProductUomQty());
                }
                mProductAdapter.setProductList(transferDetailResponse.getLines());
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

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

                vh.actualTransferIn.setText(mTransferInMap.get(bean.getProductID()+""));
                vh.heightTransferIn.setText("/"+bean.getProductUomQty()+bean.getProductUom());
                vh.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳去批次
                        if(mBatchDataMap==null)return;
                        TransferBatchResponse.TransferBatchLine transferBatchLine = mBatchDataMap.get(pId+"");
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
