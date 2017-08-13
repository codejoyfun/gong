package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLogoutEvent;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.business.BannerHolderView;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.firstpage.entity.CancleRequest;
import com.runwise.supply.firstpage.entity.DashBoardResponse;
import com.runwise.supply.firstpage.entity.LunboRequest;
import com.runwise.supply.firstpage.entity.LunboResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by libin on 2017/7/13.
 */

public class LoginedFirstFragment extends NetWorkFragment implements OrderAdapter.DoActionInterface{
    private static final int FROMORDER = 0;
    private static final int FROMLB = 1;
    private static final int FROMDB = 2;
    private static final int CANCEL = 3;        //取消订单
    private static final int FROMRETURN = 4;

    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout       loadingLayout;
    @ViewInject(R.id.header)
    private View headView;

    private LayoutInflater      layoutInflater;
    private ConvenientBanner    banner;
    private OrderAdapter        adapter;
    private TextView lastWeekBuy;
    private TextView lastMonthBuy;
    private TextView unPayMoney;
    private List orderList = new ArrayList<>();
    private View rootView;

    public LoginedFirstFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(mContext);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //表头：放轮播+统计表
//        View headView = layoutInflater.inflate(R.layout.logined_head_layout,null);
        lastWeekBuy = (TextView) headView.findViewById(R.id.lastWeekBuy);
        lastMonthBuy = (TextView)headView.findViewById(R.id.lastMonthBuy);
        unPayMoney = (TextView)headView.findViewById(R.id.unPayAccount);
        banner = (ConvenientBanner) headView.findViewById(R.id.ConvenientBanner);
        //通过图片比例，计算banner大小 375:175 = w:x
        int height = 175 * CommonUtils.getScreenWidth(mContext) / 375;
        banner.getLayoutParams().height = height;

//        pullListView.getRefreshableView().addHeaderView(headView);
        adapter = new OrderAdapter(mContext,this);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //根据点击的position，确定是退货还是正常订单
                int realPosition = (int)l;
                if (adapter.getItemViewType(realPosition) == adapter.TYPE_ORDER){
                    Intent intent = new Intent(mContext,OrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    OrderResponse.ListBean bean = (OrderResponse.ListBean) orderList.get(realPosition);
                    bundle.putParcelable("order",bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if(adapter.getItemViewType(realPosition) == adapter.TYPE_RETURN){
                    Intent intent = new Intent(mContext,ReturnDetailActivity.class);
                    Bundle bundle = new Bundle();
                    ReturnOrderBean.ListBean bean = (ReturnOrderBean.ListBean) orderList.get(realPosition);
                    bundle.putParcelable("return",bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新:只刷新列表内容
                requestList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        requestDashBoard();
        requestLB();
        pullListView.setRefreshing(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!SPUtils.isLogin(mContext)){
            MainActivity ma = (MainActivity) getActivity();
            if (ma.getCurrentTabIndex() == 0)
                this.switchContent(this,new UnLoginedFirstFragment());
        }
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_logined_first;
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where){
            case FROMORDER:
                BaseEntity.ResultBean resultBean= result.getResult();
                OrderResponse response = (OrderResponse) resultBean.getData();
                int addIndex = orderList.size();
                orderList.addAll(addIndex,response.getList());
                adapter.setOrderCount(response.getList().size());
                adapter.setData(orderList);
                pullListView.onRefreshComplete();
                break;
            case FROMLB:
                LunboResponse lRes = (LunboResponse) result.getResult();
                updateLb(lRes.getPost_list());
                break;
            case FROMDB:
                BaseEntity.ResultBean resultBean2= result.getResult();
                DashBoardResponse dbResponse = (DashBoardResponse) resultBean2.getData();
                DecimalFormat df   = new DecimalFormat("######0.00");
                lastWeekBuy.setText(df.format(dbResponse.getPurchaseAmount()));
                break;
            case CANCEL:
                BaseEntity.ResultBean resultBean3= result.getResult();
                if("A0006".equals(resultBean3.getState())){
                    ToastUtil.show(mContext,"取消成功");
                    requestList();
                }
                break;
            case FROMRETURN:
                BaseEntity.ResultBean resultBean4= result.getResult();
                ReturnOrderBean rob = (ReturnOrderBean) resultBean4.getData();
                orderList.clear();
                orderList.addAll(rob.getList());
                adapter.setReturnCount(rob.getList().size());
                adapter.setData(orderList);
                Object request = null;
                sendConnection("/gongfu/v2/order/undone_orders/",request,FROMORDER,false, OrderResponse.class);
                break;
        }
    }
    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    @Override
    public void doAction(OrderDoAction action, final int position) {
        switch(action){
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
                Intent uIntent = new Intent(mContext,UploadPayedPicActivity.class);
                int ordereId = ((OrderResponse.ListBean)adapter.getItem(position)).getOrderID();
                String orderNmae = ((OrderResponse.ListBean)adapter.getItem(position)).getName();
                uIntent.putExtra("orderid",ordereId);
                uIntent.putExtra("ordername",orderNmae);
                startActivity(uIntent);
                break;
            case LOOK:
//                hasattachment
                Intent lIntent = new Intent(mContext,UploadPayedPicActivity.class);
                int ordereId2 = ((OrderResponse.ListBean)adapter.getItem(position)).getOrderID();
                String orderNmae2 = ((OrderResponse.ListBean)adapter.getItem(position)).getName();
                lIntent.putExtra("orderid",ordereId2);
                lIntent.putExtra("ordername",orderNmae2);
                lIntent.putExtra("hasattachment",true);
                startActivity(lIntent);

                break;
            case TALLY:
                //点货，计入结算单位
                Intent tIntent = new Intent(mContext,ReceiveActivity.class);
                Bundle tBundle = new Bundle();
                tBundle.putParcelable("order",(OrderResponse.ListBean)adapter.getItem(position));
                tBundle.putInt("mode",1);
                tIntent.putExtras(tBundle);
                startActivity(tIntent);
                break;
            case RATE:
                //评价
                Intent rIntent = new Intent(mContext,EvaluateActivity.class);
                OrderResponse.ListBean bean = (OrderResponse.ListBean) orderList.get(position);
                Bundle rBundle = new Bundle();
                rBundle.putParcelable("order",bean);
                rIntent.putExtras(rBundle);
                startActivity(rIntent);
                break;
            case RECEIVE://正常收货
                Intent intent = new Intent(mContext,ReceiveActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("order",(OrderResponse.ListBean)adapter.getItem(position));
                bundle.putInt("mode",0);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case SETTLERECEIVE:
                //点货，计入结算单位
                Intent sIntent = new Intent(mContext,ReceiveActivity.class);
                Bundle sBundle = new Bundle();
                sBundle.putParcelable("order",(OrderResponse.ListBean)adapter.getItem(position));
                sBundle.putInt("mode",2);
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
            default:
                break;
        }
    }


    //一次性加载全部，无分页,先加载退货单，然后跟着正常订单
    private void requestList() {
        Object request = null;
        sendConnection("/gongfu/v2/return_order/undone/",request,FROMRETURN,false,ReturnOrderBean.class);

    }
    private void requestLB(){
        Object request = null;
        LunboRequest lbRequest = new LunboRequest("餐户端");
        sendConnection("/gongfu/blog/post/list/",lbRequest,FROMLB,false,LunboResponse.class);
    }
    private void requestDashBoard(){
        Object request = null;
        sendConnection("/gongfu/v2/shop/stock/dashboard",request,FROMDB,false,DashBoardResponse.class);
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
                .setPageIndicator(new int[]{R.drawable.guidepage_circle_normal,R.drawable.guidepage_circle_highlight})
                .setPointViewVisible(true)
                .setManualPageable(true);  //设置手动影响;
        banner.setCanLoop(true);
        banner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ImagesBean bean = post_list.get(position);
                if (bean != null && bean.getCover_url() != null){
                    Intent intent = new Intent(mContext,PageDeatailActivity.class);
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
        sendConnection(urlSb.toString(),request,CANCEL,true,BaseEntity.ResultBean.class);

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
}
