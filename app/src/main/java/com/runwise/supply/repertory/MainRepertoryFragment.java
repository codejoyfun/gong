package com.runwise.supply.repertory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.UserLoginEvent;
import com.kids.commonframe.base.util.SPUtils;
import com.kids.commonframe.base.view.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.LoginActivity;
import com.runwise.supply.R;
import com.runwise.supply.mine.RepertoryFragment;
import com.runwise.supply.repertory.entity.PandianResult;

/**
 * 库存
 */
public class MainRepertoryFragment extends NetWorkFragment {
    private final int REQUEST_EXIT = 1;
    @ViewInject(R.id.tipLayout)
    private View tipLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true, "库存" );
        this.setTitleLeftIcon(true, R.drawable.searchbar_ico_search);
        setTitleRightText(true,"盘点");
        FragmentManager manager = this.getActivity().getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new RepertoryFragment()).commitAllowingStateLoss();
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            tipLayout.setVisibility(View.GONE);
        }
        else{
            tipLayout.setVisibility(View.VISIBLE);
        }
    }
    @OnClick(R.id.left_layout)
    public void leftClick(View view){
        startActivity(new Intent(mContext,DealerSearchActivity.class));
    }
    @OnClick(R.id.right_layout)
    public void rightClick(View view){
        boolean isLogin = SPUtils.isLogin(mContext);
        if(isLogin) {
            Object parma = null;
            sendConnection("/api/inventory/create", parma, REQUEST_EXIT, true, PandianResult.class);
        }
        else{
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }
    @OnClick({R.id.tipLayout,R.id.closeTip})
    public void loginTipLayout(View view){
        switch (view.getId()) {
            case R.id.tipLayout:
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.closeTip:
                tipLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onUserLogin(UserLoginEvent userLoginEvent) {
        tipLayout.setVisibility(View.GONE);
    }

    @Override
    public void onUserLoginout() {
        tipLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_EXIT:
                PandianResult repertoryEntity = (PandianResult)result.getResult().getData();
                Intent intent =  new Intent(getContext(), EditRepertoryListActivity.class);
                intent.putExtra("bean",repertoryEntity);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        switch (where) {
            case REQUEST_EXIT:
                dialog.setModel(CustomDialog.LEFT);
                dialog.setMessage(errMsg);
                dialog.setLeftBtnListener("我知道了", null);
                dialog.show();
                break;
        }
    }
}

