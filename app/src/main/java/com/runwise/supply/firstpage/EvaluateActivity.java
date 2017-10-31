package com.runwise.supply.firstpage;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.firstpage.entity.ChangeOrderRequest;
import com.runwise.supply.firstpage.entity.EvaluateLineRequest;
import com.runwise.supply.firstpage.entity.EvaluateRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.fragment.EvaluateProductFragment;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.OrderUpdateEvent;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.AutoLinefeedLayout;
import com.runwise.supply.view.YourScrollableViewPager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.R.id.cb1;
import static com.runwise.supply.R.id.cb2;
import static com.runwise.supply.R.id.cb3;
import static com.runwise.supply.R.id.headSdv;
import static com.runwise.supply.R.id.tablayout;
import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateActivity extends NetWorkActivity implements EvaluateAdapter.RatingBarClickCallback {

    @BindView(R.id.title_iv_left)
    ImageView mTitleIvLeft;
    @BindView(R.id.title_tv_left)
    TextView mTitleTvLeft;
    @BindView(R.id.left_layout)
    LinearLayout mLeftLayout;
    @BindView(R.id.title_iv_rigth2)
    ImageView mTitleIvRigth2;
    @BindView(R.id.title_iv_rigth)
    ImageView mTitleIvRigth;
    @BindView(R.id.title_tv_rigth)
    TextView mTitleTvRigth;
    @BindView(R.id.right_layout)
    LinearLayout mRightLayout;
    @BindView(R.id.title_tv_title)
    TextView mTitleTvTitle;
    @BindView(R.id.title_iv_title)
    ImageView mTitleIvTitle;
    @BindView(R.id.mid_layout)
    LinearLayout mMidLayout;
    @BindView(headSdv)
    SimpleDraweeView mHeadSdv;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.v_line_product)
    View mVLineProduct;
    @BindView(R.id.tv_evaluate_product)
    TextView mTvEvaluateProduct;
    @BindView(R.id.rb_delivery_service)
    RatingBar mRbDeliveryService;
    @BindView(cb1)
    CheckBox mCb1;
    @BindView(cb2)
    CheckBox mCb2;
    @BindView(cb3)
    CheckBox mCb3;
    @BindView(R.id.cb4)
    CheckBox mCb4;
    @BindView(R.id.alfl_tag_negative)
    AutoLinefeedLayout mAlflTagNegative;
    @BindView(R.id.cb5)
    CheckBox mCb5;
    @BindView(R.id.cb6)
    CheckBox mCb6;
    @BindView(R.id.cb7)
    CheckBox mCb7;
    @BindView(R.id.cb8)
    CheckBox mCb8;
    @BindView(R.id.cb9)
    CheckBox mCb9;
    @BindView(R.id.alfl_tag_positive)
    AutoLinefeedLayout mAlflTagPositive;
    @BindView(R.id.v_line1_product)
    View mVLine1Product;
    @BindView(R.id.et_product)
    EditText mEtProduct;
    @BindView(R.id.sdv_product)
    SimpleDraweeView mSdvProduct;
    @BindView(R.id.tv_product_name)
    TextView mTvProductName;
    @BindView(R.id.v_line)
    View mVLine;
    @BindView(R.id.tv_deliveryman)
    TextView mTvDeliveryman;
    @BindView(R.id.rb_product_service)
    RatingBar mRbProductService;
    @BindView(R.id.cb10)
    CheckBox mCb10;
    @BindView(R.id.cb11)
    CheckBox mCb11;
    @BindView(R.id.cb12)
    CheckBox mCb12;
    @BindView(R.id.cb13)
    CheckBox mCb13;
    @BindView(R.id.cb14)
    CheckBox mCb14;
    @BindView(R.id.cb15)
    CheckBox mCb15;
    @BindView(R.id.alfl_tag_product_negative)
    AutoLinefeedLayout mAlflTagProductNegative;
    @BindView(R.id.cb16)
    CheckBox mCb16;
    @BindView(R.id.cb17)
    CheckBox mCb17;
    @BindView(R.id.cb18)
    CheckBox mCb18;
    @BindView(R.id.cb19)
    CheckBox mCb19;
    @BindView(R.id.cb20)
    CheckBox mCb20;
    @BindView(R.id.cb21)
    CheckBox mCb21;
    @BindView(R.id.alfl_tag_product_positive)
    AutoLinefeedLayout mAlflTagProductPositive;
    @BindView(R.id.v_line1)
    View mVLine1;
    @BindView(R.id.et_delivery)
    EditText mEtDelivery;
    @BindView(R.id.top_view)
    LinearLayout mTopView;
    @BindView(R.id.tv_open)
    ImageView mTvOpen;
    @BindView(tablayout)
    TabLayout mTablayout;
    @BindView(R.id.viewpager)
    YourScrollableViewPager mViewpager;
//    @BindView(R.id.drag_content_view)
//    LinearLayout mDragContentView;
//    @BindView(R.id.drag_layout)
//    DragTopLayout mDragLayout;
    @BindView(R.id.tv_submit)
    TextView mTvSubmit;
    private OrderResponse.ListBean bean;
    //维护星星分数的集合,LineId -----> 星星分数
    public Map<Integer, Integer> mRateMap = new HashMap<>();
    int orderId;
    private int flag = 0;

    private static final int ORDERREQUST = 1;
    private static final int LINEREQUEST = 2;
    private static final int CHANGE_ORDER = 3;

    float mDeliveryRating;
    float mProductRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.evaluate_layout);
        ButterKnife.bind(this);
        setTitleText(true, "评价");
        setTitleLeftIcon(true, R.drawable.nav_back);
//        mDragLayout.setOverDrag(false);
//        mDragLayout.setTouchMode(false);
        setDefaultDatas();
        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
    }

    CategoryRespone categoryRespone;

    private void getCategory() {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    private void setDefaultDatas() {
        getCategory();
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        if (bean != null) {
            orderId = bean.getOrderID();
            for (OrderResponse.ListBean.LinesBean lb : bean.getLines()) {
                mRateMap.put(Integer.valueOf(lb.getSaleOrderProductID()), Integer.valueOf(0));
            }

            if (bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)|| bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
                    || bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)){
//                findViewById(R.id.ic_evaluate_deliveryman).setVisibility(View.GONE);

            }

            if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null) {
                String deliverName = bean.getWaybill().getDeliverUser().getName();
                mTvName.setText(deliverName);
                String imgUrl = bean.getWaybill().getDeliverUser().getAvatarUrl();
                FrecoFactory.getInstance(mContext).disPlay(mHeadSdv, Constant.BASE_URL + imgUrl);
                mHeadSdv.setImageResource(R.drawable.deliveryman_header);
            } else {
                mTvName.setText("配送服务");
                mHeadSdv.setImageResource(R.drawable.delivery_evaluate_ico);
            }
            String estimatTime = bean.getEstimatedTime();
            String endUploadTime = bean.getStartUnloadDatetime();
//            StringBuffer sb = new StringBuffer("预计送达: ");
//            sb.append(TimeUtils.getHM(estimatTime));

//            if (!TextUtils.isEmpty(endUploadTime)) {
//                sb.append("\n开始卸货: ")
//                        .append(TimeUtils.getHM(endUploadTime));
//            }
//            mTvTime.setText(sb.toString());

        }
        mVLine1.setVisibility(View.GONE);
        mEtDelivery.setVisibility(View.GONE);
        mAlflTagNegative.setVisibility(View.GONE);
        mAlflTagPositive.setVisibility(View.GONE);
        mRbDeliveryService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mDeliveryRating = rating;
                if (rating >0){
                    mTvSubmit.setBackgroundResource(R.color.colorAccent);
                }else{
                    mVLine1.setVisibility(View.GONE);
                    mEtDelivery.setVisibility(View.GONE);
                    mAlflTagNegative.setVisibility(View.GONE);
                    mAlflTagPositive.setVisibility(View.GONE);
                    if (mProductRating < 1){
                        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
                    }
                    return;
                }
                if (rating <= 2) {
                    mVLine1.setVisibility(View.VISIBLE);
                    mEtDelivery.setVisibility(View.VISIBLE);
                    mAlflTagNegative.setVisibility(View.VISIBLE);
                    mAlflTagPositive.setVisibility(View.GONE);
                } else {
                    mVLine1.setVisibility(View.VISIBLE);
                    mEtDelivery.setVisibility(View.VISIBLE);
                    mAlflTagNegative.setVisibility(View.GONE);
                    mAlflTagPositive.setVisibility(View.VISIBLE);
                }
            }
        });
        mVLine1Product.setVisibility(View.GONE);
        mEtProduct.setVisibility(View.GONE);
        findViewById(R.id.ic_bar).setVisibility(View.GONE);
        mAlflTagProductNegative.setVisibility(View.GONE);
        mAlflTagProductPositive.setVisibility(View.GONE);
        mRbProductService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mProductRating = rating;
                if (rating >0){
                    mTvSubmit.setBackgroundResource(R.color.colorAccent);
                }else{
                    mVLine1Product.setVisibility(View.GONE);
                    mEtProduct.setVisibility(View.GONE);
                    findViewById(R.id.ic_bar).setVisibility(View.GONE);
                    mAlflTagProductNegative.setVisibility(View.GONE);
                    mAlflTagProductPositive.setVisibility(View.GONE);
                    if (mDeliveryRating < 1){
                        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
                    }
                    return;
                }
                if (rating <= 2) {
                    mVLine1Product.setVisibility(View.VISIBLE);
                    mEtProduct.setVisibility(View.VISIBLE);
                    findViewById(R.id.ic_bar).setVisibility(View.VISIBLE);
                    mAlflTagProductNegative.setVisibility(View.VISIBLE);
                    mAlflTagProductPositive.setVisibility(View.GONE);
                } else {
                    mVLine1Product.setVisibility(View.VISIBLE);
                    mEtProduct.setVisibility(View.VISIBLE);
                    findViewById(R.id.ic_bar).setVisibility(View.VISIBLE);
                    mAlflTagProductNegative.setVisibility(View.GONE);
                    mAlflTagProductPositive.setVisibility(View.VISIBLE);
                }
                for (OrderResponse.ListBean.LinesBean linesBean : bean.getLines()) {
                    mRateMap.put(linesBean.getSaleOrderProductID(), (int) rating);
                }
                if (orderProductFragmentList != null) {
                    for (Fragment evaluateProductFragment : orderProductFragmentList) {
                        ((EvaluateProductFragment) evaluateProductFragment).refresh();
                    }
                }
            }
        });

    }

    @OnClick({R.id.title_iv_left, R.id.tv_submit})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.title_iv_left:
                dialog.setMessage("评价尚未提交\n您确定要返回吗?");
                dialog.setMessageGravity();
                dialog.setLeftBtnListener("确认返回", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        finish();
                    }
                });
                dialog.setRightBtnListener("继续评价", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {

                    }
                });
                dialog.show();
                break;
            case R.id.tv_submit:
                if (mDeliveryRating<1&&mProductRating<1){
                    return;
                }
                dialog.setTitle("提示");
                dialog.setMessage("确认提交您的评价吗？");
                dialog.setRightBtnListener("提交", new CustomDialog.DialogListener() {
                    @Override
                    public void doClickButton(Button btn, CustomDialog dialog) {
                        //发送提交请求
                        sendEvaluateRequest();
                    }
                });
                dialog.show();
                break;
        }
    }

    List<String> getService_tags() {
        List<String> service_tags = new ArrayList<>();
        if (mDeliveryRating <= 2) {
            if (mCb1.isChecked()) {
                service_tags.add(mCb1.getText().toString());
            }
            if (mCb2.isChecked()) {
                service_tags.add(mCb2.getText().toString());
            }
            if (mCb3.isChecked()) {
                service_tags.add(mCb3.getText().toString());
            }
            if (mCb4.isChecked()) {
                service_tags.add(mCb4.getText().toString());
            }
        } else {
            if (mCb5.isChecked()) {
                service_tags.add(mCb5.getText().toString());
            }
            if (mCb6.isChecked()) {
                service_tags.add(mCb6.getText().toString());
            }
            if (mCb7.isChecked()) {
                service_tags.add(mCb7.getText().toString());
            }
            if (mCb8.isChecked()) {
                service_tags.add(mCb8.getText().toString());
            }
            if (mCb9.isChecked()) {
                service_tags.add(mCb9.getText().toString());
            }
        }
        return service_tags;
    }

    List<String> getProductTags() {
        List<String> product_tags = new ArrayList<>();
        if (mProductRating <= 2) {
            if (mCb10.isChecked()) {
                product_tags.add(mCb10.getText().toString());
            }
            if (mCb11.isChecked()) {
                product_tags.add(mCb11.getText().toString());
            }
            if (mCb12.isChecked()) {
                product_tags.add(mCb12.getText().toString());
            }
            if (mCb13.isChecked()) {
                product_tags.add(mCb13.getText().toString());
            }
            if (mCb14.isChecked()) {
                product_tags.add(mCb14.getText().toString());
            }
            if (mCb15.isChecked()) {
                product_tags.add(mCb15.getText().toString());
            }
        } else {
            if (mCb16.isChecked()) {
                product_tags.add(mCb16.getText().toString());
            }
            if (mCb17.isChecked()) {
                product_tags.add(mCb17.getText().toString());
            }
            if (mCb18.isChecked()) {
                product_tags.add(mCb18.getText().toString());
            }
            if (mCb19.isChecked()) {
                product_tags.add(mCb19.getText().toString());
            }
            if (mCb20.isChecked()) {
                product_tags.add(mCb20.getText().toString());
            }
            if (mCb21.isChecked()) {
                product_tags.add(mCb21.getText().toString());
            }
        }
        return product_tags;
    }

    private void sendEvaluateRequest() {
        showIProgressDialog();
        EvaluateRequest request = new EvaluateRequest();
        request.setQuality_evaluation(mEtProduct.getText().toString());
        request.setService_evaluation(mEtDelivery.getText().toString());
        request.setService_score((int) mRbDeliveryService.getRating());
        StringBuffer sb = new StringBuffer("/gongfu/assess/order/");
        sb.append(orderId).append("/");
        List<String> serviceTags = getService_tags();
        if (serviceTags.size() > 0) {
            request.setService_tags(serviceTags);
        }
        List<String> productTags = getProductTags();
        if (productTags.size() > 0) {
            request.setQuality_tags(productTags);
        }

        sendConnection(sb.toString(), request, ORDERREQUST, false, BaseEntity.ResultBean.class);
        //对订单商品行的评价:gongfu/assess/order/line/
        EvaluateLineRequest lineRequest = new EvaluateLineRequest();
        List<EvaluateLineRequest.OrderBean> list = new ArrayList<>();
        Iterator iterator = mRateMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = (Integer) iterator.next();
            EvaluateLineRequest.OrderBean ob = new EvaluateLineRequest.OrderBean();
            ob.setLine_id(key);
            ob.setQuality_score(mRateMap.get(key));
            list.add(ob);
        }
        lineRequest.setOrder(list);
        sendConnection("/gongfu/assess/order/line/", lineRequest, LINEREQUEST, false, BaseEntity.ResultBean.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case ORDERREQUST:
                flag++;
                break;
            case LINEREQUEST:
                flag++;
                break;
            case CHANGE_ORDER:
                dismissIProgressDialog();
                EventBus.getDefault().post(new OrderUpdateEvent());
                ToastUtil.show(mContext, "评价成功");
                finish();
//                Intent intent = new Intent(getActivityContext(),EvaluateSuccessActivity.class);
//                intent.putExtra("orderid",orderId);
//                startActivity(intent);
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage();
                break;

        }
        if (flag >= 2) {
            ChangeOrderRequest changeOrderRequest = new ChangeOrderRequest();
            changeOrderRequest.setState("rated");
            sendConnection("/gongfu/order/" + bean.getOrderID() + "/state", changeOrderRequest, CHANGE_ORDER, false, null);
        }
    }

    List<Fragment> orderProductFragmentList;

    private void setUpDataForViewPage() {
        orderProductFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<OrderResponse.ListBean.LinesBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<OrderResponse.ListBean.LinesBean>());
        }
        for (OrderResponse.ListBean.LinesBean linesBean : bean.getLines()) {
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(linesBean.getProductID()));
            if (listBean != null && !TextUtils.isEmpty(listBean.getCategory())) {
                ArrayList<OrderResponse.ListBean.LinesBean> linesBeen = map.get(listBean.getCategory());
                if (linesBeen == null) {
                    linesBeen = new ArrayList<>();
                    map.put(listBean.getCategory(), linesBeen);
                }
                linesBeen.add(linesBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<OrderResponse.ListBean.LinesBean> value = map.get(category);
            orderProductFragmentList.add(newEvaluateProductFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        orderProductFragmentList.add(0, newEvaluateProductFragment((ArrayList<OrderResponse.ListBean.LinesBean>) bean.getLines()));

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), orderProductFragmentList, titles);
        mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        mTablayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewpager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (titles.size() <= TAB_EXPAND_COUNT) {
            mTvOpen.setVisibility(View.GONE);
            mTablayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTvOpen.setVisibility(View.VISIBLE);
            mTablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        initPopWindow((ArrayList<String>) titles);
    }

    public EvaluateProductFragment newEvaluateProductFragment(ArrayList<OrderResponse.ListBean.LinesBean> value) {
        EvaluateProductFragment evaluateProductFragment = new EvaluateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EvaluateProductFragment.INTENT_KEY_LIST, value);
        evaluateProductFragment.setArguments(bundle);
        return evaluateProductFragment;
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void rateChanged(Integer lineId, Integer rateScore) {

//        rateMap.put(lineId,rateScore);
//        if (searchPart.getVisibility() == View.VISIBLE){
//            adapter.notifyDataSetChanged();
//        }
    }


    /**
     * 1到4：配送服务差评
     * 5到9：配送服务好评
     * 10到15:商品差评
     * 16到21:商品好评
     *
     * @param view
     */
    @OnClick({cb1, cb2, cb3, R.id.cb4, R.id.cb5, R.id.cb6, R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10,
            R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14, R.id.cb15, R.id.cb16, R.id.cb17, R.id.cb18,
            R.id.cb19, R.id.cb20, R.id.cb21, R.id.tv_open})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            ///////////////配送服务差评////////////////////////
            case cb1:
            case cb2:
            case cb3:
            case R.id.cb4:
                mVLine1.setVisibility(View.VISIBLE);
                mEtDelivery.setVisibility(View.VISIBLE);
                mAlflTagNegative.setVisibility(View.VISIBLE);
                break;
            ///////////////配送服务好评////////////////////////
            case R.id.cb5:
            case R.id.cb6:
            case R.id.cb7:
            case R.id.cb8:
            case R.id.cb9:
                mVLine1.setVisibility(View.VISIBLE);
                mEtDelivery.setVisibility(View.VISIBLE);
                mAlflTagPositive.setVisibility(View.VISIBLE);
                break;
            ///////////////商品差评////////////////////////
            case R.id.cb10:
            case R.id.cb11:
            case R.id.cb12:
            case R.id.cb13:
            case R.id.cb14:
            case R.id.cb15:
                break;
            ///////////////商品好评////////////////////////
            case R.id.cb16:
            case R.id.cb17:
            case R.id.cb18:
            case R.id.cb19:
            case R.id.cb20:
            case R.id.cb21:
                break;
//            case R.id.tv_open:
//                if (mDragLayout.getState() == DragTopLayout.PanelState.EXPANDED) {
//                    mDragLayout.toggleTopView();
//                    canShow = true;
//                } else {
//                    if (mProductTypeWindow.isShowing()) {
//                        mProductTypeWindow.dismiss();
//                    } else {
//                        showPopWindow();
//                    }
//                }
//                mDragLayout.listener(new DragTopLayout.PanelListener() {
//                    @Override
//                    public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
//                        if (panelState == DragTopLayout.PanelState.COLLAPSED) {
//                            if (canShow) {
//                                showPopWindow();
//                                canShow = false;
//                            }
//                        } else {
//                            mProductTypeWindow.dismiss();
//                        }
//                    }
//
//                    @Override
//                    public void onSliding(float ratio) {
//
//                    }
//
//                    @Override
//                    public void onRefresh() {
//
//                    }
//                });
//                break;
        }
    }

    private void showPopWindow() {
        int y = findViewById(R.id.title_bar).getHeight() + mTablayout.getHeight();
        mProductTypeWindow.showAtLocation(getRootView(EvaluateActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(mViewpager.getCurrentItem());
        mTvOpen.setImageResource(R.drawable.arrow_up);
    }

    boolean canShow = false;
    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, DensityUtil.getScreenW(getActivityContext()), DensityUtil.getScreenH(getActivityContext()) - (findViewById(R.id.title_bar).getHeight() + mTablayout.getHeight()), true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                mViewpager.setCurrentItem(position);
                mTablayout.getTabAt(position).select();
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
                mTvOpen.setImageResource(R.drawable.arrow);
            }
        });
    }
}
