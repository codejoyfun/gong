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
import com.runwise.supply.tools.TimeUtils;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;


/**
 * Created by mike on 2018/2/5.
 */

public class DateServiceDialog extends Dialog {

    LoopView mWheelView;
    DateServiceListener mDateServiceListener;

    public interface DateServiceListener {
        void onSelect(String ymd);
    }

    public DateServiceDialog(@NonNull Context context, int reserveGoodsAdvanceDate) {
        super(context, R.style.DialogDate);
        init(context, reserveGoodsAdvanceDate, null);
    }

    public void setCurrentItem(int index) {
        if (mWheelView != null) {
            mWheelView.setCurrentPosition(index);
        }
    }

    private ArrayList<String> generateData(int reserveGoodsAdvanceDate) {
        ArrayList<String> descList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            int index = reserveGoodsAdvanceDate - 1 + i;
            String date = TimeUtils.getABFormatDate(index).substring(5);
            String week = TimeUtils.getWeekStr(index);
            String desc = date + " " + week;
            switch (index) {
                case 0:
                    desc += "[今天]";
                    break;
                case 1:
                    desc += "[明天]";
                    break;
                case 2:
                    desc += "[后天]";
                    break;
            }
            descList.add(desc);
        }
        return descList;
    }

    private void init(Context context, int reserveGoodsAdvanceDate, final View animView) {
        ArrayList<String> descList = generateData(reserveGoodsAdvanceDate);

        Window window = this.getWindow();
        window.setWindowAnimations(R.style.MyPopwindow_anim_style);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View timepickerview = inflater.inflate(R.layout.dialog_date_service, null);
        setContentView(timepickerview);

        window.getAttributes().gravity = Gravity.BOTTOM;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setCancelable(true);

        TextView channce = (TextView) this.findViewById(R.id.picker_channce);
        TextView ok = (TextView) this.findViewById(R.id.picker_ok);
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
                    mDateServiceListener.onSelect(TimeUtils.getABFormatDate(reserveGoodsAdvanceDate - 1 + mWheelView.getSelectedItem()));
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

        mWheelView = (LoopView) this.findViewById(R.id.wv_date);
        mWheelView.setNotLoop();
        //滚动监听
        mWheelView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {

            }
        });
        //设置原始数据
        mWheelView.setItems(descList);
        //设置初始位置
        mWheelView.setCurrentPosition(0);
        //设置字体大小
        mWheelView.setTextSize(20);
        mWheelView.setCenterTextColor(getContext().getResources().getColor(R.color.colorAccent));
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
