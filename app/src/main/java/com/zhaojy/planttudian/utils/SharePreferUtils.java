package com.zhaojy.planttudian.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author: zhaojy
 * @data:On 2018/9/28.
 */

public class SharePreferUtils {
    public final static String ROOT_NAME = "planttudian";
    /**
     * 用户手机号
     */
    public final static String USER_PHONE = "userPhone";

    private static SharedPreferences sp;

    /**
     * 通过key获得数据
     *
     * @return
     */
    public static String getString(Context context, String key) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    /**
     * 通过key获得数据
     *
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 通过key获得数据
     *
     * @return
     */
    public static int getInt(Context context, String key) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }


    /**
     * 通过key存储数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void storeDataByKey(Context context, String key, String value) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 通过key存储数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void storeDataByKey(Context context, String key, boolean value) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 通过key存储数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void storeDataByKey(Context context, String key, int value) {
        sp = context.getSharedPreferences(ROOT_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
