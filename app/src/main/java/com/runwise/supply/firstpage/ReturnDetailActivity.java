package com.runwise.supply.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.ReturnActivityRefreshEvent;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.ReturnDetailResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.fragment.ReturnProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.YourScrollableViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.firstpage.ReturnSuccessActivity.INTENT_KEY_RESULTBEAN;
import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY;
import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_VENDOR_DELIVERY;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnDetailActivity extends NetWorkActivity {
    private static final int DETAIL = 0;
    private static final int FINISHRETURN = 1;
    @ViewInject(R.id.orderStateTv)
    private TextView orderStateTv;
    @ViewInject(R.id.tipTv)
    private TextView tipTv;
    @ViewInject(R.id.dateTv)
    private TextView dateTv;
    @ViewInject(R.id.orderNumTv)
    private TextView orderNumTv;
    @ViewInject(R.id.buyerValue)
    private TextView buyerValue;
    @ViewInject(R.id.orderTimeValue)
    private TextView orderTimeValue;
    @ViewInject(R.id.countTv)
    private TextView countTv;
    @ViewInject(R.id.ygMoneyTv)
    private TextView ygMoneyTv;
    @ViewInject(R.id.doBtn)
    private TextView doBtn;
    @ViewInject(R.id.priceLL)
    private LinearLayout priceLL;
    @ViewInject(R.id.rl_bottom)
    private RelativeLayout rlBottom;

    @ViewInject(R.id.payStateValue)
    private TextView payStateValue;
    @ViewInject(R.id.payStateTv)
    private TextView payStateTv;
    @ViewInject(R.id.uploadBtn)
    private Button uploadBtn;

    @ViewInject(R.id.tablayout)
    private TabLayout tablayout;
    @ViewInject(R.id.viewpager)
    private YourScrollableViewPager viewpager;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;
    @ViewInject(R.id.tv_open)
    private ImageView ivOpen;

    private ReturnOrderBean.ListBean bean;
    public static final int REQUEST_CODE_UPLOAD = 1<<0;

    private boolean hasAttatchment = false;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.return_detain_layout);
        setTitleText(true, "退货单详情");
        setTitleLeftIcon(true, R.drawable.nav_back);
        String rid = getIntent().getStringExtra("rid");
        initViews();
        //发网络请求获取
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/return_order/");
        sb.append(rid).append("/");
        sendConnection(sb.toString(), request, DETAIL, false, ReturnDetailResponse.class);
        loadingLayout.setStatusLoading();
    }

    private void initViews() {
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            priceLL.setVisibility(View.GONE);
        }
        dateTv.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("return");
        if (bean != null){
            String deliveryType = bean.getDeliveryType();
            //不显示
            if (bean.getState().equals("process")) {
                if(deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY)||
                        deliveryType.equals(TYPE_VENDOR_DELIVERY)
                        ||((deliveryType.equals(TYPE_THIRD_PART_DELIVERY)||deliveryType.equals(TYPE_THIRD_PART_DELIVERY))
                        &&bean.isReturnThirdPartLog())
                        ){
                    rlBottom.setVisibility(View.VISIBLE);
                }else{
                    rlBottom.setVisibility(View.GONE);
                }
            } else {
                rlBottom.setVisibility(View.GONE);
                payStateTv.setVisibility(View.VISIBLE);
                payStateValue.setVisibility(View.VISIBLE);
                uploadBtn.setVisibility(View.VISIBLE);
            }

            hasAttatchment = bean.getHasAttachment() >0;
            updateReturnView();
        }
        else{
            rlBottom.setVisibility(View.GONE);
        }
    }

    private void updateReturnView(){
        if (!hasAttatchment){
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("未有退货凭证");
            uploadBtn.setText("上传退货凭证");

        }else{
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("已有退货凭证");
            uploadBtn.setText("查看退货凭证");
        }
    }

    List<ReturnOrderBean.ListBean.LinesBean> listDatas;
    private List<ReturnOrderBean.ListBean.LinesBean> typeDatas = new ArrayList<>();

    private void updateUI() {
        if (bean != null) {
            //获取物流状态
            List<String> trackerList = bean.getStateTracker();
            if (trackerList != null && trackerList.size() > 0) {
                String newestTrack = trackerList.get(0);
                String[] pathStr = newestTrack.split(" ");
                if (pathStr.length == 3) {   //正常状态，取最新的物流状态
                    orderStateTv.setText(pathStr[2]);
                } else {
                    //异常，自己设置
                    if (bean.getState().equals("process")) {
                        orderStateTv.setText("退货进行中");
//                tipTv.setText("原销售订单：SO"+bean.getOrderID());
                    } else {
                        orderStateTv.setText("退货成功");
                    }
                }
            }
            StringBuffer sb = new StringBuffer("退货商品：");
            sb.append((int)bean.getAmount()).append("件");
//                    .append(" 共").append(bean.getAmountTotal()).append("元");
            tipTv.setText(sb.toString());
            orderNumTv.setText(bean.getName());
            buyerValue.setText(bean.getCreateUser());
            orderTimeValue.setText(bean.getCreateDate());
            listDatas = bean.getLines();


//            recyclerView.getLayoutParams().height = list.size() * CommonUtils.dip2px(mContext, 86);
            countTv.setText((int) bean.getAmount() + "件");
            ygMoneyTv.setText(bean.getAmountTotal() + "元");

            String deliveryType = bean.getDeliveryType();
            //不显示
            if (bean.getState().equals("process")) {
                if(deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY)||
                        deliveryType.equals(TYPE_VENDOR_DELIVERY)
                        ||((deliveryType.equals(TYPE_THIRD_PART_DELIVERY)||deliveryType.equals(TYPE_THIRD_PART_DELIVERY))
                        &&bean.isReturnThirdPartLog())
                        ){
                    rlBottom.setVisibility(View.VISIBLE);
                }else{
                    rlBottom.setVisibility(View.GONE);
                }
            } else {
                rlBottom.setVisibility(View.GONE);
                payStateTv.setVisibility(View.VISIBLE);
                payStateValue.setVisibility(View.VISIBLE);
                uploadBtn.setVisibility(View.VISIBLE);
            }

            hasAttatchment = bean.getHasAttachment() >0;
            updateReturnView();
            setUpDataForViewPage();
        }
    }
    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");

        HashMap<String, ArrayList<ReturnOrderBean.ListBean.LinesBean>> map = new HashMap<>();
        for (ReturnOrderBean.ListBean.LinesBean linesBean : listDatas) {
            ArrayList<ReturnOrderBean.ListBean.LinesBean> linesBeen = map.get(linesBean.getStockType());
            if (linesBeen == null) {
                linesBeen = new ArrayList<>();
                map.put(linesBean.getStockType(), linesBeen);
            }
            linesBeen.add(linesBean);
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            ArrayList<ReturnOrderBean.ListBean.LinesBean> value = (ArrayList<ReturnOrderBean.ListBean.LinesBean>) entry.getValue();
            titles.add(key);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(key));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<ReturnOrderBean.ListBean.LinesBean>) listDatas));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initPopWindow((ArrayList<String>) titles);
    }

    public ReturnProductFragment newProductFragment(ArrayList<ReturnOrderBean.ListBean.LinesBean> value) {
        ReturnProductFragment returnProductFragment = new ReturnProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(returnProductFragment.BUNDLE_KEY_LIST, value);
        returnProductFragment.setArguments(bundle);
        return returnProductFragment;
    }
    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;
    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewpager.setCurrentItem(position);
                tablayout.getTabAt(position).select();
                for (int i = 0;i < mProductTypeAdapter.selectList.size();i++){
                    mProductTypeAdapter.selectList.set(i,new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position,new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    @OnClick({R.id.title_iv_left, R.id.gotoStateBtn, R.id.doBtn,R.id.uploadBtn,R.id.tv_open})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.gotoStateBtn:
                Intent intent = new Intent(mContext, OrderStateActivity.class);
                intent.putExtra("mode", true);
                intent.putStringArrayListExtra("tracker",(ArrayList<String>) bean.getStateTracker());
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",bean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.doBtn:
                dialog.setMessageGravity();
                dialog.setMessage("确认数量一致?");
                dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        Object request = null;
                        sendConnection("/gongfu/v2/return_order/" +
                                bean.getReturnOrderID() +
                                "/done", request, FINISHRETURN, false, FinishReturnResponse.class);
                    }
                });
                dialog.show();
                break;
            case R.id.uploadBtn:
                Intent uIntent = new Intent(mContext, UploadReturnPicActivity.class);
                uIntent.putExtra("orderid", bean.getReturnOrderID());
                uIntent.putExtra("ordername", bean.getName());
                uIntent.putExtra("hasattachment", hasAttatchment);
                startActivityForResult(uIntent,REQUEST_CODE_UPLOAD);
                break;
            case R.id.tv_open:
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
                    dragLayout.toggleTopView();
                    canShow = true;
                }else{
                    if (mProductTypeWindow.isShowing()){
                        mProductTypeWindow.dismiss();
                    }else{
                        showPopWindow();
                    }
                }
                dragLayout.listener(new DragTopLayout.PanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                            if (canShow){
                                showPopWindow();
                                canShow = false;
                            }
                        } else {
                            mProductTypeWindow.dismiss();
                        }
                    }

                    @Override
                    public void onSliding(float ratio) {

                    }

                    @Override
                    public void onRefresh() {

                    }
                });
                break;
            default:
                break;
        }
    }
    boolean canShow = false;
    private void showPopWindow(){
        int y = findViewById(R.id.title_bar).getHeight() + tablayout.getHeight();
        mProductTypeWindow.showAtLocation(findViewById(R.id.rl_content), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewpager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_UPLOAD){
                //刷新界面
                hasAttatchment = data.getBooleanExtra("has",false);
                EventBus.getDefault().post(new ReturnActivityRefreshEvent());
                updateReturnView();
            }
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case DETAIL:
                BaseEntity.ResultBean rb = result.getResult();
                ReturnDetailResponse rdr = (ReturnDetailResponse) rb.getData();
                bean = rdr.getReturnOrder();
                updateUI();
                loadingLayout.onSuccess(1,"暂无数据");
                break;
            case FINISHRETURN:
                FinishReturnResponse finishReturnResponse = (FinishReturnResponse) result.getResult().getData();
                ToastUtil.show(mContext, "退货成功");
                Intent intent = new Intent(mContext, ReturnSuccessActivity.class);
                intent.putExtra(INTENT_KEY_RESULTBEAN, finishReturnResponse);
                startActivity(intent);
                rlBottom.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
