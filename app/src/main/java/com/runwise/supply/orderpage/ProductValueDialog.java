package com.runwise.supply.orderpage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.R;

import java.util.logging.Handler;

import io.vov.vitamio.utils.NumberUtil;

/**
 * 商品列表页输入数量对话框
 *
 * Created by Dong on 2017/11/23.
 */

public class ProductValueDialog extends Dialog implements View.OnClickListener{
    String name;
    double initValue;
    String remark;
    IProductDialogCallback callback;
    EditText mEtValue;
    EditText mEtRemark;
    TextView mTvName;

    public ProductValueDialog(@NonNull Context context, String name, double initValue, String remark,IProductDialogCallback callback ){
        super(context);
        this.name = name;
        this.initValue = initValue;
        this.callback = callback;
        this.remark = remark;
        setContentView(R.layout.dialog_product_input);
        Window window = getWindow();
        window.getAttributes().gravity = Gravity.CENTER;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        findViewById(R.id.dialog_product_input_ok).setOnClickListener(this);
        findViewById(R.id.dialog_product_input_cancel).setOnClickListener(this);
        mTvName = (TextView)findViewById(R.id.tv_dialog_product_sub_title);
        mTvName.setText(name);
        mEtValue = (EditText)findViewById(R.id.et_dialog_product_count);
        mEtRemark = (EditText)findViewById(R.id.et_product_remarks);
        mEtRemark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                CharSequence cs = super.filter(source, start, end, dest, dstart, dend);
                int length = (source==null?0:source.length()) + (dest==null?0:dest.length());
                if(length>20)ToastUtil.show(context,"只能输入20个字哦~");
                return cs;
            }
        }});

       mEtValue.addTextChangedListener(new TextWatcher() {
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
                       mEtValue.setText(dest);
                       mEtValue.setSelection(dest.length());
                   }
               }
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

        mEtValue.setText(NumberUtil.getIOrD(initValue));
        //mEtValue.selectAll();
        mEtValue.requestFocus();

        if(!TextUtils.isEmpty(remark)){
            mEtRemark.setText(remark);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showInputMethod();
    }

    public interface IProductDialogCallback{
        void onInputValue(double value,String remark);
    }

    @Override
    public void onClick(View view) {
        collapseSoftInputMethod();
        switch (view.getId()){
            case R.id.dialog_product_input_ok:
                String strValue = mEtValue.getText().toString();
                String strRemark = mEtRemark.getText().toString();
                if(!TextUtils.isEmpty(mEtValue.getText().toString())){
                    if(callback!=null) {
                        callback.onInputValue(Double.valueOf(strValue),strRemark);
                        dismiss();
                    }
                }
                break;
            default:
                dismiss();
                break;
        }
    }

    /**
     * 收起软键盘
     */
    private void collapseSoftInputMethod(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void showInputMethod(){
        if(mEtValue.requestFocus()){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!=null)imm.showSoftInput(mEtValue,InputMethodManager.SHOW_IMPLICIT);
                }
            },200);//一定要有200延时才出现，还不知道为什么
        }
    }
}
