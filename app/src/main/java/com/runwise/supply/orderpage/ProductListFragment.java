package com.runwise.supply.orderpage;

import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkFragment;
import com.runwise.supply.R;

/**
 * Created by libin on 2017/7/3.
 */

public class ProductListFragment extends NetWorkFragment {

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
