package com.runwise.supply.orderpage;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.runwise.supply.SampleApplicationLike;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.DefaultPBean;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by libin on 2017/7/8.
 */

public class OneKeyAdapter extends IBaseAdapter {
    public enum SELECTTYPE {
        NO_SELECT, ALL_SELECT, PART_SELECT
    }

    private Context mContext;
    private boolean editMode;

    private boolean ischange;
    private HashMap<Integer, Integer> countMap = new HashMap<>();
    private List<DefaultPBean> selectArr = new ArrayList<>();

    public OneKeyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public HashMap<Integer, Integer> getCountMap() {
        return countMap;
    }

    public List<DefaultPBean> getSelectArr() {
        return selectArr;
    }

    public void setSelectArr(List<DefaultPBean> selectArr) {
        this.selectArr = selectArr;
    }

    public interface OneKeyInterface {
        void countChanged();

        //选择的类型,0没选，1全选,2部分选
        void selectClicked(SELECTTYPE selectType);
    }

    private OneKeyInterface callback;

    public void setCallback(OneKeyInterface callback) {
        this.callback = callback;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        ArrayList<DefaultPBean> defaultPBeen = new ArrayList<>();
        if (!editMode){
            for (Object o:mList){
                DefaultPBean defaultPBean = (DefaultPBean) o;
                Integer integer = countMap.get(defaultPBean.getProductID());
                if (integer!=null&&integer == 0){
                    defaultPBeen.add(defaultPBean);
                }
            }
            for (DefaultPBean defaultPBean : defaultPBeen){
                mList.remove(defaultPBean);
            }
        }
    }

    @Override
    public void setData(List list) {
        super.setData(list);
        for (Object bean : list) {
            if (!countMap.containsKey(((DefaultPBean) bean).getProductID())) {
                countMap.put(((DefaultPBean) bean).getProductID(), ((DefaultPBean) bean).getPresetQty());
            }
        }
        callback.countChanged();
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final DefaultPBean bean = (DefaultPBean) mList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.defalut_product_item, null);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name);
            viewHolder.contentTv = (TextView) convertView.findViewById(R.id.content);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.sdv = (SimpleDraweeView) convertView.findViewById(R.id.productImage);
            viewHolder.mBtn = (ImageButton) convertView.findViewById(R.id.input_minus);
            viewHolder.aBtn = (ImageButton) convertView.findViewById(R.id.input_add);
            viewHolder.editText = (EditText) convertView.findViewById(R.id.editText);
            viewHolder.countTv = (TextView) convertView.findViewById(R.id.countTv);
            viewHolder.editLL = (LinearLayout) convertView.findViewById(R.id.editLL);
            viewHolder.unit1 = (TextView) convertView.findViewById(R.id.unit1);
            convertView.setTag(viewHolder);
            final EditText edittext = viewHolder.editText;
            viewHolder.editText.addTextChangedListener(new TextWatcher() {
                String mmPrevious;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mmPrevious = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!ischange) {
                        Integer num;
                        if (!TextUtils.isDigitsOnly(s)) {
                            edittext.setText(mmPrevious);
                            return;
                        }
                        if (TextUtils.isEmpty(s.toString())){
                            return;
                        }
                        num = Integer.valueOf(s.toString());
                        countMap.put(bean.getProductID(), num);
//                notifyDataSetChanged();
                        callback.countChanged();
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.editText.setTag(position);
        if (editMode) {
            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.editLL.setVisibility(View.VISIBLE);
            viewHolder.countTv.setVisibility(View.INVISIBLE);
            viewHolder.unit1.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.editLL.setVisibility(View.INVISIBLE);
            viewHolder.checkbox.setVisibility(View.GONE);
            viewHolder.countTv.setVisibility(View.VISIBLE);
            viewHolder.unit1.setVisibility(View.VISIBLE);
        }
        final EditText editText = viewHolder.editText;
        final TextView countTv = viewHolder.countTv;
        ProductBasicList.ListBean basicBean = ProductBasicUtils.getBasicMap(mContext).get(String.valueOf(bean.getProductID()));
        if (basicBean != null) {
            viewHolder.nameTv.setText(basicBean.getName());
            if (basicBean.getImage() != null)
                FrecoFactory.getInstance(mContext).displayWithoutHost(viewHolder.sdv, basicBean.getImage().getImageSmall());
            StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
            sb.append(" | ").append(basicBean.getUnit());
            boolean canSeePrice = SampleApplicationLike.getInstance().getCanSeePrice();
            DecimalFormat df = new DecimalFormat("#.##");
            if (canSeePrice) {
                sb.append("  ").append(df.format(basicBean.getPrice())).append("元/").append(basicBean.getUom());
            }
            viewHolder.contentTv.setText(sb.toString());
            viewHolder.unit1.setText(basicBean.getUom());
        }
        Integer proId = bean.getProductID();
        String count = String.valueOf(countMap.get(proId));
        ischange = true;
        editText.setText(count);
        countTv.setText("x" + count);
        ischange = false;
        viewHolder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer pId = bean.getProductID();
                int count = countMap.get(pId);
                if (count > 0) {
                    ischange = true;
                    editText.setText(String.valueOf(--count));
                    countTv.setText("x" + String.valueOf(count));
                    ischange = false;
                    countMap.put(pId, count);
                } else {
//                    countMap.remove(pId);
//                    mList.remove(bean);
//                    notifyDataSetChanged();
//                    //不管这些包含在selcetArr中，都要刷新全选
//                    if (selectArr.contains(bean)){
//                        selectArr.remove(bean);
//                    }
//                    checkSelectState();
                }
                //发送事件
                callback.countChanged();
            }
        });
        viewHolder.aBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Integer pId = bean.getProductID();
                int count = countMap.get(pId);
                if (count >= 9999) {
                    ToastUtil.show(mContext, "最大只支持到9999");
                } else {
                    ischange = true;
                    editText.setText(String.valueOf(++count));
                    countTv.setText("x" + String.valueOf(count));
                    ischange = false;
                    countMap.put(pId, Integer.valueOf(count));
                }
                callback.countChanged();
            }
        });
        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectArr.contains(bean)) {
                        selectArr.add(bean);
                    }
                } else {
                    selectArr.remove(bean);
                }
                checkSelectState();
            }
        });
        if (selectArr.contains(bean)) {
            viewHolder.checkbox.setChecked(true);
        } else {
            viewHolder.checkbox.setChecked(false);
        }
        return convertView;
    }

    private void checkSelectState() {
        //返回是否全选标记,true全选, false一个没选
        if (selectArr.size() == mList.size() && selectArr.size() != 0) {
            callback.selectClicked(SELECTTYPE.ALL_SELECT);
        } else if (selectArr.size() == 0) {
            callback.selectClicked(SELECTTYPE.NO_SELECT);
        } else {
            callback.selectClicked(SELECTTYPE.PART_SELECT);
        }
    }

    public void setAllSelect(boolean isAll) {
        if (isAll) {
            selectArr.clear();
            selectArr.addAll(mList);
        } else {
            selectArr.clear();
        }
        notifyDataSetChanged();
    }

    public void clearSelect() {
        selectArr.clear();
        notifyDataSetChanged();
    }

    public void deleteSelectItems() {
        //TODO:同时如果计数里面有值，也得一并清掉
        mList.removeAll(selectArr);
        selectArr.clear();
        if (mList.isEmpty()) {
            callback.selectClicked(SELECTTYPE.NO_SELECT);
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView nameTv;
        TextView contentTv;
        CheckBox checkbox;
        SimpleDraweeView sdv;
        ImageButton mBtn;
        ImageButton aBtn;
        EditText editText;
        TextView countTv;
        LinearLayout editLL;
        TextView unit1;
    }
}
