package com.runwise.supply.repertory;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.runwise.supply.R;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.event.InventoryEditEvent;

import org.greenrobot.eventbus.EventBus;

import io.vov.vitamio.utils.NumberUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 修改商品数量的dialog
 *
 * Created by Dong on 2017/12/13.
 */

public class EditCountDialog extends Dialog {

    private Context context;
    private SetCountListener mListener;
    private InventoryResponse.InventoryProduct mInventoryProduct;
    public interface SetCountListener{
        void onSetCount(double count);
    }

    @ViewInject(R.id.name1)
    private TextView mTvTitle;
    @ViewInject(R.id.colseIcon1)
    private ImageView mIvClose;
    @ViewInject(R.id.et_product_amount1)
    private EditText mEtCount;
    @ViewInject(R.id.rl_dialog_add_sum)
    private View mIncludeSumDialog;
    @ViewInject(R.id.finalButton1)
    private TextView mTvButton;

    public EditCountDialog(Context context) {
        super(context, com.kids.commonframe.R.style.CustomProgressDialog);
        this.context = context;
        setContentView(R.layout.dialog_add_sum);
        Window window = getWindow();
        window.getAttributes().gravity = Gravity.BOTTOM;
        window.setWindowAnimations(com.kids.commonframe.R.style.MyPopwindow_anim_style);
        window.setLayout(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this,findViewById(R.id.ll_dialog_add_sum_root));
        mTvTitle.setText(mInventoryProduct.getProduct().getName());
        mEtCount.setText(NumberUtil.getIOrD(mInventoryProduct.getEditNum()));
        mEtCount.selectAll();
        mTvButton.setOnClickListener(v->{
            String etValue = mEtCount.getText().toString();
            if(TextUtils.isDigitsOnly(etValue)){
                mInventoryProduct.setEditNum(Double.valueOf(etValue));
                if(mListener!=null)mListener.onSetCount(Double.valueOf(etValue));
                EventBus.getDefault().post(new InventoryEditEvent());
            }
            dismiss();
        });
        showInputMethod();
    }

    public EditCountDialog setup(SetCountListener listener, InventoryResponse.InventoryProduct inventoryProduct){
        mInventoryProduct = inventoryProduct;
        mListener = listener;
        return this;
    }

    private void showInputMethod(){
        if(mEtCount.requestFocus()){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!=null)imm.showSoftInput(mEtCount,InputMethodManager.SHOW_IMPLICIT);
                }
            },200);//一定要有200延时才出现，还不知道为什么
        }
    }
}
