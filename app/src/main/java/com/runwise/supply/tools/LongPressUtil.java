package com.runwise.supply.tools;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mike on 2018/3/8.
 */

public class LongPressUtil {
    Handler mHandler = new Handler();
    int flag = -1;
    public static final int UP = 1 << 0;
    public static final int DOWN = 1 << 1;

    public static final int LONG_PRESS_TIME = 2*1000;

    CallBack mCallBack;

    public interface CallBack {
        void call();
    }


    public void setUpEvent(View view,CallBack callBack) {
        mCallBack = callBack;
        view.setOnTouchListener(new LongPressTouchListener());
    }


    class LongPressTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //在按下时使用SharedPreference记录一个标记值。
                    flag = DOWN;
                    //开启一个线程，此线程定义自己需要延时多少s。
                    mHandler.postDelayed(mRunnable, LONG_PRESS_TIME);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    flag = UP;
                    mHandler.removeCallbacks(mRunnable);
                    break;
            }
            return false;
        }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (flag == DOWN) {
                //此处根据判断可添加自己延迟后要执行的逻辑
                if (mCallBack != null) {
                    mCallBack.call();
                }
            }
        }
    };
}
