package com.runwise.supply.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 2017/9/13.
 */

public class SearchListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    public DataType type;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private List<ProductData.ListBean> dataList;
    private String keyWork;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
        adapter.setData(dataList);
        loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
    }
    public void setData(List<ProductData.ListBean> dataList) {
        if(type == DataType.ALL) {
            this.dataList = dataList;
        }
        else {
            List<ProductData.ListBean> typeList = new ArrayList<>();
            for (ProductData.ListBean bean : dataList){
                if (bean.getStockType().equals(type.getType())){
                    typeList.add(bean);
                }
            }
            this.dataList = typeList;
        }
    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(SearchKeyWork event) {
        adapter.setData(findArrayByWord(event.getKeyWork()));
    }

    //返回当前标签下名称包含的
    private List<ProductData.ListBean> findArrayByWord(String word) {
        keyWork = word;
        List<ProductData.ListBean> findList = new ArrayList<>();
        if(TextUtils.isEmpty(word)) {
            return dataList;
        }
        for (ProductData.ListBean bean : dataList){
            if (bean.getName().contains(word)) {
                findList.add(bean);
            }
        }
        return findList;
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }

    public class ProductAdapter extends IBaseAdapter<ProductData.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.repertory_search_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ProductData.ListBean bean =  mList.get(position);
            ProductData.ListBean productBean = bean;
            if (productBean != null){
                if(!TextUtils.isEmpty(keyWork)) {
                    int index = productBean.getName().indexOf(keyWork);
                    if(index != -1) {
                        SpannableString spannStr = new SpannableString(productBean.getName());
                        spannStr.setSpan(new ForegroundColorSpan(Color.parseColor("#6bb400")), index, index + keyWork.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.name.setText(spannStr);
                    }
                }
                else {
                    viewHolder.name.setText(productBean.getName());
                }
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EventBus.getDefault().post(bean);
                    }
                });
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
                ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getContext()).get(String.valueOf(productBean.getProductID()));
//                if (listBean != null){
//                    viewHolder.uom.setText(listBean.getUom());
//                }else{
//                    viewHolder.uom.setText("");
//                }
                viewHolder.uom.setText(bean.getProductUom());
            }
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;
            @ViewInject(R.id.number)
            TextView number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.uom)
            TextView uom;
            @ViewInject(R.id.addBtn)
            ImageView addBtn;
        }
    }
}
