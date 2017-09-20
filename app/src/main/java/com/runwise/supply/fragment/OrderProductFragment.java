package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kids.commonframe.base.view.LoadingLayout;
import com.runwise.supply.R;
import com.runwise.supply.firstpage.OrderDtailAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;
import com.runwise.supply.tools.DensityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.runwise.supply.R.id.recyclerView;

/**
 * Created by mike on 2017/9/5.
 */

public class OrderProductFragment extends Fragment {

    @BindView(recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loadingLayout)
     LoadingLayout loadingLayout;
    Unbinder unbinder;

    OrderDtailAdapter mOrderDtailAdapter;
    private List<OrderResponse.ListBean.LinesBean> listDatas;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";
    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    public static final String BUNDLE_KEY_STATE = "bundle_key_state";
    public static final String BUNDLE_KEY_RETURN = "bundle_key_return";
    public static final String BUNDLE_KEY_TWO_UNIT = "bundle_key_two_unit";
    public static final String BUNDLE_KEY_ORDER_DATA = "bundle_key_order_data";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_product, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOrderDtailAdapter = new OrderDtailAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mOrderDtailAdapter);
        listDatas = (List<OrderResponse.ListBean.LinesBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        String name = getArguments().getString(BUNDLE_KEY_NAME);
        String state = getArguments().getString(BUNDLE_KEY_STATE);
        OrderResponse.ListBean listBean = (OrderResponse.ListBean) getArguments().getParcelable(BUNDLE_KEY_ORDER_DATA);

        mOrderDtailAdapter.setProductList(listDatas);
        mOrderDtailAdapter.setStatus(name,state,listBean);
        mOrderDtailAdapter.setHasReturn(getArguments().getBoolean(BUNDLE_KEY_RETURN));
        mOrderDtailAdapter.setTwoUnit(getArguments().getBoolean(BUNDLE_KEY_TWO_UNIT));
        mRecyclerView.setMinimumHeight(DensityUtil.getScreenH(getActivity()));
        loadingLayout.onSuccess(listDatas.size(),"哎呀！这里是空哒~~",R.drawable.default_icon_ordernone);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
