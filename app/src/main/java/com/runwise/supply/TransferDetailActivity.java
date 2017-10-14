package com.runwise.supply;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.entity.TransferDetailResponse;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.TransferInActivity.INTENT_KEY_TRANSFER_ENTITY;

/**
 * 调拨单详情页
 *
 * Created by Dong on 2017/10/12.
 */

public class TransferDetailActivity extends NetWorkActivity {

    public static final String EXTRA_TRANSFER_ENTITY = "extra_transfer";
    private static final int REQUEST_DETAIL = 0;
    private static final int REQUEST_CANCEL_TRANSFER = 1;

    @ViewInject(R.id.tv_transfer_detail_state)
    private TextView mTvTransferState;
    @ViewInject(R.id.tv_transfer_detail_state_tip)
    private TextView mTvTransferStateTip;
    @ViewInject(R.id.tv_transfer_detail_locations_value)
    private TextView mTvTransferLocations;
    @ViewInject(R.id.tv_transfer_detail_time_value)
    private TextView mTvCreateTime;
    @ViewInject(R.id.rv_transfer_products_detail)
    private RecyclerView mRvProducts;
    @ViewInject(R.id.tv_estimate_money)
    private TextView mTvEstimateMoney;
    @ViewInject(R.id.tv_count)
    private TextView mTvCount;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout mDtlDragLayout;
    @ViewInject(R.id.btn_transfer_detail_action)
    private Button mBtnDoAction;
    @ViewInject(R.id.btn_transfer_detail_action2)
    private Button mBtnDoAction2;

    private TransferEntity mTransferEntity;
    private TransferProductAdapter mTransferProductAdapter;
    private boolean isDestLocation;//是否是收货门店

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_detail);
        setTitleText(true,"调拨单详情");
        setTitleLeftIcon(true, R.drawable.nav_back);
        mTransferEntity = getIntent().getParcelableExtra(EXTRA_TRANSFER_ENTITY);
        mDtlDragLayout.setOverDrag(false);
        mRvProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mTransferProductAdapter = new TransferProductAdapter(this);
        mRvProducts.setAdapter(mTransferProductAdapter);
        //是否是接收门店
        isDestLocation = GlobalApplication.getInstance().loadUserInfo().getMendian().equals(mTransferEntity.getLocationDestName());
        initViews();
        requestData();
    }

    protected void initViews(){
        mTvTransferState.setText("调拨单"+mTransferEntity.getPickingState());
        //TODO:操作人。。。
        mTvTransferStateTip.setText("操作人");
        mTvTransferLocations.setText(mTransferEntity.getLocationName()+"\u2192"+mTransferEntity.getLocationDestName());
        mTvCreateTime.setText(mTransferEntity.getDate());
        mTvCount.setText(mTransferEntity.getTotalNum()+"件");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        mTvEstimateMoney.setText("¥"+decimalFormat.format(mTransferEntity.getTotalPrice()));
        initBottomBar();
    }

    //根据单据状态设置底部按钮
    protected void initBottomBar(){
        switch(mTransferEntity.getPickingState()){
            case TransferEntity.STATE_SUBMITTED://已提交，可以取消
                mBtnDoAction.setVisibility(View.GONE);
                break;
            case TransferEntity.STATE_DELIVER://已发出
                if(isDestLocation){//接收门店，可以取消和入库
                    mBtnDoAction.setText("入库");
                    mBtnDoAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(TransferDetailActivity.this,TransferInActivity.class);
                            intent.putExtra(INTENT_KEY_TRANSFER_ENTITY,mTransferEntity);
                            startActivity(intent);
                        }
                    });
                }else{
                    mBtnDoAction.setVisibility(View.GONE);
                }
                break;
            case TransferEntity.STATE_PENDING_DELIVER://待出库
                mBtnDoAction2.setVisibility(View.GONE);
                mBtnDoAction.setText("出库");
                mBtnDoAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(TransferOutActivity.getStartIntent(getActivityContext(),mTransferEntity));
                    }
                });
                break;
        }
    }

    @OnClick({R.id.btn_transfer_detail_state_more,R.id.btn_transfer_detail_action,R.id.btn_transfer_detail_action2})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.btn_transfer_detail_state_more://更多状态
                Intent intent = new Intent(this,TransferStateActivity.class);
                intent.putExtra(TransferStateActivity.INTENT_KEY_TRANSFER,mTransferEntity);
                startActivity(intent);
                break;
            case R.id.btn_transfer_detail_action2:
                //取消
                dialog.setTitle("提示");
                dialog.setMessage("确认取消订单?");
                dialog.setMessageGravity();
                dialog.setModel(CustomDialog.BOTH);
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        requestCancel();
                    }
                });
                dialog.show();
                break;
        }
    }

    /**
     * 请求订单详情，拿商品列表
     */
    private void requestData(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/"+mTransferEntity.getPickingID(),request,REQUEST_DETAIL,true, TransferDetailResponse.class);
    }

    /**
     * 取消调拨单
     */
    private void requestCancel(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/cancel/"+mTransferEntity.getPickingID(),REQUEST_CANCEL_TRANSFER,true,null);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_DETAIL:
                TransferDetailResponse transferDetailResponse = (TransferDetailResponse) result.getResult().getData();
                mTransferProductAdapter.setProductList(transferDetailResponse.getLines());
                break;
            case REQUEST_CANCEL_TRANSFER:
                finish();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        Toast.makeText(this,errMsg,Toast.LENGTH_LONG).show();
    }

    /**
     * 商品列表
     */
    private class TransferProductAdapter extends RecyclerView.Adapter<TransferProductAdapter.ViewHolder>{
        private static final int TYPE_FOOTER = 0;
        private static final int TYPE_ITEM = 1;
        private Context context;
        private boolean hasReturn;          //是否有退货，默认没有
        private boolean isTwoUnit;           //双单位,有值就显示
        //当前状态
        private String status;
        private String orderName;
        //private OrderResponse.ListBean mListBean;
        public void setHasReturn(boolean hasReturn) {
            this.hasReturn = hasReturn;
        }

        public void setTwoUnit(boolean twoUnit) {
            isTwoUnit = twoUnit;
        }

        private List<OrderResponse.ListBean.LinesBean> productList = new ArrayList();

        public TransferProductAdapter(Context context) {
            this.context = context;
        }

        public void setProductList(List<OrderResponse.ListBean.LinesBean> productList) {
            this.productList.clear();
            if (productList != null && productList.size() > 0){
                this.productList.addAll(productList);
            }
            notifyDataSetChanged();
        }

        public void setStatus(String orderName,String status,OrderResponse.ListBean listBean) {
            this.status = status;
            this.orderName = orderName;
            //mListBean = listBean;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==TYPE_ITEM){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetail_list_item,parent,false);
                ViewHolder vh = new ViewHolder(view);
                vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                return vh;
            }else{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_detail_bottom_layout,null);
                ViewHolder vh = new ViewHolder(view);
                return vh;
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(getItemViewType(position)==TYPE_FOOTER){boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
//                if (!canSeePrice) {
//                    view.findViewById(R.id.priceLL).setVisibility(View.GONE);
//                }
                //实收判断
//                if ((Constant.ORDER_STATE_DONE.equals(listBean.getState()) || Constant.ORDER_STATE_RATED.equals(listBean.getState())) && listBean.getDeliveredQty() != listBean.getAmount()) {
//                    ((TextView)view.findViewById(R.id.countTv)).setText((int) listBean.getDeliveredQty() + "件");
//                } else {
//                    ((TextView)view.findViewById(R.id.countTv)).setText((int) listBean.getAmount() + "件");
//                }
                //商品数量/预估金额
                DecimalFormat df = new DecimalFormat("#.##");
                holder.mmTvMoney.setText("¥" + df.format(mTransferEntity.getTotalPrice()));
                holder.mmTvNum.setText(mTransferEntity.getTotalNum()+"件");
            }
            final OrderResponse.ListBean.LinesBean bean = productList.get(position);
            int pId = bean.getProductID();
            ViewHolder vh = holder;
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
            if (basicBean != null && basicBean.getImage() != null){
                FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+basicBean.getImage().getImageSmall());
            }
            int puq = (int)bean.getProductUomQty();
            int dq = (int)bean.getDeliveredQty();
            if((Constant.ORDER_STATE_DONE.equals(status)||Constant.ORDER_STATE_RATED.equals(status)) && bean.getDeliveredQty() != bean.getProductUomQty()) {
                vh.oldPriceTv.setText("x"+puq);
                vh.nowPriceTv.setText("x"+dq);
                vh.oldPriceTv.setVisibility(View.VISIBLE);
            }
            else{
                vh.oldPriceTv.setVisibility(View.GONE);
                vh.nowPriceTv.setText("x"+puq);
            }

            if (basicBean != null){
                vh.name.setText(basicBean.getName());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                sb.append("  ").append(basicBean.getUnit());
                boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
                if (canSeePrice){
                    if (isTwoUnit){
                        sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getSettlePrice()))).append("元/").append(basicBean.getSettleUomId());
                    }else{
                        sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getPrice()))).append("元/").append(bean.getProductUom());
                    }
                }
                vh.unit1.setText(bean.getProductUom());
                vh.content.setText(sb.toString());
                if (isTwoUnit){
                    vh.weightTv.setText(bean.getSettleAmount()+basicBean.getSettleUomId());
                    vh.weightTv.setVisibility(View.VISIBLE);
                }else{
                    vh.weightTv.setVisibility(View.INVISIBLE);
                }
            }else{
                vh.name.setText("");
            }
            //发货状态订单
//        if("peisong".equals(status)) {
//            vh.rootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String deliveryType = mListBean.getDeliveryType();
//                    if (deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD)||deliveryType.equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
//                            ||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH)||deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)){
//                        if(!status.equals("peisong")&&!status.equals("done")&&!status.equals("rated")){
//                            return;
//                        }
//                    }
//                    if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY)||deliveryType.equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)){
//                        if((status.equals("done")||status.equals("rated"))&&(bean.getLotList()!=null&&bean.getLotList().size() == 0)) {
//                            ToastUtil.show(v.getContext(), "该产品无批次追踪");
//                            return;
//                        }
//                        if (status.equals(ORDER_STATE_PEISONG)||status.equals(ORDER_STATE_DRAFT)||status.equals(ORDER_STATE_SALE)){
//                            return;
//                        }
//                    }
//                    Intent intent = new Intent(context, LotListActivity.class);
//                    intent.putExtra("title",basicBean.getName());
//                    intent.putExtra("bean", (Parcelable) bean);
//                    context.startActivity(intent);
//                }
//            });
//        }

        }

        @Override
        public int getItemViewType(int position) {
            if(position==productList.size())return TYPE_FOOTER;
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return productList==null?0:productList.size()+1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public SimpleDraweeView productImage;
            public TextView name;
            public TextView content;
            public TextView oldPriceTv;
            public TextView nowPriceTv;
            public TextView weightTv;
            public TextView unit1;
            public View rootView;

            public TextView mmTvNum;
            public TextView mmTvMoney;
            public ViewHolder(View itemView) {
                super(itemView);
                rootView = itemView;
                productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
                name = (TextView) itemView.findViewById(R.id.name);
                content = (TextView)itemView.findViewById(R.id.content);
                oldPriceTv = (TextView)itemView.findViewById(R.id.oldPriceTv);
                nowPriceTv = (TextView)itemView.findViewById(R.id.nowPriceTv);
                weightTv = (TextView)itemView.findViewById(R.id.weightTv);
                unit1 = (TextView)itemView.findViewById(R.id.unit1);

                mmTvNum = (TextView)itemView.findViewById(R.id.tv_count);
                mmTvMoney = (TextView)itemView.findViewById(R.id.tv_estimate_money);
            }
        }
    }
}
