package com.runwise.supply.firstpage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.TransferInActivity;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.OrderState;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.orderpage.TempOrderManager;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.tools.SystemUpgradeHelper;
import com.runwise.supply.tools.TimeUtils;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY;
import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_VENDOR_DELIVERY;

/**
 * Created by libin on 2017/7/14.
 */

public class OrderAdapter extends IBaseAdapter {
    protected static final int TYPE_ORDER = 0;        //正常订单
    protected static final int TYPE_RETURN = 1;       //退货单
    static final int TYPE_TRANSFER = 2;                 //调拨单
    static final int TYPE_TEMP_ORDER = 3;//本地保存的提交中的订单
    static final int TYPE_INVENTORY = 4;//盘点缓存

    public static final int TRANS_ACTION_CANCEL = 0;
    public static final int TRANS_ACTION_OUTPUT_CONFIRM = 1;
    private String uid;

    public interface DoActionInterface {
        void doAction(OrderDoAction action, int postion);
        void doTransferAction(int type,TransferEntity transferEntity);
        void call(String phone);
        void resubmitOrder(TempOrderManager.TempOrder tempOrder);
        void gotoInventory(int inventoryId);
        void cancelInventory(InventoryCacheManager.InventoryBrief inventoryBrief);
    }

    private DoActionInterface callback;
    private Context context;
    private int returnCount;            //退货单数量
    private int orderCount;             //正常订单数量
    private DecimalFormat df = new DecimalFormat("#.##");

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public void setDoActionCallBack(DoActionInterface doActionCallBack) {
        this.callback = doActionCallBack;
    }

    //记录当前扩展打开的状态
    private HashMap<Integer, Boolean> expandMap = new HashMap<>();

    public OrderAdapter(Context context, DoActionInterface callback) {
        this.context = context;
        this.callback = callback;
        mFailedColor = context.getResources().getColor(R.color.colorffe7e0);
        mNormalColor = context.getResources().getColor(android.R.color.white);
        uid = GlobalApplication.getInstance().getUid();
    }

    @Override
    protected View getExView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int viewType = getItemViewType(position);
        if(viewType==TYPE_TEMP_ORDER)return getSubmittingOrderView(position,convertView,parent);
        if(viewType==TYPE_TRANSFER)return getTransferView(position,convertView,parent);
        if(viewType==TYPE_INVENTORY)return getInventoryView(position,convertView,parent);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.firstpage_order_item, null);
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
            if (!GlobalApplication.getInstance().getCanSeePrice()) {
                viewHolder.priceLL.setVisibility(View.GONE);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final LinearLayout tlLL = viewHolder.timelineLL;
        final ImageButton downArrow = viewHolder.arrowBtn;
        final RecyclerView recyclerView = viewHolder.recyclerView;
        if (getItemViewType(position) == TYPE_ORDER) {
            final OrderResponse.ListBean bean = (OrderResponse.ListBean) mList.get(position);
            //未读红点
            if(bean.isAsyncOrder() && !bean.isUserRead(uid)){
                viewHolder.ivUnread.setVisibility(View.VISIBLE);
            }else{
                viewHolder.ivUnread.setVisibility(View.GONE);
            }

            viewHolder.arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isExpand;
                    //更改boolean状态
                    if (expandMap.get(Integer.valueOf(bean.getOrderID())) != null) {
                        isExpand = expandMap.get(Integer.valueOf(bean.getOrderID())).booleanValue();
                        isExpand = !isExpand;
                    } else {
                        isExpand = true;
                    }
                    expandMap.put(Integer.valueOf(bean.getOrderID()), isExpand);
                    if (isExpand) {
                        //只有点击时，才去放timeline的内容
                        setTimeLineContent(bean.getStateTracker(), recyclerView);
                        tlLL.setVisibility(View.VISIBLE);
                        downArrow.setImageResource(R.drawable.login_btn_dropup);
                    } else {
                        tlLL.setVisibility(View.GONE);
                        downArrow.setImageResource(R.drawable.login_btn_dropdown);
                    }

                }
            });
            viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!SystemUpgradeHelper.getInstance(context).check(context))return;
                    //根据状态进行不同的逻辑处理
                    String doAction = ((TextView) v).getText().toString();
                    OrderDoAction action = OrderActionUtils.getDoActionByText(doAction, bean);
                    if (callback != null) {
                        callback.doAction(action, position);
                    }

                }
            });
            if (expandMap.get(Integer.valueOf(bean.getOrderID())) != null && expandMap.get(Integer.valueOf(bean.getOrderID())).booleanValue()) {
                viewHolder.timelineLL.setVisibility(View.VISIBLE);
                //重刷一次，免得重复
                downArrow.setImageResource(R.drawable.login_btn_dropup);
                setTimeLineContent(bean.getStateTracker(), recyclerView);
            } else {
                viewHolder.timelineLL.setVisibility(View.GONE);
                downArrow.setImageResource(R.drawable.login_btn_dropdown);
            }
            viewHolder.titleTv.setText(bean.getName());
            //派单前，派单后，用户收货后
            StringBuffer etSb = new StringBuffer();
            if (bean.getState().equals(OrderState.DRAFT.getName()) || bean.getState().equals(OrderState.SALE.getName())) {
                etSb.append("预计").append(formatTimeStr(bean.getEstimatedDate())).append("送达");
            } else if (bean.getState().equals(OrderState.PEISONG.getName())) {
                etSb.append("预计").append(formatTimeStr(bean.getEstimatedDate())).append("送达");
            } else {
                etSb.append(bean.getDoneDatetime()).append("已送达");
            }
            viewHolder.timeTv.setText(etSb.toString());
            viewHolder.stateTv.setText(OrderState.getValueByName(bean.getState()));
            viewHolder.stateTv.setTextColor(Color.parseColor("#333333"));
            if (bean.getWaybill() != null && bean.getWaybill() != null && bean.getWaybill().getDeliverVehicle() != null) {
                viewHolder.carNumTv.setText(bean.getWaybill().getDeliverVehicle().getLicensePlate());
                viewHolder.driverLL.setVisibility(View.VISIBLE);
            } else {
                viewHolder.carNumTv.setText("未指派");
                viewHolder.driverLL.setVisibility(View.GONE);
            }
            if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null) {
                viewHolder.senderTv.setText(bean.getWaybill().getDeliverUser().getName());
                viewHolder.senderTv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.senderTv.setText("未指派");
                viewHolder.senderTv.setVisibility(View.GONE);
            }
            viewHolder.callIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bean != null && bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null
                            && bean.getWaybill().getDeliverUser().getMobile() != null) {
                        callback.call(bean.getWaybill().getDeliverUser().getMobile());
                    } else {
                        callback.call(null);
                    }

                }
            });
            StringBuffer sb = new StringBuffer("共");
            if ("done".equals(bean.getState()) && bean.getDeliveredQty() != bean.getAmount()) {
                sb.append((int) bean.getDeliveredQty()).append("件商品");
            } else {
                sb.append((int) bean.getAmount()).append("件商品");
            }
            viewHolder.countTv.setText(sb.toString());
            viewHolder.moneyTv.setText(NumberUtil.getIOrD(bean.getAmountTotal()));
            StringBuffer drawableSb = new StringBuffer("state_restaurant_");
            drawableSb.append(bean.getState());
            if (getResIdByDrawableName(drawableSb.toString()) == 0) {
                viewHolder.imgIv.setImageResource(R.drawable.state_restaurant_draft);
            } else {
                viewHolder.imgIv.setImageResource(getResIdByDrawableName(drawableSb.toString()));
            }
            String doString = OrderActionUtils.getDoBtnTextByState(bean);
            if (!TextUtils.isEmpty(doString)) {
                if (doString.equals("已评价")) {
                    viewHolder.doBtn.setVisibility(View.INVISIBLE);
                } else {
                    viewHolder.doBtn.setVisibility(View.VISIBLE);
                    viewHolder.doBtn.setText(doString);
                }
            } else {
                viewHolder.doBtn.setVisibility(View.INVISIBLE);
            }
            if (bean.getHasReturn() != 0) {
                viewHolder.returnTv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.returnTv.setVisibility(View.GONE);
            }
            if ("done".equals(bean.getState()) && bean.getDeliveredQty() != bean.getAmount()) {
                viewHolder.realTv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.realTv.setVisibility(View.GONE);
            }
            if (bean.getHasAttachment() == 0 && bean.getOrderSettleName().contains("单次结算") && bean.getOrderSettleName().contains("先付款后收货") && bean.getState().equals(OrderState.DRAFT.getName())) {
                viewHolder.tvToPay.setVisibility(View.VISIBLE);
            }else{
                viewHolder.tvToPay.setVisibility(View.GONE);
            }
        } else {
            final ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) mList.get(position);
            //发货单
            viewHolder.returnTv.setVisibility(View.GONE);
            viewHolder.realTv.setVisibility(View.GONE);
            viewHolder.tvToPay.setVisibility(View.GONE);
            viewHolder.imgIv.setImageResource(R.drawable.more_restaurant_returnrecord);
            viewHolder.titleTv.setText(bean.getName());
            viewHolder.stateTv.setText("退货中");
            viewHolder.stateTv.setTextColor(Color.parseColor("#FA694D"));
            viewHolder.timeTv.setText(TimeUtils.getMMddHHmm(bean.getCreateDate()));
            StringBuffer sb = new StringBuffer("共");
            sb.append((int) bean.getAmount()).append("件商品");
            viewHolder.countTv.setText(sb.toString());
            viewHolder.moneyTv.setText(NumberUtil.getIOrD(bean.getAmountTotal()));
            viewHolder.doBtn.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(bean.getDriveMobile())) {
                viewHolder.carNumTv.setText(bean.getVehicle());
                viewHolder.carNumTv.setVisibility(View.VISIBLE);
                viewHolder.callIb.setVisibility(View.VISIBLE);
                viewHolder.driverLL.setVisibility(View.VISIBLE);
            } else {
                viewHolder.carNumTv.setText("未指派");
                viewHolder.carNumTv.setVisibility(View.GONE);
                viewHolder.callIb.setVisibility(View.GONE);
                viewHolder.driverLL.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(bean.getDriver())) {
                viewHolder.senderTv.setText(bean.getDriver());
            } else {
                viewHolder.senderTv.setText("未指派");
            }
            viewHolder.callIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(bean.getDriveMobile())) {
                        callback.call(bean.getDriveMobile());
                    } else {
                        callback.call(null);
                    }
                }
            });
            viewHolder.arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //更改boolean状态
                    Boolean isExpand = expandMap.get(Integer.valueOf(bean.getOrderID()));
                    if (isExpand != null) {
                        isExpand = !isExpand;
                    } else {
                        isExpand = true;
                    }
                    expandMap.put(Integer.valueOf(bean.getOrderID()), isExpand);
                    if (isExpand) {
                        //只有点击时，才去放timeline的内容
                        setTimeLineContent(bean.getStateTracker(), recyclerView);
                        tlLL.setVisibility(View.VISIBLE);
                        downArrow.setImageResource(R.drawable.login_btn_dropup);
                    } else {
                        tlLL.setVisibility(View.GONE);
                        downArrow.setImageResource(R.drawable.login_btn_dropdown);
                    }

                }
            });
            if (expandMap.get(Integer.valueOf(bean.getOrderID())) != null && expandMap.get(Integer.valueOf(bean.getOrderID())).booleanValue()) {
                viewHolder.timelineLL.setVisibility(View.VISIBLE);
                //重刷一次，免得重复
                downArrow.setImageResource(R.drawable.login_btn_dropup);
                setTimeLineContent(bean.getStateTracker(), recyclerView);
            } else {
                viewHolder.timelineLL.setVisibility(View.GONE);
                downArrow.setImageResource(R.drawable.login_btn_dropdown);
            }
            String deliveryType = bean.getDeliveryType();
            if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY) ||
                    deliveryType.equals(TYPE_VENDOR_DELIVERY)
                    || ((deliveryType.equals(TYPE_THIRD_PART_DELIVERY) || deliveryType.equals(TYPE_THIRD_PART_DELIVERY))
                    && bean.isReturnThirdPartLog())
                    ) {
                viewHolder.doBtn.setVisibility(View.VISIBLE);
                viewHolder.doBtn.setText("完成退货");
                viewHolder.doBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //根据状态进行不同的逻辑处理
                        if (callback != null) {
                            callback.doAction(OrderDoAction.FINISH_RETURN, position);
                        }

                    }
                });
            } else {
                viewHolder.doBtn.setVisibility(View.INVISIBLE);
            }

        }


        return convertView;
    }

    public class ViewHolder {
        @ViewInject(R.id.img)
        ImageView imgIv;
        @ViewInject(R.id.orderTimeTv)
        TextView titleTv;
        @ViewInject(R.id.orderNumTv)
        TextView timeTv;
        @ViewInject(R.id.countTv)
        TextView countTv;
        @ViewInject(R.id.moneyTv)
        TextView moneyTv;
        @ViewInject(R.id.stateTv)
        TextView stateTv;
        @ViewInject(R.id.doBtn)
        TextView doBtn;
        @ViewInject(R.id.arrowBtn)
        ImageButton arrowBtn;
        @ViewInject(R.id.carNumTv)
        TextView carNumTv;
        @ViewInject(R.id.senderTv)
        TextView senderTv;
        @ViewInject(R.id.timelineLL)
        LinearLayout timelineLL;
        @ViewInject(R.id.recyclerView)
        RecyclerView recyclerView;
        @ViewInject(R.id.priceLL)
        LinearLayout priceLL;
        @ViewInject(R.id.callIb)
        ImageButton callIb;
        @ViewInject(R.id.driverLL)
        LinearLayout driverLL;
        @ViewInject(R.id.returnTv)
        TextView returnTv;
        @ViewInject(R.id.realTv)
        TextView realTv;
        @ViewInject(R.id.tv_to_pay)
        TextView tvToPay;
        @ViewInject(R.id.iv_first_order_item_unread)
        ImageView ivUnread;
    }

    private void setTimeLineContent(List<String> stList, RecyclerView recyclerView) {
        TimeLineAdapter adapter = new TimeLineAdapter(context, stList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private int getResIdByDrawableName(String name) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        int resID = context.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return resID;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = mList.get(position);
        if (object instanceof OrderResponse.ListBean) {
            if(((OrderResponse.ListBean) object).isTransfer())return TYPE_TRANSFER;
            return TYPE_ORDER;
        }
        else if(object instanceof TempOrderManager.TempOrder){
            return TYPE_TEMP_ORDER;
        }
        else if(object instanceof InventoryCacheManager.InventoryBrief){
            return TYPE_INVENTORY;
        }
//        else if(object instanceof TransferEntity){
//            return TYPE_TRANSFER;
//        }
        return TYPE_RETURN;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdfTarget = new SimpleDateFormat("MM月dd日",Locale.getDefault());
    private String formatTimeStr(String str){
        try{
            return sdfTarget.format(sdfSource.parse(str));
        }catch (ParseException e){
            e.printStackTrace();
            return str;
        }
    }

    int mFailedColor;//提交中失败的背景色
    int mNormalColor;//提交中正常的颜色
    /**
     * 提交中的订单
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View getSubmittingOrderView(final int position, View convertView, ViewGroup parent){
        SubmittingOrderViewHolder viewHolder = null;
        if(convertView==null){
            convertView = View.inflate(context,R.layout.firstpage_order_temp_item,null);
            viewHolder = new SubmittingOrderViewHolder();
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (SubmittingOrderViewHolder) convertView.getTag();
        }
        TempOrderManager.TempOrder order = (TempOrderManager.TempOrder) mList.get(position);
        viewHolder.mmTvTitle.setText("预计"+formatTimeStr(order.getEstimateDate())+"送达");

        if(GlobalApplication.getInstance().getCanSeePrice()){
            viewHolder.mmTvPrice.setVisibility(View.VISIBLE);
            viewHolder.mmTvPrice.setText(UserUtils.formatPrice(String.valueOf(order.getTotalMoney())));
        }else{
            viewHolder.mmTvPrice.setVisibility(View.GONE);
        }
//        viewHolder.mmTvPrice.setText(UserUtils.formatPrice(String.valueOf(order.getTotalMoney())));
        viewHolder.mmTvPieces.setText("共"+order.getTotalPieces()+"件商品");

        //删除按钮
        if(order.isFailed()){
            viewHolder.mmIvClose.setVisibility(View.VISIBLE);
            viewHolder.mmTvStatus.setText("提交失败");
            viewHolder.mmTvStatus.setTextColor(context.getResources().getColor(R.color.colore64340));
            viewHolder.mmContainer.setBackgroundColor(mFailedColor);
            viewHolder.mmTvButton.setVisibility(View.VISIBLE);
            viewHolder.mmTvSubTitle.setText("--");
        }else{
            viewHolder.mmIvClose.setVisibility(View.INVISIBLE);
            viewHolder.mmTvStatus.setText("提交中");
            viewHolder.mmContainer.setBackgroundColor(mNormalColor);
            viewHolder.mmTvButton.setVisibility(View.GONE);
            viewHolder.mmTvStatus.setTextColor(context.getResources().getColor(R.color.color333333));
            viewHolder.mmTvSubTitle.setText("订单提交中");
        }

        //删除按钮
        viewHolder.mmIvClose.setOnClickListener(v->{
            TempOrderManager.getInstance(context).removeTempOrder(order);
            mList.remove(order);
            notifyDataSetChanged();
        });

        //重新提交按钮
        viewHolder.mmTvButton.setOnClickListener(v->{
            if(callback!=null)callback.resubmitOrder(order);
        });

        return convertView;
    }

    /**
     * 调拨item
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View getTransferView(final int position, View convertView, ViewGroup parent){
        TransferViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.firstpage_transfer_item, null);
            viewHolder = new TransferViewHolder();
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TransferViewHolder) convertView.getTag();
        }
        final TransferEntity transferEntity = (TransferEntity) mList.get(position);
        boolean isDest = GlobalApplication.getInstance().loadUserInfo().getMendian().equals(transferEntity.getLocationDestName());
        viewHolder.mmIvIcon.setImageResource(isDest?R.drawable.state_delivery_8_callin:R.drawable.state_delivery_8_callout);

        viewHolder.mmTvTitle.setText(transferEntity.getPickingName());
        viewHolder.mmTvCreateTime.setText(transferEntity.getDate());
        viewHolder.mmTvLocations.setText(transferEntity.getLocationName() + "\u2192" + transferEntity.getLocationDestName());
        if(GlobalApplication.getInstance().getCanSeePrice())viewHolder.mmTvPrice.setText(df.format(transferEntity.getTotalPrice()) + "元，" + transferEntity.getTotalNum() + "件商品");
        else viewHolder.mmTvPrice.setText(transferEntity.getTotalNum() + "件商品");
        viewHolder.mmTvAction.setVisibility(View.VISIBLE);

        viewHolder.mmTvStatus.setText(transferEntity.getPickingState());

        if(isDest){//入库方
            setTransferInViewHolder(viewHolder,transferEntity);
        }
        else{//出库方
            setTransferOutViewHolder(viewHolder,transferEntity,position);
        }
        return convertView;
    }

    /**
     * 盘点item
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View getInventoryView(final int position, View convertView, ViewGroup parent){
        InventoryViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.firstpage_inventory_item, null);
            viewHolder = new InventoryViewHolder();
            ViewUtils.inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InventoryViewHolder) convertView.getTag();
        }
        final InventoryCacheManager.InventoryBrief inventoryBrief = (InventoryCacheManager.InventoryBrief) mList.get(position);
        viewHolder.inventoryBrief = inventoryBrief;
        viewHolder.tvInventoryDate.setText("盘点日期："+ inventoryBrief.getCreateTime());
        viewHolder.tvInventoryId.setText(inventoryBrief.getName());
        viewHolder.tvInventoryPerson.setText(inventoryBrief.getCreateUser());
        viewHolder.tvInventoryCancel.setOnClickListener(viewHolder);
        viewHolder.cvRoot.setOnClickListener(viewHolder);
        return convertView;
    }


    /**
     * 入库方的列表项
     * @param viewHolder
     * @param transferEntity
     */
    private void setTransferInViewHolder(TransferViewHolder viewHolder,final TransferEntity transferEntity){
        switch (transferEntity.getPickingStateNum()){
            case TransferEntity.STATE_SUBMIT://已提交，可取消
                viewHolder.mmTvAction.setVisibility(View.GONE);
                viewHolder.mmTvCancel.setVisibility(View.VISIBLE);
                viewHolder.mmTvCancel.setText("取消");
                viewHolder.mmTvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback!=null)callback.doTransferAction(TRANS_ACTION_CANCEL,transferEntity);
                    }
                });
                break;
            case TransferEntity.STATE_OUT:
                viewHolder.mmTvCancel.setVisibility(View.VISIBLE);
                viewHolder.mmTvAction.setVisibility(View.VISIBLE);
                viewHolder.mmTvAction.setText("入库");
                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!SystemUpgradeHelper.getInstance(context).check(context))return;
                        Intent intent = new Intent(context, TransferInActivity.class);
                        intent.putExtra(TransferInActivity.INTENT_KEY_TRANSFER_ENTITY, transferEntity);
                        context.startActivity(intent);
                    }
                });
                viewHolder.mmTvCancel.setText("取消");
                viewHolder.mmTvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback!=null)callback.doTransferAction(TRANS_ACTION_CANCEL,transferEntity);
                    }
                });
                break;
            default:
                viewHolder.mmTvCancel.setVisibility(View.GONE);
                viewHolder.mmTvAction.setVisibility(View.GONE);
        }
    }

    /**
     * 出库方列表项
     * @param viewHolder
     * @param transferEntity
     * @param position
     */
    private void setTransferOutViewHolder(TransferViewHolder viewHolder,final TransferEntity transferEntity,final int position){
        switch (transferEntity.getPickingStateNum()){
            case TransferEntity.STATE_SUBMIT://已提交，可出库
                viewHolder.mmTvCancel.setVisibility(View.GONE);
                viewHolder.mmTvAction.setVisibility(View.VISIBLE);
                viewHolder.mmTvAction.setText("出库");
                //防止错位
                viewHolder.mmTvAction.setTag(position);
                viewHolder.mmTvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!SystemUpgradeHelper.getInstance(context).check(context))return;
                        int realPosition = (int) view.getTag();
                        if (realPosition == position) {
                            if(callback!=null){
                                callback.doTransferAction(TRANS_ACTION_OUTPUT_CONFIRM,transferEntity);
                            }
                            //变成可用状态
//                            requestOutputConfirm(transferEntity);
                        }
                    }
                });
                break;
            default:
                viewHolder.mmTvCancel.setVisibility(View.GONE);
                viewHolder.mmTvAction.setVisibility(View.GONE);
        }
    }

    private class TransferViewHolder {
        @ViewInject(R.id.iv_transfer_status)
        ImageView mmIvIcon;
        @ViewInject(R.id.item_transfer_title_tv)
        TextView mmTvTitle;
        @ViewInject(R.id.tv_item_transfer_status)
        TextView mmTvStatus;
        @ViewInject(R.id.tv_item_transfer_action)
        TextView mmTvAction;
        @ViewInject(R.id.tv_item_transfer_locations)
        TextView mmTvLocations;
        @ViewInject(R.id.tv_item_transfer_price)
        TextView mmTvPrice;
        @ViewInject(R.id.tv_item_transfer_date)
        TextView mmTvCreateTime;
        @ViewInject(R.id.tv_item_transfer_cancel)
        TextView mmTvCancel;
    }

    /**
     * 提交中订单
     */
    private class SubmittingOrderViewHolder{
        @ViewInject(R.id.tv_item_temp_title)
        TextView mmTvTitle;
        @ViewInject(R.id.tv_item_temp_sub_title)
        TextView mmTvSubTitle;
        @ViewInject(R.id.iv_first_order_item_remove)
        ImageView mmIvClose;
        @ViewInject(R.id.tv_item_temp_status)
        TextView mmTvStatus;
        @ViewInject(R.id.tv_item_temp_action)
        TextView mmTvButton;
        @ViewInject(R.id.tv_item_temp_pieces)
        TextView mmTvPieces;
        @ViewInject(R.id.tv_item_temp_price)
        TextView mmTvPrice;
        @ViewInject(R.id.iv_temp_status)
        ImageView mmIvIcon;
        @ViewInject(R.id.ll_item_temp_container)
        ViewGroup mmContainer;
    }

    /**
     * 盘点的viewholder
     */
    private class InventoryViewHolder implements View.OnClickListener{
        InventoryCacheManager.InventoryBrief inventoryBrief;
        @ViewInject(R.id.tv_item_inventory_date)
        TextView tvInventoryDate;
        @ViewInject(R.id.tv_item_inventory_person)
        TextView tvInventoryPerson;
        @ViewInject(R.id.tv_item_inventory_id)
        TextView tvInventoryId;
        @ViewInject(R.id.tv_item_inventory_cancel)
        TextView tvInventoryCancel;
        @ViewInject(R.id.cv_inventory_root)
        CardView cvRoot;

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_item_inventory_cancel:
                    //本地删除
                    if(callback!=null)callback.cancelInventory(inventoryBrief);
                    break;
                case R.id.cv_inventory_root:
                    if(callback!=null)callback.gotoInventory(inventoryBrief.getInventoryID());
                    break;
            }
        }
    }
}
