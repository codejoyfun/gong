package com.runwise.supply.repertory;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.StockListRequest;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import io.vov.vitamio.utils.NumberUtil;

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
