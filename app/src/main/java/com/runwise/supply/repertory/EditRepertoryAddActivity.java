package com.runwise.supply.repertory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.RepertoryFragment;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.view.NoWatchEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存添加收索
 */

public class EditRepertoryAddActivity extends NetWorkActivity{
    @ViewInject(R.id.searchET)
    private EditText searchET;
    private final int PRODUCT_GET = 1;

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;

    @ViewInject(R.id.name)
    private TextView name;
    @ViewInject(R.id.number)
    private TextView number;
    @ViewInject(R.id.content)
    private TextView content;
    @ViewInject(R.id.listView)
    private ListView listView;
    private AddProductAdapter addProductAdapter;
    @ViewInject(R.id.finalButton)
    private TextView finalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_repertory_layout);
        addProductAdapter = new AddProductAdapter();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString();
                SearchKeyWork bean = new SearchKeyWork();
                bean.setKeyWork(searchText);
                EventBus.getDefault().post(bean);
            }
        });
        adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        smartTabLayout.setViewPager(viewPager);
        Object param = null;
        sendConnection("/gongfu/shop/stock/list",param,PRODUCT_GET,true, RepertoryEntity.class);
    }

    @OnClick({R.id.cancelBtn})
    public void btnClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.cancelBtn:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
            Bundle bundle = new Bundle();
            SearchListFragment allFragment = new SearchListFragment();
            allFragment.type = DataType.ALL;
            allFragment.setArguments(bundle);
            SearchListFragment coldFragment = new SearchListFragment();
            coldFragment.type = DataType.LENGCANGHUO;
            coldFragment.setArguments(bundle);
            SearchListFragment freezeFragment = new SearchListFragment();
            freezeFragment.type = DataType.FREEZE;
            freezeFragment.setArguments(bundle);
            SearchListFragment dryFragment = new SearchListFragment();
            dryFragment.type = DataType.DRY;
            dryFragment.setArguments(bundle);
            fragmentList.add(allFragment);
            fragmentList.add(coldFragment);
            fragmentList.add(freezeFragment);
            fragmentList.add(dryFragment);
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }
    }


    public class AddProductAdapter extends IBaseAdapter<RepertoryEntity.ListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
           ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.add_text_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final RepertoryEntity.ListBean bean =  mList.get(position);
            RepertoryEntity.ListBean.ProductBean productBean = bean.getProduct();

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.addNumber)
            NoWatchEditText addNumber;
            @ViewInject(R.id.innerIcon)
            ImageView innerIcon;
            @ViewInject(R.id.addSum)
            NoWatchEditText addSum;
            @ViewInject(R.id.addIcon)
            ImageView addIcon;
        }
    }
}
