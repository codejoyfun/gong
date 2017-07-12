package com.runwise.supply.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.CarSettingFragmentContainer;
import com.runwise.supply.business.SelectDealerActivity;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.index.entity.CarImage;
import com.runwise.supply.index.entity.CarPeriod;
import com.runwise.supply.mine.entity.CollectCar;
import com.runwise.supply.mine.entity.CollectCarInfo;
import com.runwise.supply.mine.entity.CollectData;
import com.runwise.supply.mine.entity.CollectResult;
import com.runwise.supply.tools.StatusBarUtil;

import java.util.HashMap;

import static com.runwise.supply.R.id.carDoit;
import static com.runwise.supply.R.id.carName;
import static com.runwise.supply.R.id.carPic;
import static com.runwise.supply.R.id.carPrice;
import static com.runwise.supply.R.id.carRealPrice;

/**
 * 下单提醒推送
 */
public class NotiySettingActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private NotifyListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_notiy_list);
        this.setTitleText(true,"下单提醒推送");
        this.setTitleLeftIcon(true,R.drawable.back_btn);
        this.setTitleRigthIcon(true,R.drawable.nav_add);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new NotifyListAdapter();

        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, REQUEST_START, page+ "", "10");
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page) +"", "10");
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        loadingLayout.setStatusLoading();
        requestData(false, REQUEST_MAIN, page+ "", "10");
        loadingLayout.setOnRetryClickListener(this);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    @OnClick(R.id.right_layout)
    public void doRightClick(View view) {
        Intent intent = new Intent(this ,NotiySettingDateActivity.class);
        startActivity(intent);
    }

    public void requestData (boolean showDialog,int where, String page,String limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPage(page);
        sendConnection("collect/list.json",request,where,showDialog,CollectResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                CollectResult mainResult = (CollectResult)result;
                CollectData mainListResult = mainResult.getData();
                adapter.setData(mainListResult.getEntities());
                loadingLayout.onSuccess(adapter.getCount(),"暂无推送",R.drawable.nonocitify_icon);
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                CollectResult startResult = (CollectResult) result;
                CollectData startListResult = startResult.getData();
                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                CollectResult endResult = (CollectResult) result;
                CollectData sndListResult = endResult.getData();
                if (sndListResult.getEntities() != null && !sndListResult.getEntities().isEmpty()) {
                    adapter.appendData(sndListResult.getEntities());
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
        loadingLayout.onFailure("",R.drawable.nonocitify_icon);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CollectCarInfo bean = (CollectCarInfo)parent.getAdapter().getItem(position);
        Intent intent = new Intent(this,CarSettingFragmentContainer.class);
        intent.putExtra("carid",bean.getCar_id());
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page+ "", "10");
    }

    public class NotifyListAdapter extends IBaseAdapter<CollectCarInfo> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_notify, null);
                holder = new ViewHolder();
                holder.what = (TextView) convertView.findViewById(R.id.what);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            CollectCarInfo collectCarInfo = mList.get(position);
            final CollectCar carInfo = collectCarInfo.getCar();
            CarImage carImage = carInfo.getImage();

            return convertView;
        }

        class ViewHolder {
             TextView what;
             TextView time;
             TextView type;
        }

    }
}
