package com.runwise.supply.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.ProductCountChangeEvent;
import com.kids.commonframe.base.bean.ProductGetEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class RepertoryListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    public  DataType type;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
        private List<RepertoryEntity.ListBean> dataList;
    private String keyWork;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);

    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(RepertoryEntity event) {
        List<RepertoryEntity.ListBean> typeList = new ArrayList<>();
        for (RepertoryEntity.ListBean bean : event.getList()){
            if (bean.getProduct().getStock_type().equals(type.getType())){
                typeList.add(bean);
            }
        }
        if(type == DataType.ALL) {
            dataList = event.getList();
        }
        else {
            dataList = typeList;
        }
        adapter.setData(dataList);
        loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(SearchKeyWork event) {
        adapter.setData(findArrayByWord(event.getKeyWork()));
    }

    //返回当前标签下名称包含的
    private List<RepertoryEntity.ListBean> findArrayByWord(String word) {
        keyWork = word;
        List<RepertoryEntity.ListBean> findList = new ArrayList<>();
        if(TextUtils.isEmpty(word)) {
            return dataList;
        }
        for (RepertoryEntity.ListBean bean : dataList){
            if (bean.getProduct().getName().contains(word)) {
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

    public class ProductAdapter extends IBaseAdapter<RepertoryEntity.ListBean>{
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.repertory_layout_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final RepertoryEntity.ListBean bean =  mList.get(position);
            RepertoryEntity.ListBean.ProductBean productBean = bean.getProduct();
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
                viewHolder.number.setText(productBean.getDefault_code() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImage_small());
            }
            viewHolder.value.setText(bean.getQty()+"");
            viewHolder.uom.setText(bean.getUom());
            viewHolder.dateNumber.setText(bean.getLot_num());
            viewHolder.dateLate.setText(DateFormateUtil.getLaterFormat(bean.getLife_end_date()));
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



        }
    }
}