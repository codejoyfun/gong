package com.runwise.supply.firstpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;


public class OrderProductDiffActivity extends NetWorkActivity {

    public static final int REQUEST_PRODUCT_DETAIL = 1 << 0;
    PriceAdapter mPriceAdapter;

    public static final String INTENT_KEY_ORDER = "intent_key_order";
    @BindView(R.id.pullListView)
    PullToRefreshListView mPullListView;
    @BindView(R.id.loadingLayout)
    LoadingLayout mLoadingLayout;

    public static Intent getStartIntent(Activity activity,OrderResponse.ListBean orderBean) {
        Intent intent = new Intent(activity,OrderProductDiffActivity.class);
        intent.putExtra(INTENT_KEY_ORDER, (Parcelable) orderBean);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product_diff);
        ButterKnife.bind(this);
        setTitleText(true, "差异明细");
        showBackBtn();
        OrderResponse.ListBean orderBean = getIntent().getParcelableExtra(INTENT_KEY_ORDER);
        List<OrderResponse.ListBean.LinesBean> linesBeans = orderBean.getLines();
        List<OrderResponse.ListBean.LinesBean> diffLinesBeans = new ArrayList<>();
        for (OrderResponse.ListBean.LinesBean linesBean : linesBeans) {
            if (linesBean.getActualSendNum() != linesBean.getProductUomQty()) {
                diffLinesBeans.add(linesBean);
            }
        }
        mPriceAdapter = new PriceAdapter();
        mPullListView.setAdapter(mPriceAdapter);
        mPriceAdapter.setData(diffLinesBeans);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_PRODUCT_DETAIL:
                ProductOne productOne = (ProductOne) result.getResult().getData();
                ProductBasicList.ListBean listBean = productOne.getProduct();
                //保存进缓存
                ProductBasicUtils.getBasicMap(getActivityContext()).put(listBean.getProductID() + "", listBean);//更新内存缓存
                mPriceAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    public class PriceAdapter extends IBaseAdapter<OrderResponse.ListBean.LinesBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.orderdetail_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final OrderResponse.ListBean.LinesBean bean = mList.get(position);
            ProductBasicList.ListBean productBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (productBean != null) {
                viewHolder.name.setText(productBean.getName());
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                FrecoFactory.getInstance(mContext).displayWithoutHost(viewHolder.sDv, productBean.getImage().getImageSmall());
                if (GlobalApplication.getInstance().getCanSeePrice()) {
                    viewHolder.dateNumber.setVisibility(View.VISIBLE);
                    viewHolder.dateNumber.setText("¥" + UserUtils.formatPrice(productBean.getPrice() + "") + "/" + bean.getProductUom());
                } else {
                    viewHolder.dateNumber.setVisibility(View.GONE);
                }
            } else {//本地数据没有，查接口
                requestMissingInfo(bean.getProductID());
            }
            viewHolder.uom.setText(NumberUtil.getIOrD(bean.getProductUomQty()) + "" + bean.getProductUom());
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;
            @ViewInject(R.id.number)
            TextView number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.uom)
            TextView uom;
            @ViewInject(R.id.dateNumber)
            TextView dateNumber;
            @ViewInject(R.id.dateLate)
            TextView dateLate;
        }
    }

    private void requestMissingInfo(int productId) {
        Object request = null;
        StringBuffer sb = new StringBuffer("/gongfu/v2/product/");
        sb.append(productId).append("/");
        sendConnection(sb.toString(), request, REQUEST_PRODUCT_DETAIL, false, ProductOne.class);
    }
}
