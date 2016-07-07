package com.school.huozi.wifiHelper.utils;

/**
 * Created by zhan on 2016/7/5.
 */
public class Utils {

    /**
     * 判断字符串是否为空
     * @param str 待检测字符串
     */
    public static boolean isBlank(String str) {
        return (str == null || str.isEmpty() || str.trim().equals("")
                || str.trim().toLowerCase().equals("null"));
    }
}
