package com.school.huozi.wifiHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zhan on 2016/7/12.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mAccountEdit;
    private EditText mPasswordEdit;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mLogin = (Button) findViewById(R.id.login);
        mAccountEdit = (EditText) findViewById(R.id.account);
        mPasswordEdit = (EditText) findViewById(R.id.password);

        mPrefs = getSharedPreferences("login_info", MODE_PRIVATE);
        boolean isLogin = mPrefs.getBoolean("login_flg", false);
        if (isLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrefs = getSharedPreferences("login_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("login_flg", true);
                String account = mAccountEdit.getText().toString();
                String password = mPasswordEdit.getText().toString();
                editor.putString("account", account);
                editor.putString("password", password);
                editor.apply();
                // 跳转
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String account = mPrefs.getString("account", "");
        String password = mPrefs.getString("password", "");
        mAccountEdit.setText(account);
        mPasswordEdit.setText(password);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPrefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("account", mAccountEdit.getText().toString());
        editor.putString("password", mPasswordEdit.getText().toString());
        editor.apply();
    }
}
