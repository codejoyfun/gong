package com.runwise.supply.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.repertory.SearchListFragment;
import com.runwise.supply.repertory.entity.EditHotResult;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.view.NoWatchEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ProcurementAddActivity extends NetWorkActivity {
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
    @ViewInject(R.id.finalButton)
    private TextView finalButton;
    @ViewInject(R.id.productImage)
    private SimpleDraweeView productImage;

    @ViewInject(R.id.listView)
    private ListView listView;
    private AddProductAdapter addProductAdapter;

    private List<Fragment> fragmentList = new ArrayList<>();

    private Animation topShowAnim;
    private Animation topHideAnim;
    @ViewInject(R.id.addRootView)
    private View addRootView;
    @ViewInject(R.id.bgView)
    private View bgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurement_add);
        addProductAdapter = new AddProductAdapter();
        listView.setAdapter(addProductAdapter);
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
        Object param = null;
        sendConnection("/gongfu/shop/inventory/lot/list",param,PRODUCT_GET,true, EditHotResult.class);
//        bgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setCommontTopHide();
//            }
//        });
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowPopEvent(final EditHotResult.LotInProductListBean bean) {
        final EditHotResult.LotInProductListBean.ProductBean productBean = bean.getProduct();
        addProductAdapter.setData(bean.getLot_list());
        name.setText(productBean.getName());
        number.setText(productBean.getDefault_code() + " | ");
        content.setText(productBean.getUnit());
        FrecoFactory.getInstance(mContext).disPlay(productImage, Constant.BASE_URL + productBean.getImage().getImage_small());
        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List< EditRepertoryResult.InventoryBean.ListBean> newProductList = new ArrayList<>();
                for (EditHotResult.LotInProductListBean.LotListBean logBean : addProductAdapter.getList()){
                    //现在只添加手动添加的
                    if (!TextUtils.isEmpty(logBean.getLot_num()) && logBean.getSum() != 0 ) {
                        EditRepertoryResult.InventoryBean.ListBean bean = new EditRepertoryResult.InventoryBean.ListBean();
                        bean.setLife_end_date(logBean.getLife_end_date());
                        bean.setTheoretical_qty(logBean.getSum());
                        bean.setLot_num(logBean.getLot_num());
                        bean.setLot_id(logBean.getLot_id());
//                        bean.setUnit_price();
//                        bean.setActual_qty();
                        bean.setEditNum(logBean.getSum());
                        EditRepertoryResult.InventoryBean.ListBean.ProductBean product = new EditRepertoryResult.InventoryBean.ListBean.ProductBean();
                        product.setName(productBean.getName());
                        product.setBarcode(productBean.getBarcode());
                        product.setStock_type(productBean.getStock_type());
                        product.setDefault_code(productBean.getDefault_code());
                        product.setId(productBean.getId());
                        product.setUnit(productBean.getUnit());
                        EditRepertoryResult.InventoryBean.ListBean.ProductBean.ImageBean imageBean = new EditRepertoryResult.InventoryBean.ListBean.ProductBean.ImageBean();
                        imageBean.setImage(productBean.getImage().getImage());
                        imageBean.setImage_small(productBean.getImage().getImage_small());
                        imageBean.setImage_medium(productBean.getImage().getImage_medium());
                        product.setImage(imageBean);

                        bean.setProduct(product);
                        newProductList.add(bean);
                    }
                }
                NewAdd newAddBean = new NewAdd();
                newAddBean.setNewProductList(newProductList);
                if(!newProductList.isEmpty()) {
                    EventBus.getDefault().post(newAddBean);
                }
                setCommontTopHide();
                finish();
            }
        });
        setCommontTopShow();
    }
    @OnClick(R.id.colseIcon)
    public void closeIcon(View view) {
        setCommontTopHide();
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                EditHotResult editHotResult = (EditHotResult)result.getResult();
                List<EditHotResult.LotInProductListBean> hotList = editHotResult.getLot_in_product_list();

                Bundle bundle = new Bundle();
                SearchListFragment allFragment = new SearchListFragment();
                allFragment.type = DataType.ALL;
                allFragment.setArguments(bundle);
                allFragment.setData(hotList);

                SearchListFragment coldFragment = new SearchListFragment();
                coldFragment.type = DataType.LENGCANGHUO;
                coldFragment.setArguments(bundle);
                coldFragment.setData(hotList);

                SearchListFragment freezeFragment = new SearchListFragment();
                freezeFragment.type = DataType.FREEZE;
                freezeFragment.setArguments(bundle);
                freezeFragment.setData(hotList);

                SearchListFragment dryFragment = new SearchListFragment();
                dryFragment.type = DataType.DRY;
                dryFragment.setArguments(bundle);
                dryFragment.setData(hotList);
                fragmentList.add(allFragment);
                fragmentList.add(coldFragment);
                fragmentList.add(freezeFragment);
                fragmentList.add(dryFragment);
                adapter = new TabPageIndicatorAdapter(this.getSupportFragmentManager());
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(4);
                smartTabLayout.setViewPager(viewPager);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext,errMsg);
    }
    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            titleList.add("全部");
            titleList.add("冷藏货");
            titleList.add("冻货");
            titleList.add("干货");
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
            return fragmentList.size();
        }
    }


    public class AddProductAdapter extends IBaseAdapter<EditHotResult.LotInProductListBean.LotListBean> {
        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.add_text_item, null);
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final EditHotResult.LotInProductListBean.LotListBean bean =  mList.get(position);
            viewHolder.addNumber.removeTextChangedListener();
            viewHolder.addSum.removeTextChangedListener();
            viewHolder.addNumber.setText(bean.getLot_num()+"");
            viewHolder.addSum.setText(bean.getSum()+"");
            viewHolder.addNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    if(!TextUtils.isEmpty(editable.toString())) {
                        bean.setLot_num(editable.toString());
                        addLastItem();
//                        notifyDataSetChanged();
                    }
                    else{
                        bean.setLot_num("");
                        removeLastItem();
                        notifyDataSetChanged();
                    }
                }
            });
            viewHolder.addSum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    if(!TextUtils.isEmpty(editable.toString())) {
                        int value = Integer.parseInt(editable.toString());
                        bean.setSum(value);
                    }
                }
            });
            viewHolder.innerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int ininVal = bean.getSum();
                    ininVal-= 1;
                    if(ininVal < 0) {
                        ininVal = 0;
                    }
                    bean.setSum(ininVal);
                    viewHolder.addSum.setText(bean.getSum()+"");
                }
            });
            viewHolder.addIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int ininVal = bean.getSum();
                    ininVal+= 1;
                    bean.setSum(ininVal);
                    viewHolder.addSum.setText(bean.getSum()+"");
                }
            });
            return convertView;
        }

        @Override
        public void setData(List<EditHotResult.LotInProductListBean.LotListBean> list) {
            super.setData(list);
            addLastItem();
        }

        private void addLastItem() {
            if(!mList.isEmpty() && !TextUtils.isEmpty(mList.get(mList.size()-1).getLot_num())) {
                EditHotResult.LotInProductListBean.LotListBean add = new EditHotResult.LotInProductListBean.LotListBean();
                add.setType(1);
                add.setLot_num("");
                mList.add(add);
            }
        }
        private void removeLastItem() {
            if(mList.size() >= 2) {
                EditHotResult.LotInProductListBean.LotListBean bean = mList.get(mList.size()-2);
                EditHotResult.LotInProductListBean.LotListBean lastBean = mList.get(mList.size()-1);
                if(TextUtils.isEmpty(bean.getLot_num()) && bean.getSum() ==0 && TextUtils.isEmpty(lastBean.getLot_num()) && lastBean.getSum() ==0) {
                    mList.remove(mList.size() - 1);
                }
            }
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
    //显示弹窗
    public void setCommontTopShow() {
        if ( topShowAnim == null ) {
            topShowAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.show_popwindow);
        }
        if (addRootView.getVisibility() == View.GONE) {
            addRootView.startAnimation(topShowAnim);
            topShowAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }
            });
            bgView.setVisibility(View.VISIBLE);
        }
    }
    //影藏弹窗
    public void setCommontTopHide() {
        if (topHideAnim == null ) {
            topHideAnim = AnimationUtils.loadAnimation(mContext, com.kids.commonframe.R.anim.hide_popwindow);
        }
        if (addRootView.getVisibility() == View.VISIBLE) {
            topHideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    addRootView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    addRootView.setVisibility(View.GONE);
                }
            });
            addRootView.startAnimation(topHideAnim);
            bgView.setVisibility(View.GONE);

        }
    }
}
