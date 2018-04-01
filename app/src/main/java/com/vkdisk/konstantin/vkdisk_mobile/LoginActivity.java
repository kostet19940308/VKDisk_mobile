package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.retrofit.SessionApi;

import java.io.IOException;
import java.net.URI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getSimpleName();

    private WebView webView;
    private String loginUrl;
    private String cookie;

    public static Intent createAuthActivityIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        loginUrl = getString(R.string.auth_url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        URI uri = URI.create(loginUrl);
        webView.setWebViewClient(new OAuthWebClient());
        webView.loadUrl(uri.toString());
    }

    private class OAuthWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d(LOG_TAG, url);
            if(url.startsWith(webView.getResources().getString(R.string.redirect_url))){
                url.indexOf("#");
                String substring = url.substring(url.indexOf("#")+1, url.length());
                String[] urls = substring.split("&");
                final String code = urls[0].split("=")[1];
                String state = urls[1].split("=")[1];
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(webView.getResources().getString(R.string.basic_url))
                        .build();
                SessionApi getSession = retrofit.create(SessionApi.class);
                getSession.getSession(code, state).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                         Log.d(LOG_TAG, response.headers().toString());
                         cookie = response.headers().get("Set-Cookie");
                         Log.d(LOG_TAG, cookie);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });

            }
            view.loadUrl(url);
            return false;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String  failingUrl) {
            Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

    }
}
