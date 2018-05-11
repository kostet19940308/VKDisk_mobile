package com.vkdisk.konstantin.vkdisk_mobile.interceptors;

import android.content.Context;

import com.vkdisk.konstantin.vkdisk_mobile.CookieStore;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by nagai on 11.05.2018.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            cookies.addAll(originalResponse.headers("Set-Cookie"));
//            CookieStore.setCookies(context, cookies);
        }
        return originalResponse;
    }
}
