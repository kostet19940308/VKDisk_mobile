package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URI;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private String loginUrl;

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
        //loginUrl = "https://oauth.vk.com/authorize?response_type=code&display=page&state=5VuJZZdyMGyZHJVn0dcGwRUqRMfsQz56&redirect_uri=https%3A%2F%2Foauth.vk.com%2Fblank.html%3Fredirect_state%3D5VuJZZdyMGyZHJVn0dcGwRUqRMfsQz56&scope=email+status+docs+messages&client_id=6385626";
        loginUrl = "http://10.0.2.2:8000/social/login/vk-oauth2/";
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
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
            view.loadUrl(url);
            return false;
        }

        public void onReceivedError(WebView view, int errorCode, String description, String  failingUrl) {
            Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }

    }
}
