package com.runwise.supply.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.CommonUtils;
import com.kids.commonframe.base.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.business.entity.CarResponse;
import com.runwise.supply.business.entity.ImagesBean;
import com.runwise.supply.business.entity.Item;
import com.runwise.supply.message.DividePayStep1Activity;
import com.runwise.supply.tools.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by libin on 2017/1/20.
 */

public class CarTypeFragment extends NetWorkFragment {
    public interface CarTypeInterface{
        public void changeCollectImg(boolean isCollected,String carId,String shareUrl);
    }
    @ViewInject(R.id.dealerListView)
    private PullToRefreshListView pullListView;
    private DealerAdapter adapter;
    private ArrayList dealerArr;
    private LayoutInflater layoutInflater;
    private ConvenientBanner banner;
    private TextView pageIndicator;
    private View headView;
    private Button applybtn;
    private boolean is_collect;     //是否被收藏
    private String carSeriesTile;   //标题
    private CarTypeInterface mCallback;
    private String currentCarId;    //当前车id
    private String currentDealerId; //当前经销商id


    public boolean is_collect() {
        return is_collect;
    }

    public String getCarSeriesTile() {
        return carSeriesTile;
    }

    public void setCarSeriesTile(String carSeriesTile) {
        this.carSeriesTile = carSeriesTile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pullListView.setVisibility(View.INVISIBLE);
        layoutInflater = LayoutInflater.from(mContext);
        adapter = new DealerAdapter();
        dealerArr = new ArrayList();
        adapter.setData(dealerArr);
        pullListView.setAdapter(adapter);
        headView = layoutInflater.inflate(R.layout.cartype_head_layout,null);
        applybtn = (Button) headView.findViewById(R.id.applybtn);
        banner = (ConvenientBanner)headView.findViewById(R.id.banner);
        //通过图片比例，计算banner大小 750:500 = w:x
        int height = 500* CommonUtils.getScreenWidth(mContext)/750;
        banner.getLayoutParams().height = height;
        pageIndicator = (TextView)headView.findViewById(R.id.textview);
        pageIndicator.setText("...");
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.getRefreshableView().addHeaderView(headView);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //跳转到经销商详情
                CarResponse.DataBean.EntitiesBean bean  = (CarResponse.DataBean.EntitiesBean)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getContext(),DealerDetainActivity.class);
                String dealerId = String.valueOf(bean.getDealer_id());
                intent.putExtra("dealer_id",dealerId);
                startActivity(intent);
            }
        });
        applybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserUtils.checkLogin(null,getActivity())) {
                    //可能带着carid，去选择经销商
                    Intent intent = new Intent(mContext,SelectDealerActivity.class);
                    intent.putExtra("carId",currentCarId);
                    startActivity(intent);
                }
            }
        });
        requestData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (CarTypeInterface)context;
    }

    //从上个页面传来的id，发起网络请求
    private void requestData(){
        //String carId = mContext.getIntent().getStringExtra("car_id");
        currentCarId = getArguments().getString("carid");
        CarRequest request = new CarRequest(currentCarId);
        sendConnection("/car/detail.json",request,1000,true, CarResponse.class);
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.car_frament_layout;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        CarResponse response = (CarResponse) result;
        if (response == null || response.getData() == null || response.getData().getEntity() == null){
            ToastUtil.show(mContext,"没有该车信息");
        }else{
            pullListView.setVisibility(View.VISIBLE);
            updateUIByData(response);
        }

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    //解析请求返回的数据
    private void updateUIByData(CarResponse response) {
        //轮播图
        CarResponse.DataBean.EntityBean carBean = response.getData().getEntity();
        //修改activity任务栏的收藏按钮
        is_collect = (carBean.getIs_collect() != 0);
        String shareUrl = carBean.getShare_url();
        carSeriesTile = carBean.getTitle();
        mCallback.changeCollectImg(is_collect,currentCarId,shareUrl);
        final List<ImagesBean> images = carBean.getImages();
        if (images != null && images.size() > 0) {
            pageIndicator.setText("1/" + images.size());
            banner.setPages(new CBViewHolderCreator<BannerHolderView>() {
                @Override
                public BannerHolderView createHolder() {
                    return new BannerHolderView();
                }
            }, images).setPointViewVisible(false)
                    .startTurning(5000)
                    .setManualPageable(true);  //设置手动影响;
            banner.setCanLoop(true);
            banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    pageIndicator.setText(position + 1 + "/" + images.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        //头部其它信息
        HashMap<String,String> spMap = CommonUtils.formatMoney(carBean.getSale_price());
        HashMap<String,String> fpMap;
        HashMap<String,String> mpMap;
        if (carBean.getPeriod() != null){
            fpMap = CommonUtils.formatMoney(carBean.getPeriod().getFirst_period());
            mpMap = CommonUtils.formatMoney(carBean.getPeriod().getMonth_period());
        }else{
            fpMap = CommonUtils.formatMoney(null);
            mpMap = CommonUtils.formatMoney(null);
        }

        ((TextView)headView.findViewById(R.id.carseriesname)).setText(carBean.getTitle());
        ((TextView)headView.findViewById(R.id.pricevalue)).setText(spMap.get(CommonUtils.MONEY_VALUE+spMap.get(CommonUtils.MONEY_UNIT)));
        StringBuffer s1 =  new StringBuffer();
        s1.append("首付:").append(fpMap.get(CommonUtils.MONEY_VALUE)).append(fpMap.get(CommonUtils.MONEY_UNIT));
        StringBuffer s2 = new StringBuffer();
        s2.append("月供:").append(mpMap.get(CommonUtils.MONEY_VALUE)).append(mpMap.get(CommonUtils.MONEY_UNIT));
        int s1StartIndex = s1.indexOf(":");
        int s1LastIndex = s1.lastIndexOf(fpMap.get(CommonUtils.MONEY_UNIT));
        int s2StartIndex = s2.indexOf(":");
        int s2LastIndex = s2.lastIndexOf(mpMap.get(CommonUtils.MONEY_UNIT));
        SpannableString ss = new SpannableString(s1);
        SpannableString ss2 = new SpannableString(s2);
        ss.setSpan(new AbsoluteSizeSpan(15, true), s1StartIndex, s1LastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体大小
        ss2.setSpan(new AbsoluteSizeSpan(15, true), s2StartIndex, s2LastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView)headView.findViewById(R.id.firstPeriod)).setText(ss);
        ((TextView)headView.findViewById(R.id.monthPeriod)).setText(ss2);

        //列表
        List<CarResponse.DataBean.EntitiesBean> dealerList = response.getData().getEntities();
        if (dealerList != null && dealerList.size() > 0){
            dealerArr.clear();
            dealerArr.addAll(dealerList);
            adapter.setData(dealerArr);
        };
        //将配置信息，暂保存到宿主activity上，便于配置 fragment直接获取
        saveDataToActivity(carBean);

    }
    private void saveDataToActivity(CarResponse.DataBean.EntityBean bean){
        List<Item> itemList = new ArrayList<Item>();
        Item baseItem = new Item();
        baseItem.setType(1);
        if (bean != null && bean.getAttribute() != null && bean.getAttribute().getDetail() != null && bean.getAttribute().getDetail().getBaseinfo() != null){
            baseItem.setTitle(bean.getAttribute().getDetail().getBaseinfo().getName());
        }
        itemList.add(baseItem);
        if (bean != null && bean.getAttribute() != null && bean.getAttribute().getDetail() != null && bean.getAttribute().getDetail().getBaseinfo() != null
                && bean.getAttribute().getDetail().getBaseinfo().getItems() != null){
            for (CarResponse.DataBean.EntityBean.AttributeBean.DetailBean.BaseinfoBean.ItemsBean temp : bean.getAttribute().getDetail().getBaseinfo().getItems()){
                Item it = new Item();
                it.setType(0);
                it.setTitle(temp.getField_name());
                it.setContent(temp.getField_value());
                itemList.add(it);
            }
        }
        Item carlenInfo = new Item();
        carlenInfo.setType(1);
        if (bean.getAttribute() != null && bean.getAttribute().getDetail() != null && bean.getAttribute().getDetail().getCarleninfo() != null){
            carlenInfo.setTitle(bean.getAttribute().getDetail().getCarleninfo().getName());
        }
        itemList.add(carlenInfo);
        if (bean.getAttribute() != null && bean.getAttribute().getDetail() != null && bean.getAttribute().getDetail().getCarleninfo() != null && bean.getAttribute().getDetail().getCarleninfo().getItems() != null){
            for (CarResponse.DataBean.EntityBean.AttributeBean.DetailBean.CarleninfoBean.ItemsBeanX temp : bean.getAttribute().getDetail().getCarleninfo().getItems()){
                Item it = new Item();
                it.setType(0);
                it.setTitle(temp.getField_name());
                it.setContent(temp.getField_value());
                itemList.add(it);
            }
            ((CarSettingFragmentContainer)getActivity()).setItemList(itemList);
        }
    }
    public class DealerAdapter extends IBaseAdapter {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.dealeritem, null);
                viewHolder.ll = (LinearLayout)convertView.findViewById(R.id.dealerid);
                viewHolder.name = (TextView) convertView.findViewById(R.id.dealername);
                viewHolder.score = (TextView) convertView.findViewById(R.id.ratingscore);
                viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
                viewHolder.km = (TextView) convertView.findViewById(R.id.km);
                viewHolder.btn = (Button) convertView.findViewById(R.id.button);
                viewHolder.address = (TextView) convertView.findViewById(R.id.address);
                convertView.setTag(viewHolder);
                viewHolder.ll.setPadding(0,0,0,0);
                viewHolder.btn.setText("立即租车");
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //进行赋值操作
            final CarResponse.DataBean.EntitiesBean bean = (CarResponse.DataBean.EntitiesBean)dealerArr.get(position);
            viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //事着车id + 经销商id  跳转到申请流程
                    String dealerId = String.valueOf(bean.getDealer_id());
                    Intent intent = new Intent(mContext, DividePayStep1Activity.class);
                    intent.putExtra("dealerId",dealerId);
                    intent.putExtra("carId",currentCarId);
                    startActivity(intent);
                }
            });
            viewHolder.name.setText(bean.getDealer_name());
            viewHolder.score.setText(bean.getScore()+"分");
            viewHolder.ratingBar.setRating(bean.getScore());
            //viewHolder.km.setText(bean.get);
            viewHolder.address.setText(bean.getAddress());
            return convertView;
        }

        class ViewHolder {
            LinearLayout ll;        //rootview
            TextView name;          //名称
            TextView score;         //分数
            RatingBar ratingBar;     //星星
            TextView km;            //距离
            TextView address;       //位置
            Button btn;           //租车按钮
        }
    }

    public static class CarRequest{
        private String car_id;
        private String type;

        public CarRequest(String car_id) {
            this.car_id = car_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCar_id() {
            return car_id;
        }

        public void setCar_id(String car_id) {
            this.car_id = car_id;
        }
    }

}
