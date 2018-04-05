package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private String store;
    private String cookie_key;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = getString(R.string.cookie_store);
        cookie_key = getString(R.string.cookie);
        SharedPreferences pref = getSharedPreferences(store, MODE_PRIVATE);
        if(pref.contains(cookie_key)){
            intent = new Intent(this, ListActivity.class);
        }else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
