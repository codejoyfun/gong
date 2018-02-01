package com.runwise.supply.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.event.ProductCountUpdateEvent;
import com.runwise.supply.orderpage.ProductActivityV2;
import com.runwise.supply.orderpage.ProductValueDialog;
import com.runwise.supply.orderpage.entity.ProductData;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.base.util.UmengUtil.EVENT_ID_PRODUCT_COUNT_MODIFY;

/**
 * Created by mike on 2018/1/23.
 */

public class ProductImageDialog extends Dialog {
    @BindView(R.id.sdv_product_image)
    SimpleDraweeView mSdvProductImage;
    @BindView(R.id.tv_product_name)
    TextView mTvProductName;
    @BindView(R.id.tv_product_code)
    TextView mTvProductCode;
    @BindView(R.id.v_line)
    View mVLine;
    @BindView(R.id.tv_product_content)
    TextView mTvProductContent;
    @BindView(R.id.iv_product_sale)
    TextView mIvProductSale;
    @BindView(R.id.tv_product_price)
    TextView mTvProductPrice;
    @BindView(R.id.tv_product_price_unit)
    TextView mTvProductPriceUnit;
    @BindView(R.id.iv_product_reduce)
    ImageButton mIvProductReduce;
    @BindView(R.id.tv_product_count)
    TextView mTvProductCount;
    @BindView(R.id.iv_product_add)
    ImageButton mIvProductAdd;
    @BindView(R.id.ll_item_product_btns)
    LinearLayout mLlItemProductBtns;

    private boolean mModify = false;

    public void setListBean(ProductData.ListBean listBean) {
        mListBean = listBean;
    }

    ProductData.ListBean mListBean;
    ProductActivityV2.ProductCountSetter productCountSetter;
    boolean mCanSeePrice = false;
    DecimalFormat df = new DecimalFormat("#.##");
    public void setProductCountSetter(ProductActivityV2.ProductCountSetter productCountSetter){
        this.productCountSetter = productCountSetter;
    }

    public ProductImageDialog(@NonNull Context context) {
        super(context, R.style.CustomProgressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_product_image);
        ButterKnife.bind(this);
        Window window = getWindow();
        window.getAttributes().gravity = Gravity.CENTER;
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCanSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        setUp();
    }
    
    private void setUp(){
        if (mListBean == null){
            return;
        }
        //标签
        if(TextUtils.isEmpty(mListBean.getProductTag())){
            mIvProductSale.setVisibility(View.GONE);
        }else{
            mIvProductSale.setVisibility(View.VISIBLE);
        }

//        final int count = mCountMap.get(mListBean)==null?0:mCountMap.get(mListBean);
        double count = productCountSetter.getCount(mListBean);
        mTvProductCount.setText(NumberUtil.getIOrD(count)+mListBean.getUom());
        //先根据集合里面对应个数初始化一次
        if (count > 0) {
            mTvProductCount.setVisibility(View.VISIBLE);
            mIvProductReduce.setVisibility(View.VISIBLE);
            mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
        } else {
            mTvProductCount.setVisibility(View.INVISIBLE);
            mIvProductReduce.setVisibility(View.INVISIBLE);
            mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
        }

        mTvProductName.setText(mListBean.getName());
        mTvProductCode.setText(mListBean.getDefaultCode());
        mTvProductContent.setText(mListBean.getUnit());

        if (mCanSeePrice) {
            StringBuffer sb1 = new StringBuffer();
            if (mListBean.isIsTwoUnit()) {
                sb1.append("¥").append(df.format(Double.valueOf(mListBean.getSettlePrice())));
                mTvProductPrice.setText(sb1.toString());
                mTvProductPriceUnit.setText("/"+mListBean.getSettleUomId());
            } else {
                sb1.append("¥").append(df.format(Double.valueOf(mListBean.getPrice())));
                mTvProductPrice.setText(sb1.toString());
                mTvProductPriceUnit.setText("/"+mListBean.getUom());
            }
        } else {
            mTvProductPrice.setVisibility(View.GONE);
            mTvProductPriceUnit.setVisibility(View.GONE);
        }

        if(mListBean.getImage()!=null){
            FrecoFactory.getInstance(getContext()).displayWithoutHost(mSdvProductImage, mListBean.getImage().getImage());
        }
    }

    @OnClick({R.id.iv_product_reduce, R.id.iv_product_add,R.id.tv_product_count,R.id.iv_close})
    public void onViewClicked(View view) {
        double currentNum;
        switch (view.getId()) {
            case R.id.iv_product_reduce:
               if (!mModify){
                   MobclickAgent.onEvent(getContext(), EVENT_ID_PRODUCT_COUNT_MODIFY);
                   mModify = true;
               }
                currentNum = productCountSetter.getCount(mListBean);
                if (currentNum > 0) {
                    //https://stackoverflow.com/questions/179427/how-to-resolve-a-java-rounding-double-issue
                    //防止double的问题
                    currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                    if(currentNum<0)currentNum = 0;
                    mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + mListBean.getUom());
//                    mCountMap.put(mListBean, currentNum);
                    productCountSetter.setCount(mListBean,currentNum);
                    if (currentNum == 0) {
                        view.setVisibility(View.INVISIBLE);
                        mTvProductCount.setVisibility(View.INVISIBLE);
                        mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
//                        mCountMap.remove(mListBean);
                    }
                    EventBus.getDefault().post(new ProductCountUpdateEvent(mListBean,currentNum));
                }
                break;
            case R.id.iv_product_add:
                if (!mModify){
                    MobclickAgent.onEvent(getContext(), EVENT_ID_PRODUCT_COUNT_MODIFY);
                    mModify = true;
                }
                currentNum = productCountSetter.getCount(mListBean);
                currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                mTvProductCount.setText(NumberUtil.getIOrD(currentNum) + mListBean.getUom());
//                mCountMap.put(mListBean, currentNum);
                productCountSetter.setCount(mListBean,currentNum);
                if (currentNum == 1) {//0变到1
                    mIvProductReduce.setVisibility(View.VISIBLE);
                    mTvProductCount.setVisibility(View.VISIBLE);
                    mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                }
                EventBus.getDefault().post(new ProductCountUpdateEvent(mListBean,currentNum));
                break;
            case R.id.tv_product_count:
                if (!mModify){
                    MobclickAgent.onEvent(getContext(), EVENT_ID_PRODUCT_COUNT_MODIFY);
                    mModify = true;
                }
                /**
                 * 点击数量展示输入对话框
                 */
                double currentCount = productCountSetter.getCount(mListBean);
                ProductValueDialog productValueDialog = new ProductValueDialog(getContext(), mListBean.getName(), currentCount, productCountSetter.getRemark(mListBean),new ProductValueDialog.IProductDialogCallback() {
                    @Override
                    public void onInputValue(double value,String remark) {

                        productCountSetter.setCount(mListBean,value);
                        mListBean.setRemark(remark);
                        productCountSetter.setRemark(mListBean);
                        if (value == 0) {
                            mIvProductReduce.setVisibility(View.INVISIBLE);
                            mTvProductCount.setVisibility(View.INVISIBLE);
                            mIvProductAdd.setBackgroundResource(R.drawable.order_btn_add_gray);
//                            mCountMap.remove(mListBean);
                        }else{
                            mIvProductReduce.setVisibility(View.VISIBLE);
                            mTvProductCount.setVisibility(View.VISIBLE);
                            mTvProductCount.setText(value+mListBean.getUom());
                            mIvProductAdd.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
//                            mCountMap.put(mListBean,value);
                        }
                        mTvProductCount.setText(value + mListBean.getUom());
                        EventBus.getDefault().post(new ProductCountUpdateEvent(mListBean,(int)value));
                    }
                });
                productValueDialog.show();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }
}
