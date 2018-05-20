package com.vkdisk.konstantin.vkdisk_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getSimpleName();
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = (Button) findViewById(R.id.login);
        LoginClickListner clickListner = new LoginClickListner();
        login.setOnClickListener(clickListner);

    }

    private class LoginClickListner implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(LoginActivity.createAuthActivityIntent(getApplicationContext()));
            finish();
        }
    }
}
