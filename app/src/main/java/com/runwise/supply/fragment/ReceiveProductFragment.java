package com.runwise.supply.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ReceiveProductAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.orderpage.LotListActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.tools.UserUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.vov.vitamio.utils.NumberUtil;

import static com.kids.commonframe.config.Constant.ORDER_STATE_DRAFT;
import static com.kids.commonframe.config.Constant.ORDER_STATE_PEISONG;
import static com.kids.commonframe.config.Constant.ORDER_STATE_SALE;
import static com.runwise.supply.R.id.loadingLayout;
import static com.runwise.supply.R.id.recyclerView;

/**
 * 异步收货详情页
 * <p>
 * Created by Dong on 2017/9/5.
 */

public class ReceiveProductFragment extends BaseFragment {

    @BindView(recyclerView)
    ListView mRecyclerView;
    @ViewInject(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    Unbinder unbinder;

    private ReceiveProductAdapter mReceiveProductAdapter;
    private List<OrderResponse.ListBean.LinesBean> listDatas;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";
    public static final String BUNDLE_KEY_TWO_UNIT = "bundle_key_two_unit";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_receive_product;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listDatas = (List<OrderResponse.ListBean.LinesBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        mReceiveProductAdapter = new ReceiveProductAdapter(getActivity());
        mRecyclerView.setAdapter(mReceiveProductAdapter);
        mReceiveProductAdapter.setTwoUnit(getArguments().getBoolean(BUNDLE_KEY_TWO_UNIT));
        loadingLayout.onSuccess(listDatas == null ? 0 : listDatas.size(), "暂时没有数据");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class ReceiveProductAdapter extends BaseAdapter {
        private Context context;
        private boolean isTwoUnit;           //双单位,有值就显示

        public void setTwoUnit(boolean twoUnit) {
            isTwoUnit = twoUnit;
        }

        public ReceiveProductAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return listDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_receive_product, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            final OrderResponse.ListBean.LinesBean bean = listDatas.get(position);
            FrecoFactory.getInstance(context).disPlay(vh.productImage, Constant.BASE_URL + bean.getImageMedium());
            vh.name.setText(bean.getName());
            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
            boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
            if (canSeePrice) {
                if (isTwoUnit) {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductSettlePrice()))).append("元/").append(bean.getSettleUomId());
                } else {
                    sb.append("\n").append(UserUtils.formatPrice(String.valueOf(bean.getProductPrice()))).append("元/").append(bean.getProductUom());
                }
            }
//            vh.unit1.setText(bean.getProductUom());
            vh.content.setText(sb.toString());
            if (isTwoUnit) {
                vh.weightTv.setText(bean.getSettleAmount() + bean.getSettleUomId() + "");
                vh.weightTv.setVisibility(View.VISIBLE);
            } else {
                vh.weightTv.setVisibility(View.INVISIBLE);
            }

            vh.nowPriceTv.setText(NumberUtil.getIOrD(bean.getDeliveredQty())+"/"+NumberUtil.getIOrD(bean.getProductUomQty())+bean.getProductUom());
            return convertView;
        }

        private class ViewHolder {
            public SimpleDraweeView productImage;
            public TextView name;
            public TextView content;
            public TextView nowPriceTv;
            public TextView weightTv;
//            public TextView unit1;
            public View rootView;

            public ViewHolder(View itemView) {
                rootView = itemView;
                productImage = (SimpleDraweeView) itemView.findViewById(R.id.productImage);
                name = (TextView) itemView.findViewById(R.id.name);
                content = (TextView) itemView.findViewById(R.id.content);
                nowPriceTv = (TextView) itemView.findViewById(R.id.tv_receive_count);
                weightTv = (TextView) itemView.findViewById(R.id.weightTv);
//                unit1 = (TextView) itemView.findViewById(R.id.unit1);
            }
        }
    }
}
