package com.runwise.supply.firstpage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.R;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.tools.StatusBarUtil;
import com.runwise.supply.view.AutoLinefeedLayout;
import com.runwise.supply.view.YourScrollableViewPager;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.chenupt.dragtoplayout.DragTopLayout;

import static com.runwise.supply.R.id.cb1;
import static com.runwise.supply.R.id.cb2;
import static com.runwise.supply.R.id.cb3;
import static com.runwise.supply.R.id.headSdv;

/**
 * Created by libin on 2017/7/20.
 */

public class EvaluateActivity extends NetWorkActivity implements EvaluateAdapter.RatingBarClickCallback, CheckBox.OnCheckedChangeListener {

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
    @BindView(R.id.tablayout)
    TabLayout mTablayout;
    @BindView(R.id.viewpager)
    YourScrollableViewPager mViewpager;
    @BindView(R.id.drag_content_view)
    LinearLayout mDragContentView;
    @BindView(R.id.drag_layout)
    DragTopLayout mDragLayout;
    @BindView(R.id.tv_submit)
    TextView mTvSubmit;
    private OrderResponse.ListBean bean;
    Map<Integer,Integer> mRateMap;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.evaluate_layout);
        ButterKnife.bind(this);
        setTitleText(true, "评价");
        setTitleLeftIcon(true, R.drawable.nav_back);
        mDragLayout.setOverDrag(false);
        mDragLayout.setTouchMode(false);
        mDragLayout.listener(new DragTopLayout.PanelListener() {
            @Override
            public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                if (panelState == DragTopLayout.PanelState.EXPANDED){
                    mDragLayout.openTopView(true);
                }
            }

            @Override
            public void onSliding(float ratio) {

            }

            @Override
            public void onRefresh() {

            }
        });
        setDefaultDatas();
    }
    CategoryRespone categoryRespone;
    private void setDefaultDatas() {
        Bundle bundle = getIntent().getExtras();
        bean = bundle.getParcelable("order");
        selectProductTypeData(DataType.LENGCANGHUO,null);
        selectProductTypeData(DataType.ALL,null);
        if (bean != null){
            orderId = bean.getOrderID();
            for(OrderResponse.ListBean.LinesBean lb : bean.getLines()){
                mRateMap.put(Integer.valueOf(lb.getSaleOrderProductID()),Integer.valueOf(0));
            }
            if(bean.getWaybill() != null && bean.getWaybill().getDeliverUser() != null){
                String deliverName = bean.getWaybill().getDeliverUser().getName();
                mTvName.setText(deliverName);
                String imgUrl = bean.getWaybill().getDeliverUser().getAvatarUrl();
                FrecoFactory.getInstance(mContext).disPlay(mHeadSdv, Constant.BASE_URL+imgUrl);
            }else{
                mTvName.setText("未知");
            }
            String estimatTime = bean.getEstimatedTime();
            String endUploadTime = bean.getStartUnloadDatetime();
            StringBuffer sb = new StringBuffer("预计送达时间 ");
            sb.append(estimatTime)
                    .append("   ")
                    .append("开始卸货时间 ")
                    .append(endUploadTime);
            mTvTime.setText(sb.toString());

        }

        mCb1.setOnCheckedChangeListener(this);
        mCb2.setOnCheckedChangeListener(this);
        mCb3.setOnCheckedChangeListener(this);

        mCb4.setOnCheckedChangeListener(this);
        mCb5.setOnCheckedChangeListener(this);
        mCb6.setOnCheckedChangeListener(this);

        mCb7.setOnCheckedChangeListener(this);
        mCb8.setOnCheckedChangeListener(this);
        mCb9.setOnCheckedChangeListener(this);

        mCb10.setOnCheckedChangeListener(this);
        mCb11.setOnCheckedChangeListener(this);
        mCb12.setOnCheckedChangeListener(this);

        mCb13.setOnCheckedChangeListener(this);
        mCb14.setOnCheckedChangeListener(this);
        mCb15.setOnCheckedChangeListener(this);

        mCb16.setOnCheckedChangeListener(this);
        mCb17.setOnCheckedChangeListener(this);
        mCb18.setOnCheckedChangeListener(this);

        mCb19.setOnCheckedChangeListener(this);
        mCb20.setOnCheckedChangeListener(this);
        mCb21.setOnCheckedChangeListener(this);


        mRbDeliveryService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 2){
                    mVLine1.setVisibility(View.VISIBLE);
                    mEtDelivery.setVisibility(View.VISIBLE);
                    mAlflTagNegative.setVisibility(View.VISIBLE);
                    mAlflTagPositive.setVisibility(View.GONE);
                }else{
                    mVLine1.setVisibility(View.VISIBLE);
                    mEtDelivery.setVisibility(View.VISIBLE);
                    mAlflTagNegative.setVisibility(View.GONE);
                    mAlflTagPositive.setVisibility(View.VISIBLE);
                }
            }
        });
        mRbProductService.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating <= 2){
                    mVLine1Product.setVisibility(View.VISIBLE);
                    mEtProduct.setVisibility(View.VISIBLE);
                    mAlflTagProductNegative.setVisibility(View.VISIBLE);
                    mAlflTagProductPositive.setVisibility(View.GONE);
                }else{
                    mVLine1Product.setVisibility(View.VISIBLE);
                    mEtProduct.setVisibility(View.VISIBLE);
                    mAlflTagProductNegative.setVisibility(View.GONE);
                    mAlflTagProductPositive.setVisibility(View.VISIBLE);
                }
            }
        });

    }
//    @OnClick({R.id.coldBtn,R.id.freezeBtn,R.id.dryBtn,R.id.startSearchIB,
//            R.id.title_iv_left,R.id.onekeyTv,R.id.title_tv_rigth,R.id.searchCancleTv})
//    public void btnClick(View view){
//        switch (view.getId()){
//            case R.id.coldBtn:
//                //切换指示器
//                switchTabIndex(DataType.LENGCANGHUO);
//                //筛选列表数据
//                selectProductTypeData(DataType.LENGCANGHUO,null);
//                break;
//            case R.id.freezeBtn:
//                switchTabIndex(DataType.FREEZE);
//                selectProductTypeData(DataType.FREEZE,null);
//                break;
//            case R.id.dryBtn:
//                switchTabIndex(DataType.DRY);
//                selectProductTypeData(DataType.DRY,null);
//                break;
//            case R.id.title_iv_left:
//                dialog.setMessage("评价尚未提交\n您确定要返回吗?");
//                dialog.setMessageGravity();
//                dialog.setLeftBtnListener("返回首页", new CustomDialog.DialogListener() {
//                    @Override
//                    public void doClickButton(Button btn, CustomDialog dialog) {
//                        finish();
//                    }
//                });
//                dialog.setRightBtnListener("继续评价", new CustomDialog.DialogListener() {
//                    @Override
//                    public void doClickButton(Button btn, CustomDialog dialog) {
//
//                    }
//                });
//                dialog.show();
//                break;
//            case R.id.onekeyTv:
//                if (popupWindow == null){
//                    popView = LayoutInflater.from(this).inflate(R.layout.pop_rate_layout,null);
//                    popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
//                    popupWindow.setFocusable(true);
//                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
//                    popupWindow.setOutsideTouchable(true);
//                    RatingBar rb = (RatingBar) popView.findViewById(R.id.ratingbar);
//                    rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                        @Override
//                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                            if (fromUser){
//                                //更新全部商品的
//                               Iterator iterator =  rateMap.keySet().iterator();
//                                while (iterator.hasNext()){
//                                    Integer key = (Integer) iterator.next();
//                                    rateMap.put(key,Integer.valueOf((int)rating));
//                                }
//                                adapter.notifyDataSetChanged();
//                                searchAdapter.notifyDataSetChanged();
//                                popupWindow.dismiss();
//                            }
//                        }
//                    });
//                }
//                if (!popupWindow.isShowing()){
//                    int paddingX = CommonUtils.dip2px(mContext,100);
//                    int paddingY = CommonUtils.dip2px(mContext,10);
//                    popupWindow.showAsDropDown(onekeyTv,-paddingX,-paddingY);
//                }else{
//                    popupWindow.dismiss();
//                }
//                break;
//            case R.id.title_tv_rigth:
//                dialog.setTitle("提示");
//                dialog.setMessage("确认提交您的评价吗？");
//                dialog.setRightBtnListener("提交", new CustomDialog.DialogListener() {
//                    @Override
//                    public void doClickButton(Button btn, CustomDialog dialog) {
//                        //发送提交请求
//                        sendEvaluateRequest();
//                    }
//                });
//                dialog.show();
//                break;
//            case R.id.startSearchIB:
//                searchPart.setVisibility(View.VISIBLE);
//                inputPart.setVisibility(View.GONE);
//                break;
//            case R.id.searchCancleTv:
//                searchPart.setVisibility(View.GONE);
//                inputPart.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

    private void sendEvaluateRequest() {
//        showIProgressDialog();
//        EvaluateRequest request = new EvaluateRequest();
//        request.setQuality_evaluation(qualityEt.getText().toString());
//        request.setService_evaluation(serviceEt.getText().toString());
//        request.setService_score((int)serviceRb.getRating());
//        StringBuffer sb = new StringBuffer("/gongfu/assess/order/");
//        sb.append(orderId).append("/");
//        if(tags.size() > 0){
//            request.setTags(tags);
//        }
//        sendConnection(sb.toString(),request,ORDERREQUST,false,BaseEntity.ResultBean.class);
//        //对订单商品行的评价:gongfu/assess/order/line/
//        EvaluateLineRequest lineRequest = new EvaluateLineRequest();
//        List<EvaluateLineRequest.OrderBean> list = new ArrayList<>();
//        Iterator iterator = rateMap.keySet().iterator();
//        while(iterator.hasNext()){
//            Integer key = (Integer) iterator.next();
//            EvaluateLineRequest.OrderBean ob = new EvaluateLineRequest.OrderBean();
//            ob.setLine_id(key);
//            ob.setQuality_score(rateMap.get(key));
//            list.add(ob);
//        }
//        lineRequest.setOrder(list);
//        sendConnection("/gongfu/assess/order/line/",lineRequest,LINEREQUEST,false,BaseEntity.ResultBean.class);
    }

    private void selectProductTypeData(DataType type, String filterText) {
//        List<OrderResponse.ListBean.LinesBean> list = bean.getLines();
//        List<OrderResponse.ListBean.LinesBean> typeList = new ArrayList<>();
//        if (type == DataType.ALL && list != null) {
//            //在这里做过滤
//            if (TextUtils.isEmpty(filterText)){
//                typeList.addAll(list);
//                searchAdapter.setProductList(typeList,null);
//            }else{
//                for (OrderResponse.ListBean.LinesBean lb : list){
//                    String pid = String.valueOf(lb.getProductID());
//                    ProductBasicList.ListBean bb = ProductBasicUtils.getBasicMap(mContext).get(pid);
//                    if(bb.getName().contains(filterText)){
//                        typeList.add(lb);
//                        continue;
//                    }
//                }
//                searchAdapter.setProductList(typeList,filterText);
//            }
//            return;
//        }
//        for (OrderResponse.ListBean.LinesBean lb : list){
//            if (lb.getStockType().equals(type.getType())){
//                typeList.add(lb);
//            }
//        }
//        adapter.setProductList(typeList,null);
    }

    private void switchTabIndex(DataType type) {
//        int tabWidth = (CommonUtils.getScreenWidth(this) - CommonUtils.dip2px(this,30))/3;
//        int padding = (tabWidth - CommonUtils.dip2px(this,51))/2;
//        float translationX = 0.0F;
//        switch (type){
//            case FREEZE:
//                translationX = CommonUtils.dip2px(mContext,15) + tabWidth + padding;
//                break;
//            case LENGCANGHUO:
//                translationX = CommonUtils.dip2px(mContext,15) + padding;
//                break;
//            case DRY:
//                translationX = CommonUtils.dip2px(mContext,15) + 2*tabWidth + padding;
//                break;
//        }
//        ViewPropertyAnimator.animate(indexLine).translationX(translationX);
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
//        switch (where){
//            case ORDERREQUST:
//                flag++;
//                break;
//            case LINEREQUEST:
//                flag++;
//                break;
//            case CHANGE_ORDER:
//                dismissIProgressDialog();
//                EventBus.getDefault().post(new OrderUpdateEvent());
//                ToastUtil.show(mContext,"提交成功");
//                finish();
//                break;
//
//        }
//        if (flag >= 2){
//            ChangeOrderRequest changeOrderRequest = new ChangeOrderRequest();
//            changeOrderRequest.setState("rated");
//            sendConnection("/gongfu/order/"+ bean.getOrderID() +"/state",changeOrderRequest,CHANGE_ORDER,false,null);
//        }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//        StringBuffer sb = new StringBuffer();
//        sb.append(serviceEt.getText().toString());
//        switch (buttonView.getId()){
//            case cb1:
//                if(isChecked){
//                    cb1.setTextColor(Color.parseColor("#9ACC35"));
//                    if(sb.toString().length() > 0){
//                        sb.append("，");
//                    }
//                    sb.append(TIP1);
//                    if (!tags.contains(TIP1)){
//                        tags.add(TIP1);
//                    }
//                    serviceEt.setText(sb.toString());
//                    serviceEt.setSelection(sb.toString().length());
//                }else{
//                    if (tags.contains(TIP1)){
//                        tags.remove(TIP1);
//                    }
//                    cb1.setTextColor(Color.parseColor("#CCCCCC"));
//                    if (sb.toString().contains(TIP1)){
//                        String newStr = sb.toString().replaceAll(TIP1,"");
//                        serviceEt.setText(newStr);
//                        serviceEt.setSelection(newStr.length());
//                    }
//                }
//
//                break;
//            case cb2:
//                if(isChecked){
//                    if (!tags.contains(TIP2)){
//                        tags.add(TIP2);
//                    }
//                    cb2.setTextColor(Color.parseColor("#9ACC35"));
//                    if(sb.toString().length() > 0){
//                        sb.append("，");
//                    }
//                    sb.append(TIP2);
//                    serviceEt.setText(sb.toString());
//                    serviceEt.setSelection(sb.toString().length());
//                }else{
//                    if (tags.contains(TIP2)){
//                        tags.remove(TIP2);
//                    }
//                    cb2.setTextColor(Color.parseColor("#CCCCCC"));
//                    if (sb.toString().contains(TIP2)){
//                        String newStr = sb.toString().replaceAll(TIP2,"");
//                        serviceEt.setText(newStr);
//                        serviceEt.setSelection(newStr.length());
//                    }
//                }
//                break;
//            case cb3:
//                if(isChecked){
//                    if (!tags.contains(TIP3)){
//                        tags.add(TIP3);
//                    }
//                    cb3.setTextColor(Color.parseColor("#9ACC35"));
//                    if(sb.toString().length() > 0){
//                        sb.append("，");
//                    }
//                    sb.append(TIP3);
//                    serviceEt.setText(sb.toString());
//                    serviceEt.setSelection(sb.toString().length());
//                }else{
//                    if (tags.contains(TIP3)){
//                        tags.remove(TIP3);
//                    }
//                    cb3.setTextColor(Color.parseColor("#CCCCCC"));
//                    if (sb.toString().contains(TIP3)){
//                        String newStr = sb.toString().replaceAll(TIP3,"");
//                        serviceEt.setText(newStr);
//                        serviceEt.setSelection(newStr.length());
//                    }
//                }
//                break;
//        }
    }

    /**
     * 1到4：配送服务差评
     * 5到9：配送服务好评
     * 10到15:商品差评
     * 16到21:商品好评
     * @param view
     */
    @OnClick({cb1, cb2, cb3, R.id.cb4, R.id.cb5, R.id.cb6, R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10,
            R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14, R.id.cb15, R.id.cb16, R.id.cb17, R.id.cb18,
            R.id.cb19, R.id.cb20, R.id.cb21, R.id.tv_open, R.id.tv_submit})
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
            case R.id.tv_open:
                break;
            case R.id.tv_submit:
                break;
        }
    }
}
