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
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kids.commonframe.base.IBaseAdapter;
import com.kids.commonframe.base.util.ToastUtil;
import com.kids.commonframe.base.util.img.FrecoFactory;
import com.kids.commonframe.config.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.orderpage.entity.DefaultProductData;
import com.runwise.supply.orderpage.entity.ProductBasicList;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * Created by libin on 2017/7/8.
 */

public class OneKeyAdapter extends IBaseAdapter {
    private Context mContext;
    private boolean editMode;
    private HashMap<String,Integer> countMap = new HashMap<>();

    public OneKeyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public HashMap<String, Integer> getCountMap() {
        return countMap;
    }
    interface OneKeyInterface{
        void countChanged();
    }
    private OneKeyInterface callback;
    public void setCallback(OneKeyInterface callback) {
        this.callback = callback;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    @Override
    protected View getExView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.defalut_product_item, null);
            ViewUtils.inject(viewHolder,convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (editMode){
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        }else{
            viewHolder.checkbox.setVisibility(View.GONE);
        }
        final EditText editText = viewHolder.editText;
        final DefaultProductData.ListBean bean = (DefaultProductData.ListBean) mList.get(position);
        ProductBasicList.ListBean basicBean= ProductBasicUtils.getBasicMap().get(String.valueOf(bean.getProductID()));
        if (basicBean != null){
            viewHolder.nameTv.setText(basicBean.getName());
            FrecoFactory.getInstance(mContext).disPlay(viewHolder.sdv, Constant.BASE_URL+basicBean.getImage().getImageSmall());
        }
        if (!countMap.containsKey(String.valueOf(bean.getProductID()))){
            countMap.put(String.valueOf(bean.getProductID()),bean.getPresetQty());
        }
        editText.setText(String.valueOf(countMap.get(String.valueOf(bean.getProductID()))));
        StringBuffer sb = new StringBuffer(basicBean.getDefaultCode());
        sb.append("  ").append(basicBean.getUnit()).append("\n").append(bean.getPriceUnit()).append("元/").append(bean.getUom());
        viewHolder.contentTv.setText(sb.toString());
        viewHolder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = countMap.get(String.valueOf(bean.getProductID()));
                if (count > 1){
                    editText.setText(String.valueOf(--count));
                    countMap.put(String.valueOf(bean.getProductID()),count);
                }else{
                    countMap.remove(String.valueOf(bean.getProductID()));
                    mList.remove(bean);
                    notifyDataSetChanged();;
                }
                //发送事件
                callback.countChanged();
            }
        });
        viewHolder.aBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int count = countMap.get(String.valueOf(bean.getProductID()));
                if (count >= 9999){
                    ToastUtil.show(mContext,"最大只支持到9999");
                }else{
                    editText.setText(String.valueOf(++count));
                    countMap.put(String.valueOf(bean.getProductID()),count);
                }
                callback.countChanged();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer num;
                if (TextUtils.isEmpty(s)){
                    num = 0;
                }else{
                    num = Integer.valueOf(s.toString());
                }
                countMap.put(String.valueOf(bean.getProductID()),num);
//                notifyDataSetChanged();
                callback.countChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (position == mList.size() - 1){
           callback.countChanged();
        }
        return convertView;
    }
    class ViewHolder{
        @ViewInject(R.id.name)
        TextView nameTv;
        @ViewInject(R.id.content)
        TextView contentTv;
        @ViewInject(R.id.checkbox)
        CheckBox checkbox;
        @ViewInject(R.id.productImage)
        SimpleDraweeView sdv;
        @ViewInject(R.id.input_minus)
        ImageButton mBtn;
        @ViewInject(R.id.input_add)
        ImageButton aBtn;
        @ViewInject(R.id.editText)
        EditText editText;
    }
}
