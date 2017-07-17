package com.runwise.supply.index;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
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
import com.runwise.supply.index.entity.IndexCarInfo;
import com.runwise.supply.index.entity.IndexCarInfoList;
import com.runwise.supply.index.entity.IndexCarInfoResult;
import com.runwise.supply.tools.UserUtils;

import java.util.HashMap;

import static com.runwise.supply.R.id.carDoit;
import static com.runwise.supply.R.id.carName;
import static com.runwise.supply.R.id.carPic;
import static com.runwise.supply.R.id.carPrice;
import static com.runwise.supply.R.id.carRealPrice;
import static com.runwise.supply.R.id.parentLayout;

/**
 * 更多好车
 */
public class MoreCarListActivity extends NetWorkActivity {
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
        setContentView(R.layout.activity_collection_list);
        this.setTitleText(true,"更多好车");
        this.setTitleLeftIcon(true,R.drawable.back_btn);

        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);

        adapter = new CarInfoListAdapter();

//        loadingLayout.setStatusLoading();
        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, REQUEST_START, page, 20);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page) , 20);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
        requestData(true, REQUEST_MAIN, page, 20);
    }

    @OnClick(R.id.left_layout)
    public void doBack(View view) {
        this.finish();
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = new PageRequest();
        request.setLimit(limit);
        request.setPz(page);
        sendConnection("car/list.json",request,where,showDialog,IndexCarInfoResult.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                IndexCarInfoResult mainResult = (IndexCarInfoResult)result;
                IndexCarInfoList mainListResult = mainResult.getData();
                loadingLayout.onSuccess(mainListResult.getEntities().size(),"暂时没有数据");
                adapter.setData(mainListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                IndexCarInfoResult startResult = (IndexCarInfoResult) result;
                IndexCarInfoList startListResult = startResult.getData();
                adapter.setData(startListResult.getEntities());
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                IndexCarInfoResult endResult = (IndexCarInfoResult) result;
                IndexCarInfoList sndListResult = endResult.getData();
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
        ToastUtil.show(mContext,errMsg);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class CarInfoListAdapter extends IBaseAdapter<IndexCarInfo> {
        @Override
        protected View getExView(int position, View convertView,
                                 ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_more_car, null);
                holder = new ViewHolder();
                holder.carPic = (SimpleDraweeView) convertView.findViewById(carPic);
                holder.carName = (TextView) convertView.findViewById(carName);
                holder.carPrice = (TextView) convertView.findViewById(carPrice);
                holder.carRealPrice = (TextView) convertView.findViewById(carRealPrice);
                holder.carName = (TextView) convertView.findViewById(carName);
                holder.carDoit = (TextView) convertView.findViewById(carDoit);
                holder.parentLayout = convertView.findViewById(parentLayout);

                holder.carPic1 = (SimpleDraweeView) convertView.findViewById(R.id.carPic1);
                holder.carName1 = (TextView) convertView.findViewById(R.id.carName1);
                holder.carPrice1 = (TextView) convertView.findViewById(R.id.carPrice1);
                holder.carRealPrice1 = (TextView) convertView.findViewById(R.id.carRealPrice1);
                holder.carName1 = (TextView) convertView.findViewById(R.id.carName1);
                holder.carDoit1 = (TextView) convertView.findViewById(R.id.carDoit1);
                holder.parentLayout1 =  convertView.findViewById(R.id.parentLayout1);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            final IndexCarInfo carInfo = mList.get(position*2);
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
                    if (UserUtils.checkLogin(intent,MoreCarListActivity.this)) {
                        startActivity(intent);
                    }
                }
            });
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,CarSettingFragmentContainer.class);
                    intent.putExtra("carid",carInfo.getCar_id());
                    startActivity(intent);
                }
            });
            int newPosition = (position*2) +1;
            if(newPosition < mList.size()) {
                final IndexCarInfo carInfo1 = mList.get(newPosition);
                holder.parentLayout1.setVisibility(View.VISIBLE);
                CarImage carImage1 = carInfo1.getImage();
                if (carImage1 != null) {
                    FrecoFactory.getInstance(mContext).disPlay(holder.carPic1, carImage1.getImg_path());
                }
                holder.carName1.setText(carInfo1.getTitle());
                CarPeriod perIod1 = carInfo1.getPeriod();
                if( perIod1 != null) {
                    HashMap<String,String> spMap1 = CommonUtils.formatMoney(perIod1.getFirst_period());
                    String firstStr = "首付:" + spMap1.get(CommonUtils.MONEY_VALUE) + spMap1.get(CommonUtils.MONEY_UNIT)+" 月供:";

                    HashMap<String,String> mMap1 = CommonUtils.formatMoney(perIod1.getMonth_period());
                    String twoStr = mMap1.get(CommonUtils.MONEY_VALUE) + mMap1.get(CommonUtils.MONEY_UNIT);

                    SpannableString spannableString1 = new SpannableString(firstStr+twoStr);
                    spannableString1.setSpan(new AbsoluteSizeSpan(14,true), 3, 3 + spMap1.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    int starIndex = firstStr.length();
                    spannableString1.setSpan(new AbsoluteSizeSpan(14,true), starIndex, starIndex + mMap1.get(CommonUtils.MONEY_VALUE).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.carPrice1.setText(spannableString1);
                }
                HashMap<String,String> spMap1 = CommonUtils.formatMoney(carInfo1.getSale_price());
                holder.carRealPrice1.setText(spMap1.get(CommonUtils.MONEY_VALUE) + spMap1.get(CommonUtils.MONEY_UNIT));
                holder.carDoit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,SelectDealerActivity.class);
                        intent.putExtra("carId",String.valueOf(carInfo1.getCar_id()));
                        if (UserUtils.checkLogin(intent,MoreCarListActivity.this)) {
                            startActivity(intent);
                        }
                    }
                });
                holder.parentLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,CarSettingFragmentContainer.class);
                        intent.putExtra("carid",carInfo1.getCar_id());
                        startActivity(intent);
                    }
                });
            }
            else{
                holder.parentLayout1.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            if (mList.size() % 2 == 0) {
                return mList.size() / 2;
            }
            return mList.size() / 2 + 1;
        }

        class ViewHolder {
             SimpleDraweeView carPic;
             TextView carName;
             TextView carPrice;
             TextView carRealPrice;
             TextView carDoit;
             View parentLayout;

            SimpleDraweeView carPic1;
            TextView carName1;
            TextView carPrice1;
            TextView carRealPrice1;
            TextView carDoit1;
            View parentLayout1;

        }

    }
}
