package com.runwise.supply.repertory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kids.commonframe.base.BaseEntity;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.NetWorkFragment;
import com.kids.commonframe.base.util.DateFormateUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.base.view.LoadingLayout;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.mine.entity.RepertoryEntity;
import com.runwise.supply.mine.entity.SearchKeyWork;
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.ProductBasicUtils;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.EditRepertoryResult;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.repertory.entity.UpdateData;
import com.runwise.supply.view.NoWatchEditText;
import com.runwise.supply.view.swipmenu.SwipeMenu;
import com.runwise.supply.view.swipmenu.SwipeMenuCreator;
import com.runwise.supply.view.swipmenu.SwipeMenuItem;
import com.runwise.supply.view.swipmenu.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2017/7/3.
 * 根据传入的数据集合，显示全部、冷藏、冻货、干货集合
 */

public class EditRepertoryListFragment extends NetWorkFragment {
    @ViewInject(R.id.pullListView)
    private SwipeMenuListView pullListView;
    private ProductAdapter adapter;
    public  DataType type;
    @ViewInject(R.id.loadingLayout)
    private LoadingLayout loadingLayout;
    private List<EditRepertoryResult.InventoryBean.ListBean> dataList;
    private String keyWork;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProductAdapter();
        pullListView.setAdapter(adapter);
        adapter.setData(dataList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                openItem.setBackground(new ColorDrawable(Color.parseColor("#fec159")));
                openItem.setWidth(500);
                openItem.setTitle("确认数量一致");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        pullListView.setMenuCreator(creator);
        pullListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                EditRepertoryResult.InventoryBean.ListBean bean = adapter.getList().remove(position);
                bean.setChecked(true);
                int findIndex = 0;
                boolean findIt = false;
                for (int i = 0; i< adapter.getList().size(); i++){
                    EditRepertoryResult.InventoryBean.ListBean entity = adapter.getList().get(i);
                    if(entity.getType() == 1) {
                        findIndex = i;
                        findIt = true;
                        break;
                    }
                }
                if(findIt) {
                    adapter.getList().add(findIndex,bean);
                }
                else {
                    adapter.getList().add(bean);
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
    }

    public void setData(List<EditRepertoryResult.InventoryBean.ListBean> mDataList) {
        List<EditRepertoryResult.InventoryBean.ListBean> typeList = new ArrayList<>();
        for (EditRepertoryResult.InventoryBean.ListBean bean : mDataList){
            if (bean.getProduct().getStock_type().equals(type.getType())){
                typeList.add(bean);
            }
        }
        if(type == DataType.ALL) {
            this.dataList = mDataList;
        }
        else {
            dataList = typeList;
        }
        for(EditRepertoryResult.InventoryBean.ListBean bean : dataList) {
            bean.setEditNum(bean.getActual_qty());
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.repertory_layout_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSynEvent(SearchKeyWork event) {
        adapter.setData(findArrayByWord(event.getKeyWork()));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(UpdateData updateData) {
        if(updateData.getType() != type) {
            adapter.notifyDataSetChanged();
        }
    }
    //添加新商品
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewBean(NewAdd newBean) {
        List<EditRepertoryResult.InventoryBean.ListBean> newProductList = newBean.getNewProductList();
        if(type == DataType.ALL || newProductList.get(0).getProduct().getStock_type().equals(type.getType())) {
            boolean isOtherView = false;
            for(EditRepertoryResult.InventoryBean.ListBean bean:adapter.getList()) {
                if(bean.getType() == 1) {
                    isOtherView = true;
                    break;
                }
            }
            if(!isOtherView) {
                EditRepertoryResult.InventoryBean.ListBean otherBean = new EditRepertoryResult.InventoryBean.ListBean();
                otherBean.setType(1);
                adapter.getList().add(otherBean);
            }
            adapter.getList().addAll(newProductList);
            adapter.notifyDataSetChanged();
            loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
        }
    }
    //返回当前标签下名称包含的
    private List<EditRepertoryResult.InventoryBean.ListBean> findArrayByWord(String word) {
        keyWork = word;
        List<EditRepertoryResult.InventoryBean.ListBean> findList = new ArrayList<>();
        if(TextUtils.isEmpty(word)) {
            return dataList;
        }
        for (EditRepertoryResult.InventoryBean.ListBean bean : dataList){
            if (bean.getProduct().getName().contains(word)) {
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
    public List<EditRepertoryResult.InventoryBean.ListBean> getFinalDataList() {
        return adapter.getList();
    }

        public class ProductAdapter extends IBaseAdapter<EditRepertoryResult.InventoryBean.ListBean> implements ListAdapter {
            @Override
            protected View getExView(int position, View convertView, ViewGroup parent) {
                final ViewHolder viewHolder;
                int viewType = getItemViewType(position);
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    switch (viewType) {
                        case 0:
                            convertView = View.inflate(mContext, R.layout.edit_repertory_layout_item, null);
                            ViewUtils.inject(viewHolder,convertView);
                            break;
                        case 1:
                            convertView = View.inflate(mContext, R.layout.edit_repertory_text_list, null);
                            break;
                    }
                    convertView.setTag(viewHolder);
                }
                else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                final EditRepertoryResult.InventoryBean.ListBean bean =  mList.get(position);
                switch (viewType){
                    case 0:
                        viewHolder.editText.removeTextChangedListener();
                        viewHolder.editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }
                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                if(!TextUtils.isEmpty(editable.toString())) {
                                    bean.setEditNum(Integer.parseInt(editable.toString()));
                                    if(bean.getEditNum() == bean.getActual_qty()) {
                                        viewHolder.editText.setTextColor(Color.parseColor("#dddddd"));
                                    }
                                    else{
                                        viewHolder.editText.setTextColor(Color.parseColor("#444444"));
                                    }
                                    UpdateData updateData = new UpdateData();
                                    updateData.setType(type);
                                    EventBus.getDefault().post(updateData);
                                }
                                else{
                                    bean.setEditNum(0);
                                }
                            }
                        });
                        if(bean.getEditNum() == bean.getActual_qty()) {
                            viewHolder.editText.setTextColor(Color.parseColor("#dddddd"));
                        }
                        else{
                            viewHolder.editText.setTextColor(Color.parseColor("#444444"));
                        }
                        viewHolder.editText.setText(bean.getEditNum()+"");
                        EditRepertoryResult.InventoryBean.ListBean.ProductBean productBean = bean.getProduct();
                        if (productBean != null){
                            if(!TextUtils.isEmpty(keyWork)) {
                                int index = productBean.getName().indexOf(keyWork);
                                if(index != -1) {
                                    SpannableString spannStr = new SpannableString(productBean.getName());
                                    spannStr.setSpan(new ForegroundColorSpan(Color.parseColor("#6bb400")), index, index + keyWork.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    viewHolder.name.setText(spannStr);
                                }
                            }
                            else {
                                viewHolder.name.setText(productBean.getName());
                            }
                            viewHolder.number.setText(productBean.getDefault_code() + " | ");
                            viewHolder.content.setText(productBean.getUnit());
                            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImage_small());
                        }
                        viewHolder.value.setText("库存" + bean.getActual_qty()+"");
                        ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProduct().getId()));
                        if(basicBean != null) {
                            viewHolder.uom.setText(basicBean.getUom());
                        }
                        else {
                            viewHolder.uom.setText("");
                        }
                        viewHolder.dateNumber.setText(bean.getLot_num());
                        viewHolder.dateLate.setText(DateFormateUtil.getLaterFormat(bean.getLife_end_date()));
                        if(bean.isChecked()) {
                            viewHolder.rootLayout.setBackgroundColor(Color.parseColor("#fefce8"));
                        }
                        else {
                            viewHolder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        break;
                }
                return convertView;
            }
            @Override
            public boolean isEnabled(int position){
                EditRepertoryResult.InventoryBean.ListBean bean =  mList.get(position);
                if(bean.getType() == 0) {
                    return true;
                }
                return false;
            }
            @Override
            public boolean areAllItemsEnabled(){
                return false;
            }

            @Override
            public int getItemViewType(int position) {
                EditRepertoryResult.InventoryBean.ListBean bean =  mList.get(position);
                return bean.getType();
            }

            @Override
            public int getViewTypeCount() {
                return 2;
            }

            class ViewHolder {
                @ViewInject(R.id.name)
                TextView            name;
                @ViewInject(R.id.productImage)
                SimpleDraweeView    sDv;
                @ViewInject(R.id.number)
                TextView            number;
                @ViewInject(R.id.content)
                TextView content;
                @ViewInject(R.id.value)
                TextView         value;
                @ViewInject(R.id.uom)
                TextView         uom;
                @ViewInject(R.id.dateNumber)
                TextView         dateNumber;
                @ViewInject(R.id.dateLate)
                TextView            dateLate;
                @ViewInject(R.id.editText)
                NoWatchEditText editText;
                @ViewInject(R.id.rootLayout)
                View rootLayout;
            }
        }
}
