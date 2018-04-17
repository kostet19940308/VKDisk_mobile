package com.vkdisk.konstantin.vkdisk_mobile;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vkdisk.konstantin.vkdisk_mobile.fragments.DocumentLisFragment;
import com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderListFragment;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.ChatApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentRootApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentRootFilterApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public final String LOG_TAG = this.getClass().getSimpleName();
    Fragment folderList;
    Toolbar toolbar;
    private NavigationView navigationView;
    SharedPreferences pref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        pref =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadFilterDocuments("");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.all_items) {
            loadFilterDocuments("");
        } else if (id == R.id.dialogs) {
            loadChats("");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG, query);
                if(Objects.equals(folderList.getTag(), getString(R.string.document_list))) {
                    loadFilterDocuments(query);
                } else if (Objects.equals(folderList.getTag(), getString(R.string.chat_list))) {
                    loadChats(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                return true;
            }
        });

        return true;
    }

    private void loadChats(String filter) {
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
        chatApi.getAllChats(filter, cookies, csrf.substring(csrf.indexOf("=") + 1, csrf.indexOf(";"))).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(LOG_TAG, String.valueOf(response.code()));
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.valueOf(new JSONObject(response.body().string())));
                    FragmentManager fm = getSupportFragmentManager();
                    fm.popBackStack();
                    folderList = new FolderListFragment();
                    folderList.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment, folderList, getString(R.string.chat_list)).commit();
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

    private void loadFilterDocuments(String filter) {
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
        final DocumentRootFilterApi documentRootFilterApi = retrofit.create(DocumentRootFilterApi.class);
        documentRootFilterApi.getAllFilterDocuments(filter, cookies, csrf.substring(csrf.indexOf("=") + 1, csrf.indexOf(";"))).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(LOG_TAG, String.valueOf(response.code()));
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.valueOf(new JSONObject(response.body().string())));
                    FragmentManager fm = getSupportFragmentManager();
                    fm.popBackStack();
                    folderList = new DocumentLisFragment();
                    folderList.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment, folderList, getString(R.string.document_list)).commit();
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
