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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.BaseActivity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.UserInfo;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.ProductActivity;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.view.NoWatchEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.runwise.supply.orderpage.ProductActivity.INTENT_KEY_BACKAP;

public class CreateCallInListActivity extends BaseActivity {

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
    @BindView(R.id.tv_edit_or_finish)
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

    public static final int REQUEST_CODE_GET_PRODUCT = 1 << 0;
    private boolean canSeePrice = true;             //默认价格中可见
    //选中数量map
    private  HashMap<String, Integer> countMap = new HashMap<>();
    ProductAdapter mProductAdapter;
    boolean mEditMode = false;
    ArrayList<String> mStoreNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_call_in_list);
        ButterKnife.bind(this);
        setTitleText(true, "创建调入单");
        showBackBtn();
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
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
        mStoreNameList.add("东山口店");
        mStoreNameList.add("中大店");
        mStoreNameList.add("体育西路店");
        UserInfo userInfo = GlobalApplication.getInstance().loadUserInfo();
        if (userInfo != null) {
            mTvCallInStore.setText(userInfo.getMendian());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.rl_call_out, R.id.tv_edit_or_finish, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_call_out:
                //选择门店
                showStoreSelectDialog();
                break;
            case R.id.tv_edit_or_finish:
                //编辑或完成商品
                mEditMode = !mEditMode;
                mProductAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_submit:
                //提交调度单
                break;
        }
    }

    OptionsPickerView mPvOptions;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showStoreSelectDialog() {
        if (mPvOptions == null) {
            mPvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    String tx = mStoreNameList.get(options1);
                    mTvCallOutStore.setText(tx);
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
                                int count = countMap.get(String.valueOf(listBean.getProductID()));
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
                    refreshTotalCount();
                    break;
            }
        }
    }

    private void refreshTotalCount() {
        int totalCount = 0;
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            totalCount += entry.getValue();
        }
        mTvCount.setText(totalCount+" 件");
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
                viewHolder.editText.removeTextChangedListener();
                viewHolder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int changedNum = 0;
                        if (!TextUtils.isEmpty(s)) {
                            changedNum = Integer.valueOf(s.toString());
                        }
                        countMap.put(String.valueOf(bean.getProductID()), changedNum);
                        if (changedNum == 0) {
                            mList.remove(position);
                            notifyDataSetChanged();
                        }
                        refreshTotalCount();
                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //先根据集合里面对应个数初始化一次
            if (countMap.get(String.valueOf(bean.getProductID())) > 0) {
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.unit1.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.unit1.setVisibility(View.VISIBLE);
            }
            ischange = true;
            viewHolder.editText.setText(countMap.get(String.valueOf(bean.getProductID())) + "");
            ischange = false;
            final LinearLayout ll = viewHolder.editLL;
            final TextView unit1 = viewHolder.unit1;
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    if (currentNum > 0) {
                        --currentNum;
                        countMap.put(String.valueOf(bean.getProductID()), currentNum);
                        finalViewHolder.editText.setText(currentNum + "");
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
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    currentNum++;
                    countMap.put(String.valueOf(bean.getProductID()), currentNum);
                    finalViewHolder1.editText.setText(currentNum + "");
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

                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + basicBean.getImage().getImageSmall());
            }
            viewHolder.unit1.setText(bean.getUom());
            viewHolder.tv_count.setText(String.valueOf(countMap.get(String.valueOf(bean.getProductID()))));

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
