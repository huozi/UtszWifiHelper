package com.school.huozi.wifiHelper.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by zhan on 2016/7/5.
 */
public class Utils {

    /**
     * 判断字符串是否为空
     * @param str
    

     * @date:2016-6-4下午11:29:31
     */
    public static boolean isBlank(String str) {
        if (str == null || str.trim().equals("") || str.isEmpty()
                || str.trim().toLowerCase().equals("null")) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串是否为固定长度的非负整数
     * @param numStr
     *            待检测字符串
     * @param digit
     *            固定位数，若<=0则表示任意位数
     */
    public static boolean isNumeric(String numStr, int digit) {

        if (numStr == null || numStr.trim().equals("")) {
            return false;
        }

        Pattern pattern;

        if (digit <= 0) {
            pattern = Pattern.compile("\\d*");
        } else {
            String lenStr = String.valueOf(digit);
            pattern = Pattern.compile("\\d{" + lenStr + "}");
        }

        return pattern.matcher(numStr).matches();
    }

    // ****************************日期处理******************************

    /**
     * 时间日期格式
     */
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dtf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");

    /** 按yyyy-MM-dd格式格式化日期 */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        } else {
            return df.format(date);
        }
    }

    /**
     * 按1900-01-31 23:00:22格式返回时间日期字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        } else {
            return dtf.format(date);
        }
    }

    /**
     * 日期字符串转日期
     * @param dateStr
     *            1900-01-31 格式
     */
    public static Date toDate(String dateStr) {
        if (dateStr != null) {
            try {
                return df.parse(dateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 日期字符串转日期
     * @param dateStr
     *            1900/01/31 格式
     */
    public static Date toDate1(String dateStr) {
        if (dateStr != null) {
            try {
                return df1.parse(dateStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据字符串返回时间日期
     * @param dateTimeStr
     *            1900-01-31 23:00:22 格式
     */
    public static Date toDateTime(String dateTimeStr) {
        if (dateTimeStr != null) {
            try {
                return dtf.parse(dateTimeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * date2 - date1 返回相差分钟数，一般要求date2 >= date1
     */
    public static int minuteSub(Date date1, Date date2) {

        if (date1==null || date2==null) {
            return 0;
        }else {
            long differ = date2.getTime() - date1.getTime();
            return safeLongToInt(differ / 1000 / 60);
        }
    }

    /**
     * date2 - date1 返回相差秒数，一般要求date2 >= date1
     */
    public static int secondSub(Date date1, Date date2) {

        if (date1==null || date2==null) {
            return 0;
        }else {
            long differ = date2.getTime() - date1.getTime();
            return safeLongToInt(differ / 1000);
        }
    }

    /**
     * 安全类型转换：long --> int
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(l
                    + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }


    /**
     * 读取文本文件并返回String类型的文件内容
     * @param fileName
     */
    public static String readFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        File file = new File(fileName);
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();
        } catch (Exception e) {
            return "";
        }
        return builder.toString();
    }

    /**
     * 将一段字符串写入文本文件
     * @param fileName
     * @param content
     */
    public static void writeFile(String fileName, String content) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileName),"UTF-8");
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
