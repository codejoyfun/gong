package com.runwise.supply.repertory;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.kids.commonframe.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

/**
 * 库存搜索
 */

public class DealerSearchActivity extends BaseActivity{
    @ViewInject(R.id.searchET)
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dealer_search_layout);
        FragmentManager manager = this.getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new StockFragment(),"repertoryList").commitAllowingStateLoss();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString();
                SearchKeyAct bean = new SearchKeyAct();
                bean.setKeyWork(searchText);
                bean.setActName(DealerSearchActivity.class.getSimpleName());
                EventBus.getDefault().post(bean);
            }
        });
    }

    @OnClick({R.id.cancelBtn})
    public void btnClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.cancelBtn:
                this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("商品搜索页");
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品搜索页");
    }

}
