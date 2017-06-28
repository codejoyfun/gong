package com.runwise.supply.business;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.kids.commonframe.base.BaseEntity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 附近地图页面
 *　
 */
public class NearFragment extends NetWorkFragment{

    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;                      //底部横向滑动栏
    //地图状态更新,地图居中
    //滑动栏
    private  List<DealerEnity> dataList;                              //滑动数据源
    //适配器
    private RecyclerViewAdapter adapter;
    //
    private LinearLayoutManager layoutManager;
    OverlayManager overlayManager;
    private ArrayList overlayOptionsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true, "附近" );
        this.setTitleTextColor(Color.WHITE);
        this.setTitleRigthIcon(true, R.drawable.near_search);

    }
    private void requestInfoByLngLat(double lng, double lat){
        DealerRequest request = new DealerRequest(String.valueOf(lng),String.valueOf(lat));
        sendConnection("/dealers/about.json",request,1000,true, DealerResponse.class);
    }
    @OnClick(R.id.title_iv_rigth)
    public void rightClick(View view){
        //TODO：跳转到搜索页
        startActivity(new Intent(getContext(),DealerSearchActivity.class));
//        Intent intent = new Intent(getContext(),DealerMapActivity.class);
//        intent.putExtra("lat",locData.latitude);
//        intent.putExtra("lng",locData.longitude);
//        startActivity(intent);
//        startActivity(new Intent(getContext(),DealerDetainActivity.class));
//        startActivity(new Intent(getContext(),SignActivity.class));
//        Intent intent = new Intent(getContext(),CarSettingFragmentContainer.class);
//        intent.putExtra("carid","1");
//        startActivity(intent);
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    //请求回值
    @Override
    public void onSuccess(BaseEntity result, int where) {
        DealerResponse response = (DealerResponse)result;
        //处理列表数据
        dataList.clear();
        dataList.addAll(response.getData().getEntities());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg+where);
    }



}

