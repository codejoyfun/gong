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
import com.runwise.supply.entity.RatingModifyEvent;
import com.runwise.supply.firstpage.EvaluateActivity;
import com.runwise.supply.firstpage.EvaluateAdapter;
import com.runwise.supply.firstpage.entity.OrderResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.runwise.supply.R.id.recyclerView;

/**
 * Created by mike on 2017/9/13.
 */

public class EvaluateProductFragment extends Fragment {

    @BindView(recyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    List<OrderResponse.ListBean.LinesBean> productList;
    public static final String INTENT_KEY_LIST = "intent_key_list";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluate_product, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    EvaluateAdapter evaluateAdapter;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productList = (List<OrderResponse.ListBean.LinesBean>) getArguments().getSerializable(INTENT_KEY_LIST);
        evaluateAdapter = new EvaluateAdapter(getActivity(),productList,((EvaluateActivity)getActivity()).mRateMap);
        evaluateAdapter.setCallback(new EvaluateAdapter.RatingBarClickCallback() {
            @Override
            public void rateChanged(Integer lineId, Integer rateScore) {
                EventBus.getDefault().post(new RatingModifyEvent());
            }
        });
        mRecyclerView.setAdapter(evaluateAdapter);
    }

    public void refresh(){
        if(evaluateAdapter!= null){
            evaluateAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onRatingModify(RatingModifyEvent event){
        refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
