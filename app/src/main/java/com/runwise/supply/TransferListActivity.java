package com.runwise.supply;

import android.content.Intent;
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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.fragment.TransferListFragment;
import com.runwise.supply.mine.CreateCallInListActivity;

/**
 * 调度列表页
 *
 * Created by Dong on 2017/10/10.
 */

public class TransferListActivity extends NetWorkActivity implements View.OnClickListener{

    private Button mTransferInBtn;
    private Button mTransferOutBtn;
    @ViewInject(R.id.mid_layout)
    private ViewGroup midLayout;
    private Fragment mCurrentFragment;
    private TransferListFragment mTransferInFragment;
    private TransferListFragment mTransferOutFragment;

    private static final int REQUEST_CODE_CREATE_CALL_IN_LIST = 1<<0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_list);
        initViews();
        if(savedInstanceState==null)initFragments();
    }

    private void initViews(){
        setTitleLeftIcon(true,R.drawable.returned);
        setTitleRightText(true,"添加");
        addTitleBarBtn();
    }

    private void addTitleBarBtn(){
        midLayout.removeAllViews();
        mTransferInBtn = new Button(mContext);
        mTransferInBtn.setText("调入");
        mTransferInBtn.setTag("调入");
        mTransferInBtn.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        mTransferInBtn.setBackgroundResource(R.drawable.car_setting_circle_select);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200,80);
        params.alignWithParent = true;
        mTransferInBtn.setLayoutParams(params);
        midLayout.addView(mTransferInBtn);
        mTransferOutBtn = new Button(mContext);
        mTransferOutBtn.setText("调出");
        mTransferOutBtn.setTag("调出");
        mTransferOutBtn.setBackgroundResource(R.drawable.setting_car_circle);
        mTransferOutBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        mTransferOutBtn.setLayoutParams(params);
        midLayout.addView(mTransferOutBtn);
        mTransferInBtn.setOnClickListener(this);
        mTransferOutBtn.setOnClickListener(this);
    }

    private void initFragments(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mTransferInFragment = new TransferListFragment();
        fragmentTransaction.add(R.id.fragmentContainer, mTransferInFragment);
        mCurrentFragment = mTransferInFragment;
        fragmentTransaction.commit();
        mTransferOutFragment = new TransferListFragment();
    }

    /**
     * 切换两个tab的fragment
     * @param from
     * @param to
     */
    private void switchContent(Fragment from, Fragment to){
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
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
        mTransferOutBtn.setBackgroundResource(R.drawable.setting_car_circle);
        mTransferInBtn.setBackgroundResource(R.drawable.car_setting_circle);
        mTransferInBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        mTransferOutBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        ((Button)view).setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
        if ("调入".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.car_setting_circle_select);
            //切换fragment
            switchContent(mCurrentFragment, mTransferInFragment);
        }else if("调出".equals(view.getTag())){
            view.setBackgroundResource(R.drawable.setting_car_circle_select);
            //切换fragment
            switchContent(mCurrentFragment, mTransferOutFragment);
        }
    }

    @OnClick({R.id.title_tv_rigth})
    public void btnClick(View v){
        switch (v.getId()){
            case R.id.title_tv_rigth:
                startActivityForResult(new Intent(getActivityContext(), CreateCallInListActivity.class),REQUEST_CODE_CREATE_CALL_IN_LIST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_CREATE_CALL_IN_LIST:
                    //刷新列表

                    break;
            }
        }
    }
}
