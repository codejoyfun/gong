package com.runwise.supply.tools;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kids.commonframe.base.util.ToastUtil;
import com.runwise.supply.R;
import com.runwise.supply.entity.InventoryResponse;
import com.runwise.supply.firstpage.entity.ReceiveBean;
import com.runwise.supply.orderpage.entity.ProductData;
import com.runwise.supply.repertory.entity.EditHotResult;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.vov.vitamio.utils.NumberUtil;

/**
 * Created by mike on 2018/1/22.
 */

public class RunwiseKeyBoard extends Dialog {

    @BindView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @BindView(R.id.tv_tag)
    TextView mTvTag;
    @BindView(R.id.et_count)
    EditText mEtCount;
    @BindView(R.id.rl_count)
    RelativeLayout mRlCount;
    @BindView(R.id.tv_one)
    TextView mTvOne;
    @BindView(R.id.tv_two)
    TextView mTvTwo;
    @BindView(R.id.tv_three)
    TextView mTvThree;
    @BindView(R.id.tv_four)
    TextView mTvFour;
    @BindView(R.id.tv_five)
    TextView mTvFive;
    @BindView(R.id.tv_six)
    TextView mTvSix;
    @BindView(R.id.tv_seven)
    TextView mTvSeven;
    @BindView(R.id.tv_eight)
    TextView mTvEight;
    @BindView(R.id.tv_nine)
    TextView mTvNine;
    @BindView(R.id.tv_dot)
    TextView mTvDot;
    @BindView(R.id.tv_zero)
    TextView mTvZero;
    @BindView(R.id.rl_delete)
    RelativeLayout mRlDelete;
    @BindView(R.id.rl_confirm)
    RelativeLayout mRlConfirm;
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    private InventoryResponse.InventoryProduct mInventoryProduct;

    private ReceiveBean mReceiveBean;
    ProductData.ListBean mProductBean;
    EditHotResult.ListBean mEditHotBean;
    private SetCountListener mListener;


    public interface SetCountListener {
        void onSetCount(double count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeric_keyboard);
        Window window = getWindow();
        window.getAttributes().gravity = Gravity.BOTTOM;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this);
        if (mInventoryProduct != null) {
            setUpData(NumberUtil.getIOrD(mInventoryProduct.getEditNum()), mInventoryProduct.getProduct().getName());
        }
        if (mReceiveBean != null) {
            setUpData(NumberUtil.getIOrD(mReceiveBean.getCount()), mReceiveBean.getName(), String.valueOf(NumberUtil.getIOrD(mReceiveBean.getProductUomQty())));
        }
        if (mProductBean != null) {
            setUpData(NumberUtil.getIOrD(mProductBean.getCount()), mProductBean.getName());
        }

        if (mEditHotBean != null) {
            setUpData(String.valueOf(mEditHotBean.getCount()), mEditHotBean.getProduct().getName());
        }

        disableShowInput(mEtCount);

        longClickProcess(mTvOne);
        longClickProcess(mTvTwo);
        longClickProcess(mTvThree);

        longClickProcess(mTvFour);
        longClickProcess(mTvFive);
        longClickProcess(mTvSix);

        longClickProcess(mTvSeven);
        longClickProcess(mTvEight);
        longClickProcess(mTvNine);

        longClickProcess(mTvDot);
        longClickProcess(mTvZero);
        longClickProcess(mRlDelete);
    }


    private void setUpData(String count, String name) {
        mEtCount.setText(count);
        mEtCount.selectAll();
        mTvTitle.setText(name);
    }

    private void setUpData(String count, String name,String productUomQty) {
        mEtCount.setText(count);
        mEtCount.selectAll();
        mTvTitle.setText(name + " x " + productUomQty);
    }

    public RunwiseKeyBoard(@NonNull Context context) {
        super(context, R.style.CustomProgressDialog);

    }

    public void setUp(InventoryResponse.InventoryProduct inventoryProduct, SetCountListener listener) {
        mInventoryProduct = inventoryProduct;
        mListener = listener;
    }

    public void setUp(ReceiveBean receiveBean, SetCountListener listener) {
        mReceiveBean = receiveBean;
        mListener = listener;
    }

    public void setUp(ProductData.ListBean productBean, SetCountListener listener) {
        mProductBean = productBean;
        mListener = listener;
    }
    public void setUp(EditHotResult.ListBean editHotBean, SetCountListener listener) {
        mEditHotBean = editHotBean;
        mListener = listener;
    }


    public RunwiseKeyBoard(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RunwiseKeyBoard(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @OnClick({R.id.iv_close, R.id.rl_confirm})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.rl_confirm:
                String etValue = mEtCount.getText().toString();
                //还需要检查是否是合法数字,待编码
                if (checkLegalNum(etValue)) {
                    if (mListener != null){
                        try{
                            mListener.onSetCount(Double.valueOf(etValue));
                        }catch (Exception e){
                            ToastUtil.show(getContext(), "数字不合法！");
                            e.printStackTrace();
                            return;
                        }
                    }
                    dismiss();
                }
                break;
        }
    }

    private boolean checkLegalNum(String etValue) {
        if (TextUtils.isEmpty(etValue)) {
            ToastUtil.show(getContext(), "输入内容为空！");
            return false;
        }
        if (etValue.equals(".")) {
            ToastUtil.show(getContext(), "数字不合法！");
            return false;
        }
        int index = etValue.indexOf(".");
        String tempEtValue;
        if (index == -1) {
            tempEtValue = etValue;
        } else {
            tempEtValue = etValue.substring(0, index);
        }
        if (tempEtValue.length() > 10) {
            ToastUtil.show(getContext(), "输入的数字超过最大值！");
            return false;
        }
        return true;
    }

    private void longClickProcess(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateOperation(v.getId());    //手指按下时触发不停的发送消息
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopOperation();    //手指抬起时停止发送
                }
                return true;
            }
        });
    }

    private ScheduledExecutorService scheduledExecutor;

    private void updateOperation(int viewId) {
        final int vid = viewId;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = vid;
                handler.sendMessage(msg);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
    }

    private void stopOperation() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int viewId = msg.what;
            String addStr = "";
            switch (viewId) {
                case R.id.tv_one:
                    addStr = "1";
                    input(addStr);
                    break;
                case R.id.tv_two:
                    addStr = "2";
                    input(addStr);
                    break;
                case R.id.tv_three:
                    addStr = "3";
                    input(addStr);
                    break;
                case R.id.tv_four:
                    addStr = "4";
                    input(addStr);
                    break;
                case R.id.tv_five:
                    addStr = "5";
                    input(addStr);
                    break;
                case R.id.tv_six:
                    addStr = "6";
                    input(addStr);
                    break;
                case R.id.tv_seven:
                    addStr = "7";
                    input(addStr);
                    break;
                case R.id.tv_eight:
                    addStr = "8";
                    input(addStr);
                    break;
                case R.id.tv_nine:
                    addStr = "9";
                    input(addStr);
                    break;
                case R.id.tv_dot:
                    addStr = ".";
                    input(addStr);
                    break;
                case R.id.tv_zero:
                    addStr = "0";
                    input(addStr);
                    break;
                case R.id.rl_delete:
                    delete();
                    break;
            }
        }
    };


    private void delete() {
        int selectionStart = mEtCount.getSelectionStart();
        int selectionEnd = mEtCount.getSelectionEnd();
        String content = mEtCount.getText().toString();
        StringBuffer stringBuffer = new StringBuffer(content);
        Log.i("onViewClicked", selectionStart + " " + selectionEnd);
        if (selectionStart == 0 && selectionEnd == 0) {
            return;
        }
        if (selectionStart == selectionEnd) {
            stringBuffer.deleteCharAt(selectionStart - 1);
            mEtCount.setText(stringBuffer.toString());
            mEtCount.setSelection(selectionStart - 1);
        } else {
            stringBuffer.delete(selectionStart, selectionEnd);
            mEtCount.setText(stringBuffer.toString());
            mEtCount.setSelection(selectionStart);
        }
    }

    private void input(String addStr) {
        int selectionStart = mEtCount.getSelectionStart();
        int selectionEnd = mEtCount.getSelectionEnd();
        String content = mEtCount.getText().toString();
        StringBuffer stringBuffer = new StringBuffer(content);
        Log.i("onViewClicked", selectionStart + " " + selectionEnd);
        if (selectionStart == selectionEnd) {
            stringBuffer.insert(selectionStart, addStr);
        } else {
            stringBuffer.replace(selectionStart, selectionEnd, addStr);
        }
        mEtCount.setText(stringBuffer.toString());
        mEtCount.setSelection(selectionStart + 1);
    }

    public void disableShowInput(EditText editText) {
        if (Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {//TODO: handle exception
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {//TODO: handle exception
            }
        }
    }
}
