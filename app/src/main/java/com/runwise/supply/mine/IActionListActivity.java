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
 * 我的收藏
 */
public class IActionListActivity extends NetWorkActivity implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;

    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private CarInfoListAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;

    private int page = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarEnabled();
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.activity_collection_list);
        this.setTitleText(true,"我的收藏");
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullListView.setOnItemClickListener(this);

        adapter = new CarInfoListAdapter();

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
                loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
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
        loadingLayout.onFailure("",R.drawable.no_network);
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

    public class CarInfoListAdapter extends IBaseAdapter<CollectCarInfo> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_car_collection, null);
                holder = new ViewHolder();
                holder.carPic = (SimpleDraweeView) convertView.findViewById(carPic);
                holder.carName = (TextView) convertView.findViewById(carName);
                holder.carPrice = (TextView) convertView.findViewById(carPrice);
                holder.carRealPrice = (TextView) convertView.findViewById(carRealPrice);
                holder.carName = (TextView) convertView.findViewById(carName);
                holder.carDoit = (TextView) convertView.findViewById(carDoit);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            CollectCarInfo collectCarInfo = mList.get(position);
            final CollectCar carInfo = collectCarInfo.getCar();
            CarImage carImage = carInfo.getImage();
            if( carImage != null ) {
                FrecoFactory.getInstance(mContext).disPlay(holder.carPic, carImage.getImg_path());
            }
            holder.carName.setText(carInfo.getTitle());
            CarPeriod perIod = carInfo.getPeriod();
            if( perIod != null) {
                HashMap<String,String> spMap = CommonUtils.formatMoney(perIod.getFirst_period());
                String firstStr = "首付:" + spMap.get(CommonUtils.MONEY_VALUE) + spMap.get(CommonUtils.MONEY_UNIT)+" 月供:";

                HashMap<String,String> mMap = CommonUtils.formatMoney(perIod.getMonth_period());
                String twoStr = mMap.get(CommonUtils.MONEY_VALUE) + mMap.get(CommonUtils.MONEY_UNIT);

                SpannableString spannableString1 = new SpannableString(firstStr+twoStr);
                spannableString1.setSpan(new AbsoluteSizeSpan(14,true), 3, 3 + spMap.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                int starIndex = firstStr.length();
                spannableString1.setSpan(new AbsoluteSizeSpan(14,true), starIndex, starIndex + mMap.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.carPrice.setText(spannableString1);
            }
            HashMap<String,String> spMap = CommonUtils.formatMoney(carInfo.getSale_price());
            holder.carRealPrice.setText(spMap.get(CommonUtils.MONEY_VALUE) + spMap.get(CommonUtils.MONEY_UNIT));
            holder.carDoit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,SelectDealerActivity.class);
                    intent.putExtra("carId",String.valueOf(carInfo.getCar_id()));
                    startActivity(intent);

//                    Intent intent = new Intent(mContext,DividePayStep4Activity.class);
//                    startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder {
             SimpleDraweeView carPic;
             TextView carName;
             TextView carPrice;
             TextView carRealPrice;
             TextView carDoit;
        }

    }
}
