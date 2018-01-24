package com.runwise.supply.firstpage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.adapter.OrderProductAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.mine.entity.ProductOne;
import com.runwise.supply.orderpage.LotListActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.config.Constant.ORDER_STATE_DRAFT;
import static com.kids.commonframe.config.Constant.ORDER_STATE_PEISONG;
import static com.kids.commonframe.config.Constant.ORDER_STATE_SALE;


public class OrderProductDiffActivity extends NetWorkActivity {

    public static final int REQUEST_PRODUCT_DETAIL = 1 << 0;
    PriceAdapter mPriceAdapter;

    public static final String INTENT_KEY_ORDER = "intent_key_order";
    public static final String INTENT_KEY_LIST = "intent_key_list";
    @BindView(R.id.pullListView)
    PullToRefreshListView mPullListView;
    @BindView(R.id.loadingLayout)
    LoadingLayout mLoadingLayout;

    OrderResponse.ListBean mOrderBean;

    public static Intent getStartIntent(Activity activity, OrderResponse.ListBean orderBean) {
        Intent intent = new Intent(activity, OrderProductDiffActivity.class);
        intent.putExtra(INTENT_KEY_ORDER, (Parcelable) orderBean);
        return intent;
    }
    public static Intent getStartIntent(Activity activity, List<OrderResponse.ListBean.ProductAlteredBean.AlterProductBean> alterProducts) {
        Intent intent = new Intent(activity, OrderProductDiffActivity.class);
        intent.putExtra(INTENT_KEY_LIST, (Parcelable) alterProducts);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product_diff);
        ButterKnife.bind(this);
        setTitleText(true, "差异明细");
        showBackBtn();
        mOrderBean = getIntent().getParcelableExtra(INTENT_KEY_ORDER);
        List<OrderResponse.ListBean.LinesBean> linesBeans = mOrderBean.getLines();
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
            OrderProductAdapter.ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivityContext()).inflate(R.layout.orderdetail_list_item, parent, false);
                vh = new OrderProductAdapter.ViewHolder(convertView);
                vh.oldPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setTag(vh);
            }
            vh = (OrderProductAdapter.ViewHolder) convertView.getTag();
            final OrderResponse.ListBean.LinesBean bean = mList.get(position);
            int pId = bean.getProductID();

            String imageUrl = bean.getImageMedium();
            final ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(pId));
            if (TextUtils.isEmpty(bean.getImageMedium()) && basicBean != null && basicBean.getImage() != null) {
                imageUrl = basicBean.getImage().getImageSmall();
            }
            FrecoFactory.getInstance(getActivityContext()).displayWithoutHost(vh.productImage, imageUrl);
            double puq = bean.getProductUomQty();
            double dq = bean.getDeliveredQty();
//            实发
            vh.oldPriceTv.setText("x" + NumberUtil.getIOrD(bean.getActualSendNum()));
            vh.oldPriceTv.setVisibility(View.VISIBLE);
            vh.nowPriceTv.setText("x" + NumberUtil.getIOrD(puq));

            vh.name.setText(bean.getName());
            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
            boolean canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
            if (canSeePrice) {
                if (mOrderBean.isIsTwoUnit()) {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductSettlePrice()))).append("元/").append(bean.getSettleUomId());
                } else {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductPrice()))).append("元/").append(bean.getProductUom());
                }
            }
            if (!TextUtils.isEmpty(bean.getRemark())) {
                sb.append("\n备注：").append(bean.getRemark());
            }
            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
            if (mOrderBean.isIsTwoUnit()) {
                vh.weightTv.setText(bean.getSettleAmount() + bean.getSettleUomId() + "");
                vh.weightTv.setVisibility(View.VISIBLE);
            } else {
                vh.weightTv.setVisibility(View.INVISIBLE);
            }

            vh.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deliveryType = mOrderBean.getDeliveryType();
                    if (deliveryType.equals(OrderResponse.ListBean.TYPE_STANDARD) || deliveryType.equals(OrderResponse.ListBean.TYPE_THIRD_PART_DELIVERY)
                            || deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH) || deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_THIRD_PART_DELIVERY)) {
                        if (!mOrderBean.getState().equals("peisong") && !mOrderBean.getState().equals("done") && !mOrderBean.getState().equals("rated")) {
                            return;
                        }
                    }
                    if (deliveryType.equals(OrderResponse.ListBean.TYPE_FRESH_VENDOR_DELIVERY) || deliveryType.equals(OrderResponse.ListBean.TYPE_VENDOR_DELIVERY)) {
                        if ((mOrderBean.getState().equals("done") || mOrderBean.getState().equals("rated")) && (bean.getLotList() != null && bean.getLotList().size() == 0)) {
                            ToastUtil.show(v.getContext(), "该产品无批次追踪");
                            return;
                        }
                        if (mOrderBean.getState().equals(ORDER_STATE_PEISONG) || mOrderBean.getState().equals(ORDER_STATE_DRAFT) || mOrderBean.getState().equals(ORDER_STATE_SALE)) {
                            return;
                        }
                    }
                    Intent intent = new Intent(getActivityContext(), LotListActivity.class);
                    intent.putExtra("title", bean.getName());
                    intent.putExtra("bean", (Parcelable) bean);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            public SimpleDraweeView productImage;
            public TextView name;
            public TextView content;
            public TextView oldPriceTv;
            public TextView nowPriceTv;
            public TextView weightTv;
            public TextView unit1;
            public View rootView;

            public ViewHolder(View itemView) {
                rootView = itemView;
                productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
                name = (TextView) itemView.findViewById(R.id.name);
                content = (TextView) itemView.findViewById(R.id.content);
                oldPriceTv = (TextView) itemView.findViewById(R.id.oldPriceTv);
                nowPriceTv = (TextView) itemView.findViewById(R.id.nowPriceTv);
                weightTv = (TextView) itemView.findViewById(R.id.weightTv);
                unit1 = (TextView) itemView.findViewById(R.id.unit1);
            }
        }
    }

}
