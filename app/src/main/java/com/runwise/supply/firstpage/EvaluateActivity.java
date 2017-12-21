package com.runwise.supply.firstpage;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.FragmentAdapter;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.OrderDetailResponse;
import com.runwise.supply.firstpage.entity.ChangeOrderRequest;
import com.runwise.supply.firstpage.entity.EvaluateLineRequest;
import com.runwise.supply.firstpage.entity.EvaluateRequest;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.firstpage.entity.TagResponse;
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
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.Log;

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

public class EvaluateActivity extends NetWorkActivity {
    private static final int REQUEST_ORDER_DETAIL = 200;
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
    @BindView(R.id.alfl_tag_one)
    AutoLinefeedLayout mAlflTagOne;
    @BindView(R.id.alfl_tag_two)
    AutoLinefeedLayout mAlflTagTwo;
    @BindView(R.id.alfl_tag_three)
    AutoLinefeedLayout mAlflTagThree;
    @BindView(R.id.alfl_tag_four)
    AutoLinefeedLayout mAlflTagFour;
    @BindView(R.id.alfl_tag_five)
    AutoLinefeedLayout mAlflTagFive;
    @BindView(R.id.alfl_tag_product_one)
    AutoLinefeedLayout mAlflTagProductOne;
    @BindView(R.id.alfl_tag_product_two)
    AutoLinefeedLayout mAlflTagProductTwo;
    @BindView(R.id.alfl_tag_product_three)
    AutoLinefeedLayout mAlflTagProductThree;
    @BindView(R.id.alfl_tag_product_four)
    AutoLinefeedLayout mAlflTagProductFour;
    @BindView(R.id.alfl_tag_product_five)
    AutoLinefeedLayout mAlflTagProductFive;
    private OrderResponse.ListBean bean;
    //维护星星分数的集合,LineId -----> 星星分数
    public Map<Integer, Integer> mRateMap = new HashMap<>();
    int orderId;
    private int flag = 0;

    private static final int ORDERREQUST = 1;
    private static final int LINEREQUEST = 2;
    private static final int CHANGE_ORDER = 3;
    public static final int REQUEST_GET_TAG = 4;

    float mDeliveryRating;
    float mProductRating;

    CategoryRespone categoryRespone;

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

        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");

        //没有传商品列表，查询订单详情获取商品列表
        if(bean.getLines()==null || bean.getLines().isEmpty()){
            requestOrderDetail();
        }else{
            setDefaultDatas();
        }

        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
    }

    /**
     * 需要查详情拿商品列表
     */
    private void requestOrderDetail(){
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/order/");
        sb.append(bean.getOrderID()).append("/");
        sendConnection(sb.toString(), request, REQUEST_ORDER_DETAIL, true, OrderDetailResponse.class);
    }

    private void getCategory() {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setUser_id(Integer.parseInt(GlobalApplication.getInstance().getUid()));
        sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
    }

    private void getTags() {
        Object o = null;
        sendConnection("/gongfu/assess/tag/list", o, REQUEST_GET_TAG, false, TagResponse.class);

    }

    private void setDefaultDatas() {
        getCategory();
        getTags();
        if (bean != null) {
            orderId = bean.getOrderID();
            for (OrderResponse.ListBean.LinesBean lb : bean.getLines()) {
                mRateMap.put(Integer.valueOf(lb.getSaleOrderProductID()), Integer.valueOf(0));
            }

            if (bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY) || bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
                    || bean.getDeliveryType().equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)) {
//                findViewById(R.id.ic_evaluate_deliveryman).setVisibility(View.GONE);

            }

//            if (bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null) {
//                String deliverName = bean.getWaybill().getDeliverUser().getName();
//                mTvName.setText(deliverName);
//                String imgUrl = bean.getWaybill().getDeliverUser().getAvatarUrl();
//                FrecoFactory.getInstance(mContext).disPlay(mHeadSdv, Constant.BASE_URL + imgUrl);
//                mHeadSdv.setImageResource(R.drawable.deliveryman_header);
//            } else {
//                mTvName.setText("配送服务");
//                mHeadSdv.setImageResource(R.drawable.delivery_evaluate_ico);
//            }
            mTvName.setText("配送服务");
            mHeadSdv.setImageResource(R.drawable.delivery_evaluate_ico);

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

        mAlflTagOne.setVisibility(View.GONE);
        mAlflTagTwo.setVisibility(View.GONE);
        mAlflTagThree.setVisibility(View.GONE);
        mAlflTagFour.setVisibility(View.GONE);
        mAlflTagFive.setVisibility(View.GONE);

        mRbDeliveryService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mDeliveryRating = rating;
                setDeliveryTagLayout((int) rating);
                if (rating > 0) {
                    mTvSubmit.setBackgroundResource(R.color.colorAccent);
                    mVLine1.setVisibility(View.VISIBLE);
                    mEtDelivery.setVisibility(View.VISIBLE);
                } else {
                    mVLine1.setVisibility(View.GONE);
                    mEtDelivery.setVisibility(View.GONE);
                    if (mProductRating < 1) {
                        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
                    }
                    return;
                }

            }
        });
        mVLine1Product.setVisibility(View.GONE);
        mEtProduct.setVisibility(View.GONE);
        findViewById(R.id.ic_bar).setVisibility(View.GONE);

        mAlflTagProductOne.setVisibility(View.GONE);
        mAlflTagProductTwo.setVisibility(View.GONE);
        mAlflTagProductThree.setVisibility(View.GONE);
        mAlflTagProductFour.setVisibility(View.GONE);
        mAlflTagProductFive.setVisibility(View.GONE);

        mRbProductService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mProductRating = rating;
                setProductTagLayout((int) rating);
                if (rating > 0) {
                    mTvSubmit.setBackgroundResource(R.color.colorAccent);
                } else {
                    mVLine1Product.setVisibility(View.GONE);
                    mEtProduct.setVisibility(View.GONE);
                    findViewById(R.id.ic_bar).setVisibility(View.GONE);
                    if (mDeliveryRating < 1) {
                        mTvSubmit.setBackgroundResource(R.color.textColorSecondary);
                    }
                    return;
                }
                mVLine1Product.setVisibility(View.VISIBLE);
                mEtProduct.setVisibility(View.VISIBLE);
                findViewById(R.id.ic_bar).setVisibility(View.VISIBLE);

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

    private void setDeliveryTagLayout(int rate) {
        mAlflTagOne.setVisibility(rate == 1 ? View.VISIBLE : View.GONE);
        mAlflTagTwo.setVisibility(rate == 2 ? View.VISIBLE : View.GONE);
        mAlflTagThree.setVisibility(rate == 3 ? View.VISIBLE : View.GONE);
        mAlflTagFour.setVisibility(rate == 4 ? View.VISIBLE : View.GONE);
        mAlflTagFive.setVisibility(rate == 5 ? View.VISIBLE : View.GONE);
    }

    private void setProductTagLayout(int rate) {
        mAlflTagProductOne.setVisibility(rate == 1 ? View.VISIBLE : View.GONE);
        mAlflTagProductTwo.setVisibility(rate == 2 ? View.VISIBLE : View.GONE);
        mAlflTagProductThree.setVisibility(rate == 3 ? View.VISIBLE : View.GONE);
        mAlflTagProductFour.setVisibility(rate == 4 ? View.VISIBLE : View.GONE);
        mAlflTagProductFive.setVisibility(rate == 5 ? View.VISIBLE : View.GONE);
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
                if (mDeliveryRating < 1 && mProductRating < 1) {
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
        switch ((int) mDeliveryRating) {
            case 1:
                return getTags(mAlflTagOne);
            case 2:
                return getTags(mAlflTagTwo);
            case 3:
                return getTags(mAlflTagThree);
            case 4:
                return getTags(mAlflTagFour);
            case 5:
                return getTags(mAlflTagFive);
        }
        return new ArrayList<>();
    }

    private List<String> getTags(AutoLinefeedLayout autoLinefeedLayout){
        List<String> tags = new ArrayList<>();
        int childCount = autoLinefeedLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBox checkBox = (CheckBox) autoLinefeedLayout.getChildAt(i);
            if (checkBox.isChecked()){
                tags.add(checkBox.getText().toString());
            }
        }
        return tags;
    }

    List<String> getProductTags() {
        switch ((int) mProductRating) {
            case 1:
                return getTags(mAlflTagProductOne);
            case 2:
                return getTags(mAlflTagProductTwo);
            case 3:
                return getTags(mAlflTagProductThree);
            case 4:
                return getTags(mAlflTagProductFour);
            case 5:
                return getTags(mAlflTagProductFive);
        }
        return new ArrayList<>();
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
            case REQUEST_ORDER_DETAIL:
                OrderDetailResponse orderDetailResponse = (OrderDetailResponse) result.getResult().getData();
                bean = orderDetailResponse.getOrder();
                setDefaultDatas();
                break;
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
            case REQUEST_GET_TAG:
                TagResponse tagResponse = (TagResponse) result.getResult().getData();
                setUpTag(tagResponse);
                break;
        }
        if (flag >= 2) {
            ChangeOrderRequest changeOrderRequest = new ChangeOrderRequest();
            changeOrderRequest.setState("rated");
            sendConnection("/gongfu/order/" + bean.getOrderID() + "/state", changeOrderRequest, CHANGE_ORDER, false, null);
        }
    }

    private void setUpTag(TagResponse tagResponse) {
        HashMap<String, List<String>> service_tags = tagResponse.getService_tags();
        addTags(mAlflTagOne, service_tags.get("1"));
        addTags(mAlflTagTwo, service_tags.get("2"));
        addTags(mAlflTagThree, service_tags.get("3"));
        addTags(mAlflTagFour, service_tags.get("4"));
        addTags(mAlflTagFive, service_tags.get("5"));

        HashMap<String, List<String>> quantity_tags = tagResponse.getQuantity_tags();
        addTags(mAlflTagProductOne, quantity_tags.get("1"));
        addTags(mAlflTagProductTwo, quantity_tags.get("2"));
        addTags(mAlflTagProductThree, quantity_tags.get("3"));
        addTags(mAlflTagProductFour, quantity_tags.get("4"));
        addTags(mAlflTagProductFive, quantity_tags.get("5"));

    }

    private void addTags(AutoLinefeedLayout autoLinefeedLayout, List<String> tags) {
        for (String tag : tags) {
            CheckBox checkBox = (CheckBox) LayoutInflater.from(getActivityContext()).inflate(R.layout.checkbox_evaluate_tag, null);
            checkBox.setText(tag);
            autoLinefeedLayout.addView(checkBox);
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

    //点空白收起键盘
    @OnClick({R.id.top_view})
    public void t(View v){
        hideKeyboard();
    }

    InputMethodManager imm;
    private void hideKeyboard(){
        if(imm==null)imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if(imm!=null && v!=null)imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
