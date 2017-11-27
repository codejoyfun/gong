package com.runwise.supply.orderpage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.runwise.supply.R;

import java.util.logging.Handler;

/**
 * 商品列表页输入数量对话框
 *
 * Created by Dong on 2017/11/23.
 */

public class ProductValueDialog extends Dialog implements View.OnClickListener{
    String name;
    int initValue;
    IProductDialogCallback callback;
    EditText mEtValue;
    TextView mTvName;

    public ProductValueDialog(@NonNull Context context, String name, int initValue, IProductDialogCallback callback ){
        super(context);
        this.name = name;
        this.initValue = initValue;
        this.callback = callback;
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
        if(initValue>0){
            mEtValue.setText(String.valueOf(initValue));
            //mEtValue.selectAll();
            mEtValue.requestFocus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showInputMethod();
    }

    public interface IProductDialogCallback{
        void onInputValue(int value);
    }

    @Override
    public void onClick(View view) {
        collapseSoftInputMethod();
        switch (view.getId()){
            case R.id.dialog_product_input_ok:
                String strValue = mEtValue.getText().toString();
                if(!TextUtils.isEmpty(mEtValue.getText().toString()) && TextUtils.isDigitsOnly(strValue)){
                    if(callback!=null) {
                        callback.onInputValue(Integer.valueOf(strValue));
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
