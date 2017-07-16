package com.runwise.supply.message;

import android.os.Bundle;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.runwise.supply.R;

/**
 * Created by mychao on 2017/7/14.
 */

public class MessageFragment extends NetWorkFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitleText(true,"消息");
        this.setTitleRigthIcon(true,R.drawable.nav_service_message);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
