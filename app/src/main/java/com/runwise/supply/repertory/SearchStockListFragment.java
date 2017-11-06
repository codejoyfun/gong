package com.runwise.supply.repertory;

import android.os.Bundle;
import android.text.TextUtils;

import com.kids.commonframe.base.BaseEntity;
import com.runwise.supply.R;

/**
 * 搜索库存页
 *
 * Created by Dong on 2017/11/2.
 */

public class SearchStockListFragment extends AbstractStockListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始为空数据
        clear();
    }

    /**
     * 清空数据并显示
     */
    private void clear(){
        adapter.clear();
        loadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
    }

    @Override
    public void refresh(String keyword) {

        //搜索词为空的时候，不显示数据
        if(TextUtils.isEmpty(keyword)){
            mKeyword = keyword;
            //清空,显示空数据
            clear();
            return;
        }

        //搜索词有修改的话，刷新
        if(!mKeyword.equals(keyword)){
            mKeyword = keyword;
            super.refresh(true);
            return;
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        if(TextUtils.isEmpty(mKeyword)){
            clear();//防止清空搜索词后，之前的接口请求才返回
            return;
        }
        super.onSuccess(result, where);
    }
}
