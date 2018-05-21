package com.vkdisk.konstantin.vkdisk_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.SessionApi;

import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements Storage.DataSubscriber {

    public final String LOG_TAG = this.getClass().getSimpleName();
    private final static int LOGIN_CODE = 1;


    private WebView webView;
    private String loginUrl;
    private String cookies;
    private String redirectUrl;
    private SharedPreferences pref;

    private Storage mStorage;

    public static Intent createAuthActivityIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Тут вообще по-хорошему надо чтобы при выходе из приложения, мы переходили после "Зайти через вк" в webView
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_login);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        loginUrl = getString(R.string.auth_url);
        redirectUrl = getString(R.string.redirect_url);
        mStorage = Storage.getOrCreateInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        URI uri = URI.create(loginUrl);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new OAuthWebClient(this));
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
        });
        webView.loadUrl(uri.toString());
    }

    @Override
    protected void onStop() {
        mStorage.unsubscribe(this);
        super.onStop();
        finish();
    }

    private class OAuthWebClient extends WebViewClient {

        private Storage.DataSubscriber subscriber;

        public OAuthWebClient(Storage.DataSubscriber newSubscriber) {
            subscriber = newSubscriber;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith(redirectUrl)) {
                cookies = CookieManager.getInstance().getCookie(loginUrl);

                mStorage.addCookiesFromString(URI.create(getString(R.string.basic_url)), cookies);
                Log.d(LOG_TAG, "All the cookies in a string:" + cookies);

                url.indexOf("#");
                String substring = url.substring(url.indexOf("#") + 1, url.length());
                String[] urls = substring.split("&");
                final String code = urls[0].split("=")[1];
                final String state = urls[1].split("=")[1];

                SessionApi getSession = mStorage.getRetrofit().create(SessionApi.class);
                ApiHandlerTask<ResponseBody> apiHandlerTask = new ApiHandlerTask<>(getSession.getSession(code, state), LOGIN_CODE);
                mStorage.addApiHandlerTask(apiHandlerTask, subscriber);

                return true;
            }
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

    }

    @Override
    public void onDataLoaded(int type, com.vkdisk.konstantin.vkdisk_mobile.pipline.Response response) {
        switch (type) {
            case LOGIN_CODE:
                runOnUiThread(() -> {
                    Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                    startActivity(intent);
                });
                break;

        }
    }

    @Override
    public void onDataLoadFailed() {

    }
}
