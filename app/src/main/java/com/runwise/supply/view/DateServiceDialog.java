package com.runwise.supply.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.runwise.supply.R;
import com.runwise.supply.adapter.DateServiceAdapter;
import com.runwise.supply.view.timepacker.WheelView;


/**
 * Created by mike on 2018/2/5.
 */

public class DateServiceDialog extends Dialog {

    DateServiceAdapter mDateServiceAdapter;
    WheelView mWheelView;
    DateServiceListener mDateServiceListener;

    public interface DateServiceListener {
        void onSelect(String ymd);
    }

    public DateServiceDialog(@NonNull Context context, int reserveGoodsAdvanceDate) {
        super(context, R.style.DialogDate);
        init(context, reserveGoodsAdvanceDate, null);
    }

    public void setCurrentItem(int index){
        if (mWheelView != null){
            mWheelView.setCurrentItem(index);
        }
    }

    private void init(Context context, int reserveGoodsAdvanceDate, final View animView) {
        mDateServiceAdapter = new DateServiceAdapter(reserveGoodsAdvanceDate);
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.MyPopwindow_anim_style);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View timepickerview = inflater.inflate(R.layout.dialog_date_service, null);
        this.setContentView(timepickerview);
        setContentView(timepickerview);
        window.getAttributes().gravity = Gravity.BOTTOM;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setCancelable(true);
        TextView channce = (TextView) this.findViewById(R.id.picker_channce);
        TextView ok = (TextView) this.findViewById(R.id.picker_ok);
        mWheelView = (WheelView) this.findViewById(R.id.wv_date);
        mWheelView.setAdapter(mDateServiceAdapter);
        channce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != animView) {
                    positive(animView);
                }
                dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDateServiceListener != null) {
                    mDateServiceListener.onSelect(mDateServiceAdapter.getItemYMD(mWheelView.getCurrentItem()));
                    return;
                }
                if (null != animView) {
                    positive(animView);
                }
                dismiss();
            }
        });
        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (null != animView) {
                    positive(animView);
                }
            }
        });
    }

    /**
     * 顺时针旋转
     */
    public void positive(View view) {
        Animation anim = new RotateAnimation(-180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        /** 匀速插值器 */
        LinearInterpolator lir = new LinearInterpolator();
        anim.setInterpolator(lir);
        anim.setDuration(300);
        /** 动画完成后不恢复原状 */
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    public DateServiceListener getDateServiceListener() {
        return mDateServiceListener;
    }

    public void setDateServiceListener(DateServiceListener dateServiceListener) {
        mDateServiceListener = dateServiceListener;
    }
}
