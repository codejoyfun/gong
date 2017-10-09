package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.runwise.supply.business.BannerHolderView;
import com.runwise.supply.business.entity.ImagesBean;
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
import com.runwise.supply.tools.PollingUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.runwise.supply.R.id.lqLL;
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

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.rl_title)
    private RelativeLayout rl_title;

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
                    return;
                }
                if (top < 0) {
                    top = -top;
                    if (top >= headView.getHeight() / 3) {
                        rl_title.setBackgroundResource(R.color.white);
                        rl_title.setAlpha(1);
                        return;
                    }
                    float ratio = top / (float) headView.getHeight() * 3;
                    rl_title.setBackgroundResource(R.color.white);
                    rl_title.setAlpha(ratio);
                } else {
                    rl_title.setAlpha(0);
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
        loadingLayout = new LoadingLayout(getActivity());
    }

    public void getProcurement() {
        Object request = null;
        sendConnection("/api/sale/shop/info", request, REQUEST_SUM, false, SumMoneyData.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible()) {
            PollingUtil.getInstance().requestOrder(netWorkHelper, FROMRETURN);
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

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case FROMORDER:
                BaseEntity.ResultBean resultBean = result.getResult();
                OrderResponse response = (OrderResponse) resultBean.getData();
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
                break;
            case FROMLB:
                LunboResponse lRes = (LunboResponse) result.getResult();
                updateLb(lRes.getPost_list());
                break;
            case FROMDB:
                BaseEntity.ResultBean resultBean2 = result.getResult();
                DashBoardResponse dbResponse = (DashBoardResponse) resultBean2.getData();
                updateDashBoard(dbResponse);
                break;
            case CANCEL:
                ToastUtil.show(mContext, "取消成功");
                requestReturnList();
                break;
            case FROMRETURN:
                BaseEntity.ResultBean resultBean4 = result.getResult();
                ReturnOrderBean rob = (ReturnOrderBean) resultBean4.getData();
                //加入轮询判断，不定时刷新，只有都拉到数据，才一起更新列表
                orderList.clear();
                orderList.addAll(rob.getList());
                if (adapter.getCount() == 0) {
                    //首次
                    adapter.setReturnCount(rob.getList().size());
                    adapter.setData(orderList);
                }
                cachReturnList(rob.getList());
                Object request = null;
                sendConnection("/gongfu/v2/order/undone_orders/", request, FROMORDER, false, OrderResponse.class);
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
                    dialog.setTitle("致电 " + userInfo.getCompany() + " 客服");
                } else {
                    dialog.setTitle("致电 供鲜生 客服");
                }
                dialog.setModel(CustomDialog.BOTH);
                dialog.setMessageGravity();
                dialog.setMessage(number);
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
        Object request = null;
        sendConnection("/gongfu/v2/return_order/undone/", request, FROMRETURN, false, ReturnOrderBean.class);
    }

    private void requestLB() {
        Object request = null;
        LunboRequest lbRequest = new LunboRequest("餐户端");
        sendConnection("/gongfu/blog/post/list/login", lbRequest, FROMLB, false, LunboResponse.class);
    }

    private void requestDashBoard() {
        Object request = null;
        sendConnection("/gongfu/v2/shop/stock/dashboard", request, FROMDB, false, DashBoardResponse.class);
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
            lastWeekBuy.setText(df.format(dbResponse.getPurchaseAmount()));
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
}
