package com.runwise.supply.business;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;

import com.baidu.mapapi.map.Text;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.db.converter.BooleanColumnConverter;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mob.tools.utils.SharePrefrenceHelper;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.SearchRequest;
import com.runwise.supply.business.entity.SearchResponse;
import com.runwise.supply.mine.RepertoryFragment;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.tools.HistoryUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by libin on 2017/1/4.
 */

public class DealerSearchActivity extends BaseActivity{
    @ViewInject(R.id.searchET)
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dealer_search_layout);
        FragmentManager manager = this.getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.contextLayout,new RepertoryFragment(),"repertoryList").commitAllowingStateLoss();

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

}
