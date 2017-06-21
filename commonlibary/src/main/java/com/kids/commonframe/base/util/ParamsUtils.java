package com.kids.commonframe.base.util;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mychao on 2016/10/29.
 */

public class ParamsUtils {
    /**
     * 构造参数工具类
     * @param object
     * @return
     */
    public static Map<String,String> buildParam(Object object) {
        Map<String,String> paramMap = new HashMap<>();
        Class<?> classInstance = object.getClass();
        Field[] fileList = classInstance.getDeclaredFields();
        for (Field filed:fileList) {
            String fidldName = filed.getName();
            try {
                Method method = classInstance.getDeclaredMethod("get"+fidldName.substring(0, 1).toUpperCase()+fidldName.substring(1));
                String value = (String)method.invoke(object);
                if(!TextUtils.isEmpty(value)) {
                    paramMap.put(fidldName, value);
                }
            } catch (Exception e) {
            }
        }
        return paramMap;
    }
}
