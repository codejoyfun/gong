package com.runwise.supply.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runwise.supply.R;

import java.util.ArrayList;

/**
 * Created by mike on 2017/9/6.
 */

public class ProductTypeAdapter extends BaseAdapter {

    ArrayList<String> typeList;
    public ArrayList<Boolean> selectList;

    public ProductTypeAdapter(ArrayList<String> typeList) {
        this.typeList = typeList;
        selectList = new ArrayList<>();
        for (int i = 0; i < typeList.size(); i++) {
            Boolean aBoolean = new Boolean(false);
            selectList.add(aBoolean);
        }
    }

    public void setSelectIndex(int index) {
        for (int i = 0;i < selectList.size();i++){
            selectList.set(i,new Boolean(false));
        }
        selectList.set(index,new Boolean(true));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public String getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        public TextView textView;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_type, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView;
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView.setText(typeList.get(position));
        if (selectList.get(position)){
            viewHolder.textView.setTextColor(parent.getContext().getResources().getColor(R.color.white));
            viewHolder.textView.setBackgroundResource(R.drawable.bg_base_corner);
        }else{
            viewHolder.textView.setTextColor(parent.getContext().getResources().getColor(R.color.second_color));
            viewHolder.textView.setBackgroundResource(R.drawable.bg_gray_corner);
        }
        return convertView;
    }
}
