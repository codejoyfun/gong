package com.runwise.supply.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.MainActivity;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RefreshPepertoy;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyAct;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.fragment.OrderProductFragment.BUNDLE_KEY_LIST;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class RepertoryListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private List<RepertoryEntity.ListBean> dataList;
    private String keyWork;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        if (mContext instanceof MainActivity) {
            pullListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        pullListView.setPullToRefreshOverScrollEnabled(false);
        pullListView.setScrollingWhileRefreshingEnabled(true);
        pullListView.setAdapter(adapter);
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                EventBus.getDefault().post(new RefreshPepertoy());
            }
        });
        dataList = (List<RepertoryEntity.ListBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        if (dataList != null) {
            adapter.setData(dataList);
            loadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_goodsnone);
        }
    }
    DataType type;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(RepertoryEntity event) {
        List<RepertoryEntity.ListBean> typeList = new ArrayList<>();
        for (RepertoryEntity.ListBean bean : event.getList()){
            ProductBasicList.ListBean product = ProductBasicUtils.getBasicMap(getActivity()).get(String.valueOf(bean.getProductID()));
            if (product.getStockType().equals(type.getType())){
                typeList.add(bean);
            }
        }
        if(type == DataType.ALL) {
            dataList = event.getList();
        }
        else {
            dataList = typeList;
        }
        if(adapter != null) {
            adapter.setData(dataList);
            loadingLayout.onSuccess(adapter.getCount(),"哎呀！这里是空哒~~",R.drawable.default_icon_goodsnone);
        }
        if(pullListView != null) {
            pullListView.onRefreshComplete();
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(SearchKeyAct event) {
        if (mContext.getClass().getSimpleName().equals(event.getActName())) {
            adapter.setData(findArrayByWord(event.getKeyWork()));
            loadingLayout.onSuccess(adapter.getCount(), "哎呀！这里是空哒~~", R.drawable.default_icon_searchnone);

        }
    }

    //返回当前标签下名称包含的
    private List<RepertoryEntity.ListBean> findArrayByWord(String word) {
        keyWork = word;
        List<RepertoryEntity.ListBean> findList = new ArrayList<>();
        if (TextUtils.isEmpty(word)) {
            return dataList;
        }
        for (RepertoryEntity.ListBean bean : dataList) {
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

    public class ProductAdapter extends IBaseAdapter<RepertoryEntity.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.repertory_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final RepertoryEntity.ListBean bean = mList.get(position);
            ProductBasicList.ListBean productBean = bean.getProduct();
            if (productBean != null) {
                if (!TextUtils.isEmpty(keyWork)) {
                    int index = productBean.getName().indexOf(keyWork);
                    if (index != -1) {
                        SpannableString spannStr = new SpannableString(productBean.getName());
                        spannStr.setSpan(new ForegroundColorSpan(Color.parseColor("#6bb400")), index, index + keyWork.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder.name.setText(spannStr);
                    }
                } else {
                    viewHolder.name.setText(productBean.getName());
                }
                viewHolder.number.setText(productBean.getDefaultCode() + " | ");
                viewHolder.content.setText(productBean.getUnit());
                if (productBean.getImage() !=null){
                    FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
                }
            }
            viewHolder.value.setText(NumberUtil.getIOrD(String.valueOf(bean.getQty())));
            viewHolder.uom.setText(bean.getUom());
            if (TextUtils.isEmpty(bean.getUom())){
                ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getContext()).get(String.valueOf(bean.getProductID()));
                if (listBean!=null){
                    viewHolder.uom.setText(listBean.getProductUom());
                }
            }
            if (TextUtils.isEmpty(bean.getLotNum())){
                viewHolder.dateNumber.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.dateNumber.setText(bean.getLotNum());
                viewHolder.dateNumber.setVisibility(View.VISIBLE);
            }
            viewHolder.dateLate.setText(DateFormateUtil.getLaterFormat(bean.getLifeEndDate()));
            if (bean.getImageId() != 0){
                viewHolder.sDv.setImageResource(bean.getImageId());
            }
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;
            @ViewInject(R.id.number)
            TextView number;
            @ViewInject(R.id.content)
            TextView content;
            @ViewInject(R.id.value)
            TextView value;
            @ViewInject(R.id.uom)
            TextView uom;
            @ViewInject(R.id.dateNumber)
            TextView dateNumber;
            @ViewInject(R.id.dateLate)
            TextView dateLate;
        }
    }
}
