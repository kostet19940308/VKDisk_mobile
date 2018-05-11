package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import java.net.HttpCookie;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nagai on 11.05.2018.
 */

public class CookieStore {
    public static String getCookies(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(context.getString(R.string.cookie), "");
    }

    public static String getCsrf(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(context.getString(R.string.csrf), "");
    }

    public static void setCookies(Context context, String cookies) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
//        for (HttpCookie cookie : cookies) {
//            editor.putStringSet(context.getString(R.string.cookie), cookie).apply();
//        }
        editor.putString(context.getString(R.string.cookie), cookies).apply();
    }

    public static void setCsrf(Context context, String csrf) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(context.getString(R.string.csrf), csrf).apply();
    }
}
