package com.runwise.supply.business;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.SelectDealerResponse;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/2/17.
 */

public class SelectDealerActivity extends NetWorkActivity {
    @ViewInject(R.id.selectlistview)
    private PullToRefreshListView pullListView;
    private SelectDealerAdapter adapter;
    private ArrayList<SelectDealerResponse.DataBean.EntitiesBean> dealerArr;
    private String carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.dealer_select_layout);
        setTitleText(true,"选择经销商");
        setTitleLeftIcon(true, R.drawable.returned);
        setTitleRightText(true,"立即申请");
        carId = getIntent().getStringExtra("carId");
        initViews();
        requestData();
    }
    //列表适配器
    private void initViews(){
        dealerArr = new ArrayList();
        adapter = new SelectDealerAdapter(this);
        adapter.setData(dealerArr);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //改变选中情况
                adapter.selectIndex = i-1;
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void requestData(){
        if (TextUtils.isEmpty(carId)){
            ToastUtil.show(mContext,"数据异常，请退出重试");
            return;
        }
        CarTypeFragment.CarRequest request = new CarTypeFragment.CarRequest(carId);
        sendConnection("/dealers/list.json",request,1000,true, SelectDealerResponse.class);
    }
    @OnClick({R.id.title_iv_left,R.id.title_tv_rigth})
    public void btnClick(View view){
        switch (view.getId()){
            case R.id.title_iv_left:
                finish();
                break;
            case R.id.title_tv_rigth:
                //跳转到申请页面：经销商id+车id
                if (adapter.selectIndex != -1){
//                    SelectDealerResponse.DataBean.EntitiesBean bean = dealerArr.get(adapter.selectIndex);
//                    String dealerId = String.valueOf(bean.getDealer_id());
//                    Intent intent = new Intent(mContext, DividePayStep1Activity.class);
//                    intent.putExtra("dealerId",dealerId);
//                    intent.putExtra("carId",carId);
//                    startActivity(intent);
                }else{
                    ToastUtil.show(mContext,"请选择经销商");
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onSuccess(BaseEntity result, int where) {
        SelectDealerResponse response = (SelectDealerResponse)result;
        if (response != null && response.getData() != null
                && response.getData().getEntities() != null
                && response.getData().getEntities().size() > 0){
            dealerArr.clear();
            List<SelectDealerResponse.DataBean.EntitiesBean> list = response.getData().getEntities();
            dealerArr.addAll(list);
            adapter.setData(dealerArr);
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
