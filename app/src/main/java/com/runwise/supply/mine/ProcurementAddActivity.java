package com.runwise.supply.mine;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.ProcurementAddResult;
import com.runwise.supply.mine.entity.ProcurenmentAddRequest;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.repertory.SearchListFragment;
import com.runwise.supply.repertory.entity.EditHotResult;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;

public class ProcurementAddActivity extends NetWorkActivity {
    @ViewInject(R.id.searchET)
    private EditText searchET;
    private final int PRODUCT_GET = 1;
    private final int PRODUCT_ADD_1 = 2;
    private final int PRODUCT_ADD_2 = 3;

    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    private TabPageIndicatorAdapter adapter;
//TYPE1

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
    @ViewInject(R.id.et_batch_number)
    private EditText et_batch_number;
    @ViewInject(R.id.tv_product_date)
    private TextView tv_product_date;
    @ViewInject(R.id.tv_product_date_value)
    private TextView tv_product_date_value;
    @ViewInject(R.id.et_product_amount)
    private EditText et_product_amount;
    //TYPE2
    @ViewInject(R.id.name1)
    private TextView name1;
    @ViewInject(R.id.number1)
    private TextView number1;
    @ViewInject(R.id.content1)
    private TextView content1;
    @ViewInject(R.id.productImage1)
    private SimpleDraweeView productImage1;
    @ViewInject(R.id.et_product_amount1)
    private EditText et_product_amount1;
    @ViewInject(R.id.finalButton1)
    private TextView finalButton1;


    private List<Fragment> fragmentList = new ArrayList<>();
    private Animation topShowAnim;
    private Animation topHideAnim;

    @ViewInject(R.id.bgView)
    private View bgView;
    @ViewInject(R.id.popView1)
    private View popView1;
    @ViewInject(R.id.popView2)
    private View popView2;
    @ViewInject(R.id.addRootView)
    private View addRootView;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
    private TimePickerView pvCustomTime;
    private WheelView wheelView;
    private EditHotResult.ListBean.ProductBean productBean;
    private EditHotResult.ListBean returnBean;
    //数量
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurement_add);
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
        sendConnection("/api/inventory/add/list", param, PRODUCT_GET, true, EditHotResult.class);
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
    public void onShowPopEvent(final EditHotResult.ListBean returnBean) {
        this.returnBean = returnBean;
        productBean = returnBean.getProduct();
        //有批次
        if ("lot".equals(productBean.getTracking())) {
            popView1.setVisibility(View.VISIBLE);
            popView2.setVisibility(View.GONE);
            name.setText(productBean.getName());
            number.setText(productBean.getDefaultCode() + " | ");
            content.setText(productBean.getUnit());
            tv_product_date_value.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setFiniishBtnStatus();
                }
            });
            et_product_amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setFiniishBtnStatus();
                }
            });
            FrecoFactory.getInstance(mContext).disPlay(productImage, Constant.BASE_URL + productBean.getImage().getImageSmall());
            finalButton.setEnabled(false);

            tv_product_date_value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pvCustomTime == null) {
                        pvCustomTime = new TimePickerView.Builder(mContext, new TimePickerView.OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v) {//选中事件回调
                                tv_product_date_value.setText(TimeUtils.getYMD(date));
                                tv_product_date.setText(wheelView.getAdapter().getItem(wheelView.getCurrentItem()).toString());
                            }
                        }).setLayoutRes(R.layout.custom_time_picker, new CustomListener() {

                            @Override
                            public void customLayout(View v) {
                                final Button btnSubmit = (Button) v.findViewById(R.id.btnSubmit);
                                Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
                                wheelView = (WheelView) v.findViewById(R.id.options);
                                ArrayList<String> stringArrayList = new ArrayList<String>();
                                stringArrayList.add("生产日期");
                                stringArrayList.add("到期日期");
                                wheelView.setAdapter(new ArrayWheelAdapter(stringArrayList));
                                wheelView.setCyclic(false);
                                wheelView.setTextSize(18);
                                wheelView.setLineSpacingMultiplier(1.6F);

                                btnSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvCustomTime.returnData();
                                        pvCustomTime.dismiss();
                                    }
                                });
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pvCustomTime.dismiss();
                                    }
                                });
                            }
                        })
                                .setType(new boolean[]{true, true, true, false, false, false})
                                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                                .build();
                        pvCustomTime.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(Object o) {
                            }
                        });
                    }
                    pvCustomTime.show();
                }
            });
            finalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String number = et_batch_number.getText().toString();
                    amount = et_product_amount.getText().toString();
                    ProcurenmentAddRequest procurenmentAddRequest = new ProcurenmentAddRequest();
                    List<ProcurenmentAddRequest.ProductsBean> products = new ArrayList<ProcurenmentAddRequest.ProductsBean>();
                    ProcurenmentAddRequest.ProductsBean productsBean = new ProcurenmentAddRequest.ProductsBean();
                    productsBean.setLot_name(number);
                    productsBean.setTracking("lot");
                    productsBean.setProduct_id(productBean.getProductID());

                    if ("生产日期".equals(tv_product_date.getText().toString())) {
                        productsBean.setProduct_datetime(tv_product_date_value.getText().toString());
                    } else {
                        productsBean.setLife_datetime(tv_product_date_value.getText().toString());
                    }
                    productsBean.setQty(Integer.parseInt(amount));
                    products.add(productsBean);
                    procurenmentAddRequest.setProducts(products);
                    sendConnection("/gongfu/shop/zicai", procurenmentAddRequest, PRODUCT_ADD_1, true, ProcurementAddResult.class);
                }
            });
        } else {
            popView1.setVisibility(View.GONE);
            popView2.setVisibility(View.VISIBLE);
            name1.setText(productBean.getName());
            number1.setText(productBean.getDefaultCode() + " | ");
            content1.setText(productBean.getUnit());
            finalButton1.setEnabled(false);
            FrecoFactory.getInstance(mContext).disPlay(productImage1, Constant.BASE_URL + productBean.getImage().getImageSmall());
            et_product_amount1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!TextUtils.isEmpty(et_product_amount1.getText().toString())) {
                        finalButton1.setEnabled(true);
                    } else {
                        finalButton1.setEnabled(false);
                    }
                }
            });
            finalButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount = et_product_amount1.getText().toString();
                    ProcurenmentAddRequest procurenmentAddRequest = new ProcurenmentAddRequest();
                    List<ProcurenmentAddRequest.ProductsBean> products = new ArrayList<ProcurenmentAddRequest.ProductsBean>();
                    ProcurenmentAddRequest.ProductsBean productsBean = new ProcurenmentAddRequest.ProductsBean();
                    productsBean.setProduct_id(productBean.getProductID());
                    productsBean.setTracking("");
                    productsBean.setLot_name("none");
                    productsBean.setLife_datetime("");
                    productsBean.setProduct_datetime("");
                    productsBean.setQty(Integer.parseInt(amount));
                    products.add(productsBean);
                    procurenmentAddRequest.setProducts(products);
                    sendConnection("/gongfu/shop/zicai", procurenmentAddRequest, PRODUCT_ADD_1, true, ProcurementAddResult.class);
                }
            });
        }
        setCommontTopShow();
    }

    @OnClick({R.id.colseIcon, R.id.colseIcon1,R.id.iv_open})
    public void closeIcon(View view) {
        switch (view.getId()) {
            case R.id.colseIcon:
                setCommontTopHide();
                break;
            case R.id.colseIcon1:
                setCommontTopHide();
                break;
            case R.id.iv_open:
                if (mProductTypeWindow == null){
                    return;
                }
                if (!mProductTypeWindow.isShowing()){
                    showPopWindow();
                }else{
                    mProductTypeWindow.dismiss();
                }
                break;
        }

    }

    private void setFiniishBtnStatus() {
        if (!TextUtils.isEmpty(tv_product_date_value.getText().toString()) && !TextUtils.isEmpty(et_product_amount.getText().toString())) {
            finalButton.setEnabled(true);
        } else {
            finalButton.setEnabled(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                EditHotResult editHotResult = (EditHotResult) result.getResult().getData();
                List<EditHotResult.ListBean> hotList = editHotResult.getList();
                setUpDataForViewPage(hotList);
                break;
            case PRODUCT_ADD_1:
                ProcurementAddResult procurementAddResult = (ProcurementAddResult) result.getResult();
                EventBus.getDefault().post(procurementAddResult);
                finish();
                break;
        }
    }

    private void setUpDataForViewPage(List<EditHotResult.ListBean> listBeen) {
        List<Fragment> productDataFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("全部");

        HashMap<String, ArrayList<EditHotResult.ListBean>> map = new HashMap<>();
        for (EditHotResult.ListBean listBean : listBeen) {
            ArrayList<EditHotResult.ListBean> tempListBeen = map.get(listBean.getProduct().getCategory());
            if (tempListBeen == null) {
                tempListBeen = new ArrayList<>();
                map.put(listBean.getProduct().getCategory(), tempListBeen);
            }
            tempListBeen.add(listBean);
        }
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            ArrayList<EditHotResult.ListBean> value = (ArrayList<EditHotResult.ListBean>) entry.getValue();
            titles.add(key);
            productDataFragmentList.add(newSearchListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(key));
        }
        productDataFragmentList.add(0, newSearchListFragment((ArrayList<EditHotResult.ListBean>) listBeen));
        initUI(titles, productDataFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    private void initUI(List<String> titles, List<Fragment> priceFragmentList) {
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, priceFragmentList);
        viewPager.setAdapter(adapter);
        smartTabLayout.setupWithViewPager(viewPager);
        smartTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                mProductTypeWindow.dismiss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(titles.size()<=TAB_EXPAND_COUNT){
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }else{
            ivOpen.setVisibility(View.VISIBLE);
            smartTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        int position = this.getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position, false);
    }

    public SearchListFragment newSearchListFragment(ArrayList<EditHotResult.ListBean> value) {
        SearchListFragment searchListFragment = new SearchListFragment();
        searchListFragment.type = DataType.ALL;
        searchListFragment.setData(value);
        return searchListFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setFocusable(false);
        mProductTypeWindow.setOutsideTouchable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProductTypeWindow.dismiss();
                viewPager.setCurrentItem(position);
                smartTabLayout.getTabAt(position).select();
                for (int i = 0; i < mProductTypeAdapter.selectList.size(); i++) {
                    mProductTypeAdapter.selectList.set(i, new Boolean(false));
                }
                mProductTypeAdapter.selectList.set(position, new Boolean(true));
                mProductTypeAdapter.notifyDataSetChanged();
            }
        });
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProductTypeWindow.dismiss();
            }
        });
        mProductTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivOpen.setImageResource(R.drawable.arrow);
            }
        });
    }

    private void showPopWindow() {
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mProductTypeWindow.showAtLocation(getRootView(ProcurementAddActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext, errMsg);
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<Fragment> priceFragmentList) {
            super(fm);
            titleList.addAll(titles);
            fragmentList.addAll(priceFragmentList);
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

    //显示弹窗
    public void setCommontTopShow() {
        if (topShowAnim == null) {
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
        if (topHideAnim == null) {
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
