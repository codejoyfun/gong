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
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.ProductBasicHelper;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.TransferInActivity.INTENT_KEY_TRANSFER_ENTITY;
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
    @ViewInject(R.id.rl_bottom)
    private View mLayoutBottomBar;
    @ViewInject(R.id.tv_transfer_state_date)
    private TextView mTvStateDate;
    @ViewInject(R.id.ll_price)
    private ViewGroup mVGPrice;

    private TransferEntity mTransferEntity;
    private TransferDetailResponse mTransferDetail;
    private TransferProductAdapter mTransferProductAdapter;
    private boolean isDestLocation;//是否是收货门店
    private ProductBasicHelper productBasicHelper;//用于检查商品信息
    private boolean canSeePrice = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_transfer_detail);
        showBackBtn();
        mTransferEntity = getIntent().getParcelableExtra(EXTRA_TRANSFER_ENTITY);
        mDtlDragLayout.setOverDrag(false);
        mRvProducts.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mTransferProductAdapter = new TransferProductAdapter(this);
        mRvProducts.setAdapter(mTransferProductAdapter);
        productBasicHelper = new ProductBasicHelper(this,netWorkHelper);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        showUI();
        requestData();
    }

    private void showUI(){
        if(mTransferEntity==null)return;
        //是否是接收门店
        isDestLocation = GlobalApplication.getInstance().loadUserInfo().getMendian().equals(mTransferEntity.getLocationDestName());
        setTitleText(true,"调拨单详情");
        initViews();
        setStatusText();
    }

    protected void initViews(){
        mTvTransferState.setText("调拨单"+mTransferEntity.getPickingState());
        mTvTransferLocations.setText(mTransferEntity.getLocationName()+"\u2192"+mTransferEntity.getLocationDestName());
        mTvCreateTime.setText(mTransferEntity.getDate());
        mTvCount.setText(mTransferEntity.getTotalNum()+"件");
        if(canSeePrice){
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            mTvEstimateMoney.setText("¥"+decimalFormat.format(mTransferEntity.getTotalPrice()));
        }else{
            mVGPrice.setVisibility(View.GONE);
        }
        mTvTransferId.setText(mTransferEntity.getPickingName());
        //调入方-》提交，待出库，已修改, 未confirm-》可以修改
        if(isDestLocation && mTransferEntity.getPickingStateNum()==TransferEntity.STATE_SUBMIT &&
                !mTransferEntity.isConfirmed()){
            setTitleRightText(true,"修改");
        }
        initBottomBar();
    }

    //根据单据状态设置底部按钮
    protected void initBottomBar(){
        //取消按钮
        if(!isDestLocation || mTransferEntity.getPickingStateNum()==TransferEntity.STATE_FINISH){
            //出库方或已完成状态不可取消
            mBtnDoAction2.setVisibility(View.GONE);
        }else{
            mBtnDoAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!SystemUpgradeHelper.getInstance(TransferDetailActivity.this).check(TransferDetailActivity.this))return;
                    showCancelDialog();
                }
            });
        }

        switch(mTransferEntity.getPickingStateNum()){
            case TransferEntity.STATE_OUT://已发出
                if(isDestLocation){//接收门店，可以取消和入库
                    mBtnDoAction.setText("入库");
                    mBtnDoAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!SystemUpgradeHelper.getInstance(TransferDetailActivity.this).check(TransferDetailActivity.this))return;
                            Intent intent = new Intent(TransferDetailActivity.this,TransferInActivity.class);
                            intent.putExtra(INTENT_KEY_TRANSFER_ENTITY,mTransferEntity);
                            startActivity(intent);
                        }
                    });
                }else{
                    mBtnDoAction.setVisibility(View.GONE);
                    mLayoutBottomBar.getLayoutParams().height=0;
                    mLayoutBottomBar.requestLayout();
                }
                break;
            case TransferEntity.STATE_SUBMIT://已提交
                if(!isDestLocation){//发出方
                    mBtnDoAction.setText("出库");
                    mBtnDoAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!SystemUpgradeHelper.getInstance(TransferDetailActivity.this).check(TransferDetailActivity.this))return;
                            requestOutputConfirm(mTransferEntity);
//                            mSelectTransferEntity = mTransferEntity;
//                            startActivity(TransferOutActivity.getStartIntent(getActivityContext(),mSelectTransferEntity));
                        }
                    });
                }else{//接收方
                    mBtnDoAction.setVisibility(View.GONE);
                }
                break;
            default:
                mBtnDoAction.setVisibility(View.GONE);
                mLayoutBottomBar.getLayoutParams().height=0;
                mLayoutBottomBar.requestLayout();
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
            case R.id.btn_transfer_detail_action2://取消
                if(!SystemUpgradeHelper.getInstance(this).check(this))return;
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
            case R.id.right_layout://修改
                if(!SystemUpgradeHelper.getInstance(this).check(this))return;
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
                if(productBasicHelper.checkTransfer(mTransferDetail.getLines())){//商品信息OK，显示
                    mTransferProductAdapter.setProductList(mTransferDetail.getLines());
                }
                else {//不齐全，查接口
                    productBasicHelper.requestDetail(PRODUCT_DETAIL);
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
                if(productBasicHelper.onSuccess(result)){
                mTransferProductAdapter.setProductList(mTransferDetail.getLines());
            }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where){
            case REQUEST_OUTPUT_CONFIRM:
                if(errMsg.contains("库存不足")){
                        dialog.setMessage("当前调拨商品库存不足，请重新盘点更新库存");
                        dialog.setMessageGravity();
                        dialog.setModel(CustomDialog.BOTH);
                        dialog.setRightBtnListener("查看库存", new CustomDialog.DialogListener() {
                            @Override
                            public void doClickButton(Button btn, CustomDialog dialog) {
                                //发送取消订单请求
                                Intent intent = new Intent(TransferDetailActivity.this,MainActivity.class);
                                intent.putExtra(MainActivity.INTENT_KEY_TAB,2);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                }
                return;
        }
//        Toast.makeText(this,errMsg,Toast.LENGTH_LONG).show();
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
            String productDetail;
            if(canSeePrice){
                productDetail = pieces[3];
            }else{
                String details[] = pieces[3].split(",");
                productDetail = details[0];
            }
            mTvStateDate.setText(pieces[0]);
            if(state.contains("提交")){//已提交
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨单号：").append(mTransferEntity.getPickingName()).append("；")
                        .append("调拨商品：").append(productDetail);
            }
            else if(state.contains("修改")){//已修改
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨商品：").append(productDetail);
            }
            else if(state.contains("发出")){//已发出
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨路径：").append(mTransferEntity.getLocationName()).append("\u2192")
                        .append(mTransferEntity.getLocationDestName());
            }else if(state.contains("完成")){//已完成
                sbContent.append("入库人：").append(pieces[4]).append("；")
                        .append("收货商品：").append(productDetail);
            }else{
                sbContent.append("操作人：").append(pieces[4]).append("；")
                        .append("调拨单号：").append(mTransferEntity.getPickingName()).append("；")
                        .append("调拨商品：").append(productDetail);
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

        private List<TransferDetailResponse.LinesBean> productList = new ArrayList();

        public TransferProductAdapter(Context context) {
            this.context = context;
        }

        public void setProductList(List<TransferDetailResponse.LinesBean> productList) {
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
                holder.mmTvNum.setText(mTransferEntity.getTotalNum()+"件");
                if(canSeePrice){
                    holder.mmTvMoney.setText("¥" + df.format(mTransferEntity.getTotalPrice()));
                }else {
                    holder.mmVgPrice.setVisibility(View.GONE);
                }
                return;
            }
            final TransferDetailResponse.LinesBean bean = productList.get(position);
            int pId = bean.getProductID();
            ViewHolder vh = holder;
//            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));
//            if (basicBean != null && basicBean.getImage() != null){
//                FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+basicBean.getImage().getImageSmall());
//            }
            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(context).get(String.valueOf(pId));

            FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL+bean.getProductImage());

            //根据调入方，调出方，调拨单状态设置
            //已完成情况下，入库数量和单据数量不一样,显示有删除线的订单数量
            switch (mTransferEntity.getPickingStateNum()){
                case TransferEntity.STATE_OUT:
                    //已出库情况下，出库与订单不一样, 展示删除的订单数量，和实际出库数量
                    if(bean.getActualOutputNum()!= bean.getProductUomQty()){
                        vh.oldPriceTv.setText("x"+bean.getProductUomQty());//订单数量
                        vh.oldPriceTv.setVisibility(View.VISIBLE);
                        vh.nowPriceTv.setText("x"+bean.getActualOutputNum());//实际出库
                    }else{
                        vh.oldPriceTv.setVisibility(View.GONE);
                        vh.nowPriceTv.setText("x"+bean.getActualOutputNum());
                    }
                    break;
                case TransferEntity.STATE_FINISH:
                    //已完成情况下，入库与订单不一样, 展示删除的订单数量，和实际入库数量
                    if(bean.getProductQtyDone()!= bean.getProductUomQty()){
                        vh.oldPriceTv.setText("x"+bean.getProductUomQty());//订单数量
                        vh.oldPriceTv.setVisibility(View.VISIBLE);
                        vh.nowPriceTv.setText("x"+bean.getProductQtyDone());//实际入库
                    }else{
                        vh.oldPriceTv.setVisibility(View.GONE);
                        vh.nowPriceTv.setText("x"+bean.getProductQtyDone());
                    }
                    break;
                case TransferEntity.STATE_SUBMIT:
                default:
                    vh.nowPriceTv.setText("x"+bean.getProductUomQty());//显示订单数量
                    vh.oldPriceTv.setVisibility(View.GONE);
                    break;
            }

            vh.name.setText(bean.getProductName());
            StringBuffer sb = new StringBuffer(bean.getProductCode());
            sb.append(" | ").append(bean.getProductUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice){
                if (isTwoUnit && basicBean!=null){
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getSettlePrice()))).append("元/").append(basicBean.getSettleUomId());
                }else{
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(basicBean.getPrice()))).append("元/").append(bean.getProductUom());
                }
            }
            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
            vh.weightTv.setVisibility(View.INVISIBLE);


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
            public ViewGroup mmVgPrice;
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
                mmVgPrice = (ViewGroup)itemView.findViewById(R.id.ll_price);
            }
        }
    }
}
