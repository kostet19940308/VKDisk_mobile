package com.vkdisk.konstantin.vkdisk_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getSimpleName();
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();
//        CookieManager cookieManager = new CookieManager();
//        CookieHandler.setDefault(cookieManager);
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//        client.setCookieHandler(cookieManager);

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/accounts/oauth_urls/")
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.d(LOG_TAG, response.headers().toString());
                Log.d(LOG_TAG, response.body().string());
                HttpUrl url = HttpUrl.parse("http://10.0.2.2:8000/accounts/oauth_urls/");
                Log.d(LOG_TAG, url.toString());
                Log.d(LOG_TAG, client.cookieJar().loadForRequest(url).toString());
            }
        });
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
