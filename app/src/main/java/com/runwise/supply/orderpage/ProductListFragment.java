package com.runwise.supply.orderpage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.kids.commonframe.base.bean.ProductGetEvent;
import com.kids.commonframe.base.bean.ProductQueryEvent;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.runwise.supply.GlobalApplication;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.AddedProduct;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.orderpage.entity.ProductData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class ProductListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private PullToRefreshListView pullListView;
    private ProductAdapter adapter;
    public  DataType type;
    private ArrayList<AddedProduct> addedPros;
    //选中数量map
    private static HashMap<String,Integer> countMap = new HashMap<>();
    //缓存全部商品列表
    private static ArrayList<ProductData.ListBean> arrayList;

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
        pullListView.setMode(PullToRefreshBase.Mode.DISABLED);
        pullListView.setAdapter(adapter);
        canSeePrice = GlobalApplication.getInstance().getCanSeePrice();

    }
    @Override
    protected int createViewByLayoutId() {
        return R.layout.product_layout_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductGetEvent event) {
        //得到数据，更新UI
        if (arrayList == null) {
            arrayList = ((ProductActivity)getActivity()).getDataList();
        }
        if (type == DataType.ALL){
            //先统计一次id,个数
            for (ProductData.ListBean bean : arrayList){
                countMap.put(String.valueOf(bean.getProductID()),Integer.valueOf(0));
                //同时根据上个页面传值更新一次
                int count = existInLastPager(bean);
                countMap.put(String.valueOf(bean.getProductID()),count);
            }

            adapter.setData(arrayList);
        }else{
            ArrayList<ProductData.ListBean> typeList = new ArrayList<>();
            for (ProductData.ListBean bean : arrayList){
                    if (bean.getStockType().equals(type.getType())){
                        typeList.add(bean);
                    }
            }
            adapter.setData(typeList);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(ProductCountChangeEvent event){
        adapter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public  void onDataSynEvent(ProductQueryEvent event){
        String word = event.getSearchWord();
        //只在当前类型下面找名称包括的元素
        List<ProductData.ListBean> findArray = findArrayByWord(word);
        adapter.setData(findArray);
    }
    //返回当前标签下名称包含的
    private List<ProductData.ListBean> findArrayByWord(String word) {
        List<ProductData.ListBean> findList = new ArrayList<>();
        if (type == DataType.ALL){
            for (ProductData.ListBean bean : arrayList){
                ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
                if (basicBean.getName().contains(word)) {
                    findList.add(bean);
                }
            }
        }else{
            for (ProductData.ListBean bean : arrayList){
                if (bean.getStockType().equals(type.getType())){
                    ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
                    if (basicBean.getName().contains(word)) {
                        findList.add(bean);
                    }
                }
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
                ViewUtils.inject(viewHolder,convertView);
                convertView.setTag(viewHolder);
                EditText et = viewHolder.editText;
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!ischange){
                            int changedNum = 0;
                            if (!TextUtils.isEmpty(s)){
                                changedNum = Integer.valueOf(s.toString());
                            }
                            countMap.put(String.valueOf(bean.getProductID()),changedNum);
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
            if (countMap.get(String.valueOf(bean.getProductID())) > 0){
                viewHolder.editLL.setVisibility(View.VISIBLE);
                viewHolder.addBtn.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.editLL.setVisibility(View.INVISIBLE);
                viewHolder.addBtn.setVisibility(View.VISIBLE);
            }
            final EditText editText = viewHolder.editText;
            ischange = true;
            editText.setText(countMap.get(String.valueOf(bean.getProductID()))+"");
            ischange = false;
            final LinearLayout ll = viewHolder.editLL;
            final ImageButton addBtn = viewHolder.addBtn;
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.INVISIBLE);
                    ll.setVisibility(View.VISIBLE);
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    ischange = true;
                    editText.setText(++currentNum+"");
                    ischange = false;
                    countMap.put(String.valueOf(bean.getProductID()),currentNum);
                }
            });
            viewHolder.inputMBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    if (currentNum > 0){
                        ischange = true;
                        editText.setText(--currentNum+"");
                        ischange = false;
                        countMap.put(String.valueOf(bean.getProductID()),currentNum);
                        if (currentNum == 0){
                            addBtn.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            });
            viewHolder.inputPBtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int currentNum = countMap.get(String.valueOf(bean.getProductID()));
                    ischange = true;
                    editText.setText(++currentNum+"");
                    ischange = false;
                    countMap.put(String.valueOf(bean.getProductID()),currentNum);
                }
            });

            ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
            if (basicBean != null){
                viewHolder.name.setText(basicBean.getName());
                StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
                sb.append("  ").append(basicBean.getUnit());
                if (canSeePrice){
                    if (bean.isIsTwoUnit()){
                        sb.append("\n¥")
                                .append(bean.getSettlePrice())
                                .append("元/")
                                .append(bean.getSettleUomId());
                    }else{
                        sb.append("\n¥")
                                .append(bean.getPrice())
                                .append("元/")
                                .append(bean.getUom());
                    }
                }
                viewHolder.content.setText(sb.toString());
                FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + basicBean.getImage().getImageSmall());
            }

            return convertView;
        }

        class ViewHolder {
            @ViewInject(R.id.name)
            TextView            name;   //名称
            @ViewInject(R.id.productImage)
            SimpleDraweeView    sDv;    //头像
            @ViewInject(R.id.content)
            TextView            content;//内容
            @ViewInject(R.id.editLL)
            LinearLayout editLL;        //整体编辑框
            @ViewInject(R.id.addBtn)
            ImageButton         addBtn; //添加按钮
            @ViewInject(R.id.input_minus)
            ImageButton         inputMBtn;//减
            @ViewInject(R.id.input_add)
            ImageButton         inputPBtn;//加
            @ViewInject(R.id.editText)
            EditText            editText; //输入框

        }
    }
    private int existInLastPager(ProductData.ListBean bean) {
        if (addedPros != null){
            for (AddedProduct product : addedPros){
                if(product.getProductId().equals(String.valueOf(bean.getProductID()))){
                    return product.getCount();
                }
            }
        }
        return 0;
    }
}
