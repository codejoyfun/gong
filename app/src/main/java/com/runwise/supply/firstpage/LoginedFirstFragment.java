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
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.entity.FirstPageOrder;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.entity.TransferListResponse;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.DashBoardResponse;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.LunboRequest;
import com.runwise.supply.firstpage.entity.LunboResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.mine.ProcurementLimitActivity;
import com.runwise.supply.mine.entity.SumMoneyData;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.TransferOutActivity;
import com.runwise.supply.tools.PollingUtil;
import com.runwise.supply.tools.SystemUpgradeHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.runwise.supply.R.id.lqLL;
import static com.runwise.supply.TransferDetailActivity.EXTRA_TRANSFER_ID;
import static com.runwise.supply.firstpage.OrderAdapter.TRANS_ACTION_CANCEL;
import static com.runwise.supply.firstpage.OrderAdapter.TRANS_ACTION_OUTPUT_CONFIRM;
import static com.runwise.supply.firstpage.OrderAdapter.TYPE_TRANSFER;
import static com.runwise.supply.firstpage.ReturnSuccessActivity.INTENT_KEY_RESULTBEAN;
import static com.runwise.supply.mine.ProcurementLimitActivity.KEY_SUM_MONEY_DATA;

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
    private static final int REQUEST_TRANSFER_IN = 7;
    private static final int REQUEST_TRANSFER_OUT = 8;
    private static final int REQUEST_CANCEL_TRANSFER = 9;
    private static final int REQUEST_OUTPUT_CONFIRM = 10;

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
    private boolean isFirst = true;
    private List<FirstPageOrder> mTransferAndOrderList = new ArrayList<>();//缓存调拨和一般订单，用于排序

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
            PollingUtil.getInstance().requestOrder(netWorkHelper, FROMRETURN);
        } else {
            PollingUtil.getInstance().stopRequestOrder();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            PollingUtil.getInstance().requestOrder(netWorkHelper, FROMRETURN);
            mTimeStartFROMRETURN = System.currentTimeMillis();
        } else {
            PollingUtil.getInstance().stopRequestOrder();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PollingUtil.getInstance().stopRequestOrder();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_logined_first;
    }

    LoadingLayout loadingLayout;
    SumMoneyData mSumMoneyData;
    //一次刷新要连续查多个接口的数据，要标记当前查询是否在进行，是的话不可刷新
    boolean inProgress = false;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case FROMORDER:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderResponse response = (OrderResponse) resultBean.getData();
                //有调拨权限，请求调拨单
                userInfo = GlobalApplication.getInstance().loadUserInfo();
                if(!TextUtils.isEmpty(userInfo.getIsShopTransfer())&&userInfo.getIsShopTransfer().equals("true")){
                    //暂存一般订单
                    mTransferAndOrderList.addAll(response.getList());
                    requestTransferIn();
                    return;
                }
                //原有流程，适用于没有调拨权限的流程
                int addIndex = orderList.size();
                orderList.addAll(addIndex, response.getList());
                adapter.setReturnCount(addIndex);
                adapter.setOrderCount(response.getList().size());
                adapter.setData(orderList);
                pullListView.onRefreshComplete();
//                if (!isFirst){
//                    ToastUtil.show(mContext,"订单已刷新");
//                }
                isFirst = false;
                if (adapter.getCount() == 0 && pullListView.getRefreshableView().getHeaderViewsCount() == 1) {
                    loadingLayout.onSuccess(0, "暂无在途订单", R.drawable.default_icon_ordernone);
                    pullListView.getRefreshableView().addHeaderView(loadingLayout);
                } else {
                    loadingLayout.onSuccess(adapter.getCount(), "暂无在途订单", R.drawable.default_icon_ordernone);
                }
                inProgress = false;
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
                if(inProgress)return;
                inProgress = true;
                BaseEntity.ResultBean resultBean4 = result.getResult();
                ReturnOrderBean rob = (ReturnOrderBean) resultBean4.getData();
                //加入轮询判断，不定时刷新，只有都拉到数据，才一起更新列表
                orderList.clear();
                mTransferAndOrderList.clear();
                orderList.addAll(rob.getList());
                if (adapter.getCount() == 0) {
                    //首次
                    adapter.setReturnCount(rob.getList().size());
                    adapter.setData(orderList);
                }
                cachReturnList(rob.getList());
                Object request = null;
                sendConnection("/gongfu/v2/order/undone_orders/", request, FROMORDER, false, OrderResponse.class);
                mTimeStartFROMORDER = System.currentTimeMillis();
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
            case REQUEST_TRANSFER_IN:
                TransferListResponse transferInResponse = (TransferListResponse) result.getResult().getData();
                mTransferAndOrderList.addAll(filter(transferInResponse.getList()));
                requestTransferOut();
                break;
            case REQUEST_TRANSFER_OUT:
                TransferListResponse transferOutResponse = (TransferListResponse) result.getResult().getData();
                mTransferAndOrderList.addAll(filter(transferOutResponse.getList()));
                //按照创建时间排序
                Collections.sort(mTransferAndOrderList);

                int addedIndex = orderList.size();
                orderList.addAll(addedIndex, mTransferAndOrderList);
                adapter.setReturnCount(addedIndex);
                adapter.setData(orderList);
                pullListView.onRefreshComplete();
//                if (!isFirst){
//                    ToastUtil.show(mContext,"订单已刷新");
//                }
                isFirst = false;
                if (adapter.getCount() == 0 && pullListView.getRefreshableView().getHeaderViewsCount() == 1) {
                    loadingLayout.onSuccess(0, "暂无在途订单", R.drawable.default_icon_ordernone);
                    pullListView.getRefreshableView().addHeaderView(loadingLayout);
                } else {
                    loadingLayout.onSuccess(adapter.getCount(), "暂无在途订单", R.drawable.default_icon_ordernone);
                }
                inProgress = false;
                break;
            case REQUEST_CANCEL_TRANSFER:
                ToastUtil.show(mContext, "取消成功");
                requestReturnList();
                break;
            case REQUEST_OUTPUT_CONFIRM:
                startActivity(TransferOutActivity.getStartIntent(getActivity(),mSelectTransferEntity));
                mInTheRequest = false;
                break;
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
            case REQUEST_TRANSFER_IN:
            case REQUEST_TRANSFER_OUT:
            case FROMRETURN:
            case FROMORDER:
                inProgress = false;
                break;
            case REQUEST_OUTPUT_CONFIRM:
                if(errMsg.contains("库存不足")){
                    dialog.setMessage("当前调拨商品库存不足，请重新盘点更新库存");
                    dialog.setMessageGravity();
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
        }
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
                bundle.putParcelable("order", (OrderResponse.ListBean) adapter.getItem(position));
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
                dialog.setTitle("提示");
                dialog.setMessage("确认取消订单?");
                dialog.setMessageGravity();
                dialog.setModel(CustomDialog.BOTH);
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送取消订单请求
                        //发送取消订单请求
                        requestCancel(transferEntity);
                    }
                });
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

    //一次性加载全部，无分页,先加载退货单，然后跟着正常订单
    private void requestReturnList() {
        if(inProgress)return;
        Object request = null;
        sendConnection("/gongfu/v2/return_order/undone/", request, FROMRETURN, false, ReturnOrderBean.class);
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

    private void requestTransferIn(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/input_list", request, REQUEST_TRANSFER_IN, false, TransferListResponse.class);
    }

    private void requestTransferOut(){
        Object request = null;
        sendConnection("/gongfu/shop/transfer/output_list", request, REQUEST_TRANSFER_OUT, false, TransferListResponse.class);
    }

    private void showSystemUpgradeNotice(){
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
     * 过滤掉已完成的
     */
    private List<TransferEntity> filter(List<TransferEntity> list){
        if(list==null)return null;
        for(int i=list.size()-1;i>=0;i--){
            if(list.get(i).getPickingStateNum()==TransferEntity.STATE_FINISH)list.remove(i);
        }
        return list;
    }
}
