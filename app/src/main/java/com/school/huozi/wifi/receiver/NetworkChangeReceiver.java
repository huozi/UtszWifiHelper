package com.school.huozi.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.school.huozi.wifi.utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhan on 2016/7/5.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{

    private static final int CONN_CODE = 1;
    private static final int SIGN_CODE = 2;

    private Context mContext;


    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_CODE:
                    boolean state = msg.getData().getBoolean("state");
                    if (state) {
                        Toast.makeText(mContext, "Network is available.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        signIn();
                    }
                    break;
                case SIGN_CODE:
                    boolean signState = msg.getData().getBoolean("state");
                    String tip = signState ? "good." : "bad.";
                    Toast.makeText(mContext, "Network is " + tip,
                            Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (!"wifi".equalsIgnoreCase(type)) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean state = HttpUtil.checkConnect();
                    Message msg = new Message();
                    msg.what = CONN_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("state", state);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }).start();
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
                put("ac_id", "3");
                put("type", "1");
                put("wbaredirect", "");
                put("mac", "");
                put("user_ip", "");
                put("pop", "1");
                put("is_ldap", "1");
                put("nas_init_port", "1");
            }};


    /**
     * 登入校园网账号
     */
    private  void signIn() {
        final String signUrl = "http://10.0.10.66/cgi-bin/srun_portal";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String resp = HttpUtil.sendPostRequest(signUrl,
                        new HashMap<String, String>(), PARAMS_MAP);
                Log.d("", resp);
                Message msg = new Message();
                msg.what = SIGN_CODE;
                Bundle bundle = new Bundle();
                bundle.putBoolean("state", "login_ok".equalsIgnoreCase(resp));
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }}
