package io.vov.vitamio.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by mike on 2017/9/15.
 */

public class NumberUtil {
    /**
     * 如果是整数就返回，小数的话保留两位
     * @param price
     * @return
     */
    public static String getIOrD(String price){
        if (isInteger(price)){
            return price;
        }else{
            String doubleStr = formatTwoBit(price);
            int index = doubleStr.indexOf(".");
            if (doubleStr.substring(index+1).equals("00")){
                return doubleStr.substring(0,index);
            }else{
                return doubleStr;
            }
        }
    }

    public static String getIOrD(double price){
        return getIOrD(String.valueOf(price));
    }

    public static boolean isInteger(String str){
        Pattern pattern= Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String formatTwoBit(String price) {
        if(TextUtils.isEmpty(price)) {
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return decimalFormat.format(Double.parseDouble(price));
    }
    public static String formatOneBit(String price) {
        if(TextUtils.isEmpty(price)) {
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.0");
        return decimalFormat.format(Double.parseDouble(price));
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
