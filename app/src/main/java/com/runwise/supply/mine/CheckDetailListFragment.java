package com.runwise.supply.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
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
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.devInterface.LoadingLayoutInterface;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.PageRequest;
import com.runwise.supply.mine.entity.CheckResult;
import com.runwise.supply.mine.entity.PandianDetail;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.ProductBasicHelper;

import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 盘点记录详情
 */
public class CheckDetailListFragment extends NetWorkFragment implements AdapterView.OnItemClickListener,LoadingLayoutInterface {
    private static final int REQUEST_MAIN = 1;
    private static final int REQUEST_START = 2;
    private static final int REQUEST_DEN = 3;
    private static final int REQUEST_PRODUCT = 4;



    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private PriceAdapter adapter;
    private PullToRefreshBase.OnRefreshListener2 mOnRefreshListener2;
    ProductBasicHelper mProductBasicHelper;

    private int page = 1;
    public DataType type;
    private List<PandianResult.InventoryBean.LinesBean> typeList;
    private boolean reading;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckResult.ListBean bean = (CheckResult.ListBean)mContext.getIntent().getSerializableExtra("bean");
        if ("confirm".equals(bean.getState())) {
            reading = true;
        }
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setOnItemClickListener(this);

        adapter = new PriceAdapter();

        if(mOnRefreshListener2 == null){
            mOnRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    page = 1;
                    requestData(false, REQUEST_START, page, 10);
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    requestData(false, REQUEST_DEN, (++page) , 10);
                }
            };

        }
        pullListView.setOnRefreshListener(mOnRefreshListener2);
        pullListView.setAdapter(adapter);
        page = 1;
//        requestData(false, REQUEST_MAIN, page, 10);
//        loadingLayout.setStatusLoading();
        loadingLayout.setOnRetryClickListener(this);
        adapter.setData(typeList);
        loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_ico_none);
        pullListView.setMinimumHeight(DensityUtil.getScreenH(getActivity()));
        mProductBasicHelper = new ProductBasicHelper(getActivity(),netWorkHelper);
        mProductBasicHelper.checkInventory(typeList);
        mProductBasicHelper.requestDetail(REQUEST_PRODUCT);
    }

    public void setData( List<PandianResult.InventoryBean.LinesBean> typeList) {
        this.typeList = typeList;
    }

    public void requestData (boolean showDialog,int where, int page,int limit) {
        PageRequest request = null;
//        request.setLimit(limit);
//        request.setPz(page);
        sendConnection("/gongfu/shop/inventory/"+mContext.getIntent().getStringExtra("id")+"/list",request,where,showDialog,PandianDetail.class);
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_MAIN:
                PandianDetail mainListResult = (PandianDetail)result.getResult();

//                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_START:
                PandianDetail startListResult = (PandianDetail)result.getResult();
//                adapter.setData(handlerDataList(startListResult.getInventory().getList()));
                pullListView.onRefreshComplete(Integer.MAX_VALUE);
                break;
            case REQUEST_DEN:
                PandianDetail sndListResult = (PandianDetail)result.getResult();
                if (sndListResult.getInventory().getList() != null && !sndListResult.getInventory().getList().isEmpty()) {
//                    adapter.appendData(handlerDataList(sndListResult.getInventory().getList()));
                    pullListView.onRefreshComplete(Integer.MAX_VALUE);
                }
                else {
                    pullListView.onRefreshComplete(adapter.getCount());
                }
                break;
            case REQUEST_PRODUCT:
                if(mProductBasicHelper.onSuccess(result)){
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
        loadingLayout.onFailure(errMsg);
        pullListView.onRefreshComplete(Integer.MAX_VALUE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        PandianDetail.ListBean bean = (PandianDetail.ListBean)parent.getAdapter().getItem(position);
    }


    @Override
    protected int createViewByLayoutId() {
        return R.layout.activity_notitle_list;
    }

    @Override
    public void retryOnClick(View view) {
        loadingLayout.setStatusLoading();
        page = 1;
        requestData(false, REQUEST_MAIN, page, 10);
    }


    public class PriceAdapter extends IBaseAdapter<PandianResult.InventoryBean.LinesBean>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.pandian_layout_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final PandianResult.InventoryBean.LinesBean bean =  mList.get(position);
                ProductBasicList.ListBean productBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (productBean != null){
                viewHolder.name.setText(productBean.getName());
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                if(productBean.getImage() != null)
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
            }
            if(TextUtils.isEmpty(bean.getLotNum())){
                viewHolder.dateNumber.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.dateNumber.setText(bean.getLotNum());
            }

            if( bean.getDiff() == 0 || reading) {
                viewHolder.value.setText("--");
                viewHolder.value.setTextColor(Color.parseColor("#9b9b9b"));
            }
            else if(bean.getDiff() > 0) {
                viewHolder.value.setText(NumberUtil.getIOrD(bean.getDiff()));
                viewHolder.value.setTextColor(Color.parseColor("#9cb62e"));
            }
            else{
                viewHolder.value.setText(NumberUtil.getIOrD(bean.getDiff()));
                viewHolder.value.setTextColor(Color.parseColor("#e75967"));
            }
            viewHolder.dateLate.setText(NumberUtil.getIOrD(bean.getActualQty()));
            viewHolder.dateLate1.setText("/"+ NumberUtil.getIOrD(bean.getTheoreticalQty()));
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView    sDv;
            @ViewInject(R.id.number)
            TextView            number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.value)
            TextView         value;
            @ViewInject(R.id.uom)
            TextView         uom;
            @ViewInject(R.id.dateNumber)
            TextView         dateNumber;
            @ViewInject(R.id.dateLate)
            TextView            dateLate;
            @ViewInject(R.id.dateLate1)
            TextView            dateLate1;


        }
    }

}
