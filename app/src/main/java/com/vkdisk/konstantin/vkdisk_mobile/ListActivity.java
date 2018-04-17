package com.vkdisk.konstantin.vkdisk_mobile;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.fragments.DocumentLisFragment;
import com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderListFragment;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.ChatApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getSimpleName();
    Fragment folderList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String cookies = pref.getString(getString(R.string.cookie), "");
        final String csrf = pref.getString(getString(R.string.csrf), "");

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .client(client)
                .build();
        final ChatApi chatApi = retrofit.create(ChatApi.class);
        chatApi.getAllChats(cookies, csrf.substring(csrf.indexOf("=") + 1, csrf.indexOf(";"))).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(LOG_TAG, String.valueOf(response.code()));
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.valueOf(new JSONObject(response.body().string())));
//                    bundle.putString("cookies", cookies);
//                    bundle.putString("csrf", csrf);
                    FragmentManager fm = getSupportFragmentManager();
                    folderList = new FolderListFragment();
                    folderList.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment, folderList, "fragmentList").commit();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(LOG_TAG, t.getMessage());
            }
        });
    }

}
