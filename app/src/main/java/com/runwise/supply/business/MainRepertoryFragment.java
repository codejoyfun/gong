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
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.business.entity.DealerRequest;
import com.runwise.supply.business.entity.DealerResponse;
import com.runwise.supply.business.entity.DealerEnity;
import com.runwise.supply.mine.RepertoryFragment;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.mine.entity.ProductData;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.repertory.EditRepertoryListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存
 */
public class MainRepertoryFragment extends NetWorkFragment {
    private final int REQUEST_EXIT = 1;
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
    public void leftClick(View view){
        startActivity(new Intent(getContext(),DealerSearchActivity.class));
    }
    @OnClick(R.id.right_layout)
    public void rightClick(View view){
        Object parma = null;
        sendConnection("/gongfu/shop/inventory/list",parma,REQUEST_EXIT,true, CheckResult.class);

    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_EXIT:
                CheckResult repertoryEntity = (CheckResult)result.getResult();
                boolean findCheckOrder = false;
                for(CheckResult.ListBean bean : repertoryEntity.getList()) {
                    if ("confirm".equals(bean.getState())) {
                        findCheckOrder = true;
                        //本人盘点
                        if(GlobalApplication.getInstance().loadUserInfo().getUid().equals( bean.getCreate_partner().getId())) {
                            forWardAct(bean.getId()+"");
                        }
                        else{
                            dialog.setModel(CustomDialog.LEFT);
                            dialog.setMessage("当前正在盘点中,无法创建新的盘点单");
                            dialog.setLeftBtnListener("我知道了", null);
                            dialog.show();
                        }
                        break;
                    }
                }
                if(!findCheckOrder) {
                    if(repertoryEntity.getList() != null && !repertoryEntity.getList().isEmpty()) {
                        forWardAct(repertoryEntity.getList().get(0).getId()+"");
                    }
                }
                break;
        }
    }
    private void forWardAct(String id) {
        Intent intent =  new Intent(getContext(), EditRepertoryListActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }
}

