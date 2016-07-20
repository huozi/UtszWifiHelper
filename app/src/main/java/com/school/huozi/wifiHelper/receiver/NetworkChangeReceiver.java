package com.school.huozi.wifiHelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.school.huozi.wifiHelper.utils.HttpUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhan on 2016/7/5.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    private static final int CONN_CODE = 1;
    private static final int SIGN_CODE = 2;

    private static Context mContext;
    private final MHandler mHandler = new MHandler(NetworkChangeReceiver.this);


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (!readAccount()) {
            return; // 用户未提前登记wifi账号和密码
        }

        // 网络连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (!"wifi".equalsIgnoreCase(type)) {
                return;
            }
            checkConnect(); // 开启新线程测试是否已验证portal.
        } else {
            Toast.makeText(context, "Network is unavailable.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    // 构造请求参数
    public static final Map<String, String> PARAMS_MAP =
            new HashMap<String, String>(){{
                put("action", "login");
                put("username", "***");
                put("password", "***");
                put("ac_id", "1");
                put("wbaredirect", "");
                put("user_mac", "");
                put("user_ip", "");
                put("nas_ip", "");
                put("save_me", "1");
                put("ajax", "1");
            }};


    /**
     * 读取用户登记的wifi账号和密码，并设置portal验证的请求参数
     * @return 如果用户没有提前登记账号和密码，返回false
     */
    private boolean readAccount() {
        // 读取登记账号
        SharedPreferences pref = mContext.getSharedPreferences("login_info",
                Context.MODE_PRIVATE);
        boolean isLogin = pref.getBoolean("login_flg", false);

        if (!isLogin) {
            return false;
        }
        PARAMS_MAP.put("username", pref.getString("account", ""));
        PARAMS_MAP.put("password", pref.getString("password", ""));

        return true;
    }


    /**
     * 登入校园网账号
     */
    private void signIn() {
        final String signUrl = "http://10.0.10.66/include/auth_action.php";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = HttpUtil.sendPostRequest(signUrl,
                        new HashMap<String, String>(), PARAMS_MAP);
                Message msg = new Message();
                msg.what = SIGN_CODE;
                Bundle bundle = new Bundle();
                bundle.putBoolean("state", resp.startsWith("login_ok"));
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    /**
     * 开启新线程测试是否已验证portal
     */
    private void checkConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean state = HttpUtil.checkConnect();
                Message msg = new Message();
                msg.what = CONN_CODE;
                Bundle bundle = new Bundle();
                bundle.putBoolean("state", state);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }).start();
    }


    // 静态Handler类，防止内存泄漏
    private static class MHandler extends  Handler {
        private WeakReference<NetworkChangeReceiver> mReceiver;

        public MHandler(NetworkChangeReceiver receiver) {
            mReceiver = new WeakReference<>(receiver);
        }

        @Override
        public void handleMessage(Message msg) {
            NetworkChangeReceiver NcReceiver = mReceiver.get();
            switch (msg.what) {
                case CONN_CODE:
                    boolean state = msg.getData().getBoolean("state");
                    if (state) {
                        Toast.makeText(NetworkChangeReceiver.mContext,
                                "Network is available.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 进行portal验证
                        NcReceiver.signIn();
                    }
                    break;
                case SIGN_CODE:
                    boolean signState = msg.getData().getBoolean("state");
                    String tips = signState ? "available." : "unavailable.";
                    Toast.makeText(NetworkChangeReceiver.mContext,
                            "WIFI is " + tips, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
