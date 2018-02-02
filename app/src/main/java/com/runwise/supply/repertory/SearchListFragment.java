package com.runwise.supply.repertory;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.runwise.supply.event.RefreshEvent;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.repertory.entity.EditHotResult;
import com.runwise.supply.tools.RunwiseKeyBoard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class SearchListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    public DataType type;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private List<EditHotResult.ListBean> dataList;
    private String keyWork;
    private EditRepertoryAddActivity.ProductCountSetter mProductCountSetter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
        adapter.setData(dataList);
        loadingLayout.onSuccess(adapter.getCount(), "暂时没有数据");
    }

    public void setData(List<EditHotResult.ListBean> dataList) {
        if (type == DataType.ALL) {
            this.dataList = dataList;
        } else {
            List<EditHotResult.ListBean> typeList = new ArrayList<>();
            for (EditHotResult.ListBean bean : dataList) {
                if (bean.getProduct().getStockType().equals(type.getType())) {
                    typeList.add(bean);
                }
            }
            this.dataList = typeList;
        }
    }

    public void setProductCountSetter(EditRepertoryAddActivity.ProductCountSetter productCountSetter) {
        this.mProductCountSetter = productCountSetter;
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(SearchKeyWork event) {
        adapter.setData(findArrayByWord(event.getKeyWork()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        adapter.notifyDataSetChanged();
    }

    //返回当前标签下名称包含的
    private List<EditHotResult.ListBean> findArrayByWord(String word) {
        keyWork = word;
        List<EditHotResult.ListBean> findList = new ArrayList<>();
        if (TextUtils.isEmpty(word)) {
            return dataList;
        }
        for (EditHotResult.ListBean bean : dataList) {
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

    public class ProductAdapter extends IBaseAdapter<EditHotResult.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.repertory_search_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final EditHotResult.ListBean bean = mList.get(position);
            EditHotResult.ListBean.ProductBean productBean = bean.getProduct();
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
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
            }
            double count = mProductCountSetter.getCount(bean);
            viewHolder.mTvProductCount.setText(NumberUtil.getIOrD(count)+productBean.getUom());

            if (count > 0) {
                viewHolder.mTvProductCount.setVisibility(View.VISIBLE);
                viewHolder.reduceBtn.setVisibility(View.VISIBLE);
                viewHolder.addBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
            } else {
                viewHolder.mTvProductCount.setVisibility(View.INVISIBLE);
                viewHolder.reduceBtn.setVisibility(View.INVISIBLE);
                viewHolder.addBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
            }
            /**
             * 减
             */
            viewHolder.reduceBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    double currentNum = mProductCountSetter.getCount(bean);
                    if (currentNum > 0) {
                        //https://stackoverflow.com/questions/179427/how-to-resolve-a-java-rounding-double-issue
                        //防止double的问题
                        currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                        if(currentNum<0)currentNum = 0;
                        viewHolder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum));
                        mProductCountSetter.setCount(bean,currentNum);
                        if (currentNum == 0) {
                            v.setVisibility(View.INVISIBLE);
                            viewHolder.mTvProductCount.setVisibility(View.INVISIBLE);
                            viewHolder.addBtn.setBackgroundResource(R.drawable.order_btn_add_gray);
                        }
                        EventBus.getDefault().post(new RefreshEvent());
                    }

                }
            });
            /**
             * 加
             */
            viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    double currentNum = mProductCountSetter.getCount(bean);
                    currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                    viewHolder.mTvProductCount.setText(NumberUtil.getIOrD(currentNum));
//                mCountMap.put(bean, currentNum);
                    mProductCountSetter.setCount(bean,currentNum);
                    if (currentNum == 1) {//0变到1
                        viewHolder.addBtn.setVisibility(View.VISIBLE);
                        viewHolder.mTvProductCount.setVisibility(View.VISIBLE);
                        viewHolder.addBtn.setBackgroundResource(R.drawable.ic_order_btn_add_green_part);
                    }
                    EventBus.getDefault().post(new RefreshEvent());
                }
            });

            /**
             * 点击数量展示输入对话框
             */
            viewHolder.mTvProductCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double currentCount = mProductCountSetter.getCount(bean);
                    bean.setCount(currentCount);
                    RunwiseKeyBoard runwiseKeyBoard = new RunwiseKeyBoard(getContext());
                    runwiseKeyBoard.setUp(bean, new RunwiseKeyBoard.SetCountListener() {
                        @Override
                        public void onSetCount(double count) {
                            mProductCountSetter.setCount(bean,count);
                            EventBus.getDefault().post(new RefreshEvent());
                        }
                    });
                    runwiseKeyBoard.show();
                }
            });

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.tv_product_name)
            TextView name;
            @ViewInject(R.id.sdv_product_image)
            SimpleDraweeView sDv;
            @ViewInject(R.id.tv_product_code)
            TextView number;
            @ViewInject(R.id.tv_product_content)
            TextView content;
            @ViewInject(R.id.iv_product_add)
            ImageButton addBtn;
            @ViewInject(R.id.iv_product_reduce)
            ImageButton reduceBtn;
            @ViewInject(R.id.tv_product_count)
            TextView mTvProductCount;
        }
    }
}
