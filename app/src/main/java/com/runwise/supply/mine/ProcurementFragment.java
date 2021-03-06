package com.runwise.supply.mine;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.ProcurementRequest;
import com.runwise.supply.mine.entity.ProcurementAddResult;
import com.runwise.supply.mine.entity.ProcurementEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2017/8/25.
 */

public class ProcurementFragment extends NetWorkFragment implements LoadingLayoutInterface {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private List<ProcurementEntity.ListBean.ProductsBean> dataList = new ArrayList<>();
    private boolean canSeePrice = true;             //默认价格中可见
    //选中数量map
    private static HashMap<String,Integer> countMap = new HashMap<>();
    public static HashMap<String, Integer> getCountMap() {
        return countMap;
    }
    private String keyWork;
    ArrayList<Integer> mSelection = new ArrayList<>();
    HashMap<Integer,ProcurementEntity.ListBean> mHeadMap = new HashMap<>();

    public  final int REQUEST_CODE_PROCUREMENT = 1 << 0;
    public int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setAdapter(adapter);
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData();
            }
        });
        loadingLayout.setOnRetryClickListener(this);
        requestData();
        loadingLayout.setStatusLoading();
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    private void requestData() {
        ProcurementRequest procurementRequest = new ProcurementRequest();
        procurementRequest.setType(type);
        sendConnection("/gongfu/shop/zicai/list",procurementRequest,REQUEST_CODE_PROCUREMENT, false, ProcurementEntity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountSynEvent(ProcurementAddResult procurementAddResult) {
        requestData();
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_PROCUREMENT:
                ProcurementEntity procurementEntity = (ProcurementEntity)result.getResult().getData();
                mSelection.clear();
                dataList.clear();
                mHeadMap.clear();

                for (ProcurementEntity.ListBean listBean : procurementEntity.getList()){
                    mSelection.add(dataList.size());
                    mHeadMap.put(dataList.size(),listBean);
                    for (ProcurementEntity.ListBean.ProductsBean productsBean:listBean.getProducts()){
                        dataList.add(productsBean);
                    }
                }
                if(dataList != null) {
                    adapter.setData(dataList);
                }
                pullListView.onRefreshComplete();
                loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_ico_none);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        if (errMsg.equals(getResources().getString(R.string.network_error))){
            ToastUtil.show(getActivity(),getResources().getString(R.string.network_error));
            loadingLayout.onFailure(errMsg, R.drawable.default_icon_checkconnection);
        }else{
            loadingLayout.onSuccess(0,"哎呀！这里是空哒~~",R.drawable.default_ico_none);
        }
    }

    @Override
    public void retryOnClick(View view) {
        requestData();
    }

    public class ProductAdapter extends IBaseAdapter<ProcurementEntity.ListBean.ProductsBean> {
        private boolean ischange;
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final ProcurementEntity.ListBean.ProductsBean bean = (ProcurementEntity.ListBean.ProductsBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.procurement_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ischange = true;
            ischange = false;

//            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
                viewHolder.name.setText(bean.getName());
                StringBuffer sb = new StringBuffer(bean.getDefaultCode());
                sb.append("  ").append(bean.getUnit());
                DecimalFormat df = new DecimalFormat("#.##");
                if (canSeePrice) {
//                    if (basicBean.isIsTwoUnit()) {
//                        sb.append("  ¥")
//                                .append(df.format(Double.valueOf(bean.getSettlePrice())))
//                                .append("元/")
//                                .append(bean.getSettleUomId());
//                    } else {
//                        sb.append("  ¥")
//                                .append(df.format(Double.valueOf(bean.getPrice())))
//                                .append("元/")
//                                .append(bean.getUom());
//                    }
                }
                viewHolder.content.setText(sb.toString());
                viewHolder.tvCount.setText(NumberUtil.getIOrD(String.valueOf(bean.getQty())) + bean.getStockUom());
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImageMedium());
                if (isHead(position)){
                    viewHolder.rl_head.setVisibility(View.VISIBLE);
                    ProcurementEntity.ListBean listBean = mHeadMap.get(position);
                    viewHolder.tv_date.setText(listBean.getDate());
                    viewHolder.tv_cai_gou_ren.setText("采购人:"+listBean.getUser());
                }else{
                    viewHolder.rl_head.setVisibility(View.GONE);
                }
            return convertView;
        }

        private boolean isHead(int position){
            for (Integer integer:mSelection){
                if (position == integer){
                    return true;
                }
            }
            return false;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView    sDv;    //头像
            @ViewInject(R.id.content)
            TextView            content;//内容
            @ViewInject(R.id.tv_count)
            TextView tvCount;//数量
            @ViewInject(R.id.rl_head)
            RelativeLayout rl_head;//数量
            @ViewInject(R.id.tv_date)
            TextView tv_date;//日期
            @ViewInject(R.id.tv_cai_gou_ren)
            TextView tv_cai_gou_ren;//采购人
        }
    }
}
