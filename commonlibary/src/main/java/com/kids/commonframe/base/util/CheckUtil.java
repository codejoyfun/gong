package com.kids.commonframe.base.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class CheckUtil {

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}

	public static boolean isMobileNumber(String mobiles) {
		/*
		 * 修改起来就是11位数字
		 */
		String telRegex = "^1[\\d]{10}$";
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}

	/**
	 * 验证邮编格式
	 */
	public static boolean isPostNO(String postno) {

		String telRegex = "^[1-9]\\d{5}$";
		if (TextUtils.isEmpty(postno)) {
			return false;
		} else {
			return postno.matches(telRegex);
		}
	}

	/**
	 * 验证身份证号
	 */
	public static boolean isIdCode(String idcode) {
		String telRegex = "(\\d{14}[0-9xX])|(\\d{17}[0-9xX])";
		if (TextUtils.isEmpty(idcode)) {
			return false;
		} else {
			return idcode.matches(telRegex);
		}
	}

	/**
	 * 汉字人名验证
	 */
	public static boolean isTrueName(String name) {
		String telRegex = "(^[\u4e00-\u9fa5]{2,4}$)|(^[\u4e00-\u9fa5]{1,4}[•][\u4e00-\u9fa5]{1,4}$)";
		if (TextUtils.isEmpty(name)) {
			return false;
		} else {
			return name.matches(telRegex);
		}
	}

	/**
	 * 验证字符窜是否是纯数字，也就是可转为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
