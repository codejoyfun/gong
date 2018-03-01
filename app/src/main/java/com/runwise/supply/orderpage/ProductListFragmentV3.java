package com.runwise.supply.orderpage;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;

/**
 * Created by mike on 2018/3/1.
 */

public class ProductListFragmentV3 extends NetWorkFragment {
    @Override
    protected int createViewByLayoutId() {
        return 0;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
