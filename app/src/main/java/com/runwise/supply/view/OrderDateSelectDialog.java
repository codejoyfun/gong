package com.runwise.supply.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.runwise.supply.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mike on 2018/3/12.
 */

public class OrderDateSelectDialog extends Dialog {
    @BindView(R.id.picker_channce)
    TextView mPickerChannce;
    @BindView(R.id.picker_ok)
    TextView mPickerOk;
    @BindView(R.id.tv_start_date_tag)
    TextView mTvStartDateTag;
    @BindView(R.id.tv_end_date_tag)
    TextView mTvEndDateTag;
    @BindView(R.id.tv_start_date)
    TextView mTvStartDate;
    @BindView(R.id.iv_start_date)
    ImageView mIvStartDate;
    @BindView(R.id.tv_end_date)
    TextView mTvEndDate;
    @BindView(R.id.iv_end_date)
    ImageView mIvEndDate;
    @BindView(R.id.end_year)
    WheelView mEndYear;
    @BindView(R.id.end_month)
    WheelView mEndMonth;
    @BindView(R.id.end_day)
    WheelView mEndDay;
    @BindView(R.id.ll_timepicker_end)
    LinearLayout mLlTimepickerEnd;
    @BindView(R.id.year)
    WheelView mYear;
    @BindView(R.id.month)
    WheelView mMonth;
    @BindView(R.id.day)
    WheelView mDay;
    @BindView(R.id.ll_timepicker_start)
    LinearLayout mLlTimepickerStart;

    public void setPickerClickListener(PickerClickListener pickerClickListener) {
        mPickerClickListener = pickerClickListener;
    }

    PickerClickListener mPickerClickListener;

    public OrderDateSelectDialog(@NonNull Context context) {
        super(context, R.style.DialogDate);
        init(context, null, null);
    }

    public OrderDateSelectDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.DialogDate);
        init(context, null, null);
    }

    protected OrderDateSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, R.style.DialogDate);
        init(context, null, null);
    }

    private void init(Context context, String defaultTime, final View animView) {
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.MyPopwindow_anim_style);
        LayoutInflater inflater = LayoutInflater.from(context);

        final View timepickerview = inflater.inflate(R.layout.dialog_order_date_select, null);
        this.setContentView(timepickerview);
        ButterKnife.bind(this);
        mLlTimepickerEnd.setVisibility(View.GONE);

        WheelMain wheelMainStart = initWheelView(timepickerview, false);
        WheelMain wheelMainEnd = initWheelView(timepickerview, true);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mTvEndDate.setText(year + "-" + month + "-" + day);
        mTvStartDate.setText(year + "-" + month + "-" + day);

        window.getAttributes().gravity = Gravity.BOTTOM;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setCancelable(true);
        mPickerChannce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != animView) {
                    positive(animView);
                }
                dismiss();
            }
        });
        mPickerOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPickerClickListener != null) {
                    mPickerClickListener.doPickClick(wheelMainStart.getTime(),
                            wheelMainEnd.getTime());
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


    private WheelMain initWheelView(View contentView, boolean isEndTime) {
        final WheelMain wheelMain = new WheelMain(contentView, isEndTime);
        Calendar calendar = Calendar.getInstance();
        String time = calendar.get(Calendar.YEAR) + "年"
                + (calendar.get(Calendar.MONTH) + 1) + "月"
                + calendar.get(Calendar.DAY_OF_MONTH) + "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(dateFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        wheelMain.initDateTimePicker(year, month, day);
        return wheelMain;
    }

    int mSelectColor = Color.parseColor("#FF4BB400");
    int mTextColor = Color.parseColor("#FF2E2E2E");

    @OnClick({R.id.picker_channce, R.id.picker_ok, R.id.rl_start_date, R.id.rl_end_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picker_channce:
                dismiss();
                break;
            case R.id.picker_ok:

                break;
            case R.id.tv_start_date_tag:
                break;
            case R.id.tv_end_date_tag:
                break;
            case R.id.rl_start_date:
                mLlTimepickerEnd.setVisibility(View.GONE);
                mLlTimepickerStart.setVisibility(View.VISIBLE);
                mIvStartDate.setVisibility(View.VISIBLE);
                mIvEndDate.setVisibility(View.GONE);
                mTvEndDate.setTextColor(mTextColor);
                mTvStartDate.setTextColor(mSelectColor);
                break;
            case R.id.rl_end_date:
                mLlTimepickerEnd.setVisibility(View.VISIBLE);
                mLlTimepickerStart.setVisibility(View.GONE);
                mIvStartDate.setVisibility(View.GONE);
                mIvEndDate.setVisibility(View.VISIBLE);
                mTvEndDate.setTextColor(mSelectColor);
                mTvStartDate.setTextColor(mTextColor);
                break;
        }
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

    private class WheelMain {
        private View view;
        private WheelView wv_year;
        private WheelView wv_month;
        private WheelView wv_day;
        private boolean hasSelectTime;
        private int START_YEAR; // 当前年往前推100年
        private int END_YEAR; // 当前年

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 0);
            END_YEAR = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.YEAR, -10);
            START_YEAR = calendar.get(Calendar.YEAR);
        }

        private boolean isEndTime;

        public WheelMain(View view, boolean isEndTime) {
            super();
            this.view = view;
            this.isEndTime = isEndTime;
            hasSelectTime = false;
            setView(view);
        }


        public void initDateTimePicker(int year, int month, int day) {
            this.initDateTimePicker(year, month, day, 0, 0);
        }

        /**
         * @Description: TODO 弹出日期时间选择器
         */
        public void initDateTimePicker(int year, int month, int day, int h,
                                       int m) {
            // int year = calendar.get(Calendar.YEAR);
            // int month = calendar.get(Calendar.MONTH);
            // int day = calendar.get(Calendar.DATE);
            // 添加大小月月份并将其转换为list,方便之后的判断
            String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
            String[] months_little = {"4", "6", "9", "11"};

            final List<String> list_big = Arrays.asList(months_big);
            final List<String> list_little = Arrays.asList(months_little);

            // 年
            if (isEndTime) {
                wv_year = (WheelView) view.findViewById(R.id.end_year);
            } else {
                wv_year = (WheelView) view.findViewById(R.id.year);
            }
            wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
            wv_year.setCyclic(true);// 可循环滚动
            wv_year.setLabel("年");// 添加文字
            wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

            // 月
            if (isEndTime) {
                wv_month = (WheelView) view.findViewById(R.id.end_month);
            } else {
                wv_month = (WheelView) view.findViewById(R.id.month);
            }
            wv_month.setAdapter(new NumericWheelAdapter(1, 12));
            wv_month.setCyclic(true);
            wv_month.setLabel("月");
            wv_month.setCurrentItem(month);

            // 日
            if (isEndTime) {
                wv_day = (WheelView) view.findViewById(R.id.end_day);
            } else {
                wv_day = (WheelView) view.findViewById(R.id.day);
            }
            wv_day.setCyclic(true);
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (list_big.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (list_little.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                // 闰年
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                    wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                else
                    wv_day.setAdapter(new NumericWheelAdapter(1, 28));
            }
            wv_day.setLabel("日");
            wv_day.setCurrentItem(day - 1);


            wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    int currentIndex = wv_day.getCurrentItem();
                    int year_num = index + START_YEAR;
                    // 判断大小月及是否闰年,用来确定"日"的数据
                    if (list_big.contains(String.valueOf(wv_month
                            .getCurrentItem() + 1))) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                        wv_day.setCurrentItem(currentIndex);
                    } else if (list_little.contains(String.valueOf(wv_month
                            .getCurrentItem() + 1))) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                        if (currentIndex > 29) {
                            wv_day.setCurrentItem(29);
                        } else {
                            wv_day.setCurrentItem(currentIndex);
                        }
                    } else {
                        if ((year_num % 4 == 0 && year_num % 100 != 0)
                                || year_num % 400 == 0) {
                            wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                            if (currentIndex > 28) {
                                wv_day.setCurrentItem(28);
                            } else {
                                wv_day.setCurrentItem(currentIndex);
                            }
                        } else {
                            wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                            if (currentIndex > 27) {
                                wv_day.setCurrentItem(27);
                            } else {
                                wv_day.setCurrentItem(currentIndex);
                            }
                        }
                    }
                    if (isEndTime) {
                        mTvEndDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (wv_day.getCurrentItem() + 1));
                    } else {
                        mTvStartDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (wv_day.getCurrentItem() + 1));
                    }
                }
            });

            wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    int month_num = index + 1;
                    int currentIndex = wv_day.getCurrentItem();
                    // 判断大小月及是否闰年,用来确定"日"的数据
                    if (list_big.contains(String.valueOf(month_num))) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                        wv_day.setCurrentItem(currentIndex);
                    } else if (list_little.contains(String.valueOf(month_num))) {
                        wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                        if (currentIndex > 29) {
                            wv_day.setCurrentItem(29);
                        } else {
                            wv_day.setCurrentItem(currentIndex);
                        }
                    } else {
                        if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                                .getCurrentItem() + START_YEAR) % 100 != 0)
                                || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0) {
                            wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                            if (currentIndex > 28) {
                                wv_day.setCurrentItem(28);
                            } else {
                                wv_day.setCurrentItem(currentIndex);
                            }
                        } else {
                            wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                            if (currentIndex > 27) {
                                wv_day.setCurrentItem(27);
                            } else {
                                wv_day.setCurrentItem(currentIndex);
                            }
                        }
                    }
                    if (isEndTime) {
                        mTvEndDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (wv_day.getCurrentItem() + 1));
                    } else {
                        mTvStartDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (wv_day.getCurrentItem() + 1));
                    }
                }
            });
            wv_day.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    if (isEndTime) {
                        mTvEndDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (index + 1));
                    } else {
                        mTvStartDate.setText((START_YEAR + wv_year.getCurrentItem()) + "-" + (wv_month.getCurrentItem() + 1) + "-" + (index + 1));
                    }
                }
            });
        }

        public String getTime() {
            StringBuffer sb = new StringBuffer();
            sb.append((wv_year.getCurrentItem() + START_YEAR))
                    .append("/")
                    .append(String.format("%02d", wv_month.getCurrentItem() + 1))
                    .append("/")
                    .append(String.format("%02d", (wv_day.getCurrentItem() + 1)));
            return sb.toString();
        }

        public String getTime1() {
            StringBuffer sb = new StringBuffer();
            sb.append((wv_year.getCurrentItem() + START_YEAR))
                    .append("-")
                    .append(String.format("%02d", wv_month.getCurrentItem() + 1))
                    .append("-")
                    .append(String.format("%02d", (wv_day.getCurrentItem() + 1)));
            return sb.toString();
        }
    }

    /**
     * CustomDatePickerDialog 中监听
     *
     * @author shiye
     */
    public interface PickerClickListener {
        void doPickClick(String startYMD, String endYMD);

    }
}
