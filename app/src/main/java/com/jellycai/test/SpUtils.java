package com.jellycai.test;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具操作类
 */
public class SpUtils {

    /**
     * 保存数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 保存数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 保存数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveValue(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 读取数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getStringValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 读取数据
     *
     * @param context
     * @param key
     * @return
     */
    public static int getIntegerValue(Context context, String key,int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 读取数据
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBooleanValue(Context context,String key,boolean defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

}
