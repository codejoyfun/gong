package com.kids.commonframe.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.kids.commonframe.config.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SPUtils {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "sharefile_data";
    public static final String FILE_KEY_HOST = "file_key_host";
    public static final String FILE_KEY_DB_NAME = "X-Odoo-Db";
    public static final String FILE_KEY_LOGIN_CONFLICT = "file_key_login_conflict";
    public static final String FILE_KEY_PLACE_ORDER_CACHE = "file_key_place_order_cache";
    public static final String FILE_KEY_COMPANY_NAME = "file_key_company_name";
    public static final String FILE_KEY_USER_INFO = "userInfo";
    public static final String FILE_KEY_PASSWORD = "password";
    public static final String FILE_KEY_SELF_ORDER_ELAPSED_TIME = "file_key_self_order_elapsed_time";
    public static final String FILE_KEY_ALWAYS_ORDER_ELAPSED_TIME = "file_key_always_order_elapsed_time";
    public static final String FILE_KEY_SMART_ORDER_ELAPSED_TIME = "file_key_smart_order_elapsed_time";
    public static final String FILE_KEY_VERSION_PRODUCT_LIST = "FILE_KEY_VERSION_PRODUCT_LIST";
    public static final String FILE_KEY_PRODUCT_CATEGORY_LIST = "FILE_KEY_PRODUCT_CATEGORY_LIST";
    public static final String FILE_KEY_FIRST_LOAD_PRODUCT_LIST = "file_key_first_load_product_list";



    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {
        if (object == null) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    /**
     * desc:保存对象
     *
     * @param context
     * @param key
     * @param obj     要保存的对象，只能保存实现了serializable的对象
     */
    public static void saveObject(Context context, String key, Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            put(context, key, bytesToHexString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    private static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param context
     * @param key
     * @return modified:
     */
    public static Object readObject(Context context, String key) {
        try {
            if (contains(context, key)) {
                String string = (String) get(context, key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    public static boolean isLogin(Context context) {
        return (boolean) SPUtils.get(context, "mLogin", false);
    }

    public static void setLogin(Context context, boolean isLogin) {
        SPUtils.put(context, "mLogin", isLogin);
    }

    public static boolean isLoginConflict(Context context) {
        return (boolean) SPUtils.get(context, FILE_KEY_LOGIN_CONFLICT, false);
    }

    public static void setLoginConflict(Context context, boolean isLogin) {
        SPUtils.put(context, FILE_KEY_LOGIN_CONFLICT, isLogin);
    }

    public static void loginOut(Context context) {
        SPUtils.put(context, "sign", "");
        SPUtils.put(context, "mLogin", false);
        SPUtils.put(context, FILE_KEY_USER_INFO, "");
        SPUtils.put(context, FILE_KEY_DB_NAME, "LBZ-Golive-01");
        SPUtils.put(context, FILE_KEY_HOST, "");
        Constant.BASE_URL = Constant.UNLOGIN_URL;
        SPUtils.put(context, FILE_KEY_PLACE_ORDER_CACHE, "");
        SPUtils.put(context, FILE_KEY_VERSION_PRODUCT_LIST, 0);
    }

    /**
     * 是否是第一次运行
     *
     * @param context
     * @return
     */
    public static boolean firstLaunch(Context context) {
        if (TextUtils.isEmpty((String) SPUtils.get(context, "firstlaunch", ""))) {
            return true;
        }
        return false;
    }

    public static void setFirstLaunch(Context context, boolean first) {
        if (first) {
            SPUtils.put(context, "firstlaunch", "first");
        }
    }

    /**
     * 是否是第一次运行
     *
     * @param context
     * @return
     */
    public static boolean firstLaunchInfo(Context context) {
        if (TextUtils.isEmpty((String) SPUtils.get(context, "firstlaunch1", ""))) {
            SPUtils.put(context, "firstlaunch1", "first");
            return true;
        }
        return false;
    }


    /**
     * desc:保存对象
     *
     * @param preferences
     * @param key
     * @param obj     要保存的对象，只能保存实现了serializable的对象
     */
    public static void saveObject(SharedPreferences preferences, String key, Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(obj);
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            preferences.edit().putString(key,bytesToHexString).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * desc:获取保存的Object对象
     *
     * @param preferences
     * @param key
     * @return modified:
     */
    public static Object readObject(SharedPreferences preferences, String key) {
        try {
            String string = preferences.getString(key,null);
            if (TextUtils.isEmpty(string)) {
                return null;
            } else {
                //将16进制的数据转为数组，准备反序列化
                byte[] stringToBytes = StringToBytes(string);
                ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                ObjectInputStream is = new ObjectInputStream(bis);
                //返回反序列化得到的对象
                Object readObject = is.readObject();
                return readObject;
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}