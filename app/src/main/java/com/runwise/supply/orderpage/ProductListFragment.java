package com.runwise.supply.orderpage;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.bean.ProductCountChangeEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.runwise.supply.fragment.OrderProductFragment.BUNDLE_KEY_LIST;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class ProductListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout mLoadingLayout;
    private ProductAdapter adapter;
    public DataType type;
    private ArrayList<AddedProduct> addedPros;
    //选中数量map
    private static HashMap<String, Integer> countMap = new HashMap<>();
    //缓存全部商品列表
    private ArrayList<ProductData.ListBean> arrayList;

    public ProductListFragment() {
    }

    public static HashMap<String, Integer> getCountMap() {
        return countMap;
    }

    private boolean canSeePrice = true;             //默认价格中可见

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        addedPros = getArguments().getParcelableArrayList("ap");
        //最先从上个页面，取一次有的数据
//        int count = existInLastPager();
//        if (count > 0){
//            countMap.put(String.valueOf(bean.getProductID()),count);
//        }
//        countMap.clear();
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();
        setUpListData();
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    public void setUpListData() {
        //得到数据，更新UI
        if (arrayList == null) {
            arrayList = (ArrayList<ProductData.ListBean>) getArguments().getSerializable(BUNDLE_KEY_LIST);
        }
        //先统计一次id,个数
        for (ProductData.ListBean bean : arrayList) {
            countMap.put(String.valueOf(bean.getProductID()), Integer.valueOf(0));
            //同时根据上个页面传值更新一次
            int count = existInLastPager(bean);
            countMap.put(String.valueOf(bean.getProductID()), count);
        }

        adapter.setData(arrayList);
        mLoadingLayout.onSuccess(adapter.getCount(), "这里是空哒~~", R.drawable.default_ico_none);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductCountChangeEvent event) {
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductQueryEvent event) {
        String word = event.getSearchWord();
        //只在当前类型下面找名称包括的元素
        List<ProductData.ListBean> findArray = findArrayByWord(word);
        adapter.setData(findArray);
    }

    //返回当前标签下名称包含的
    private List<ProductData.ListBean> findArrayByWord(String word) {
        List<ProductData.ListBean> findList = new ArrayList<>();
        for (ProductData.ListBean bean : arrayList) {
            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (basicBean.getName().contains(word)) {
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

    public class ProductAdapter extends IBaseAdapter {
        private boolean ischange;

        @Override
        protected View getExView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final ProductData.ListBean bean = (ProductData.ListBean) mList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.product_layout_item, null);
                ViewUtils.inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
                final EditText et = viewHolder.editText;

                et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            checkText(bean.getProductID()+"",(EditText) textView);
                        }
                        return false;
                    }
                });
                final LinearLayout ll = viewHolder.editLL;

                //失去焦点的时候要检查edittext中的数字格式
                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        //按减号使edittext为0，隐藏Layout，这种情况不用检查数字格式
                        if(!b && ll.getVisibility()==View.VISIBLE){
                            checkText(bean.getProductID()+"",(EditText) view);
                        }
                    }
                });

                et.addTextChangedListener(new TextWatcher() {
                    String mmPreString;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        mmPreString = s.toString();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!ischange) {//直接输入
                            if(!TextUtils.isDigitsOnly(s)){//过滤非数字
                                et.setText(mmPreString);
                                return;
                            }

                            int changedNum = 0;
                            if (!TextUtils.isEmpty(s)) {
                                changedNum = Integer.valueOf(s.toString());
                            }
                            countMap.put(String.valueOf(bean.getProductID()), changedNum);

                        }else{//按加减号
                            if (!TextUtils.isEmpty(s)){
                                et.setSelection(s.toString().length());
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //先根据集合里面对应个数初始化一次
            if (countMap.get(String.valueOf(bean.getProductID())) > 0) {
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.addBtn.setVisibility(View.INVISIBLE);
                viewHolder.unit1.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.addBtn.setVisibility(View.VISIBLE);
                viewHolder.unit1.setVisibility(View.VISIBLE);
            }
            final EditText editText = viewHolder.editText;
            ischange = true;
            editText.setText(countMap.get(String.valueOf(bean.getProductID())) + "");
            ischange = false;
            final LinearLayout ll = viewHolder.editLL;
            final ImageButton addBtn = viewHolder.addBtn;
            final TextView unit1 = viewHolder.unit1;
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    view.setVisibility(View.INVISIBLE);
                    unit1.setVisibility(View.INVISIBLE);
                    ll.setVisibility(View.VISIBLE);
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    ischange = true;
                    editText.setText(++currentNum + "");
                    ischange = false;
                    countMap.put(String.valueOf(bean.getProductID()), currentNum);
                }
            });
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    if (currentNum > 0) {
                        ischange = true;
                        editText.setText(--currentNum + "");
                        ischange = false;
                        countMap.put(String.valueOf(bean.getProductID()), currentNum);
                        if (currentNum == 0) {
                            addBtn.setVisibility(View.VISIBLE);
                            unit1.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            });
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearFocus();//点击加减的时候，去掉所有edittext的focus，关闭软键盘
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    ischange = true;
                    editText.setText(++currentNum + "");
                    ischange = false;
                    countMap.put(String.valueOf(bean.getProductID()), currentNum);
                }
            });
            viewHolder.name.setText(bean.getName());

            StringBuffer sb = new StringBuffer(bean.getDefaultCode());
            sb.append(" | ").append(bean.getUnit());
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

            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + bean.getImage().getImageSmall());
            viewHolder.unit1.setText(bean.getUom());
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
            @ViewInject(R.id.addBtn)
            ImageButton addBtn; //添加按钮
            @ViewInject(R.id.input_minus)
            ImageButton inputMBtn;//减
            @ViewInject(R.id.input_add)
            ImageButton inputPBtn;//加
            @ViewInject(R.id.editText)
            EditText editText; //输入框
            @ViewInject(R.id.unit1)
            TextView unit1;  //单位
            @ViewInject(R.id.tv_price)
            TextView tv_price;  //单位

        }
    }

    private int existInLastPager(ProductData.ListBean bean) {
        if (addedPros != null) {
            for (AddedProduct product : addedPros) {
                if (product.getProductId().equals(String.valueOf(bean.getProductID()))) {
                    return product.getCount();
                }
            }
        }
        return 0;
    }

    /**
     * 检查edittext中的数字格式是否合法
     * 去掉开头的0
     * 为0或空则设为1
     *
     * @param beanId
     * @param editText
     */
    private void checkText(String beanId,EditText editText){
        String tmpStr = editText.getText().toString();
        if(TextUtils.isEmpty(tmpStr) || Integer.valueOf(tmpStr)==0){
            ToastUtil.show(getActivity(),"数量超出范围");
            editText.setText("1");
            countMap.put(beanId, 1);
            return;
        }
        if(tmpStr.startsWith("0")){
            editText.setText(String.valueOf(Integer.valueOf(tmpStr)));
        }
    }

    private void clearFocus(){
        View v = getActivity().getCurrentFocus();
        if(v!=null){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }
}
