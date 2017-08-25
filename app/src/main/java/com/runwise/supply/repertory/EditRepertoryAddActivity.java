package com.runwise.supply.repertory;

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
import android.widget.Button;
import android.widget.EditText;
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
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.AddRepertoryData;
import com.runwise.supply.repertory.entity.AddRepertoryRequest;
import com.runwise.supply.repertory.entity.EditHotResult;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 库存添加收索
 */

public class EditRepertoryAddActivity extends NetWorkActivity{
    @ViewInject(R.id.searchET)
    private EditText searchET;
    private final int PRODUCT_GET = 1;
    private final int PRODUCT_ADD_1 = 2;
    private final int PRODUCT_ADD_2 = 3;

    @ViewInject(R.id.indicator)
    private SmartTabLayout smartTabLayout;
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
    private TimePickerView pvCustomTime;
    private WheelView wheelView;
    private EditHotResult.ListBean.ProductBean productBean;
    private EditHotResult.ListBean returnBean;
    //数量
    private  String amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_repertory_layout);
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
        sendConnection("/api/inventory/add/list",param,PRODUCT_GET,true, EditHotResult.class);
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
        if("lot".equals(productBean.getTracking())) {
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
                    if(pvCustomTime == null){
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
                    AddRepertoryRequest dataRequest = new AddRepertoryRequest();
                    if("生产日期".equals(tv_product_date.getText().toString())) {
                        dataRequest.setProduce_datetime(tv_product_date_value.getText().toString());
                    }
                    else {
                        dataRequest.setLife_datetime(tv_product_date_value.getText().toString());
                    }

                    dataRequest.setLot_name(number);
                    dataRequest.setProduct_id(productBean.getProductID());
                    sendConnection("/api/shop/inventory/lot/check",dataRequest,PRODUCT_ADD_1,true, AddRepertoryData.class);
                }
            });
        }
        else {
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
                    if(!TextUtils.isEmpty(et_product_amount1.getText().toString())) {
                        finalButton1.setEnabled(true);
                    }
                    else {
                        finalButton1.setEnabled(false);
                    }
                }
            });
            finalButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    amount = et_product_amount1.getText().toString();

                    PandianResult.InventoryBean.LinesBean bean = new PandianResult.InventoryBean.LinesBean();
//                    bean.setLifeEndDate(lotBean.getLifeEndDate());
                    bean.setTheoreticalQty(0);
//                    bean.setLotNum(lotBean.getLotName());
                    bean.setLotID(0);
                    bean.setCode(productBean.getDefaultCode());
                    bean.setInventoryLineID(returnBean.getInventoryAddLineID());
                    bean.setProductID(productBean.getProductID());
                    bean.setEditNum(Integer.parseInt(amount));

                    ProductBasicList.ListBean product = new ProductBasicList.ListBean();
                    product.setName(productBean.getName());
                    product.setBarcode(productBean.getBarcode());
                    product.setStockType(productBean.getStockType());
                    product.setDefaultCode(productBean.getDefaultCode());
                    product.setUnit(productBean.getUnit());
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImage(productBean.getImage().getImage());
                    imageBean.setImageSmall(productBean.getImage().getImageSmall());
                    imageBean.setImageMedium(productBean.getImage().getImageMedium());
                    product.setImage(imageBean);

                    bean.setProduct(product);

                    NewAdd newAddBean = new NewAdd();
                    newAddBean.setType(1);
                    newAddBean.setBean(bean);
                    EventBus.getDefault().post(newAddBean);
                    setCommontTopHide();
                    finish();
                }
            });
        }
        setCommontTopShow();
    }
    @OnClick({R.id.colseIcon,R.id.colseIcon1})
    public void closeIcon(View view) {
        setCommontTopHide();
    }

    private void setFiniishBtnStatus() {
        if( !TextUtils.isEmpty(tv_product_date_value.getText().toString()) && !TextUtils.isEmpty(et_product_amount.getText().toString())) {
            finalButton.setEnabled(true);
        }
        else{
            finalButton.setEnabled(false);
        }
    }

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                EditHotResult editHotResult = (EditHotResult)result.getResult().getData();
                List<EditHotResult.ListBean> hotList = editHotResult.getList();

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
            case PRODUCT_ADD_1:
                AddRepertoryData addRepertoryData = (AddRepertoryData) result.getResult().getData();
                AddRepertoryData.LotNewsBean lotBean = addRepertoryData.getLotNews();

                PandianResult.InventoryBean.LinesBean bean = new PandianResult.InventoryBean.LinesBean();
                bean.setLifeEndDate(lotBean.getLifeEndDate());
                bean.setTheoreticalQty(0);
                bean.setLotNum(lotBean.getLotName());
                bean.setLotID(lotBean.getLotID());
                bean.setCode(productBean.getDefaultCode());
                bean.setInventoryLineID(returnBean.getInventoryAddLineID());
                bean.setProductID(lotBean.getProductID());
//                        bean.setUnit_price();
//                        bean.setActual_qty();
                bean.setEditNum(Integer.parseInt(amount));

                ProductBasicList.ListBean product = new ProductBasicList.ListBean();
                product.setName(productBean.getName());
                product.setBarcode(productBean.getBarcode());
                product.setStockType(productBean.getStockType());
                product.setDefaultCode(productBean.getDefaultCode());
                product.setUnit(productBean.getUnit());
                ImageBean imageBean = new ImageBean();
                imageBean.setImage(productBean.getImage().getImage());
                imageBean.setImageSmall(productBean.getImage().getImageSmall());
                imageBean.setImageMedium(productBean.getImage().getImageMedium());
                product.setImage(imageBean);

                bean.setProduct(product);

                NewAdd newAddBean = new NewAdd();
                newAddBean.setBean(bean);
                EventBus.getDefault().post(newAddBean);
                setCommontTopHide();
                finish();
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
