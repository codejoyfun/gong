package com.runwise.supply.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkActivity;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.CustomDialog;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.TransferListActivity;
import com.runwise.supply.entity.ModifyTransferRequest;
import com.runwise.supply.entity.TransferEntity;
import com.runwise.supply.orderpage.ProductActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.orderpage.entity.StoreResponse;
import com.runwise.supply.view.NoWatchEditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

import static com.runwise.supply.R.id.tv_edit_or_finish;
import static com.runwise.supply.orderpage.ProductActivity.INTENT_KEY_BACKAP;

public class TransferInModifyActivity extends NetWorkActivity {

    @BindView(R.id.tv_call_out_store)
    TextView mTvCallOutStore;
    @BindView(R.id.rl_call_out)
    RelativeLayout mRlCallOut;
    @BindView(R.id.v_line)
    View mVLine;
    @BindView(R.id.tv_call_in_store)
    TextView mTvCallInStore;
    @BindView(R.id.rl_call_in)
    RelativeLayout mRlCallIn;
    @BindView(R.id.tv_allocation)
    TextView mTvAllocation;
    @BindView(tv_edit_or_finish)
    TextView mTvEditOrFinish;
    @BindView(R.id.rl_allocation)
    RelativeLayout mRlAllocation;
    @BindView(R.id.v_line1)
    View mVLine1;
    @BindView(R.id.lv_product)
    ListView mLvProduct;
    @BindView(R.id.tv_submit)
    TextView mTvSubmit;
    @BindView(R.id.tv_count)
    TextView mTvCount;
    @BindView(R.id.tv_total_money)
    TextView mTvTotalMoney;
    @BindView(R.id.rl_bottom_bar)
    RelativeLayout mRlBottomBar;
    @BindView(R.id.tv_no_price_count)
    TextView mTvNoPrice;

    public static final String INTENT_KEY_TRANSFER = "extra_products";
    public static final int REQUEST_CODE_GET_PRODUCT = 1 << 0;
    private boolean canSeePrice = true;             //默认价格中可见
    //选中数量map
    private HashMap<String, Double> countMap = new HashMap<>();
    ProductAdapter mProductAdapter;
    boolean mEditMode = false;
    ArrayList<String> mStoreNameList = new ArrayList<>();
    public static final int REQUEST_CODE_GET_STORE_LIST = 1 << 0;
    public static final int REQUEST_CODE_MODIFY = 1 << 1;
    StoreResponse mStoreResponse;
    UserInfo mUserInfo;
    private TransferEntity mTransferEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_call_in_list);
        setTitleText(true, "修改调入单");
        showBackBtn();
        ButterKnife.bind(this);
        canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
        mProductAdapter = new ProductAdapter();
        mLvProduct.setAdapter(mProductAdapter);
        View footView = LayoutInflater.from(getActivityContext()).inflate(R.layout.allocation_add_product, null);
        mLvProduct.addFooterView(footView);
        footView.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivityContext(), ProductActivity.class), REQUEST_CODE_GET_PRODUCT);
            }
        });
        mUserInfo = SampleApplicationLike.getInstance().loadUserInfo();
        if (mUserInfo != null) {
            mTvCallInStore.setText(mUserInfo.getMendian());
        }
        mTransferEntity = getIntent().getParcelableExtra(INTENT_KEY_TRANSFER);
        mTvCallOutStore.setText(mTransferEntity.getLocationName());
        mTvCallOutStore.setClickable(false);
        initProductList();
    }

    private void initProductList(){
        ArrayList<AddedProduct> addedList = getIntent().getParcelableArrayListExtra(INTENT_KEY_BACKAP);
        for (AddedProduct addedProduct : addedList) {
            boolean findIt = false;
            for (Object object : mProductAdapter.getList()) {
                ProductData.ListBean listBean = (ProductData.ListBean) object;
                if (addedProduct.getProductId().equals(String.valueOf(listBean.getProductID()))) {
                    double count = countMap.get(String.valueOf(listBean.getProductID()));
                    count += addedProduct.getCount();
                    countMap.put(String.valueOf(listBean.getProductID()), count);
                    findIt = true;
                    break;
                }
            }
            //新增产品种类
            if (!findIt) {
                countMap.put(addedProduct.getProductId(), addedProduct.getCount());
                ProductBasicList.ListBean bean = ProductBasicUtils.getBasicMap(getActivityContext()).get(addedProduct.getProductId());
                ProductData.ListBean listBean = new ProductData.ListBean();
                listBean.setName(bean.getName());
                listBean.setSettlePrice(String.valueOf(bean.getSettlePrice()));
                listBean.setUom(bean.getUom());
                listBean.setSettleUomId(bean.getSettleUomId());
                listBean.setPrice(bean.getPrice());
                listBean.setDefaultCode(bean.getDefaultCode());
                listBean.setStockType(bean.getStockType());
                listBean.setCategory(bean.getCategory());
                listBean.setUnit(bean.getUnit());
                listBean.setProductUom(bean.getProductUom());
                listBean.setProductID(bean.getProductID());
                listBean.setTracking(bean.getTracking());
                mProductAdapter.append(listBean);
            }
        }
        mProductAdapter.notifyDataSetChanged();
        refreshTotalCountAndMoney();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.title_iv_left, R.id.rl_call_out, tv_edit_or_finish, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_call_out:
                //选择门店
                if (mStoreResponse == null) {
                    Object param = null;
                    sendConnection("/gongfu/shop/list", param, REQUEST_CODE_GET_STORE_LIST, true, StoreResponse.class);
                } else {
                    showStoreSelectDialog();
                }
                break;
            case tv_edit_or_finish:
                //编辑或完成商品
                mEditMode = !mEditMode;
                if (mEditMode) {
                    mTvEditOrFinish.setText("完成");
                } else {
                    mTvEditOrFinish.setText("编辑");
                }
                mProductAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_submit:
                //修改调度单
                if (!checkInput()){
                    return;
                }
                ModifyTransferRequest modifyTransferRequest = new ModifyTransferRequest();
                if(selectShopIndex!=-1 && mStoreResponse!=null){
                    modifyTransferRequest.setMenDianID(String.valueOf(mStoreResponse.getList().get(selectShopIndex).getShopID()));
                }
                modifyTransferRequest.setPickingID(mTransferEntity.getPickingID());
                List<ModifyTransferRequest.Product> products = new ArrayList<>();
                for (Map.Entry<String, Double> entry : countMap.entrySet()) {
                    ModifyTransferRequest.Product product = new ModifyTransferRequest.Product();
                    product.setProductID(Integer.parseInt(entry.getKey()));
                    product.setQty(entry.getValue());
                    products.add(product);
                }
                modifyTransferRequest.setProducts(products);
                sendConnection("/gongfu/shop/transfer/change/"+mTransferEntity.getPickingID(), modifyTransferRequest, REQUEST_CODE_MODIFY, true, null);
                break;
            case R.id.title_iv_left:
                if (!mProductAdapter.getList().isEmpty()) {
                    dialog.setMessage("单据还没保存,确定退出?");
                    dialog.setModel(CustomDialog.BOTH);
                    dialog.setMessageGravity();
                    dialog.setLeftBtnListener("取消", null);
                    dialog.setRightBtnListener("确定", new CustomDialog.DialogListener() {
                        @Override
                        public void doClickButton(Button btn, CustomDialog dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                } else {
                    finish();
                }
                break;
        }
    }

    private boolean checkInput() {
//        if (selectShopIndex == -1) {
//            toast("还没选择门店");
//            return false;
//        }
        if (mProductAdapter.getList().size() == 0) {
            toast("还没选择任何商品!");
            return false;
        }
        return true;
    }

    OptionsPickerView mPvOptions;
    int selectShopIndex = -1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showStoreSelectDialog() {
        if (mPvOptions == null) {
            mPvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String tx = mStoreResponse.getList().get(options1).getShopName();
                    mTvCallOutStore.setText(tx);
                    selectShopIndex = options1;
                }
            })
                    .setSubmitText("确认")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleText("")//标题
                    .setSubCalSize(15)//确定和取消文字大小
                    .setTitleSize(20)//标题文字大小
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                    .setCancelColor(Color.BLACK)//取消按钮文字颜色
                    .setTitleBgColor(getColor(R.color.bg_titlebar_select))//标题背景颜色 Night mode
                    .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                    .setContentTextSize(18)//滚轮文字大小
                    .setLinkage(false)//设置是否联动，默认true
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(false, false, false)//循环与否
                    .setSelectOptions(1)  //设置默认选中项
                    .setOutSideCancelable(false)//点击外部dismiss default true
                    .isDialog(false)//是否显示为对话框样式
                    .build();
            mPvOptions.setPicker(mStoreNameList);//添加数据源
        }
        mPvOptions.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GET_PRODUCT:
                    ArrayList<AddedProduct> addedList = data.getParcelableArrayListExtra(INTENT_KEY_BACKAP);
                    for (AddedProduct addedProduct : addedList) {
                        boolean findIt = false;
                        for (Object object : mProductAdapter.getList()) {
                            ProductData.ListBean listBean = (ProductData.ListBean) object;
                            if (addedProduct.getProductId().equals(String.valueOf(listBean.getProductID()))) {
                                double count = countMap.get(String.valueOf(listBean.getProductID()));
                                count += addedProduct.getCount();
                                countMap.put(String.valueOf(listBean.getProductID()), count);
                                findIt = true;
                                break;
                            }
                        }
                        //新增产品种类
                        if (!findIt) {
                            countMap.put(addedProduct.getProductId(), addedProduct.getCount());
                            ProductBasicList.ListBean bean = ProductBasicUtils.getBasicMap(getActivityContext()).get(addedProduct.getProductId());
                            ProductData.ListBean listBean = new ProductData.ListBean();
                            listBean.setName(bean.getName());
                            listBean.setSettlePrice(String.valueOf(bean.getSettlePrice()));
                            listBean.setUom(bean.getUom());
                            listBean.setSettleUomId(bean.getSettleUomId());
                            listBean.setPrice(bean.getPrice());
                            listBean.setDefaultCode(bean.getDefaultCode());
                            listBean.setStockType(bean.getStockType());
                            listBean.setCategory(bean.getCategory());
                            listBean.setUnit(bean.getUnit());
                            listBean.setProductUom(bean.getProductUom());
                            listBean.setProductID(bean.getProductID());
                            listBean.setTracking(bean.getTracking());
                            mProductAdapter.append(listBean);
                        }
                    }
                    mProductAdapter.notifyDataSetChanged();
                    refreshTotalCountAndMoney();
                    break;
            }
        }
    }

    private void refreshTotalCountAndMoney() {
        double totalCount = 0;
        double totalMoney = 0;
        String removeId = "";
        for (Map.Entry<String, Double> entry : countMap.entrySet()) {
            totalCount += entry.getValue();
            if (entry.getValue() == 0) {
                removeId = entry.getKey();
            }
        }
        mTvCount.setText(NumberUtil.getIOrD(totalCount) + " 件");
        if (!TextUtils.isEmpty(removeId)) {
            countMap.remove(removeId);
            ProductData.ListBean listBean = null;
            for (Object object : mProductAdapter.getList()) {
                ProductData.ListBean bean = (ProductData.ListBean) object;
                if (bean.getProductID() == Integer.parseInt(removeId)) {
                    listBean = bean;
                    break;
                }
            }
            if (listBean != null) {
                mProductAdapter.getList().remove(listBean);
                mProductAdapter.notifyDataSetChanged();
            }
        }
        for (Object object : mProductAdapter.getList()) {
            ProductData.ListBean bean = (ProductData.ListBean) object;
            double count = countMap.get(String.valueOf(bean.getProductID()));
            totalMoney += bean.getPrice() * count;
        }
        mTvTotalMoney.setText("¥" + NumberUtil.getIOrD(totalMoney));
        if (mProductAdapter.getList().size() == 0) {
            mTvEditOrFinish.setVisibility(View.INVISIBLE);
        } else {
            mTvEditOrFinish.setVisibility(View.VISIBLE);
        }

        if(!canSeePrice){
            mTvNoPrice.setVisibility(View.VISIBLE);
            mTvNoPrice.setText(NumberUtil.getIOrD(totalCount )+ " 件");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSuccess(BaseEntity result, int where) {
        switch (where) {
            case REQUEST_CODE_GET_STORE_LIST:
                BaseEntity.ResultBean resultBean = result.getResult();
                mStoreResponse = new StoreResponse();
                com.alibaba.fastjson.JSONObject jsonObject = resultBean.getDataJson();
                com.alibaba.fastjson.JSONArray shopList = jsonObject.getJSONArray("shopList");
                List<StoreResponse.Store> shopArrayList = new ArrayList<>();
                for (int i = 0; i < shopList.size(); i++) {
                    com.alibaba.fastjson.JSONObject json = shopList.getJSONObject(i);
                    int shopID = (int) json.get("shopID");
                    String shopName = (String) json.get("shopName");
                    if (shopName.equals(mUserInfo.getMendian())){
                        continue;
                    }
                    StoreResponse.Store store = new StoreResponse.Store();
                    store.setShopID(shopID);
                    store.setShopName(shopName);
                    shopArrayList.add(store);
                    mStoreNameList.add(shopName);
                }
                mStoreResponse.setList(shopArrayList);
                showStoreSelectDialog();
                break;
            case REQUEST_CODE_MODIFY:
                toast("修改成功");
                Intent intent = new Intent(this, TransferListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {
        Toast.makeText(this,errMsg,Toast.LENGTH_LONG).show();
    }

    public class ProductAdapter extends IBaseAdapter {
        private boolean ischange;

        @Override
        protected View getExView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final ProductData.ListBean bean = (ProductData.ListBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_product_call_in, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.editText.removeTextChangedListener();
            final EditText editText = viewHolder.editText;
            viewHolder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String strSource = s.toString();
                    int dotIndex = strSource.indexOf(".");
                    if(dotIndex>=0){
                        int length = strSource.substring(dotIndex,strSource.length()-1).length();
                        if(length>2){
                            String dest = strSource.substring(0,dotIndex+3);
                            editText.setText(dest);
                            editText.setSelection(dest.length());
                            countMap.put(bean.getProductID()+"",Double.valueOf(dest));
                            return;
                        }
                    }
                    countMap.put(bean.getProductID()+"",Double.valueOf(strSource));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    refreshTotalCountAndMoney();
                }
            });
            //先根据集合里面对应个数初始化一次
            if (countMap.get(String.valueOf(bean.getProductID())) > 0) {
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.unit1.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.unit1.setVisibility(View.VISIBLE);
            }
            ischange = true;
            viewHolder.editText.setText(NumberUtil.getIOrD(countMap.get(String.valueOf(bean.getProductID()))));
            ischange = false;
            final LinearLayout ll = viewHolder.editLL;
            final TextView unit1 = viewHolder.unit1;
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    double currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    if (currentNum > 0) {
                        currentNum = BigDecimal.valueOf(currentNum).subtract(BigDecimal.ONE).doubleValue();
                        if(currentNum<0)currentNum = 0;
                        countMap.put(String.valueOf(bean.getProductID()), currentNum);
                        finalViewHolder.editText.setText(NumberUtil.getIOrD(currentNum ));
                        if (currentNum == 0) {
                            unit1.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            });
            final ViewHolder finalViewHolder1 = viewHolder;
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    double currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    currentNum = BigDecimal.valueOf(currentNum).add(BigDecimal.ONE).doubleValue();
                    countMap.put(String.valueOf(bean.getProductID()), currentNum);
                    finalViewHolder1.editText.setText(NumberUtil.getIOrD(currentNum));
                }
            });

            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (basicBean != null) {
                viewHolder.name.setText(basicBean.getName());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                sb.append(" | ").append(basicBean.getUnit());
                viewHolder.content.setText(sb.toString());
                DecimalFormat df = new DecimalFormat("#.##");
                if (canSeePrice) {
                    StringBuffer sb1 = new StringBuffer();
                    if (bean.isIsTwoUnit()) {
                        sb1.append("¥")
                                .append(df.format(Double.valueOf(bean.getSettlePrice())))
                                .append("元/")
                                .append(bean.getSettleUomId());
                    } else {
                        sb1.append("¥")
                                .append(df.format(Double.valueOf(bean.getPrice())))
                                .append("元/")
                                .append(bean.getUom());
                    }
                    viewHolder.tv_price.setText(sb1.toString());
                } else {
                    viewHolder.tv_price.setText("");
                }
                viewHolder.unit1.setText(basicBean.getStockUom());
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + basicBean.getImage().getImageSmall());
            }

            viewHolder.tv_count.setText(NumberUtil.getIOrD(countMap.get(String.valueOf(bean.getProductID()))));

            viewHolder.iv_delete.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
            if (mEditMode) {
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.unit1.setVisibility(View.INVISIBLE);
                viewHolder.unit_tag.setVisibility(View.INVISIBLE);
                viewHolder.tv_count.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.unit1.setVisibility(View.VISIBLE);
                viewHolder.unit_tag.setVisibility(View.VISIBLE);
                viewHolder.tv_count.setVisibility(View.VISIBLE);
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(position);
                    countMap.put(String.valueOf(bean.getProductID()), 0d);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView sDv;    //头像
            @ViewInject(R.id.content)
            TextView content;//内容
            @ViewInject(R.id.editLL)
            LinearLayout editLL;        //整体编辑框
            @ViewInject(R.id.input_minus)
            ImageButton inputMBtn;//减
            @ViewInject(R.id.input_add)
            ImageButton inputPBtn;//加
            @ViewInject(R.id.editText)
            NoWatchEditText editText; //输入框
            @ViewInject(R.id.unit1)
            TextView unit1;  //单位
            @ViewInject(R.id.tv_price)
            TextView tv_price;  //单位
            @ViewInject(R.id.iv_delete)
            ImageView iv_delete;  //删除
            @ViewInject(R.id.unit_tag)
            TextView unit_tag;
            @ViewInject(R.id.tv_count)
            TextView tv_count;

        }
    }
}
