package com.runwise.supply;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.fragment.TransferListFragment;

/**
 * 调度列表页
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferListActivity extends NetWorkActivity implements View.OnClickListener{

    private Button transferInBtn;
    private Button transferOutBtn;
    @ViewInject(R.id.mid_layout)
    private ViewGroup midLayout;
    private Fragment currentFragment;
    private TransferListFragment transferInFragment;
    private TransferListFragment transferOutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_list);
        initViews();
        if(savedInstanceState==null)initFragments();
    }

    private void initViews(){
        setTitleLeftIcon(true,R.drawable.returned);
        addTitleBarBtn();
    }

    private void addTitleBarBtn(){
        midLayout.removeAllViews();
        transferInBtn = new Button(mContext);
        transferInBtn.setText("调入");
        transferInBtn.setTag("调入");
        transferInBtn.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        transferInBtn.setBackgroundResource(R.drawable.car_setting_circle_select);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,80);
        params.alignWithParent = true;
        transferInBtn.setLayoutParams(params);
        midLayout.addView(transferInBtn);
        transferOutBtn = new Button(mContext);
        transferOutBtn.setText("调出");
        transferOutBtn.setTag("调出");
        transferOutBtn.setBackgroundResource(R.drawable.setting_car_circle);
        transferOutBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        transferOutBtn.setLayoutParams(params);
        midLayout.addView(transferOutBtn);
        transferInBtn.setOnClickListener(this);
        transferOutBtn.setOnClickListener(this);
    }

    private void initFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        transferInFragment = new TransferListFragment();
        fragmentTransaction.add(R.id.fragmentContainer, transferInFragment);
        currentFragment = transferInFragment;
        fragmentTransaction.commit();
        transferOutFragment = new TransferListFragment();
    }

    /**
     * 切换两个tab的fragment
     * @param from
     * @param to
     */
    private void switchContent(Fragment from, Fragment to){
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragmentContainer, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    @Override
    public void onClick(View view) {
        transferOutBtn.setBackgroundResource(R.drawable.setting_car_circle);
        transferInBtn.setBackgroundResource(R.drawable.car_setting_circle);
        transferInBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        transferOutBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        ((Button)view).setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        if ("调入".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.car_setting_circle_select);
            //切换fragment
            switchContent(currentFragment, transferInFragment);
        }else if("调出".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.setting_car_circle_select);
            //切换fragment
            switchContent(currentFragment, transferOutFragment);
        }
    }
}
