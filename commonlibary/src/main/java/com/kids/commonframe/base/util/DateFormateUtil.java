package com.kids.commonframe.base.util;

import android.annotation.SuppressLint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class DateFormateUtil {
	public final static String GMT_8 = "GMT+08:00";

	/**
	 * 年月日 yyyy-MM-dd
	 */
	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_YM = "yyyy-MM";
	/**
	 * 时分 HH:mm
	 */
	public final static String FORMAT_TIME = "HH:mm";

	/**
	 * 年月日 时分 yyyy-MM-dd HH:mm
	 */
	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	/**
	 * 年月日 时分 yyyy年MM月dd日 HH:mm
	 */
	public final static String FORMAT_DATE_TIME_STRING = "yyyy年MM月dd日 HH:mm";

	/**
	 * MM月dd日 HH:mm
	 */
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public final static String FORMAT_FULL_DATE_TIME_WITH_SYMBOL = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyyMMddHHmmss
	 */
	public final static String FORMAT_FULL_DATE_TIME_NO_SYMBOL = "yyyyMMddHHmmss";

	/**
	 * yyyyMMddHHmmssSSS
	 */
	public final static String FORMAT_FULL_DATE_TIME_WITH_MILLS_NO_SYMBOL = "yyyyMMddHHmmssSSS";

	public static Calendar dateFormatFromString(String dateTimeStr,
			String pattern) {

		// "yyyy-MM-dd HH:mm:ss"
		Calendar c = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			if (dateTimeStr.indexOf(":") == -1) {
				dateTimeStr += " 00:00:00";
			}
			Date dateTime = format.parse(dateTimeStr);
			c = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
			c.setTime(dateTime);
		} catch (ParseException e) {

			Log.e("DateFormateUtil", "dateFormatFromString() -> 日期解析异常 = "
					+ dateTimeStr);
			c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
		}
		return c;
	}

	public static String dateFormatFromCalender(Calendar c, String pattern) {

		// "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		String dateTime = format.format(c.getTime());

		return dateTime;
	}

	public static long dateFormatFromStringToLong(String dateTimeStr) {

		return dateFormatFromString(dateTimeStr,
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL).getTime()
				.getTime();

	}

	public static long dateFormatFromStringToLong(String dateTimeStr,
			String format) {

		return dateFormatFromString(dateTimeStr, format).getTime().getTime();

	}

	@SuppressLint("SimpleDateFormat")
	public static String dateFormatFromMs(long mss, String formats) {

		Date dateTime = new Date(mss);
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		c.setTime(dateTime);

		// "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat format = new SimpleDateFormat(formats);

		String dateTimeStr = format.format(c.getTime());

		return dateTimeStr;
	}

	/**
	 * Description：格式化当前时间
	 * 
	 * @param formats
	 *            格式化模板
	 * @return
	 * @return String
	 * @author name：拜力文
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 *         Modified No： Modified By： Modified Date： Modified Description:
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 **/
	public static String dateFormatCurrentTime(String formats) {

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));

		// "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat format = new SimpleDateFormat(formats);

		String dateTimeStr = format.format(c.getTime());

		return dateTimeStr;
	}

	public static String dateFormatFromMs(long mss) {

		Date dateTime = new Date(mss);

		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		c.setTime(dateTime);

		// "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat format = new SimpleDateFormat(
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL);

		String dateTimeStr = format.format(c.getTime());

		return dateTimeStr;
	}

	/**
	 * Description：格式化消息展示列表时间
	 * 
	 * @param mss
	 * @return
	 * @return String
	 * @author name：拜力文
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 *         Modified No： Modified By： Modified Date： Modified Description:
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 **/
	public static String InfoShowdateFormat(long mss) {

		final Calendar calend = dateFormatFromString(
				dateFormatFromMs(mss,
						DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL),
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL);

		if (isToday(calend)) {
			return dateFormatFromCalender(calend, "HH:mm");
		}
		if (isYesterday(calend)) {
			return dateFormatFromCalender(calend, "昨天:HH:mm");
		}
		if (isCurrentYear(calend)) {
			return dateFormatFromCalender(calend, "MM月dd日 HH:mm");
		} else {
			return dateFormatFromCalender(calend, "yyyy年MM月dd日 HH:mm");
		}

	}

	public static String InfoShowdateFormat2(String date) {
		final Calendar calend = dateFormatFromString(date,
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL);
		if (isToday(calend)) {
			return dateFormatFromCalender(calend, "HH:mm");
		}
		if (isYesterday(calend)) {
			return dateFormatFromCalender(calend, "昨天:HH:mm");
		}
		if (isCurrentYear(calend)) {
			return dateFormatFromCalender(calend, "MM月dd日 HH:mm");
		} else {
			return dateFormatFromCalender(calend, "yyyy年MM月dd日 HH:mm");
		}
	}

	/**
	 * 方法名称: InfoClassShowdateFormat<br>
	 * @param time
	 * @return
	 */
	public static String InfoClassShowdateFormat(String time) {
		if ( TextUtils.isEmpty(time) ) {
			return "";
		}
		// 24小时内：几分钟前。。。，超过24小时：4月11日 20:20， 历史年份：2014年4月12日；
		final Calendar calend = Calendar.getInstance();
		calend.setTimeInMillis(Long.parseLong(time)*1000);
		Date newtime = new Date();
		newtime.setTime(Long.parseLong(time) * 1000);
		if (isToday(calend)) {
			long nm = 1000 * 60;
			long minPoor = getMinPoor(newtime, new Date());
			if (minPoor < nm) {
//				long second = minPoor / 1000;
				return  "刚刚";
			}
			else if (minPoor < 60 * nm) {
				return minPoor / nm + "分钟前";
			}
			else {
				return (minPoor / nm) / 60 + "小时前";
			}
		}
		else if (isCurrentYear(calend)) {
			return dateFormatFromCalender(calend, "MM月dd日 HH:mm");
		}
		else {
			return dateFormatFromCalender(calend, "yyyy年MM月dd日 HH:mm");
		}
	}
	public static SpannableString formatData(String time) {
		if ( TextUtils.isEmpty(time) ) {
			return null;
		}
		// 24小时内：几分钟前。。。，超过24小时：4月11日 20:20， 历史年份：2014年4月12日；
		final Calendar calend = dateFormatFromString(time,
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL);
		if (isToday(calend)) {
			SpannableString ss = new SpannableString("今天");
			ss.setSpan(new AbsoluteSizeSpan(18,true),0,ss.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			return ss;
		}
		else if (isYesterday(calend)) {
			SpannableString yy = new SpannableString("昨天");
			yy.setSpan(new AbsoluteSizeSpan(18,true),0,yy.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			return yy;
		}
		else {
			int day = calend.get(Calendar.DAY_OF_MONTH);
			String datStr = String.format("%02d",day);
			int month = calend.get(Calendar.MONTH)+1;
			String monthStr = getMonthForStr(month);
			SpannableString spannableString = new SpannableString(datStr+"\n"+monthStr);
			spannableString.setSpan(new AbsoluteSizeSpan(20,true),0,datStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(new AbsoluteSizeSpan(16,true),datStr.length()+1,spannableString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			return spannableString;
		}
	}
	private static String getMonthForStr(int month) {
		String monthStr = "";
		switch (month){
			case 1:
				monthStr = "一月";
				break;
			case 2:
				monthStr = "二月";
				break;
			case 3:
				monthStr = "三月";
				break;
			case 4:
				monthStr = "四月";
				break;
			case 5:
				monthStr = "五月";
				break;
			case 6:
				monthStr = "六月";
				break;
			case 7:
				monthStr = "七月";
				break;
			case 8:
				monthStr = "八月";
				break;
			case 9:
				monthStr = "九月";
				break;
			case 10:
				monthStr = "十月";
				break;
			case 11:
				monthStr = "十一月";
				break;
			case 12:
				monthStr = "十二月";
				break;
		}
		return monthStr;
	}

	/**
	 * 按分、小时、天、周，月 具体时间五个纬度统一时间
	 */
	public static Map<String, Long> formatDuring(long mss) {

		Map<String, Long> datetimeMap = new LinkedHashMap<String, Long>();

		long months = mss / (1000 * 60 * 60 * 24 * 30);
		long weeks = (mss % (1000 * 60 * 60 * 24 * 30))
				/ (1000 * 60 * 60 * 24 * 7);
		long days = (mss % (1000 * 60 * 60 * 24 * 7)) / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;

		datetimeMap.put("months", months);
		datetimeMap.put("weeks", weeks);
		datetimeMap.put("days", days);
		datetimeMap.put("hours", hours);
		datetimeMap.put("minutes", minutes);
		datetimeMap.put("seconds", seconds);

		return datetimeMap;
	}

	public static String formatDuring1(long mss) {

		long minutes = mss / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;

		if (minutes == 0) {

			return seconds + " \" ";
		}
		return minutes + " ' " + seconds + " \" ";
	}

	/**
	 * Description：判断是不是今天
	 * 
	 * @param c
	 * @return
	 * @return boolean
	 * @author name：拜力文
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 *         Modified No： Modified By： Modified Date： Modified Description:
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 **/
	public static boolean isToday(Calendar c) {
		Calendar today_start = Calendar
				.getInstance(TimeZone.getTimeZone(GMT_8));
		today_start.set(Calendar.HOUR_OF_DAY, 0);
		today_start.set(Calendar.MINUTE, 0);
		Calendar today_end = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		today_end.set(Calendar.HOUR_OF_DAY, 0);
		today_end.set(Calendar.MINUTE, 0);
		today_end.add(Calendar.DAY_OF_MONTH, 1);

		return c.before(today_end) && c.after(today_start);
	}

	/**
	 * Description：判断是不是昨天
	 * 
	 * @param c
	 * @return
	 * @return boolean
	 * @author name：拜力文
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 *         Modified No： Modified By： Modified Date： Modified Description:
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 **/
	public static boolean isYesterday(Calendar c) {
		Calendar today_start = Calendar
				.getInstance(TimeZone.getTimeZone(GMT_8));
		today_start.set(Calendar.HOUR_OF_DAY, 0);
		today_start.set(Calendar.MINUTE, 0);
		today_start.add(Calendar.DAY_OF_MONTH, -1);
		Calendar today_end = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		today_end.set(Calendar.HOUR_OF_DAY, 0);
		today_end.set(Calendar.MINUTE, 0);

		return c.before(today_end) && c.after(today_start);
	}

	/**
	 * Description：判断是不是进年
	 * 
	 * @param c
	 * @return
	 * @return boolean
	 * @author name：拜力文
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 *         Modified No： Modified By： Modified Date： Modified Description:
	 *         <p>=
	 *         ===========================================
	 *         </p>
	 **/
	public static boolean isCurrentYear(Calendar c) {
		Calendar today_start = Calendar
				.getInstance(TimeZone.getTimeZone(GMT_8));
		today_start.set(Calendar.HOUR_OF_DAY, 0);
		today_start.set(Calendar.MINUTE, 0);
		today_start.set(Calendar.MONTH, 0);
		today_start.set(Calendar.DAY_OF_MONTH, 1);
		today_start.set(Calendar.SECOND, 0);
		System.out.println(dateFormatFromCalender(today_start,
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL));
		Calendar today_end = Calendar.getInstance(TimeZone.getTimeZone(GMT_8));
		today_end.set(Calendar.HOUR_OF_DAY, 23);
		today_end.set(Calendar.MINUTE, 59);
		today_end.set(Calendar.MONTH, 11);
		today_end.set(Calendar.DAY_OF_MONTH, 31);
		today_end.set(Calendar.SECOND, 59);
		System.out.println(dateFormatFromCalender(today_end,
				DateFormateUtil.FORMAT_FULL_DATE_TIME_WITH_SYMBOL));
		return c.before(today_end) && c.after(today_start);
	}

	/**
	 * 方法名称: getMinPoor<br>
	 * 描述：得到两个时间相隔了多少分钟 作者: 赵亚男 修改日期：2015-7-6上午10:08:13
	 * 
	 * @param endDate
	 * @param nowDate
	 * @return
	 */
	public static long getMinPoor(Date endDate, Date nowDate) {
		if(endDate == null){
			return 0;
		}
		boolean result = compareDate(endDate, nowDate);

		Date compare;

		if (!result) {
			compare = nowDate;
			nowDate = endDate;
			endDate = compare;
		}
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		long min = diff / nm;
		// 计算差多少秒//输出结果
		// long sec = diff / ns;
		return /* min */diff;
	}

	public static boolean compareDate(Date date1, Date date2) {
		int result = date1.compareTo(date2);
		if (result > 0) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static Date string2Date(String dateStr, String format) {
		if (TextUtils.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		Date date = null;
		try {
			if (dateStr.indexOf(":") == -1) {
				dateStr += " 00:00:00";
			}
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {

			Log.e("log", "转换日期发生错误：日期字符串(" + dateStr + ")和日期格式(" + format
					+ ")不匹配！");
		}
		return date;
	}

	public static String date2String(Date date, String format) {
		if (date == null)
			return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
}
