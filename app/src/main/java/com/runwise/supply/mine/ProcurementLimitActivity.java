package com.runwise.supply.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.view.LoadingLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SumMoneyData;
import com.runwise.supply.tools.UserUtils;

public class ProcurementLimitActivity extends NetWorkActivity {


    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.tv_total_money)
    private TextView tv_total_money;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    SumMoneyData mSumMoneyData;

    public static final String KEY_SUM_MONEY_DATA = "key_sum_money_data";

    @ViewInject(R.id.amountTitle)
    private TextView amountTitle;
    @ViewInject(R.id.unitText)
    private TextView unitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurement_limit);
        showBackBtn();
        if (GlobalApplication.getInstance().getCanSeePrice()) {
            setTitleText(true, "上周采购额");
            amountTitle.setText("采购额");
            unitText.setText("总额");
        } else {
            setTitleText(true, "上周采购量");
            amountTitle.setText("采购量");
            unitText.setText("总数");
        }
        mSumMoneyData = (SumMoneyData) getIntent().getSerializableExtra(KEY_SUM_MONEY_DATA);
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);

        if (GlobalApplication.getInstance().getCanSeePrice()) {
            if (mSumMoneyData.getTotal_amount() > 10000) {
                double price = mSumMoneyData.getTotal_amount() / 10000;
                tv_total_money.setText(UserUtils.formatPrice(price + "") + "万元");
            } else {
                tv_total_money.setText(UserUtils.formatPrice(mSumMoneyData.getTotal_amount() + "") + "元");
            }
        } else {
            tv_total_money.setText(mSumMoneyData.getTotal_number() + "件");
        }

        CaiGouEAdapter caiGouEAdapter = new CaiGouEAdapter();
        caiGouEAdapter.setData(mSumMoneyData.getProduct_list());
        pullListView.setAdapter(caiGouEAdapter);
        mLoadingLayout.onSuccess(caiGouEAdapter.getCount(),"哎呀!这里是空哒~",R.drawable.default_ico_none);
    }

    class CaiGouEAdapter extends IBaseAdapter<String> {

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cai_gou_e, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_product_name.setText(mSumMoneyData.getProduct_list().get(position));
            if (GlobalApplication.getInstance().getCanSeePrice()) {
                viewHolder.tv_price.setText(UserUtils.formatPrice(mSumMoneyData.getPurchase_volume_list().get(position) + "") + "元");
                double progress = (mSumMoneyData.getPurchase_volume_list().get(position) / mSumMoneyData.getTotal_amount() * 100);
                if (progress > 0 && progress < 1) {
                    viewHolder.pbBar.setProgress(1);
                } else {
                    viewHolder.pbBar.setProgress((int) progress);
                }
            } else {
                viewHolder.tv_price.setText(mSumMoneyData.getPurchase_number_list().get(position) + "件");
                double progress = (mSumMoneyData.getPurchase_number_list().get(position) / ((float) mSumMoneyData.getTotal_number()) * 100);
                if (progress > 0 && progress < 1) {
                    viewHolder.pbBar.setProgress(1);
                } else {
                    viewHolder.pbBar.setProgress((int) progress);
                }
            }
            return convertView;
        }
    }

    public class ViewHolder {
        @ViewInject(R.id.tv_product_name)
        TextView tv_product_name;
        @ViewInject(R.id.tv_price)
        TextView tv_price;
        @ViewInject(R.id.pbBar)
        ProgressBar pbBar;
    }


    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }


}
