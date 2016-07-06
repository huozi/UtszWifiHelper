package com.school.huozi.wifi.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by zhan on 2016/7/4.
 */
public class HttpUtil {

    /**
     * 判断是否连接上互联网
     */
    public static boolean checkConnect() {
        // 个人觉得使用miui这个链接有失效的风险，访问百度主页验证200状态码亦可
        // 因为连接有portal的wifi没通过验证访问互联网时一般是302跳转到登录页面
        final String checkUrl = "http://connect.rom.miui.com/generate_204";
        final int SOCKET_TIMEOUT_MS = 1000;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(checkUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(SOCKET_TIMEOUT_MS);
            connection.setReadTimeout(SOCKET_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.connect();
            Log.d("HttpUtil", String.valueOf(connection.getResponseCode()));
            return connection.getResponseCode() == 204;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /**
     * 发送http post请求
     * @param address 链接url
     * @param headerMap 请求头map
     * @param paramsMap 参数map
     */
    public static String sendPostRequest(final String address,
                                         Map<String, String> headerMap, Map<String, String> paramsMap) {

        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Const.SOCKET_TIMEOUT_MS);
            connection.setReadTimeout(Const.SOCKET_TIMEOUT_MS);
            // 设置默认的请求头[来自配置文件***.properties]
//            setHeaders(connection);
            // 设置特殊请求头
            for (String header_name : headerMap.keySet()) {
                connection.setRequestProperty(header_name,
                        headerMap.get(header_name));
            }
            // 发送参数
            String params  = urlEncode(paramsMap);
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());

            // 接收响应内容
            InputStream in = connection.getInputStream();
            String encoding = connection.getContentEncoding();
            // 判断返回的信息是否已编码
            if(!Utils.isBlank(encoding) && encoding.contains("gzip")) {
                in = new GZIPInputStream(in);
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }


    /**
     * 从配置文件中设置请求头
     * @author 詹亮名
     * @date:2016-6-4上午11:39:21
     */
//    private static void setHeaders(HttpURLConnection connection) {
//
//        String headerName = Const.HEADER;
//        if(Utils.isBlank(headerName)) {
//            System.out.println("Warning: No \"header_name\" " +
//                    "in configuration file.");
//            return;
//        }
//        // 获取需要设置的request header
//        headerName = headerName.substring(1, headerName.length()-1);
//        String [] headers = headerName.split(",");
//
//        for (String header : headers) {
//            header = header.trim();
//            connection.setRequestProperty(header, CfgUtil.get(header));
//        }
//    }

    /**
     * 将参数map转换成url参数形式: k1=v1&k2=v2...
     * @param paramsMap map类型的参数
     */
    private static String urlEncode(Map<String, String> paramsMap) {
        if (paramsMap.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String param : paramsMap.keySet()) {
            builder.append(param);
            builder.append("=");
            builder.append(paramsMap.get(param));
            builder.append("&");
        }
        // 除去最后多余的 &
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
