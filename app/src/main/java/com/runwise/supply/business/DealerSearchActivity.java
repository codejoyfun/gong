package com.runwise.supply.business;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mob.tools.utils.SharePrefrenceHelper;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.SearchRequest;
import com.runwise.supply.business.entity.SearchResponse;
import com.runwise.supply.tools.HistoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by libin on 2017/1/4.
 */

public class DealerSearchActivity extends NetWorkActivity implements AdapterView.OnItemClickListener{
    @ViewInject(R.id.history_list)
    private ListView history_list;
    @ViewInject(R.id.search_list)
    private ListView search_list;
    @ViewInject(R.id.searchET)
    private EditText searchET;
    @ViewInject(R.id.tip)
    private TextView tip;
    @ViewInject(R.id.clearBtn)
    private Button clearBtn;
    @ViewInject(R.id.nodata)
    private ImageView noDataIv;

    //适配器
    private ArrayAdapter<String>  historyAdapter;
    //搜索列表的适配器
    private SearchListAdapter sListAdapter;
    private ArrayList historyArr;
    private ArrayList searchArr;
    //sp文件中对应的记录，name->value
    LinkedHashMap<String,String> historyMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dealer_search_layout);
        initData();
        switchSearchMode(false);//默认显示历史页面

    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新历史数据
        historyArr.clear();
        historyMap = HistoryUtils.getHistory(mContext);
        for (Iterator iter = historyMap.entrySet().iterator(); iter.hasNext();){
            Map.Entry element = (Map.Entry)iter.next();
            String strKey = (String)element.getKey();
            historyArr.add(strKey);
        }
        historyAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.cancelBtn, R.id.clearBtn})
    public void btnClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.clearBtn:
                historyArr.clear();
                HistoryUtils.clear(mContext);
                historyAdapter.notifyDataSetChanged();
                break;
            case R.id.cancelBtn:
                this.finish();
                break;
            default:
                break;
        }
        Log.i("DealearSearchAct", "点击");
    }

    //自定义方法
    private void initData() {
        historyArr = new ArrayList();
        searchArr = new ArrayList();
        historyAdapter = new ArrayAdapter<String>(this, R.layout.dealer_history_layout,R.id.textview, historyArr);
        history_list.setTag("1000");
        search_list.setTag("1001");
        history_list.setAdapter(historyAdapter);
        history_list.setOnItemClickListener(this);
        sListAdapter = new SearchListAdapter(this);
        search_list.setAdapter(sListAdapter);
        search_list.setOnItemClickListener(this);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //改变中，在这里做文本监听

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //1隐藏历史搜索界面
                //2发送请求
                if (TextUtils.isEmpty(editable.toString())){
                    searchArr.clear();
                    sListAdapter.setData(searchArr);
                    switchSearchMode(false);

                }else {
                    switchSearchMode(true);
                    SearchRequest request = new SearchRequest(editable.toString());
                    DealerSearchActivity.this.sendConnection("/search/list.json",request,1000,false, SearchResponse.class);
                }
            }
        });
    }

    //历史列表,搜过列表点击回调
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String tag = (String)adapterView.getTag();
        switch (tag){
            case "1000":
                String dealerName = (String)adapterView.getAdapter().getItem(i);
                if (historyMap.containsKey(dealerName)){
                    Intent intent = new Intent(mContext,DealerDetainActivity.class);
                    intent.putExtra("dealer_id",historyMap.get(dealerName));
                    startActivity(intent);
                }else{
                    ToastUtil.show(mContext,"该经销商信息异常，请重新退出再试");
                }
                break;
            case "1001":
                //点击进入经销商详情
                SearchResponse.DataBean.EntitiesBean bean = (SearchResponse.DataBean.EntitiesBean)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(mContext,DealerDetainActivity.class);
                intent.putExtra("dealer_id",String.valueOf(bean.getDealer_id()));
                startActivity(intent);
                //将经销商信息放到历史中
                HistoryUtils.updateDealer(mContext,bean.getDealer_name(),String.valueOf(bean.getDealer_id()));
                break;
            default:
                break;
        }
    }
    //切换页面模式：历史／搜索页面
    private void switchSearchMode(Boolean isSearch){
        if (isSearch){
            tip.setVisibility(View.GONE);
            clearBtn.setVisibility(View.GONE);
            history_list.setVisibility(View.GONE);
            search_list.setVisibility(View.VISIBLE);
        }else {
            tip.setVisibility(View.VISIBLE);
            clearBtn.setVisibility(View.VISIBLE);
            history_list.setVisibility(View.VISIBLE);
            search_list.setVisibility(View.GONE);
            noDataIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        SearchResponse response = (SearchResponse)result;
        searchArr.clear();
        if (response != null && response.getData() != null && response.getData().getEntities() != null){
            searchArr.addAll(response.getData().getEntities());
        }
        if (searchArr.size() == 0){
            noDataIv.setVisibility(View.VISIBLE);
        }else{
            noDataIv.setVisibility(View.GONE);
        }
        sListAdapter.setData(searchArr);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
}
