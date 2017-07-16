package com.runwise.supply.business;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.BaseFragment;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.business.entity.DealerRequest;
import com.runwise.supply.business.entity.DealerResponse;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.mine.RepertoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存
 */
public class MainRepertoryFragment extends BaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true, "当前库存" );
        this.setTitleLeftIcon(true, R.drawable.searchbar_ico_search);
        setTitleRightText(true,"盘点");
        FragmentManager manager = this.getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new RepertoryFragment()).commitAllowingStateLoss();
    }
    @OnClick(R.id.left_layout)
    public void rightClick(View view){
        //TODO：跳转到搜索页
        startActivity(new Intent(getContext(),DealerSearchActivity.class));
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }
}

