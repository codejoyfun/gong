package com.runwise.supply.tools;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.kids.commonframe.base.util.DateFormateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @version 1.0
 * @Desc 工具类
 */

public class TimeUtils {

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static long getFormatTime(String time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.parse(time).getTime();
		} catch (Exception e) {
			return 0l;
		}
	}

	public static String getFormatTime3(long time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(new Date(time));
		} catch (Exception e) {
			return "";
		}
	}

	public static String getFormatTime(long time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
			return format.format(new Date(time));
		} catch (Exception e) {
			return "";
		}
	}

	public static long getFormatTime4(String time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
			return format.parse(time).getTime();
		} catch (Exception e) {
			return 0l;
		}
	}

	public static long getFormatTime5(String time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.parse(time).getTime();
		} catch (Exception e) {
			return 0l;
		}
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}

	public static String getCurrentTime2() {
		return getCurrentTime("yyyy-MM-dd");
	}

	public static int getDiffDays(String startDate, String sysTime) {
		long diff = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date sDate = ft.parse(startDate);
			Date mCurrDate = new Date();
			long mTime = mCurrDate.getTime();
			if (!TextUtils.isEmpty(sysTime)) {
				mTime = Long.parseLong(sysTime);
			}
			diff = sDate.getTime() - mTime;
			diff = diff / 86400000;// 1000*60*60*24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) diff;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String date) {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ft2 = new SimpleDateFormat("yyyy年MM月dd日");
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate).toString();
		} catch (ParseException e) {
			return "";
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate2(String date) {
		if(TextUtils.isEmpty(date)) {
			return "";
		}
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate).toString();
		} catch (ParseException e) {
			return "";
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate3(String date) {
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		try {
			return ft.parse(date).toString();
		} catch (ParseException e) {
			return "";
		}
	}

	public static String formatDate(String date, String pattern1, String pattern2) {
		SimpleDateFormat ft = new SimpleDateFormat(pattern1);
		SimpleDateFormat ft2 = new SimpleDateFormat(pattern2);
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate).toString();
		} catch (ParseException e) {
			return "";
		}
	}

	// 获取时间戳
	public static String getTimeStamp() { // 关于日期与时间的实现
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return sdf.format(new Date());
	}

	// 获取时间戳
	public static String getTimeStamps() { // 关于日期与时间的实现
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	public static String getTimeStamps2(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(new Date(time));
	}

	public static String getTimeStamps3(String date) {
		if(TextUtils.isEmpty(date)) {
			return "";
		}
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate).toString();
		} catch (ParseException e) {
			return date;
		}
	}
	public static String getMMdd(String date){
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ft2 = new SimpleDateFormat("MM-dd");
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate);
		} catch (ParseException e) {
			return date;
		}
	}
	public static String getMMddHHmm(String date){
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date sDate = ft.parse(date);
			return ft2.format(sDate);
		} catch (ParseException e) {
			return date;
		}
	}

	public static String getYMD(Date date){
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			return ft.format(date);
	}
	/**
	 * 计算宝宝年龄
	 *
	 * @param birthDay 出生时间
	 * @param endDay   截至时间
	 * @return
	 * @throws ParseException
	 */
	public static String babyAge(String birthDay, String endDay) throws ParseException {

		if (birthDay == null) {
			return "";
		} else if (birthDay.length() > 10) {
			birthDay = birthDay.substring(0, 10);
		} else if (birthDay.length() < 10) {
			return "";
		}

		String[] birArr = birthDay.split("-");
		if (birArr == null || birArr.length < 3) {
			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar endTime = Calendar.getInstance();
		try {
			endTime.setTime(sdf.parse(endDay));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar birthday = new GregorianCalendar(Integer.parseInt(birArr[0]), Integer.parseInt(birArr[1]) - 1, Integer.parseInt(birArr[2]));//2010年10月12日，month从0开始

		Calendar now = Calendar.getInstance();
		int day = 0;
		int month = 0;
		int year = 0;
		int timeCompare = endTime.getTime().compareTo(birthday.getTime());
		if (timeCompare == 0) {
			//等于0 出生当天
			return "出生";
		} else if (timeCompare < 0) {
			//小于0 出生之前
			//	        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
			day = birthday.get(Calendar.DAY_OF_MONTH) - endTime.get(Calendar.DAY_OF_MONTH);
			//	        int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
			month = birthday.get(Calendar.MONTH) - endTime.get(Calendar.MONTH);
			//	        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
			year = birthday.get(Calendar.YEAR) - endTime.get(Calendar.YEAR);
		} else {
			//出生之后
			//	        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
			day = endTime.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
			//	        int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
			month = endTime.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
			//	        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
			year = endTime.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
		}
		//按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
		if (day < 0) {
			month -= 1;
			now.add(Calendar.MONTH, -1);//得到上一个月，用来得到上个月的天数。
			day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (month < 0) {
			month = (month + 12) % 12;
			year--;
		}

		StringBuffer buffer = new StringBuffer();

		if (timeCompare < 0) {
			buffer.append("出生前");
			if (year != 0) {
				buffer.append(year + "年");
			}
		} else {
			if (year != 0) {
				buffer.append(year + "岁");
			}
		}

		if (month != 0) {
			buffer.append(month + "个月");
		}
		if (day != 0) {
			buffer.append(day + "天");
		}
		return buffer.toString();
	}

	//参数是几天前或后是星期几
	public static String getWeekStr(int numDay) {
		String weekStr = "";
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, numDay);//把日期往后增加一天.整数往后推,负数往前移动
		int mWay = calendar.get(Calendar.DAY_OF_WEEK);
		if (1 == mWay) {
			weekStr = "周日";
		} else if (2 == mWay) {
			weekStr = "周一";
		} else if (3 == mWay) {
			weekStr = "周二";
		} else if (4 == mWay) {
			weekStr = "周三";
		} else if (5 == mWay) {
			weekStr = "周四";
		} else if (6 == mWay) {
			weekStr = "周五";
		} else if (7 == mWay) {
			weekStr = "周六";
		}
		return weekStr;

	}

	/**
	 * 获取几天前或后的YYYY-MM-dd
	 * 参数：正数往后推，负数是往前移动
	 */
	public static String getABFormatDate(int dayNum) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, dayNum);//把日期往后增加一天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str =  sdf.format(calendar.getTime());
		return str;
	}
	public static String getAB2FormatData(int dayNum){
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, dayNum);//把日期往后增加一天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str =  sdf.format(calendar.getTime());
		return str;
	}

    //当前时间
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str =  sdf.format(date.getTime());
		return str;
	}
	//本周开始时间
	public static String getThisWeekStart() {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		dayInWeek -= 1;
		//本周第一天
		calendar.add(Calendar.DATE, -dayInWeek);//把日期往后增加一天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str =  sdf.format(calendar.getTime());
		return str;
	}
	//上周结束时间
	public static String getPerWeekEnd() {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		//本周第一天
		calendar.add(Calendar.DATE, -dayInWeek);//把日期往后增加一天.整数往后推,负数往前移动
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str =  sdf.format(calendar.getTime());
		return str;
	}
	//上周开始
	public static String getPerWeekStart() {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		//本周第一天
		calendar.add(Calendar.DATE, -dayInWeek);//把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(Calendar.DATE, -6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str =  sdf.format(calendar.getTime());
		return str;
	}

	//获取前月的第一天
	public static String getPerMonthStart() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal_1=Calendar.getInstance();//获取当前日期 
		cal_1.add(Calendar.MONTH,-1);
		cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
		return format.format(cal_1.getTime());
	}
	//获取前月的最后一天
	public static String getPerMonthEnd() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天 
		return format.format(cale.getTime());
	}

	/**
	 * 今天是否在指定的时间段内
     */
	public static boolean isTimeInner(String beginTimeStr,String endTimeStr) {
		final Calendar beginCalend = DateFormateUtil.dateFormatFromString(beginTimeStr, DateFormateUtil.FORMAT_DATE);
		final Calendar endCalend = DateFormateUtil.dateFormatFromString(endTimeStr, DateFormateUtil.FORMAT_DATE);
		Calendar today = Calendar.getInstance();
        if(today.before(endCalend) && today.after(beginCalend)) {
			return  true;
		}
		return false;
	}
	public static boolean isMoreThan7Days(String timeStr) {
		if (TextUtils.isEmpty(timeStr)){
			return false;
		}
		long time = getFormatTime(timeStr);
		Calendar calendar = Calendar.getInstance();  //得到日历
		calendar.setTime(new Date());//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -7);  //设置为7天前
		Date before7days = calendar.getTime();   //得到7天前的时间
		if (before7days.getTime() < time){
			return false;
		}
		return true;
	}
}
