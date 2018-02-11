package com.runwise.supply.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
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
import com.runwise.supply.view.timepacker.NumericWheelAdapter;
import com.runwise.supply.view.timepacker.OnWheelChangedListener;
import com.runwise.supply.view.timepacker.WheelView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 日期选择器
 * 
 */
public class CustomDatePickerDialog extends Dialog {
	private TextView ok;
	private PickerClickListener listener;

	public CustomDatePickerDialog(Context context) {
		super(context, R.style.DialogNoBg);
		init(context, null, null);

	}

	public CustomDatePickerDialog(Context context, String defaultTime) {
		super(context, R.style.DialogNoBg);
		init(context, defaultTime, null);
	}

	public CustomDatePickerDialog(Context context, String defaultTime,
								  View animView) {
		super(context, R.style.DialogNoBg);
		init(context, defaultTime, animView);
	}

	private void init(Context context, String defaultTime, final View animView) {
		Window window = this.getWindow();
		window.setWindowAnimations(R.style.MyPopwindow_anim_style);
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		this.setContentView(timepickerview);
		setContentView(timepickerview);
		window.getAttributes().gravity = Gravity.BOTTOM;
		final WheelMain wheelMain = new WheelMain(timepickerview);
		Calendar calendar = Calendar.getInstance();
		String time = null;
		if (TextUtils.isEmpty(defaultTime)) {
			time = calendar.get(Calendar.YEAR) + "年"
					+ (calendar.get(Calendar.MONTH) + 1) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "";
		} else {
			time = defaultTime;
		}
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
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setCancelable(true);
		TextView channce = (TextView) this.findViewById(R.id.picker_channce);
		ok = (TextView) this.findViewById(R.id.picker_ok);
		channce.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != animView) {
					positive(animView);
				}
				CustomDatePickerDialog.this.dismiss();
			}
		});
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.doPickClick(wheelMain.getTime(),
							wheelMain.getTime1(), 0);
					return;
				}
				if (null != animView) {
					positive(animView);
				}
				CustomDatePickerDialog.this.dismiss();
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

	public void addPickerListener(String text,
			final PickerClickListener listener) {
		ok.setText(text);
		this.listener = listener;
	}
	public void addPickerListener(final PickerClickListener listener) {
		this.listener = listener;
	}

	private class WheelMain {
		private View view;
		private WheelView wv_year;
		private WheelView wv_month;
		private WheelView wv_day;
		private WheelView wv_hours;
		private WheelView wv_mins;
		private boolean hasSelectTime;
		private int START_YEAR; // 当前年往前推100年
		private int END_YEAR; // 当前年

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, +100);
			END_YEAR= calendar.get(Calendar.YEAR);
			calendar.add(Calendar.YEAR, -200);
			START_YEAR = calendar.get(Calendar.YEAR);
		}

		public WheelMain(View view) {
			super();
			this.view = view;
			hasSelectTime = false;
			setView(view);
		}

		public WheelMain(View view, boolean hasSelectTime) {
			super();
			this.view = view;
			this.hasSelectTime = hasSelectTime;
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
			String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
			String[] months_little = { "4", "6", "9", "11" };

			final List<String> list_big = Arrays.asList(months_big);
			final List<String> list_little = Arrays.asList(months_little);

			// 年
			wv_year = (WheelView) view.findViewById(R.id.year);
			wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
			wv_year.setCyclic(true);// 可循环滚动
			wv_year.setLabel("年");// 添加文字
			wv_year.setCurrentItem(year-END_YEAR-1);// 初始化时显示的数据

			// 月
			wv_month = (WheelView) view.findViewById(R.id.month);
			wv_month.setAdapter(new NumericWheelAdapter(1, 12));
			wv_month.setCyclic(true);
			wv_month.setLabel("月");
			wv_month.setCurrentItem(month);

			// 日
			wv_day = (WheelView) view.findViewById(R.id.day);
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

			wv_hours = (WheelView) view.findViewById(R.id.hour);
			wv_mins = (WheelView) view.findViewById(R.id.min);
			if (hasSelectTime) {
				wv_hours.setVisibility(View.VISIBLE);
				wv_mins.setVisibility(View.VISIBLE);

				wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
				wv_hours.setCyclic(true);// 可循环滚动
				wv_hours.setLabel("时");// 添加文字
				wv_hours.setCurrentItem(h);

				wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
				wv_mins.setCyclic(true);// 可循环滚动
				wv_mins.setLabel("分");// 添加文字
				wv_mins.setCurrentItem(m);
			} else {
				wv_hours.setVisibility(View.GONE);
				wv_mins.setVisibility(View.GONE);
			}

			// 添加"年"监听
			OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					int year_num = newValue + START_YEAR;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(wv_month
							.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(wv_month
							.getCurrentItem() + 1))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if ((year_num % 4 == 0 && year_num % 100 != 0)
								|| year_num % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			};
			// 添加"月"监听
			OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					int month_num = newValue + 1;
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(month_num))) {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
								.getCurrentItem() + START_YEAR) % 100 != 0)
								|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						else
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
			};
			wv_year.addChangingListener(wheelListener_year);
			wv_month.addChangingListener(wheelListener_month);
		}

		public String getTime() {
			StringBuffer sb = new StringBuffer();
			sb.append((wv_year.getCurrentItem() + START_YEAR))
					.append("年")
					.append(String.format("%02d", wv_month.getCurrentItem() + 1))
					.append("月")
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

	/**
	 * 逆时针旋转
	 */
	public void negative(View view) {
		Animation anim = new RotateAnimation(0, -180,
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
	/**
	 * CustomDatePickerDialog 中监听
	 *
	 * @author shiye
	 */
	public interface PickerClickListener {
		/**
		 * 当前选中项目，当前选中position
		 *
		 * @param currentStr
		 * @param currentPosition
		 */
		public void doPickClick(String currentStr, String currenStr1,
								int currentPosition);
	}
}
