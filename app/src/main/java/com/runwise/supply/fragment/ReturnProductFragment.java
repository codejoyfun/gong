package com.runwise.supply.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kids.commonframe.base.BaseFragment;
import com.runwise.supply.R;
import com.runwise.supply.event.IntEvent;
import com.runwise.supply.firstpage.ReturnDetailAdapter;
import com.runwise.supply.firstpage.entity.ReturnOrderBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.runwise.supply.R.id.recyclerView;

/**
 * Created by mike on 2017/9/6.
 */

public class ReturnProductFragment extends BaseFragment {

    @BindView(recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loadingLayout)
    LinearLayout loadingLayout;
    Unbinder unbinder;
    int mHeight;

    ReturnDetailAdapter mReturnDetailAdapter;
    private List<ReturnOrderBean.ListBean.LinesBean> listDatas;

    public static final String BUNDLE_KEY_LIST = "bundle_key_list";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_return_product;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetHeight(IntEvent intEvent) {
        mHeight = intEvent.getHeight();
        if (loadingLayout != null){
            ViewGroup.LayoutParams layoutParams = loadingLayout.getLayoutParams();
            layoutParams.height = mHeight;
            loadingLayout.setLayoutParams(layoutParams);
            loadingLayout.invalidate();
            loadingLayout.requestLayout();
            if (listDatas.size() == 0){
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}