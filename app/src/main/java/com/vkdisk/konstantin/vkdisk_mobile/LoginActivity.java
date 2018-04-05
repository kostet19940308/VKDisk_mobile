package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.SessionApi;

import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getSimpleName();


    private WebView webView;
    private String loginUrl;
    private String cookies;
    private String redirectUrl;
    private String csrf;
    private String session;
    private String store;
    private String cookie_key;

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
        redirectUrl = getString(R.string.redirect_url);
        store = getString(R.string.cookie_store);
        cookie_key = getString(R.string.cookie);
    }

    @Override
    protected void onResume() {
        super.onResume();
        URI uri = URI.create(loginUrl);
        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new OAuthWebClient());
        webView.setWebChromeClient(new android.webkit.WebChromeClient() {});
        webView.loadUrl(uri.toString());
    }

    private class OAuthWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url){

            if(url.startsWith(redirectUrl)){
                url.indexOf("#");
                String substring = url.substring(url.indexOf("#")+1, url.length());
                String[] urls = substring.split("&");
                final String code = urls[0].split("=")[1];
                final String state = urls[1].split("=")[1];
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
//                        .addInterceptor(interceptor)
                        .addNetworkInterceptor(interceptor)
                        .followRedirects(false)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(webView.getResources().getString(R.string.basic_url))
                        .client(client)
                        .build();
                SessionApi getSession = retrofit.create(SessionApi.class);
                getSession.getSession(code, state, cookies).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        session = response.headers().toMultimap().get("Set-Cookie").get(0);
                        csrf = response.headers().toMultimap().get("Set-Cookie").get(1);
                        cookies += "; " + session.substring(0, session.indexOf(";"))  + "; " + csrf.substring(0, csrf.indexOf(";"));
                        Log.d(LOG_TAG, cookies);
                        SharedPreferences pref = getSharedPreferences(store, MODE_PRIVATE);
                        pref.edit().putString(cookie_key, cookies).apply();
                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder()
                                .addInterceptor(interceptor)
//                                .addNetworkInterceptor(interceptor)
                                .build();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(webView.getResources().getString(R.string.basic_url))
                                .client(client)
                                .build();
                        DocumentApi documentApi = retrofit.create(DocumentApi.class);
                        documentApi.getAllDocument(cookies, csrf.substring(csrf.indexOf("=") + 1, csrf.indexOf(";"))).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d(LOG_TAG, String.valueOf(response.code()));
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(LOG_TAG, t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });

                //return true;
            }
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            cookies = CookieManager.getInstance().getCookie(loginUrl);
            Log.d(LOG_TAG, "All the cookies in a string:" + cookies);
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
