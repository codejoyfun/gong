package com.runwise.supply.repertory;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
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
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.adapter.ProductTypeAdapter;
import com.runwise.supply.entity.CategoryRespone;
import com.runwise.supply.entity.GetCategoryRequest;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.fragment.TabFragment;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ImageBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.AddRepertoryData;
import com.runwise.supply.repertory.entity.AddRepertoryRequest;
import com.runwise.supply.repertory.entity.EditHotResult;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.tools.DensityUtil;
import com.runwise.supply.tools.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.runwise.supply.firstpage.OrderDetailActivity.CATEGORY;
import static com.runwise.supply.firstpage.OrderDetailActivity.TAB_EXPAND_COUNT;
import static com.runwise.supply.orderpage.ProductBasicUtils.getBasicMap;


/**
 * 库存添加收索
 * <p>
 * 新加逻辑：过滤掉已有的商品
 */
public class EditRepertoryAddActivity extends NetWorkActivity {
    public static final String INTENT_FILTER = "intent_filter";
    @ViewInject(R.id.searchET)
    private EditText searchET;
    private final int PRODUCT_GET = 1;
    private final int PRODUCT_ADD_1 = 2;
    private final int PRODUCT_ADD_2 = 3;

    @ViewInject(R.id.indicator)
    private TabLayout smartTabLayout;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.iv_open)
    private ImageView ivOpen;
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
    private String amount;

    //过滤
    Set<Integer> filters = new HashSet<>();
    protected Map<EditHotResult.ListBean, Double> mMapCount = new HashMap<>();

    /**
     * 供子fragment统一设置商品数量,隐藏细节
     */
    public interface ProductCountSetter {
        void setCount(EditHotResult.ListBean bean, double count);

        double getCount(EditHotResult.ListBean bean);
    }

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
        sendConnection("/api/inventory/add/list", param, PRODUCT_GET, true, EditHotResult.class);
//        bgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setCommontTopHide();
//            }
//        });
        filters.addAll(getIntent().getIntegerArrayListExtra(INTENT_FILTER));
    }

    /**
     * 供子fragment统一设置商品数量的接口，向子fragment隐藏实现
     */
    ProductCountSetter mCountSetter = new ProductCountSetter() {
        @Override
        public void setCount(EditHotResult.ListBean bean, double count) {
            if (count == 0) {
                mMapCount.remove(bean);
            } else {
                mMapCount.put(bean, count);
            }

        }

        @Override
        public double getCount(EditHotResult.ListBean bean) {
            return mMapCount.get(bean) == null ? 0 : mMapCount.get(bean);
        }

    };

    /**
     * 供子fragment统一设置商品数量
     */
    public ProductCountSetter getProductCountSetter() {
        return mCountSetter;
    }


    public void postData() {
        if (mMapCount.size() == 0){
            toast("你尚未添加任何盘点商品!");
            return;
        }
        for (Map.Entry<EditHotResult.ListBean, Double> entry : mMapCount.entrySet()) {
            EditHotResult.ListBean editHotBean = entry.getKey();
            EditHotResult.ListBean.ProductBean productBean = editHotBean.getProduct();
            InventoryResponse.InventoryProduct bean = new InventoryResponse.InventoryProduct();
            bean.setTheoreticalQty(0);
            bean.setLotID(0);
            bean.setCode(productBean.getDefaultCode());
            bean.setInventoryLineID(editHotBean.getInventoryAddLineID());
            bean.setProductID(productBean.getProductID());
            bean.setEditNum(entry.getValue());

            ProductBasicList.ListBean product = new ProductBasicList.ListBean();
            product.setName(productBean.getName());
            product.setBarcode(productBean.getBarcode());
            product.setStockType(productBean.getStockType());
            product.setDefaultCode(productBean.getDefaultCode());
            product.setUnit(productBean.getUnit());
            product.setTracking(productBean.getTracking());

            product.setCategory(productBean.getCategory());
            product.setCategoryParent(productBean.getCategoryParent());
            product.setCategoryChild(productBean.getCategoryChild());

            ProductBasicList.ListBean listBean = getBasicMap(EditRepertoryAddActivity.this).get(String.valueOf(productBean.getProductID()));
            if (listBean != null) {
                product.setUom(listBean.getUom());
                product.setProductUom(listBean.getStockUom());
            }
            ImageBean imageBean = new ImageBean();
            imageBean.setImage(productBean.getImage().getImage());
            imageBean.setImageSmall(productBean.getImage().getImageSmall());
            imageBean.setImageMedium(productBean.getImage().getImageMedium());
            product.setImage(imageBean);
            product.setProductUom(productBean.getStockUom());
            bean.setProduct(product);

            NewAdd newAddBean = new NewAdd();
            newAddBean.setType(1);//无批次
            newAddBean.setBean(bean);
            EventBus.getDefault().post(newAddBean);
        }
        finish();

    }


    @OnClick({R.id.cancelBtn, R.id.iv_open, R.id.tv_add})
    public void btnClick(View view) {
        int vid = view.getId();
        switch (vid) {
            case R.id.cancelBtn:
                customFinish();
                break;
            case R.id.iv_open:
                if (mProductTypeWindow == null) {
                    return;
                }
                if (!mProductTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
                    mProductTypeWindow.dismiss();
                }
                break;
            case R.id.tv_add:
                postData();
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
            ProductBasicList.ListBean listBean = ProductBasicUtils.getBasicMap(getActivityContext()).get(String.valueOf(productBean.getProductID()));
            content.setText(listBean.getUnit());
            tv_product_date_value.setText("");
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
                                if (0 == wheelView.getCurrentItem()) {
                                    if (!DateFormateUtil.befToday(TimeUtils.getYMDHMS(date))) {
                                        ToastUtil.show(mContext, "生产日期不能是未来时间");
                                        return;
                                    }
                                }
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
                    if ("生产日期".equals(tv_product_date.getText().toString())) {
                        dataRequest.setProduce_datetime(tv_product_date_value.getText().toString());
                    } else {
                        dataRequest.setLife_datetime(tv_product_date_value.getText().toString());
                    }

                    dataRequest.setLot_name(number);
                    dataRequest.setProduct_id(productBean.getProductID());
                    sendConnection("/api/shop/inventory/lot/check", dataRequest, PRODUCT_ADD_1, true, AddRepertoryData.class);
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
            finalButton1.setOnClickListener(new View.OnClickListener() {//无批次
                @Override
                public void onClick(View view) {
                    amount = et_product_amount1.getText().toString();

                    //PandianResult.InventoryBean.LinesBean bean = new PandianResult.InventoryBean.LinesBean();
//                    bean.setLifeEndDate(lotBean.getLifeEndDate());
                    InventoryResponse.InventoryProduct bean = new InventoryResponse.InventoryProduct();
                    bean.setTheoreticalQty(0);
//                    bean.setLotNum(lotBean.getLotName());
                    bean.setLotID(0);
                    bean.setCode(productBean.getDefaultCode());
                    bean.setInventoryLineID(returnBean.getInventoryAddLineID());
                    bean.setProductID(productBean.getProductID());
                    bean.setEditNum(Double.parseDouble(amount));

                    ProductBasicList.ListBean product = new ProductBasicList.ListBean();
                    product.setName(productBean.getName());
                    product.setBarcode(productBean.getBarcode());
                    product.setStockType(productBean.getStockType());
                    product.setDefaultCode(productBean.getDefaultCode());
                    product.setUnit(productBean.getUnit());
                    product.setTracking(productBean.getTracking());
                    ProductBasicList.ListBean listBean = getBasicMap(EditRepertoryAddActivity.this).get(String.valueOf(productBean.getProductID()));
                    if (listBean != null) {
                        product.setUom(listBean.getUom());
                        product.setProductUom(listBean.getProductUom());
                    }
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImage(productBean.getImage().getImage());
                    imageBean.setImageSmall(productBean.getImage().getImageSmall());
                    imageBean.setImageMedium(productBean.getImage().getImageMedium());
                    product.setImage(imageBean);
                    bean.setProduct(product);

                    NewAdd newAddBean = new NewAdd();
                    newAddBean.setType(1);//无批次
                    newAddBean.setBean(bean);
                    EventBus.getDefault().post(newAddBean);
                    setCommontTopHide();
                    customFinish();
                }
            });
        }
        setCommontTopShow();
    }

    @OnClick({R.id.colseIcon, R.id.colseIcon1})
    public void closeIcon(View view) {
        switch (view.getId()) {
            case R.id.colseIcon:
                setCommontTopHide();
                break;
            case R.id.colseIcon1:
                setCommontTopHide();
                break;
            case R.id.iv_open:
                if (mProductTypeWindow == null) {
                    return;
                }
                if (!mProductTypeWindow.isShowing()) {
                    showPopWindow();
                } else {
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

    CategoryRespone categoryRespone;
    List<EditHotResult.ListBean> hotList = new ArrayList<>();

    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case PRODUCT_GET:
                EditHotResult editHotResult = (EditHotResult) result.getResult().getData();
                Observable.from(editHotResult.getList())
                        .filter(listBean -> !filters.contains(listBean.getProductID()))
                        .subscribe(listBean -> hotList.add(listBean));
                //hotList = editHotResult.getList();
                GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
                getCategoryRequest.setUser_id(Integer.parseInt(SampleApplicationLike.getInstance().getUid()));
                sendConnection("/api/product/category", getCategoryRequest, CATEGORY, false, CategoryRespone.class);
                break;
            case PRODUCT_ADD_1://有批次
                AddRepertoryData addRepertoryData = (AddRepertoryData) result.getResult().getData();
                AddRepertoryData.LotNewsBean lotBean = addRepertoryData.getLotNews();

//                PandianResult.InventoryBean.LinesBean bean = new PandianResult.InventoryBean.LinesBean();
                InventoryResponse.InventoryProduct bean = new InventoryResponse.InventoryProduct();
                bean.setLifeEndDate(lotBean.getLifeEndDate());
                bean.setTheoreticalQty(0);
                bean.setLotNum(lotBean.getLotName());
                bean.setLotID(lotBean.getLotID());
                bean.setCode(productBean.getDefaultCode());
                bean.setInventoryLineID(returnBean.getInventoryAddLineID());
                bean.setProductID(lotBean.getProductID());
//                        bean.setUnit_price();
//                        bean.setActual_qty();
                bean.setEditNum(Double.parseDouble(amount));

                ProductBasicList.ListBean product = new ProductBasicList.ListBean();
                product.setName(productBean.getName());
                product.setBarcode(productBean.getBarcode());
                product.setStockType(productBean.getStockType());
                product.setDefaultCode(productBean.getDefaultCode());
                product.setUnit(productBean.getUnit());
                product.setTracking(productBean.getTracking());
                ImageBean imageBean = new ImageBean();
                imageBean.setImage(productBean.getImage().getImage());
                imageBean.setImageSmall(productBean.getImage().getImageSmall());
                imageBean.setImageMedium(productBean.getImage().getImageMedium());
                product.setImage(imageBean);
                ProductBasicList.ListBean listBean = getBasicMap(EditRepertoryAddActivity.this).get(String.valueOf(productBean.getProductID()));
                if (listBean != null) {
                    product.setUom(listBean.getUom());
                    product.setProductUom(listBean.getProductUom());
                }
                bean.setProduct(product);

                InventoryResponse.InventoryLot inventoryLot = new InventoryResponse.InventoryLot();
                inventoryLot.setLotNum(bean.getLotNum());
                inventoryLot.setEditNum(bean.getEditNum());
                inventoryLot.setLifeEndDate(bean.getLifeEndDate());
                inventoryLot.setTheoreticalQty(0);
                inventoryLot.setLotID(lotBean.getLotID());
                inventoryLot.setCode(productBean.getDefaultCode());
                inventoryLot.setInventoryLineID(returnBean.getInventoryAddLineID());
                inventoryLot.setProductID(lotBean.getProductID());
                ArrayList<InventoryResponse.InventoryLot> lotList = new ArrayList<>();
                lotList.add(inventoryLot);
                bean.setLotList(lotList);

                NewAdd newAddBean = new NewAdd();
                newAddBean.setBean(bean);
                EventBus.getDefault().post(newAddBean);
                setCommontTopHide();
//                finish();
                customFinish();
                break;
            case CATEGORY:
                BaseEntity.ResultBean resultBean1 = result.getResult();
                categoryRespone = (CategoryRespone) resultBean1.getData();
                setUpDataForViewPage(hotList);
                break;
        }
    }

    private void setUpDataForViewPage(List<EditHotResult.ListBean> listBeen) {
        List<Fragment> productDataFragmentList = new ArrayList<>();
        List<Fragment> tabFragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        HashMap<String, ArrayList<EditHotResult.ListBean>> map = new HashMap<>();
        titles.add("全部");
        for (String category : categoryRespone.getCategoryList()) {
            titles.add(category);
            map.put(category, new ArrayList<EditHotResult.ListBean>());
        }
        for (EditHotResult.ListBean listBean : listBeen) {
            if (!TextUtils.isEmpty(listBean.getProduct().getCategory())) {
                ArrayList<EditHotResult.ListBean> tempListBeen = map.get(listBean.getProduct().getCategory());
                if (tempListBeen == null) {
                    tempListBeen = new ArrayList<>();
                    map.put(listBean.getProduct().getCategory(), tempListBeen);
                }
                tempListBeen.add(listBean);
            }
        }

        for (String category : categoryRespone.getCategoryList()) {
            ArrayList<EditHotResult.ListBean> value = map.get(category);
            productDataFragmentList.add(newSearchListFragment(value));
            tabFragmentList.add(TabFragment.newInstance(category));
        }
        productDataFragmentList.add(0, newSearchListFragment((ArrayList<EditHotResult.ListBean>) listBeen));
        initUI(titles, productDataFragmentList);
        initPopWindow((ArrayList<String>) titles);
    }

    private void initUI(List<String> titles, List<Fragment> priceFragmentList) {
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager(), titles, priceFragmentList);
        viewPager.setOffscreenPageLimit(priceFragmentList.size());
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
        if (titles.size() <= TAB_EXPAND_COUNT) {
            ivOpen.setVisibility(View.GONE);
            smartTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
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
        searchListFragment.setProductCountSetter(mCountSetter);
        return searchListFragment;
    }

    private PopupWindow mProductTypeWindow;
    ProductTypeAdapter mProductTypeAdapter;

    private void initPopWindow(ArrayList<String> typeList) {
        View dialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_tab_type, null);
        GridView gridView = (GridView) dialog.findViewById(R.id.gv);
        mProductTypeAdapter = new ProductTypeAdapter(typeList);
        gridView.setAdapter(mProductTypeAdapter);
        final int[] location = new int[2];
        smartTabLayout.getLocationOnScreen(location);
        int y = (int) (location[1] + smartTabLayout.getHeight());
        mProductTypeWindow = new PopupWindow(gridView, ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.getScreenH(getActivityContext()) - y, true);
        mProductTypeWindow.setContentView(dialog);
        mProductTypeWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mProductTypeWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mProductTypeWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
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
        mProductTypeWindow.showAtLocation(getRootView(EditRepertoryAddActivity.this), Gravity.NO_GRAVITY, 0, y);
        mProductTypeAdapter.setSelectIndex(viewPager.getCurrentItem());
        ivOpen.setImageResource(R.drawable.arrow_up);
    }


    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        ToastUtil.show(mContext, errMsg);
    }

    private class TabPageIndicatorAdapter extends FragmentStatePagerAdapter {
        private List<String> titleList = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public TabPageIndicatorAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragmentList) {
            super(fm);
            titleList.addAll(titles);
            this.fragmentList.addAll(fragmentList);

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

    InputMethodManager imm;

    private void hideKeyboard() {
        if (imm == null) imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if (imm != null && v != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 超级蛋疼的，键盘展示的时候回退到InventoryActivity，会造成draglayout显示错误，必须先收起键盘，暂时找不到原因
     * 先收起键盘，等待一小段时间，再finish
     */
    private void customFinish() {
        hideKeyboard();
//        smartTabLayout.postDelayed(()->finish(),500);
        finish();
    }
}
