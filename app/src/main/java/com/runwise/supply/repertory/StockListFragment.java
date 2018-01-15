package com.runwise.supply.repertory;

import android.os.Bundle;

/**
 * 单个类别的库存列表
 * 分页加载
 *
 */

public class StockListFragment extends AbstractStockListFragment {

    private boolean isFirstLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getBoolean(ARG_CURRENT,false)){
            isFirstLoaded = true;
            refresh(true);
        }
    }

    /**
     * 首次点击时刷新
     * @param keyword 搜索词
     */
    @Override
    public void refresh(String keyword){
        if(!isFirstLoaded){
            isFirstLoaded = true;
            refresh(true);
            return;
        }
    }
}
