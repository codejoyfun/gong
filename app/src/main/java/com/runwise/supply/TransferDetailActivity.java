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
import com.runwise.supply.mine.TransferInModifyActivity;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.CreateCallInListRequest;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.TransferInActivity.INTENT_KEY_TRANSFER_ENTITY;
import static com.runwise.supply.entity.TransferEntity.STATE_INSUFFICIENT;
import static com.runwise.supply.mine.TransferInModifyActivity.INTENT_KEY_TRANSFER;
import static com.runwise.supply.orderpage.ProductActivity.INTENT_KEY_BACKAP;

/**
 * 调拨单详情页
 *
 * Created by Dong on 2017/10/12.
 */

public class TransferDetailActivity extends NetWorkActivity {

    public static final String EXTRA_TRANSFER_ENTITY = "extra_transfer";
    public static final String EXTRA_TRANSFER_ID = "extra_transfer_id";
    private static final int REQUEST_DETAIL = 0;
    private static final int REQUEST_CANCEL_TRANSFER = 1;
    private static final int REQUEST_OUTPUT_CONFIRM = 2;
    private static final int PRODUCT_DETAIL = 3;

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
    @ViewInject(R.id.tv_transfer_detail_id)
    private TextView mTvTransferId;
    @ViewInject(R.id.bottom_bar)
    private View mLayoutBottomBar;

    private TransferEntity mTransferEntity;
    private TransferDetailResponse mTransferDetail;
    private TransferProductAdapter mTransferProductAdapter;
    private boolean isDestLocation;//是否是收货门店

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_detail);
        showBackBtn();
        setTitleText(true,"调拨单详情");
        mTransferEntity = getIntent().getParcelableExtra(EXTRA_TRANSFER_ENTITY);
        mDtlDragLayout.setOverDrag(false);
        mRvProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mTransferProductAdapter = new TransferProductAdapter(this);
        mRvProducts.setAdapter(mTransferProductAdapter);
        showUI();
        requestData();
    }

    private void showUI(){
        if(mTransferEntity==null)return;
        //是否是接收门店
        isDestLocation = GlobalApplication.getInstance().loadUserInfo().getMendian().equals(mTransferEntity.getLocationDestName());
        initViews();
        setStatusText();
    }

    protected void initViews(){
        mTvTransferState.setText("调拨单"+mTransferEntity.getPickingState());
        mTvTransferLocations.setText(mTransferEntity.getLocationName()+"\u2192"+mTransferEntity.getLocationDestName());
        mTvCreateTime.setText(mTransferEntity.getDate());
        mTvCount.setText(mTransferEntity.getTotalNum()+"件");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        mTvEstimateMoney.setText("¥"+decimalFormat.format(mTransferEntity.getTotalPrice()));
        mTvTransferId.setText(mTransferEntity.getPickingName());
        //提交，待出库，已修改-》可以修改
        if(TransferEntity.STATE_SUBMITTED.equals(mTransferEntity.getPickingState())
                || TransferEntity.STATE_PENDING_DELIVER.equals(mTransferEntity.getPickingState())
                || TransferEntity.STATE_MODIFIED.equals(mTransferEntity.getPickingState())){
            setTitleRightText(true,"修改");
        }
        initBottomBar();
    }

    //根据单据状态设置底部按钮
    protected void initBottomBar(){
        switch(mTransferEntity.getPickingState()){
            case TransferEntity.STATE_DELIVER://已发出
            case TransferEntity.STATE_DELIVER2:
                mBtnDoAction2.setVisibility(View.GONE);
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
                    mLayoutBottomBar.setVisibility(View.INVISIBLE);
                }
                break;
            case TransferEntity.STATE_PENDING_DELIVER://待出库
            case TransferEntity.STATE_SUBMITTED:
            case TransferEntity.STATE_MODIFIED:
                if(!isDestLocation){//发出方
                    mBtnDoAction.setText("出库");
                    mBtnDoAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestOutputConfirm(mTransferEntity);
                        }
                    });
                    mBtnDoAction2.setVisibility(View.GONE);
                }else{//接收方
                    setTitleRightText(true,"修改");
                    mBtnDoAction.setVisibility(View.GONE);
                    mBtnDoAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showCancelDialog();
                        }
                    });
                }
                break;
            case STATE_INSUFFICIENT:
                mBtnDoAction.setVisibility(View.GONE);
                break;
            default:
                mBtnDoAction.setVisibility(View.GONE);
                mBtnDoAction2.setVisibility(View.GONE);
                mLayoutBottomBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    TransferEntity mSelectTransferEntity;
    boolean mInTheRequest  = false;
    private void requestOutputConfirm(TransferEntity transferEntity) {
        if(mInTheRequest){
            return;
        }
        mInTheRequest = true;
        mSelectTransferEntity = transferEntity;
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output_confirm/" + transferEntity.getPickingID(), request, REQUEST_OUTPUT_CONFIRM, true, null);
    }

    @OnClick({R.id.btn_transfer_detail_state_more,R.id.btn_transfer_detail_action,R.id.btn_transfer_detail_action2,R.id.right_layout})
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
            case R.id.right_layout:
                Intent intent1 = new Intent(this, TransferInModifyActivity.class);
                intent1.putExtra(INTENT_KEY_TRANSFER,mTransferEntity);
                ArrayList<AddedProduct> addedProducts = new ArrayList<AddedProduct>();
                for(TransferDetailResponse.LinesBean linesBean:mTransferDetail.getLines()){
                    addedProducts.add(new AddedProduct(linesBean.getProductID()+"",(int)linesBean.getProductUomQty()));
                }
                intent1.putParcelableArrayListExtra(INTENT_KEY_BACKAP,addedProducts);
                startActivity(intent1);
                break;
        }
    }

    /**
     * 请求订单详情，拿商品列表
     */
    private void requestData(){
        String pickingID = null;
        if(mTransferEntity!=null)pickingID = mTransferEntity.getPickingID();
        else pickingID = getIntent().getStringExtra(EXTRA_TRANSFER_ID);

        Object request = null;
        sendConnection("/gongfu/shop/transfer/"+pickingID,request,REQUEST_DETAIL,true, TransferDetailResponse.class);
    }

    /**
     * 取消调拨单
     */
    private void requestCancel(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/cancel/"+mTransferEntity.getPickingID(),REQUEST_CANCEL_TRANSFER,true,null);
    }

    Set<Integer> missingInfo;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case REQUEST_DETAIL:
                mTransferDetail = (TransferDetailResponse) result.getResult().getData();
                mTransferEntity = mTransferDetail.getInfo();
                if(mTransferDetail.getInfo().getStateTracker()==null || mTransferDetail.getInfo().getStateTracker().size()==0){
                    //TODO:test
                    List<String> list = new ArrayList<>();
                    list.add("2017-10-17 13:46 调拨单已提交 4.0袋,共4.0元 刘志滑");
                    mTransferDetail.getInfo().setStateTracker(list);
                }
                mTransferEntity.setStateTracker(mTransferDetail.getInfo().getStateTracker());
                showUI();
                //检查信息是否齐全
                missingInfo = ProductBasicUtils.check(this,mTransferDetail.getLines());
                if(missingInfo==null||missingInfo.size()==0){//商品信息OK，显示
                    mTransferProductAdapter.setProductList(mTransferDetail.getLines());
                }
                else {//不齐全，查接口
                    ProductBasicUtils.request(netWorkHelper,PRODUCT_DETAIL,missingInfo);
                }
                break;
            case REQUEST_CANCEL_TRANSFER:
                finish();
                break;
            case REQUEST_OUTPUT_CONFIRM:
                startActivity(TransferOutActivity.getStartIntent(getActivityContext(),mSelectTransferEntity));
                mInTheRequest = false;
                break;
            case PRODUCT_DETAIL:
                ProductOne productOne = (ProductOne) result.getResult().getData();
                ProductBasicList.ListBean listBean = productOne.getProduct();
                //保存进缓存
                missingInfo.remove(listBean.getProductID());
                ProductBasicUtils.getBasicMap(this).put(listBean.getProductID()+"",listBean);//更新内存缓存
                if(missingInfo.size()==0){//所有都返回了
                    mTransferProductAdapter.setProductList(mTransferDetail.getLines());
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        Toast.makeText(this,errMsg,Toast.LENGTH_LONG).show();
    }

    /**
     * 拿statetracker最新一条设置状态textview
     */
    private void setStatusText(){
        //拿statetracker最新一条
        if(mTransferEntity.getStateTracker()!=null){
            List<String> stateList = mTransferEntity.getStateTracker();
            String latestState = stateList.get(0);
            String[] pieces = latestState.split(" ");
            String state = pieces[2];
            StringBuilder sbContent = new StringBuilder();
            if(state.contains(TransferEntity.STATE_SUBMITTED)){//已提交
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨单号：").append(mTransferEntity.getPickingName()).append("；")
                        .append("调拨商品：").append(pieces[3]);
            }
            else if(state.contains(TransferEntity.STATE_MODIFIED)){//已修改
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨商品：").append(pieces[3]);
            }
            else if(state.contains(TransferEntity.STATE_DELIVER)){//已发出
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨路径：").append(mTransferEntity.getLocationName()).append("\u2192")
                        .append(mTransferEntity.getLocationDestName());
            }else if(state.contains(TransferEntity.STATE_COMPLETE)){//已完成
                sbContent.append("入库人：").append(pieces[4]).append("；")
                        .append("收货商品：").append(pieces[3]);
            }else{
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨单号：").append(mTransferEntity.getPickingName()).append("；")
                        .append("调拨商品：").append(pieces[3]);
            }
            mTvTransferStateTip.setText(sbContent.toString());
        }
    }

    private void showCancelDialog(){
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

        public void setProductList(List<? extends OrderResponse.ListBean.LinesBean> productList) {
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
            if(getItemViewType(position)==TYPE_FOOTER){
//                boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
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
                if(mTransferEntity==null)return;
                DecimalFormat df = new DecimalFormat("#.##");
                holder.mmTvMoney.setText("¥" + df.format(mTransferEntity.getTotalPrice()));
                holder.mmTvNum.setText(mTransferEntity.getTotalNum()+"件");
                return;
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
