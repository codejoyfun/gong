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
import com.runwise.supply.orderpage.DataType;
import com.runwise.supply.orderpage.entity.ProductBasicList;
import com.runwise.supply.repertory.entity.NewAdd;
import com.runwise.supply.repertory.entity.PandianResult;
import com.runwise.supply.repertory.entity.UpdateData;
import com.runwise.supply.view.NoWatchEditText;
import com.runwise.supply.view.swipmenu.SwipeMenu;
import com.runwise.supply.view.swipmenu.SwipeMenuCreator;
import com.runwise.supply.view.swipmenu.SwipeMenuItem;
import com.runwise.supply.view.swipmenu.SwipeMenuListView;

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
    private List<PandianResult.InventoryBean.LinesBean> dataList;
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
//                PandianResult.InventoryBean.LinesBean bean = adapter.getList().remove(position);
                PandianResult.InventoryBean.LinesBean bean = adapter.getList().get(position);
                bean.setEditNum(bean.getTheoreticalQty());
                bean.setChecked(true);
//                int findIndex = 0;
//                boolean findIt = false;
//                for (int i = 0; i< adapter.getList().size(); i++){
//                    PandianResult.InventoryBean.LinesBean entity = adapter.getList().get(i);
//                    if(entity.getType() == 1) {
//                        findIndex = i;
//                        findIt = true;
//                        break;
//                    }
//                }
//                if(findIt) {
//                    adapter.getList().add(findIndex,bean);
//                }
//                else {
//                    adapter.getList().add(bean);
//                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
    }

    public void setData(List<PandianResult.InventoryBean.LinesBean> mDataList) {
        List<PandianResult.InventoryBean.LinesBean> typeList = new ArrayList<>();
        for (PandianResult.InventoryBean.LinesBean bean : mDataList){
            if (bean.getProduct().getStockType().equals(type.getType())){
                typeList.add(bean);
            }
        }
        if(type == DataType.ALL) {
            this.dataList = mDataList;
        }
        else {
            dataList = typeList;
        }
        for(PandianResult.InventoryBean.LinesBean bean : dataList) {
            bean.setEditNum(bean.getTheoreticalQty());
        }
    }

    @Override
    protected int createViewByLayoutId() {
        return R.layout.repertory_layout_list;
    }
    //
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDataSynEvent(SearchKeyWork event) {
//        adapter.setData(findArrayByWord(event.getKeyWork()));
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(UpdateData updateData) {
        if(!updateData.getType().getType().equals(type.getType())) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //添加新商品
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNewBean(NewAdd newBean) {
        if(type == DataType.ALL || newBean.getBean().getProduct().getStockType().equals(type.getType())) {
//            boolean isOtherView = false;
//            for(PandianResult.InventoryBean.LinesBean bean:adapter.getList()) {
//                if(bean.getType() == 1) {
//                    isOtherView = true;
//                    break;
//                }
//            }
//            if(!isOtherView) {
//                PandianResult.InventoryBean.LinesBean otherBean = new PandianResult.InventoryBean.LinesBean();
//                otherBean.setType(1);
//                adapter.getList().add(otherBean);
//            }
            if(newBean.getType() == 1) {
                boolean isFind = false;
                for(PandianResult.InventoryBean.LinesBean bean : adapter.getList()) {
                   if(bean.getProduct().getName().equals(newBean.getBean().getProduct().getName()) ) {
                       bean.setEditNum(bean.getEditNum() + newBean.getBean().getEditNum());
                       isFind = true;
                       break;
                   }
                }
                if(!isFind) {
                    adapter.getList().add(0,newBean.getBean());
                }
            }
            else {
                adapter.getList().add(0, newBean.getBean());
            }
            adapter.notifyDataSetChanged();
            loadingLayout.onSuccess(adapter.getCount(),"暂时没有数据");
        }
    }
//    //返回当前标签下名称包含的
//    private List<PandianResult.InventoryBean.LinesBean> findArrayByWord(String word) {
//        keyWork = word;
//        List<PandianResult.InventoryBean.LinesBean> findList = new ArrayList<>();
//        if(TextUtils.isEmpty(word)) {
//            return dataList;
//        }
//        for (PandianResult.InventoryBean.LinesBean bean : dataList){
//            if (bean.getProduct().getName().contains(word)) {
//                findList.add(bean);
//            }
//        }
//        return findList;
//    }

    @Override
    public void onSuccess(BaseEntity result, int where) {

    }

    @Override
    public void onFailure(String errMsg, BaseEntity result, int where) {

    }
    public List<PandianResult.InventoryBean.LinesBean> getFinalDataList() {
        return adapter.getList();
    }

    public class ProductAdapter extends IBaseAdapter<PandianResult.InventoryBean.LinesBean> implements ListAdapter {
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
            final PandianResult.InventoryBean.LinesBean bean =  mList.get(position);
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
                                if(bean.getEditNum() == bean.getTheoreticalQty()) {
                                    viewHolder.editText.setTextColor(Color.parseColor("#dddddd"));
                                }
                                else{
                                    viewHolder.editText.setTextColor(Color.parseColor("#444444"));
                                }
//                                viewHolder.editText.setFocusable(true);
//                                viewHolder.editText.setFocusableInTouchMode(true);
//                                viewHolder.editText.requestFocus();
//                                UpdateData updateData = new UpdateData();
//                                updateData.setType(type);
//                                EventBus.getDefault().post(updateData);
                            }
                            else{
                                bean.setEditNum(0);
                            }
                        }
                    });
                    if(bean.getEditNum() == bean.getTheoreticalQty()) {
                        viewHolder.editText.setTextColor(Color.parseColor("#dddddd"));
                    }
                    else{
                        viewHolder.editText.setTextColor(Color.parseColor("#444444"));
                    }
                    viewHolder.editText.setText(bean.getEditNum()+"");
                    ProductBasicList.ListBean productBean = bean.getProduct();
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
                        viewHolder.number.setText(bean.getCode() + " | ");
                        viewHolder.content.setText(productBean.getUnit());
                        if(productBean.getImage() != null ) {
                            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sDv, Constant.BASE_URL + productBean.getImage().getImageSmall());
                        }
                    }
                    viewHolder.value.setText("库存" + bean.getTheoreticalQty()+"");
                    viewHolder.uom.setText(productBean.getUom());
                    viewHolder.uom_other.setText(productBean.getUom());
                    if(!TextUtils.isEmpty(bean.getLotNum())) {
                        viewHolder.dateNumber.setText(bean.getLotNum());
                        viewHolder.dateNumber.setVisibility(View.VISIBLE);
                    }
                    else{
                        viewHolder.dateNumber.setVisibility(View.GONE);
                    }
                    viewHolder.dateLate.setText(DateFormateUtil.getLaterFormat(bean.getLifeEndDate()));
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
//            PandianResult.InventoryBean.LinesBean bean =  mList.get(position);
//            if(bean.getType() == 0) {
//                return true;
//            }
            return true;
        }
        @Override
        public boolean areAllItemsEnabled(){
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            PandianResult.InventoryBean.LinesBean bean =  mList.get(position);
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
            @ViewInject(R.id.uom_other)
            TextView         uom_other;
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
