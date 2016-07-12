package com.school.huozi.wifiHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mResetBtn = (Button) findViewById(R.id.reset);

        TextView mAccount = (TextView) findViewById(R.id.username);
        mPref = getSharedPreferences("login_info", MODE_PRIVATE);
        boolean isLogin = mPref.getBoolean("login_flg", false);
        if (isLogin) {
            mAccount.setText(mPref.getString("account", ""));
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除登录账号
                mPref = getSharedPreferences("login_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = mPref.edit();
                editor.clear();
                editor.apply();

                // 清楚缓存账号
                mPref = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor otherEdt = mPref.edit();
                otherEdt.clear();
                otherEdt.apply();

                // 跳转
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
