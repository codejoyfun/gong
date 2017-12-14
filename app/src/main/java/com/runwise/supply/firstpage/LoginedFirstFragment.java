package com.runwise.supply.firstpage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.bean.SystemUpgradeNoticeEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.TransferDetailActivity;
import com.runwise.supply.business.BannerHolderView;
import com.runwise.supply.business.entity.CheckOrderResponse;
import com.runwise.supply.business.entity.FirstPageInventoryResult;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.entity.CheckOrderSuccessRequest;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.event.OrderStatusChangeEvent;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.DashBoardResponse;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.LunboRequest;
import com.runwise.supply.firstpage.entity.LunboResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.mine.ProcurementLimitActivity;
import com.runwise.supply.mine.entity.ChannelPandian;
import com.runwise.supply.mine.entity.SumMoneyData;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TempOrderManager;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.repertory.EditCountDialog;
import com.runwise.supply.repertory.InventoryActivity;
import com.runwise.supply.tools.InventoryCacheManager;
import com.runwise.supply.tools.SystemUpgradeHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.runwise.supply.R.id.lqLL;
import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ID;
import static com.runwise.supply.firstpage.OrderAdapter.TRANS_ACTION_CANCEL;
import static com.runwise.supply.firstpage.OrderAdapter.TRANS_ACTION_OUTPUT_CONFIRM;
import static com.runwise.supply.firstpage.OrderAdapter.TYPE_TEMP_ORDER;
import static com.runwise.supply.firstpage.OrderAdapter.TYPE_TRANSFER;
import static com.runwise.supply.firstpage.ReturnSuccessActivity.INTENT_KEY_RESULTBEAN;
import static com.runwise.supply.mine.ProcurementLimitActivity.KEY_SUM_MONEY_DATA;
import static com.runwise.supply.repertory.InventoryActivity.INTENT_KEY_INVENTORY_BEAN;

/**
 * Created by libin on 2017/7/13.
 */

public class LoginedFirstFragment extends NetWorkFragment implements OrderAdapter.DoActionInterface {
    private static final int FROMORDER = 0;
    private static final int FROMLB = 1;
    private static final int FROMDB = 2;
    private static final int CANCEL = 3;        //取消订单
    private static final int FROMRETURN = 4;
    private static final int FINISHRETURN = 5;
    private static final int REQUEST_SUM = 6;
//    private static final int REQUEST_TRANSFER_IN = 7;
//    private static final int REQUEST_TRANSFER_OUT = 8;
    private static final int REQUEST_SUBMITTING_ORDER = 7;
    private static final int REQUEST_CANCEL_TRANSFER = 9;
    private static final int REQUEST_OUTPUT_CONFIRM = 10;
    private static final int REQUEST_READ = 11;
    private static final int REQUEST_CANCEL_INVENTORY = 12;
    private static final int REQUEST_INVENTORY_LIST = 13;

    long mTimeStartFROMORDER;
    long mTimeStartFROMLB;
    long mTimeStartFROMDB;
    long mTimeStartFROMRETURN;
    long mTimeStartREQUEST_SUM;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.rl_title)
    private RelativeLayout rl_title;
    @ViewInject(R.id.iv_call)
    private ImageView mIvCallBtn;

    private LayoutInflater layoutInflater;
    private ConvenientBanner banner;
    private OrderAdapter adapter;
    private TextView lastWeekKey;
    private TextView lastWeekBuy;
    private TextView lastMonthBuy;
    private TextView unPayAccount;
    private TextView unPayMoney;
    private TextView lqCountTv;
    private TextView dqCountTv;
    private List orderList = new ArrayList<>();
    private View rootView;
    private String number = "02037574563";
    private UserInfo userInfo;

    private boolean returnRequesting = false;//标记是否在查询退货单
    private boolean orderRequesting = false;//标记是否在查询订单
    private boolean submitRequesting = false;//标记是否在查询提交中的订单
    private boolean inventoryRequesting = false;//是否在查询盘点列表中
    private List<TempOrderManager.TempOrder> mTempOrders;//本地保存的提交中的订单的数据

    public LoginedFirstFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(mContext);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        final View headView = LayoutInflater.from(getContext()).inflate(R.layout.logined_head_layout, null);
        //表头：放轮播+统计表
//        View headView = layoutInflater.inflate(R.layout.logined_head_layout,null);
        lastWeekKey = (TextView) headView.findViewById(R.id.lastWeekKey);
        lastWeekBuy = (TextView) headView.findViewById(R.id.lastWeekBuy);
        lastMonthBuy = (TextView) headView.findViewById(R.id.lastMonthBuy);
        unPayAccount = (TextView) headView.findViewById(R.id.unPayAccount);
        unPayMoney = (TextView) headView.findViewById(R.id.unPayAccount);
        lqCountTv = (TextView) headView.findViewById(R.id.lqCountTv);
        dqCountTv = (TextView) headView.findViewById(R.id.dqCountTv);
        loadingLayout = (LoadingLayout)headView.findViewById(R.id.loadingLayout);
        banner = (ConvenientBanner) headView.findViewById(R.id.ConvenientBanner);
        headView.findViewById(R.id.lqLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                ma.gotoTabByIndex(2);
            }
        });
        headView.findViewById(R.id.dqLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity ma = (MainActivity) getActivity();
                ma.gotoTabByIndex(2);
            }
        });

        headView.findViewById(R.id.ll_procurement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSumMoneyData == null) {
                    return;
                }
                if (SPUtils.isLogin(getActivity())) {
                    Intent intent = new Intent(mContext, ProcurementLimitActivity.class);
                    intent.putExtra(KEY_SUM_MONEY_DATA, mSumMoneyData);
                    startActivity(intent);
                }
            }
        });


        //通过图片比例，计算banner大小 375:175 = w:x
        int height = 175 * CommonUtils.getScreenWidth(mContext) / 375;
        banner.getLayoutParams().height = height;

        pullListView.getRefreshableView().addHeaderView(headView);
        adapter = new OrderAdapter(mContext, this);
        pullListView.setAdapter(adapter);
        pullListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int[] location = new int[2];
                headView.getLocationOnScreen(location);
                float top = location[1];
                if (firstVisibleItem > 1) {
                    rl_title.setBackgroundResource(R.color.white);
                    rl_title.setAlpha(1);
                    mIvCallBtn.setAlpha(0f);
                    return;
                }
                if (top < 0) {
                    top = -top;
                    if (top >= headView.getHeight() / 3) {
                        rl_title.setBackgroundResource(R.color.white);
                        rl_title.setAlpha(1);
                        mIvCallBtn.setAlpha(0f);
                        return;
                    }
                    float ratio = top / (float) headView.getHeight() * 3;
                    rl_title.setBackgroundResource(R.color.white);
                    rl_title.setAlpha(ratio);
                    mIvCallBtn.setAlpha(1-ratio);
                } else {
                    rl_title.setAlpha(0);
                    mIvCallBtn.setAlpha(1f);
                }
            }
        });
        pullListView.scrollTo(0, 0);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //根据点击的position，确定是退货还是正常订单
                int realPosition = (int) l;
                if (adapter.getItemViewType(realPosition) == adapter.TYPE_ORDER) {
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    OrderResponse.ListBean bean = (OrderResponse.ListBean) adapter.getList().get(realPosition);
                    setOrderRead(bean);
                    bundle.putParcelable("order", bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (adapter.getItemViewType(realPosition) == adapter.TYPE_RETURN) {
                    Intent intent = new Intent(mContext, ReturnDetailActivity.class);
                    ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) adapter.getList().get(realPosition);
                    intent.putExtra("rid", bean.getReturnOrderID() + "");
                    startActivity(intent);
                } else if(adapter.getItemViewType(realPosition) == TYPE_TRANSFER){
                    TransferEntity transferEntity = (TransferEntity) adapter.getList().get(realPosition);
                    Intent intent = new Intent(getActivity(), TransferDetailActivity.class);
                    intent.putExtra(EXTRA_TRANSFER_ID, transferEntity.getPickingID());
                    startActivity(intent);
                } else if(adapter.getItemViewType(realPosition) == TYPE_TEMP_ORDER){
                    TempOrderManager.TempOrder tempOrder = (TempOrderManager.TempOrder)adapter.getList().get(realPosition);
                    TempOrderActivity.start(getActivity(),tempOrder);
                }
            }
        });
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新:只刷新列表内容
                requestReturnList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        requestDashBoard();
        requestLB();
        getProcurement();
        //加载电话
        userInfo = GlobalApplication.getInstance().loadUserInfo();
        if(SystemUpgradeHelper.getInstance(getActivity()).needShowNotice(LoginedFirstFragment.class.getName()))showSystemUpgradeNotice();
    }

    public void getProcurement() {
        Object request = null;
        sendConnection("/api/sale/shop/info", request, REQUEST_SUM, false, SumMoneyData.class);
        mTimeStartREQUEST_SUM = System.currentTimeMillis();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            requestReturnList();
//            PollingUtil.getInstance().requestOrder(netWorkHelper, FROMRETURN);
        } else {
//            PollingUtil.getInstance().stopRequestOrder();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            requestReturnList();
            mTimeStartFROMRETURN = System.currentTimeMillis();
        } else {
//            PollingUtil.getInstance().stopRequestOrder();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        PollingUtil.getInstance().stopRequestOrder();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_logined_first;
    }

    LoadingLayout loadingLayout;
    SumMoneyData mSumMoneyData;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case FROMORDER:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderResponse response = (OrderResponse) resultBean.getData();
                orderList.addAll(orderList.size(),response.getList());//加到后边
                adapter.setOrderCount(response.getList().size());
                //adapter.setData(orderList);//notify adapter
                orderRequesting = false;
                checkSuccess();
//                LogUtils.e("onSuccessTime FROMORDER "+String.valueOf(System.currentTimeMillis() - mTimeStartFROMORDER));
                break;
            case FROMLB:
                LunboResponse lRes = (LunboResponse) result.getResult();
                updateLb(lRes.getPost_list());
//                LogUtils.e("onSuccessTime FROMLB "+String.valueOf(System.currentTimeMillis() - mTimeStartFROMLB));
                break;
            case FROMDB:
                BaseEntity.ResultBean resultBean2 = result.getResult();
                DashBoardResponse dbResponse = (DashBoardResponse) resultBean2.getData();
                updateDashBoard(dbResponse);
//                LogUtils.e("onSuccessTime FROMDB "+String.valueOf(System.currentTimeMillis() - mTimeStartFROMDB));
                break;
            case CANCEL:
                ToastUtil.show(mContext, "取消成功");
                requestReturnList();
                break;
            case FROMRETURN:
                BaseEntity.ResultBean resultBean4 = result.getResult();
                ReturnOrderBean rob = (ReturnOrderBean) resultBean4.getData();
                orderList.addAll(0,rob.getList());//加在前边
                adapter.setReturnCount(rob.getList().size());
                //adapter.setData(orderList);
                returnRequesting = false;
                checkSuccess();
                cachReturnList(rob.getList());
//                Object request = null;
//                sendConnection("/gongfu/v2/order/undone_orders/", request, FROMORDER, false, OrderResponse.class);

//                mTimeStartFROMORDER = System.currentTimeMillis();
//                LogUtils.e("onSuccessTime FROMRETURN "+String.valueOf(System.currentTimeMillis() - mTimeStartFROMRETURN));
                break;
            case FINISHRETURN:
                FinishReturnResponse finishReturnResponse = (FinishReturnResponse) result.getResult().getData();
                ToastUtil.show(mContext, "退货成功");
                Intent intent = new Intent(getActivity(), ReturnSuccessActivity.class);
                intent.putExtra(INTENT_KEY_RESULTBEAN, finishReturnResponse);
                startActivity(intent);
//                requestReturnList();
                break;
            case REQUEST_SUM:
                mSumMoneyData = (SumMoneyData) result.getResult().getData();
//                LogUtils.e("onSuccessTime REQUEST_SUM "+(System.currentTimeMillis() - mTimeStartREQUEST_SUM));
                break;
            case REQUEST_CANCEL_TRANSFER:
                ToastUtil.show(mContext, "取消成功");
                requestReturnList();
                break;
            case REQUEST_OUTPUT_CONFIRM:
                startActivity(TransferOutActivity.getStartIntent(getActivity(),mSelectTransferEntity));
                mInTheRequest = false;
                break;
            case REQUEST_SUBMITTING_ORDER:
                CheckOrderResponse checkOrderResponse = (CheckOrderResponse) result.getResult().getData();
                if(checkOrderResponse.getOrderingList()!=null){
                    Iterator<TempOrderManager.TempOrder> iterator = mTempOrders.iterator();
                    while(iterator.hasNext()){
                        TempOrderManager.TempOrder tempOrder = iterator.next();
                        for(CheckOrderResponse.OrderingBean orderingBean:checkOrderResponse.getOrderingList()){
                            if(tempOrder.getHashKey().equals(orderingBean.getHash()) && "A0006".equals(orderingBean.getState())){
                                iterator.remove();
                                TempOrderManager.getInstance(getActivity()).removeTempOrder(tempOrder);
                            }
                        }
                    }
                }
                submitRequesting = false;
                checkSuccess();
                break;
            case REQUEST_CANCEL_INVENTORY:
                //本地删除，刷新页面
                InventoryCacheManager.getInstance(getActivity()).removeInventory(mCancelInventory.getInventoryID());
                adapter.getList().remove(mCancelInventory);
                adapter.notifyDataSetChanged();
                break;
            case REQUEST_INVENTORY_LIST:
                FirstPageInventoryResult inventoryResult = (FirstPageInventoryResult) result.getResult().getData();
                if(inventoryResult.getList()!=null && inventoryResult.getList().size()>0){
                    InventoryResponse.InventoryBean inventoryBean = inventoryResult.getList().get(0);
                    //有确认中的盘点单，则显示
                    if("confirm".equals(inventoryBean.getState())) inventoryList.add(inventoryBean);
                }
                inventoryRequesting = false;
                checkSuccess();
                break;
        }
    }

    /**
     * 检查是否订单，退货单，提交中订单三个接口全部返回，是则更新界面
     */
    private void checkSuccess(){
        if(!orderRequesting && !returnRequesting && !submitRequesting && !inventoryRequesting){
            orderList.addAll(0,inventoryList);
            if(mTempOrders!=null)orderList.addAll(0,mTempOrders);//提交中订单加在最前边
            adapter.setData(orderList);
            pullListView.onRefreshComplete();
            if (adapter.getCount() == 0 && pullListView.getRefreshableView().getHeaderViewsCount() == 1) {
                loadingLayout.onSuccess(0, "暂无在途订单", R.drawable.default_icon_ordernone);
                pullListView.getRefreshableView().addHeaderView(loadingLayout);
            } else {
                loadingLayout.onSuccess(adapter.getCount(), "暂无在途订单", R.drawable.default_icon_ordernone);
            }
        }
    }

    private void cachReturnList(List<ReturnOrderBean.ListBean> list) {
        ProductBasicUtils.getReturnMap().clear();
        if (list != null) {
            for (ReturnOrderBean.ListBean rlb : list) {
                ProductBasicUtils.getReturnMap().put(String.valueOf(rlb.getReturnOrderID()), rlb.getName());
            }
        }
    }

    @Override
    public void onUserLoginout() {
        super.onUserLoginout();
        MainActivity ma = (MainActivity) getActivity();
        if (ma.getCurrentTabIndex() == 0)
            this.switchContent(this, new UnLoginedFirstFragment());
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where){
//            case REQUEST_TRANSFER_IN:
//            case REQUEST_TRANSFER_OUT:
            case FROMRETURN:
                returnRequesting = false;
                checkSuccess();
            case FROMORDER:
                orderRequesting = false;
                checkSuccess();
                break;
            case REQUEST_OUTPUT_CONFIRM:
                if(errMsg.contains("库存不足")){
                    mInTheRequest = false;
                    dialog.setMessage("当前调拨商品库存不足，请重新盘点更新库存");
                    dialog.setMessageGravity();
                    dialog.setTitleGone();
                    dialog.setModel(CustomDialog.BOTH);
                    dialog.setRightBtnListener("查看库存", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            //发送取消订单请求
                            Intent intent = new Intent(getActivity(),MainActivity.class);
                            intent.putExtra(MainActivity.INTENT_KEY_TAB,2);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
                return;
            case REQUEST_SUBMITTING_ORDER:
                //TODO:test
                //TODO:删除提交成功的订单，或者更新状态为提交失败订单
                //展示提交中的订单
                submitRequesting = false;
                checkSuccess();
                break;
            case REQUEST_INVENTORY_LIST:
                inventoryRequesting = false;
                checkSuccess();
                break;
        }
    }

    /**
     * 跳去盘点页
     * 先查找是否有缓存数据
     */
    @Override
    public void gotoInventory(InventoryResponse.InventoryBean inventoryBean) {

        if(!GlobalApplication.getInstance().getUserName().equals(inventoryBean.getCreateUser())){
            ToastUtil.show(getActivity(),"当前"+inventoryBean.getCreateUser()+"正在盘点中，无法创建新的盘点单");
            return;
        }

        InventoryResponse.InventoryBean cacheBean = InventoryCacheManager.getInstance(getActivity()).loadInventory(inventoryBean.getInventoryID());
        //读取缓存
        if(cacheBean!=null)inventoryBean = cacheBean;
        Intent intent = new Intent(getActivity(), InventoryActivity.class);
        intent.putExtra(INTENT_KEY_INVENTORY_BEAN,inventoryBean);
        startActivity(intent);
    }

    private InventoryResponse.InventoryBean mCancelInventory;//记录删除的盘点对象
    /**
     * 取消盘点缓存
     */
    @Override
    public void cancelInventory(InventoryResponse.InventoryBean inventoryBrief) {
        mCancelInventory = inventoryBrief;
        ChannelPandian request = new ChannelPandian();
        request.setId(inventoryBrief.getInventoryID());
        request.setState("draft");
        sendConnection("/api/inventory/state",request,REQUEST_CANCEL_INVENTORY,true,null);
    }

    @Override
    public void doAction(OrderDoAction action, final int position) {
        switch (action) {
            case CANCLE:
                dialog.setTitle("提示");
                dialog.setMessage("确认取消订单?");
                dialog.setMessageGravity();
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        cancleOrderRequest(position);
                    }
                });
                dialog.show();
                break;
            case UPLOAD:
                Intent uIntent = new Intent(mContext, UploadPayedPicActivity.class);
                int ordereId = ((OrderResponse.ListBean) adapter.getItem(position)).getOrderID();
                String orderNmae = ((OrderResponse.ListBean) adapter.getItem(position)).getName();
                uIntent.putExtra("orderid", ordereId);
                uIntent.putExtra("ordername", orderNmae);
                startActivity(uIntent);
                break;
            case LOOK:
//                hasattachment
                Intent lIntent = new Intent(mContext, UploadPayedPicActivity.class);
                int ordereId2 = ((OrderResponse.ListBean) adapter.getItem(position)).getOrderID();
                String orderNmae2 = ((OrderResponse.ListBean) adapter.getItem(position)).getName();
                lIntent.putExtra("orderid", ordereId2);
                lIntent.putExtra("ordername", orderNmae2);
                lIntent.putExtra("hasattachment", true);
                startActivity(lIntent);

                break;
            case TALLY:
                //点货，计入结算单位
                Intent tIntent = new Intent(mContext, ReceiveActivity.class);
                Bundle tBundle = new Bundle();
                tBundle.putParcelable("order", (OrderResponse.ListBean) adapter.getItem(position));
                tBundle.putInt("mode", 1);
                tIntent.putExtras(tBundle);
                startActivity(tIntent);
                break;
            case TALLYING:
                String name = ((OrderResponse.ListBean) adapter.getItem(position)).getTallyingUserName();
                dialog.setMessageGravity();
                dialog.setMessage(name + "正在点货");
                dialog.setModel(CustomDialog.RIGHT);
                dialog.setRightBtnListener("我知道了", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case RATE:
                //评价
                Intent rIntent = new Intent(mContext, EvaluateActivity.class);
                final OrderResponse.ListBean bean = (OrderResponse.ListBean) adapter.getList().get(position);
                Bundle rBundle = new Bundle();
                rBundle.putParcelable("order", bean);
                rIntent.putExtras(rBundle);
                startActivity(rIntent);
                break;
            case RECEIVE://正常收货
                Intent intent = new Intent(mContext, ReceiveActivity.class);
                Bundle bundle = new Bundle();
                OrderResponse.ListBean listBean = (OrderResponse.ListBean) adapter.getItem(position);
                bundle.putParcelable("order", listBean);
                bundle.putInt("mode", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case SETTLERECEIVE:
                //点货，计入结算单位
                Intent sIntent = new Intent(mContext, ReceiveActivity.class);
                Bundle sBundle = new Bundle();
                sBundle.putParcelable("order", (OrderResponse.ListBean) adapter.getItem(position));
                sBundle.putInt("mode", 2);
                sIntent.putExtras(sBundle);
                startActivity(sIntent);
                break;
            case SELFTALLY:
                dialog.setMessageGravity();
                dialog.setMessage("您已经点过货了，应由其他人完成收货");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {

                    }
                });
                dialog.show();
                break;
            case FINISH_RETURN:
                mSelectBean = (ReturnOrderBean.ListBean) adapter.getList().get(position);
                dialog.setTitle("提示");
                dialog.setMessageGravity();
                dialog.setMessage("确认数量一致?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        Object request = null;
                        sendConnection("/gongfu/v2/return_order/" +
                                mSelectBean.getReturnOrderID() +
                                "/done", request, FINISHRETURN, false, FinishReturnResponse.class);
                    }
                });
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void doTransferAction(int type, final TransferEntity transferEntity) {
        switch (type){
            case TRANS_ACTION_CANCEL:
                //取消
                if(!SystemUpgradeHelper.getInstance(getActivity()).check(getActivity()))return;
                dialog.setTitleGone();
                dialog.setMessage("确认取消订单?");
                dialog.setMessageGravity();
                dialog.setModel(CustomDialog.BOTH);
                dialog.setRightBtnListener("取消订单", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        //发送取消订单请求
                        requestCancel(transferEntity);
                    }
                });
                dialog.setLeftBtnListener("我再想想",null);
                dialog.show();
                break;
            case TRANS_ACTION_OUTPUT_CONFIRM:
                requestOutputConfirm(transferEntity);
                break;
        }
    }

    ReturnOrderBean.ListBean mSelectBean;

    @Override
    public void call(final String phone) {
//        final String number = GlobalApplication.getInstance().loadUserInfo().getCompanyHotLine();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(mContext, "尚未指派");
            return;
        }
        dialog.setModel(CustomDialog.BOTH);
        dialog.setTitle("联系配送员");
        dialog.setMessageGravity();
        dialog.setMessage(phone);
        dialog.setLeftBtnListener("取消", null);
        dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
            @Override
            public void doClickButton(Button btn, CustomDialog dialog) {
                CommonUtils.callNumber(mContext, phone);
            }
        });
        dialog.show();
    }

    @OnClick({R.id.callIcon, lqLL, R.id.dqLL})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.callIcon:
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getCompanyHotLine())) {
                    number = userInfo.getCompanyHotLine();
                    dialog.setTitleGone();
                } else {
                    dialog.setTitle("致电 供鲜生 客服");
                }
                dialog.setModel(CustomDialog.BOTH);
                dialog.setMessageGravity();
                dialog.setMessage("致电 " + userInfo.getCompany() + " 客服\n"+number);
                dialog.setLeftBtnListener("取消", null);
                dialog.setRightBtnListener("呼叫", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        CommonUtils.callNumber(mContext, number);
                    }
                });
                dialog.show();
                break;
            case lqLL:
            case R.id.dqLL:
                MainActivity ma = (MainActivity) getActivity();
                ma.gotoTabByIndex(2);
                break;
        }
    }

    /**
     * 检查订单是否已经提交完成
     * @param orders
     */
    private void checkTempOrders(List<TempOrderManager.TempOrder> orders){
        if(orders==null){//没有提交中订单
            submitRequesting = false;
            checkSuccess();
            return;
        }
        mTempOrders = orders;
        CheckOrderSuccessRequest request = new CheckOrderSuccessRequest(mTempOrders);
        sendConnection("/api/order/is/success",request,REQUEST_SUBMITTING_ORDER,false, CheckOrderResponse.class);
    }

    List<InventoryResponse.InventoryBean> inventoryList = new ArrayList<>();
    /**
     *  一次性加载全部，无分页,【先加载退货单，然后跟着正常订单】改为:
     *  并行查询退货单、正常订单、和提交中的订单的状态
     */
    private void requestReturnList() {
        if(returnRequesting || orderRequesting || submitRequesting || inventoryRequesting)return;
        inventoryRequesting = true;
        returnRequesting = true;
        orderRequesting = true;
        submitRequesting = true;
        orderList.clear();
        //查询提交中订单是否完成
        TempOrderManager.getInstance(getActivity().getApplicationContext())
                .getTempOrdersAsync(this::checkTempOrders);

        //清空盘点信息
        inventoryList.clear();
        checkInventory();

        Object request = null;
        sendConnection("/gongfu/v2/return_order/undone/", request, FROMRETURN, false, ReturnOrderBean.class);

        //同时查订单和退货单
        Object requestOrder = null;
        sendConnection("/gongfu/v2/order/undone_orders/", request, FROMORDER, false, OrderResponse.class);
    }

    private void requestLB() {
        Object request = null;
        LunboRequest lbRequest = new LunboRequest("餐户端");
        sendConnection("/gongfu/blog/post/list/login", lbRequest, FROMLB, false, LunboResponse.class);
        mTimeStartFROMLB = System.currentTimeMillis();
    }

    private void requestDashBoard() {
        Object request = null;
        sendConnection("/gongfu/v2/shop/stock/dashboard", request, FROMDB, false, DashBoardResponse.class);
        mTimeStartFROMDB = System.currentTimeMillis();
    }

    private void updateLb(final List<ImagesBean> post_list) {
        banner.setPages(new CBViewHolderCreator<BannerHolderView>() {
            @Override
            public BannerHolderView createHolder() {
                return new BannerHolderView();
            }
        }, post_list)
                .setPointViewVisible(true)
//                .setPageIndicator(new Int[]{R.id.})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(5000)
                .setPageIndicator(new int[]{R.drawable.guidepage_circle_normal, R.drawable.guidepage_circle_highlight})
                .setPointViewVisible(true)
                .setManualPageable(true);  //设置手动影响;
        banner.setCanLoop(true);
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ImagesBean bean = post_list.get(position);
                if (bean != null && bean.getCover_url() != null) {
                    Intent intent = new Intent(mContext, PageDeatailActivity.class);
                    intent.putExtra("url", Constant.BASE_URL + bean.getPost_url());
                    startActivity(intent);
                }
            }
        });
    }

    private void cancleOrderRequest(int position) {
        OrderResponse.ListBean bean = (OrderResponse.ListBean) adapter.getList().get(position);
        StringBuffer urlSb = new StringBuffer("/gongfu/order/");
        urlSb.append(bean.getOrderID()).append("/state");
        CancleRequest request = new CancleRequest();
        request.setState("cancel");
        sendConnection(urlSb.toString(), request, CANCEL, true, BaseEntity.ResultBean.class);

    }

    public void switchContent(Fragment from, Fragment to) {
        FragmentManager mManager = getFragmentManager();
        if (from != to) {
            FragmentTransaction mTransaction = mManager.beginTransaction();
            if (!to.isAdded()) {
                mTransaction.hide(from).add(R.id.realtabcontent, to);

            } else
                mTransaction.hide(from).show(to);
            mTransaction.commit();
        }

    }

    private void updateDashBoard(DashBoardResponse dbResponse) {
        //能看价格，看价格，不能则看件数
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (canSeePrice) {
            DecimalFormat df = new DecimalFormat("#.##");
            lastWeekBuy.setText(df.format(dbResponse.getPurchaseAmount()/10000));//万元单位
            double adventNum = dbResponse.getAdventValue();
            double maturityNum = dbResponse.getMaturityValue();
            double adventValue = dbResponse.getAdventValue();
            double maturityValue = dbResponse.getMaturityValue();
            lastMonthBuy.setText(df.format(adventValue));
            unPayAccount.setText(df.format(maturityValue));
        } else {
            lastWeekKey.setText("上周采购量(件)");
            lqCountTv.setText("临期食材(件)");
            dqCountTv.setText("到期食材(件)");
            lastWeekBuy.setText(String.valueOf(dbResponse.getTotalNumber()));
            int adventNum = dbResponse.getAdventNum();
            int maturityNum = dbResponse.getMaturityNum();
            lastMonthBuy.setText(String.valueOf(adventNum));
            unPayAccount.setText(String.valueOf(maturityNum));

        }
//        SpannableString ssLq = new SpannableString("临期食材"+adventNum +"件");
//        ssLq.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 4,4+String.valueOf(adventNum).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        lqCountTv.setText(ssLq);
//        SpannableString ssDq = new SpannableString("到期食材"+maturityNum+"件");
//        ssDq.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 4,4+String.valueOf(maturityNum).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        dqCountTv.setText(ssDq);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemUpgradeNotice(SystemUpgradeNoticeEvent receiverLogoutEvent) {
        if(SystemUpgradeHelper.getInstance(getActivity()).needShowNotice(LoginedFirstFragment.class.getName()))showSystemUpgradeNotice();
    }

    //接收到订单状态更新推送，更新列表
    @Subscribe
    public void onOrderStatusChanged(OrderStatusChangeEvent orderStatusChangeEvent) {
        requestReturnList();
    }

    @Override
    public void resubmitOrder(TempOrderManager.TempOrder tempOrder) {
        //TODO:重新下单
    }

    boolean isDialogShown = false;//是不是有可能onCreate未调用同时收到推送，就弹两次框，防止这种情况
    private void showSystemUpgradeNotice(){
        if(isDialogShown)return;
        isDialogShown = true;
        final Dialog dialog = new Dialog(getActivity(),R.style.CustomProgressDialog);
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_system_upgrade_notice,null,false);
        dialog.setContentView(rootView);
        dialog.show();
        TextView tvNotice = (TextView)rootView.findViewById(R.id.tv_system_upgrade_notice);
        SystemUpgradeHelper systemUpgradeHelper = SystemUpgradeHelper.getInstance(getContext());
        rootView.findViewById(R.id.iv_noitce_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUpgradeHelper.getInstance(getContext()).setIsRead(LoginedFirstFragment.class.getName());
                dialog.dismiss();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(systemUpgradeHelper.getStartTime()*1000);
        StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(cal.getTime())).append("~");
        cal.setTimeInMillis(systemUpgradeHelper.getEndTime()*1000);
        sb.append(sdf.format(cal.getTime()));
        tvNotice.setText(Html.fromHtml("<font color=\"#666666\">后台将于" + sb.toString() +
                "进行系统更新维护，届时只能查看内容，</font>" +
                "<font color=\"#ff8b00\">操作功能暂时无法使用，</font>" +
                "<font color=\"#666666\">感谢您的谅解</font>"));
    }

    /**
     * 取消调拨单
     */
    private void requestCancel(TransferEntity transferEntity) {
        Object request = null;
        sendConnection("/gongfu/shop/transfer/cancel/" + transferEntity.getPickingID(), request, REQUEST_CANCEL_TRANSFER, true, null);
    }

    /**
     * 出库
     */
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

    /**
     * 设置一个订单为已读状态
     */
    private void setOrderRead(OrderResponse.ListBean order){
        order.setUserRead(GlobalApplication.getInstance().getUid());
        adapter.notifyDataSetChanged();
    }

    /**
     * 检查盘点单
     */
    private void checkInventory(){
        PageRequest request = new PageRequest();
        //只查盘点列表第一个，盘点中的单一定在第一条
        request.setLimit(1);
        request.setPz(1);
        request.setDate_type(0);
        sendConnection("/api/v2/inventory/list",request,REQUEST_INVENTORY_LIST,false,FirstPageInventoryResult.class);
    }
}
