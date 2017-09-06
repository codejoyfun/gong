package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runwise.supply.R;
import com.runwise.supply.firstpage.ReturnDetailAdapter;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.runwise.supply.R.id.recyclerView;

/**
 * Created by mike on 2017/9/6.
 */

public class ReturnProductFragment extends Fragment {

    @BindView(recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    ReturnDetailAdapter mReturnDetailAdapter;
    private List<ReturnOrderBean.ListBean.LinesBean> listDatas;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_product, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReturnDetailAdapter = new ReturnDetailAdapter(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mReturnDetailAdapter);
        listDatas = (List<ReturnOrderBean.ListBean.LinesBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        mReturnDetailAdapter.setReturnList(listDatas);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}