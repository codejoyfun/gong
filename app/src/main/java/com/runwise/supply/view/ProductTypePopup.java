package com.runwise.supply.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.runwise.supply.R;

import java.util.List;

/**
 * 商品类型弹出窗
 *
 * Created by Dong on 2017/10/31.
 */

public class ProductTypePopup extends PopupWindow implements View.OnClickListener{

    Context context;
    ViewGroup mLayoutContainer;
    int mSelectIndex;

    public ProductTypePopup(Context context,int width, int height, List<String> typeList, int selectIndex) {
        super(width,height);
        this.context = context;
        init();
        setup(typeList,selectIndex);
    }



    private void init(){
        View view = LayoutInflater.from(context).inflate(R.layout.popup_tab_type, null);
        setContentView(view);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setBackgroundDrawable(new ColorDrawable(0x66000000));
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(false);
        setOutsideTouchable(false);
        mLayoutContainer = (ViewGroup)view.findViewById(R.id.layout_tab_container);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ivOpen.setImageResource(R.drawable.arrow);
//            }
//        });
    }

    public void setup(List<String> typeList, int selectIndex){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mSelectIndex = selectIndex;
        for(int i=0;i<typeList.size();i++){
            String strType = typeList.get(i);
            TextView tv = (TextView)layoutInflater.inflate(R.layout.item_product_type,mLayoutContainer,false);
            mLayoutContainer.addView(tv);
            tv.setText(strType);
            tv.setTag(i);
            if (selectIndex==i){
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setBackgroundResource(R.drawable.bg_base_corner);
            }else{
                tv.setTextColor(context.getResources().getColor(R.color.second_color));
                tv.setBackgroundResource(R.drawable.bg_gray_corner);
            }
            tv.setOnClickListener(this);
        }
    }

    public void setSelect(int selectIndex){
        mSelectIndex = selectIndex;
        for(int i=0;i<mLayoutContainer.getChildCount();i++){
            TextView tv = (TextView)mLayoutContainer.getChildAt(i);
            if (mSelectIndex==i){
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setBackgroundResource(R.drawable.bg_base_corner);
            }else{
                tv.setTextColor(context.getResources().getColor(R.color.second_color));
                tv.setBackgroundResource(R.drawable.bg_gray_corner);
            }
        }
    }

    public interface OnSelectListener{
        public void onSelect(int index);
    }

    OnSelectListener mListener;
    ViewPager mViewPager;

    public void setSelectListener(OnSelectListener listener){
        mListener = listener;
    }

    public void setViewPager(ViewPager viewPager){
        this.mViewPager = viewPager;
    }

    @Override
    public void onClick(View view) {
        mSelectIndex = (Integer)view.getTag();
        for(int i=0;i<mLayoutContainer.getChildCount();i++){
            TextView tv = (TextView)mLayoutContainer.getChildAt(i);
            if (mSelectIndex==i){
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setBackgroundResource(R.drawable.bg_base_corner);
            }else{
                tv.setTextColor(context.getResources().getColor(R.color.second_color));
                tv.setBackgroundResource(R.drawable.bg_gray_corner);
            }
        }
        dismiss();
        if(mListener!=null)mListener.onSelect(mSelectIndex);
        if(mViewPager!=null)mViewPager.setCurrentItem(mSelectIndex);
    }
}
