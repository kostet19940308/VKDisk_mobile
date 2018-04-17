package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private String cookie_key;
    private Intent intent;
    private String csrf_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cookie_key = getString(R.string.cookie);
        csrf_key = getString(R.string.csrf);
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("Fakkkkk", pref.getString(cookie_key, ""));
        if(pref.contains(cookie_key) && pref.contains(csrf_key)){
            intent = new Intent(this, ListActivity.class);
        }else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
