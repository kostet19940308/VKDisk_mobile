package com.vkdisk.konstantin.vkdisk_mobile.interceptors;

import android.content.Context;
import android.util.Log;

import com.vkdisk.konstantin.vkdisk_mobile.CookieStore;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nagai on 11.05.2018.
 */

public class AddCookiesInterceptor implements Interceptor {
    private Context context;
    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookies = CookieStore.getCookies(context);
        String csrf = CookieStore.getCsrf(context);
        builder.addHeader("Cookie", cookies);
        builder.addHeader("X-CSRFtoken", csrf);
        return chain.proceed(builder.build());
    }
}
