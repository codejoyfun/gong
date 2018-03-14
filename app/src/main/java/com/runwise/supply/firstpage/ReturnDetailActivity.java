package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.ReturnActivityRefreshEvent;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.entity.FinishReturnResponse;
import com.runwise.supply.firstpage.entity.ReturnDetailResponse;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;
import com.runwise.supply.fragment.ReturnProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.chenupt.dragtoplayout.DragTopLayout;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.firstpage.ReturnSuccessActivity.INTENT_KEY_RESULTBEAN;
import static com.runwise.supply.firstpage.entity.OrderResponse.ListBean.TYPE_VENDOR_DELIVERY;

/**
 * Created by libin on 2017/8/1.
 */

public class ReturnDetailActivity extends NetWorkActivity {
    private static final int DETAIL = 0;
    private static final int FINISHRETURN = 1;
    public static final int REQUEST_CANCEL_RETURN_ORDER = 1 << 1;
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
    private ViewPager viewpager;
    @ViewInject(R.id.drag_layout)
    private DragTopLayout dragLayout;
    @ViewInject(R.id.tv_open)
    private ImageView ivOpen;

    private ReturnOrderBean.ListBean bean;
    public static final int REQUEST_CODE_UPLOAD = 1 << 0;

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
        dragLayout.setOverDrag(false);
    }

    private void initViews() {
        boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        if (!canSeePrice) {
            priceLL.setVisibility(View.GONE);
        }
        dateTv.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("return");
        if (bean != null) {
            rlBottom.setVisibility(View.VISIBLE);
            //不显示
            if (bean.getState().equals("process")) {
                if (bean.getDeliveryType().equals(TYPE_VENDOR_DELIVERY)){
                    doBtn.setText("完成退货");
                }else{
                    rlBottom.setVisibility(View.GONE);
                }
            } else {
                if (bean.getState().equals("draft")){
                    doBtn.setText("取消申请");
                }else{
                    rlBottom.setVisibility(View.GONE);
                }
            }

            payStateTv.setVisibility(View.VISIBLE);
            payStateValue.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

            hasAttatchment = bean.getHasAttachment() > 0;
            updateReturnView();
        } else {
            rlBottom.setVisibility(View.GONE);
        }
    }

    private void updateReturnView() {
        if (!hasAttatchment) {
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("未有凭证");
            uploadBtn.setText("上传凭证");

        } else {
            payStateTv.setText("退货凭证: ");
            payStateValue.setText("已有凭证");
            uploadBtn.setText("查看凭证");
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
                    } else if (bean.getState().equals("draft")) {
                        orderStateTv.setText("退货单已提交");
                    } else {
                        orderStateTv.setText("退货成功");
                    }
                }
            }
            StringBuffer sb = new StringBuffer("退货商品：");
            sb.append(NumberUtil.getIOrD(bean.getAmount())).append("件");
//                    .append(" 共").append(bean.getAmountTotal()).append("元");
            tipTv.setText(sb.toString());
            orderNumTv.setText(bean.getName());
            buyerValue.setText(bean.getCreateUser());
            orderTimeValue.setText(bean.getCreateDate());
            listDatas = bean.getLines();


//            recyclerView.getLayoutParams().height = list.size() * CommonUtils.dip2px(mContext, 86);
            countTv.setText(NumberUtil.getIOrD(bean.getAmount()) + "件");
            ygMoneyTv.setText(bean.getAmountTotal() + "元");
            rlBottom.setVisibility(View.VISIBLE);
            //不显示
            if (bean.getState().equals("process")) {
                doBtn.setText("完成退货");
            } else {
                if (bean.getState().equals("draft")){
                    doBtn.setText("取消申请");
                }else{
                    rlBottom.setVisibility(View.GONE);
                }
            }

            payStateTv.setVisibility(View.VISIBLE);
            payStateValue.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

            hasAttatchment = bean.getHasAttachment() > 0;
            updateReturnView();
            setUpDataForViewPage();
        }
    }

    private void setUpDataForViewPage() {
        List<Fragment> orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<ReturnOrderBean.ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<ReturnOrderBean.ListBean.LinesBean>());
        }

        for (ReturnOrderBean.ListBean.LinesBean linesBean : listDatas) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(linesBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<ReturnOrderBean.ListBean.LinesBean> linesBeen = map.get(listBean.getCategory());
                if (linesBeen == null) {
                    linesBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), linesBeen);
                }
                linesBeen.add(linesBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<ReturnOrderBean.ListBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newProductFragment((ArrayList<ReturnOrderBean.ListBean.LinesBean>) listDatas));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        viewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewpager.setOffscreenPageLimit(titles.size());
        tablayout.setupWithViewPager(viewpager);//将TabLayout和ViewPager关联起来
        viewpager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                IntEvent intEvent = new IntEvent();
                intEvent.setHeight(viewpager.getHeight());
                EventBus.getDefault().post(intEvent);
                viewpager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewpager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED)
                    dragLayout.toggleTopView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            tablayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            ivOpen.setVisibility(View.VISIBLE);
            tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        initPopWindow((ArrayList<String>) titles);
    }

    public ReturnProductFragment newProductFragment(ArrayList<ReturnOrderBean.ListBean.LinesBean> value) {
        ReturnProductFragment returnProductFragment = new ReturnProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(returnProductFragment.BUNDLE_KEY_LIST, value);
        bundle.putParcelable(returnProductFragment.BUNDLE_KEY_BEAN, bean);
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
        mProductTypeWindow = new PopupWindow(gridView, DensityUtil.getScreenW(getActivityContext()), DensityUtil.getScreenH(getActivityContext()) - (findViewById(R.id.title_bar).getHeight() + tablayout.getHeight()), true);
        mProductTypeWindow.setContentView(dialog);
//        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewpager.setCurrentItem(position);
                tablayout.getTabAt(position).select();
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
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

    //R.id.top_view设置onclick，防止点击dragview收起
    @OnClick({R.id.title_iv_left, R.id.gotoStateBtn, R.id.doBtn, R.id.uploadBtn, R.id.tv_open, R.id.top_view})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.gotoStateBtn:
                Intent intent = new Intent(mContext, OrderStateActivity.class);
                intent.putExtra("mode", true);
                //intent.putStringArrayListExtra("tracker",(ArrayList<String>) bean.getStateTracker());
                intent.putExtra("order", (Parcelable) bean);
                startActivity(intent);
                break;
            case R.id.doBtn:
                if (bean.getState().equals("draft")) {
                    cancelReturnOrder(bean.getReturnOrderID());
                } else {
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
                }

                break;
            case R.id.uploadBtn:
                Intent uIntent = new Intent(mContext, UploadReturnPicActivity.class);
                uIntent.putExtra("orderid", bean.getReturnOrderID());
                uIntent.putExtra("ordername", bean.getName());
                uIntent.putExtra("hasattachment", hasAttatchment);
                startActivityForResult(uIntent, REQUEST_CODE_UPLOAD);
                break;
            case R.id.tv_open:
                if (dragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
                    dragLayout.toggleTopView();
                    canShow = true;
                } else {
                    if (mProductTypeWindow.isShowing()) {
                        mProductTypeWindow.dismiss();
                    } else {
                        showPopWindow();
                    }
                }
                dragLayout.listener(new DragTopLayout.PanelListener() {
                    @Override
                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                            if (canShow) {
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

    private void cancelReturnOrder(int returnOrderId) {
        dialog.setTitle("提示");
        dialog.setMessageGravity();
        dialog.setMessage("确认取消申请退货?");
        dialog.setRightBtnListener("确认", new CustomDialog.DialogListener() {
            @Override
            public void doClickButton(Button btn, CustomDialog dialog) {
                String url = "/api/return_order/" + returnOrderId + "/cancel";
                Object obj = null;
                sendConnection(url, obj, REQUEST_CANCEL_RETURN_ORDER, true, BaseEntity.ResultBean.class);
            }
        });
        dialog.show();
    }


    boolean canShow = false;

    private void showPopWindow() {
        int y = findViewById(R.id.title_bar).getHeight() + tablayout.getHeight();
        mProductTypeWindow.showAtLocation(findViewById(R.id.rl_content), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewpager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == 200) {
            if (requestCode == REQUEST_CODE_UPLOAD) {
                //刷新界面
                hasAttatchment = data.getBooleanExtra("has", false);
                EventBus.getDefault().post(new ReturnActivityRefreshEvent());
                updateReturnView();
            }
        }
    }

    CategoryRespone categoryRespone;

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case DETAIL:
                BaseEntity.ResultBean rb = result.getResult();
                ReturnDetailResponse rdr = (ReturnDetailResponse) rb.getData();
                bean = rdr.getReturnOrder();
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
                loadingLayout.onSuccess(1, "暂无数据");

                break;
            case FINISHRETURN:
                FinishReturnResponse finishReturnResponse = (FinishReturnResponse) result.getResult().getData();
                ToastUtil.show(mContext, "退货成功");
                Intent intent = new Intent(mContext, ReturnSuccessActivity.class);
                intent.putExtra(INTENT_KEY_RESULTBEAN, finishReturnResponse);
                startActivity(intent);
                rlBottom.setVisibility(View.GONE);
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                updateUI();
                break;
            case REQUEST_CANCEL_RETURN_ORDER:
                finish();
                toast("取消申请退货成功!");
                break;
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
