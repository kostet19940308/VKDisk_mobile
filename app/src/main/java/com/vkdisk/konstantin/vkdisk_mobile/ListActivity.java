package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.NavigationView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vkdisk.konstantin.vkdisk_mobile.fragments.DocumentLisFragment;
import com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderListFragment;
import com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderViewFragment;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
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

public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Storage.DataSubscriber {

    public final String LOG_TAG = this.getClass().getSimpleName();
    private final static int LOAD_CHATS_TASK = 1;
    private final static int LOAD_FILTERED_DOCS_TASK = 2;

    Fragment folderList;
    Toolbar toolbar;
    private NavigationView navigationView;
    SharedPreferences pref;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    private boolean isNameSort = true;
    private boolean isNameReverse = false;
    private boolean isDateReverse = false;
    MenuItem sortItem;
    MenuItem searchItem;
    MenuItem sortNameItem;
    MenuItem sortDateItem;
    MenuItem sortNameArrowItem;
    MenuItem sortDateArrowItem;
    String sort;
    String filter;
    private Storage mStorage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sort = "created";
        filter = "";

        mStorage = Storage.getOrCreateInstance(getApplicationContext());
        // Эту загрузку надо вынести в scheduler
        loadFilterDocuments();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();

        if (id == R.id.all_items) {
            //в scheduler
            loadFilterDocuments();
        } else if (id == R.id.dialogs) {
            // в scheduler
            loadChats();
        } else if (id == R.id.exit) {
            pref.edit().clear().apply();
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        // Тут надо где-то добавить кнопку,
        // чтобы если мы находимся на фрагменте со списком файлов в чате
        // (а в перспективе еще и списком папок в папках),
        // то место hamburger menu надо чтобы появлялась стрелка
    }

    public void setSort() {
        // Это сортировка. Возможно это можно как-то более-грамотно сделать..
        // Например, перенести в scheduler. Хотя ей и тут не так плохо
        TextView titleName = new TextView(this.getApplicationContext());
        TextView titleDate = new TextView(this.getApplicationContext());
        if (!isNameSort) {
            SpannableString contentName = new SpannableString(getString(R.string.name));
            contentName.setSpan(new UnderlineSpan(), 0, contentName.length(), 0);
            titleName.setText(contentName);
            titleDate.setText(getString(R.string.date));
        } else {
            SpannableString contentName = new SpannableString(getString(R.string.date));
            contentName.setSpan(new UnderlineSpan(), 0, contentName.length(), 0);
            titleDate.setText(contentName);
            titleName.setText(getString(R.string.name));
        }
        titleName.setTextColor(Color.WHITE);
        titleName.setTypeface(null, Typeface.BOLD);
        titleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNameSort) {
                    isNameSort = false;
                    setSort();
                    sort = "title";
                    isDateReverse = false;
                    sortDateArrowItem.setIcon(R.drawable.sort_direct);
                } else {
                    if (isNameReverse) {
                        sortNameArrowItem.setIcon(R.drawable.sort_direct);
                    } else {
                        sortNameArrowItem.setIcon(R.drawable.sort_reverse);
                    }
                    isNameReverse = !isNameReverse;
                }
                // в scheduler
                loadData();
            }
        });
        sortDateItem.setActionView(titleName);
        titleDate.setTextColor(Color.WHITE);
        titleDate.setTypeface(null, Typeface.BOLD);
        titleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNameSort) {
                    isNameSort = true;
                    setSort();
                    sort = "created";
                    isNameReverse = false;
                    sortNameArrowItem.setIcon(R.drawable.sort_direct);
                } else {
                    if (isDateReverse) {
                        sortDateArrowItem.setIcon(R.drawable.sort_direct);
                    } else {
                        sortDateArrowItem.setIcon(R.drawable.sort_reverse);
                    }
                    isDateReverse = !isDateReverse;
                }
                // в scheduler
                loadData();
            }
        });
        sortNameItem.setActionView(titleDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mail, menu);
        searchItem = menu.findItem(R.id.action_search);
        sortItem = menu.findItem(R.id.action_sort);
        sortNameItem = menu.findItem(R.id.action_sort_name);
        sortDateItem = menu.findItem(R.id.action_sort_date);
        sortDateArrowItem = menu.findItem(R.id.action_sort_date_arrow);
        sortNameArrowItem = menu.findItem(R.id.action_sort_name_arrow);

        setSort();

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG, query);
                filter = query;
                loadData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                return true;
            }
        });

        sortNameItem.setVisible(false);
        sortDateItem.setVisible(false);
        sortDateArrowItem.setVisible(false);
        sortNameArrowItem.setVisible(false);
        // Тут надо добавить если выделяется хотя бы один файл, появляется возможность удалить их,
        // Переименовать, если выделен только один файл, в перспективе переместить в папку
        // Изменящиеся хрени в toolbar надо сунуть в menu_main
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(sortItem)) {
            setSortToolbar(true);
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSortToolbar(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);
                }
            });
        } else if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortToolbar(boolean isSort) {
        searchItem.setVisible(!isSort);
        sortNameItem.setVisible(isSort);
        sortDateItem.setVisible(isSort);
        sortNameArrowItem.setVisible(isSort);
        sortDateArrowItem.setVisible(isSort);
        getSupportActionBar().setDisplayShowTitleEnabled(!isSort);
    }

    private void loadData() {
        if (Objects.equals(folderList.getTag(), getString(R.string.document_list))) {
            // в scheduler
            loadFilterDocuments();
        } else if (Objects.equals(folderList.getTag(), getString(R.string.chat_list))) {
            loadChats();
        }
    }

    private void loadChats() {
        // Эту всю херню надо убрать. Это просто говноглушка

        final ChatApi chatApi = mStorage.getRetrofit().create(ChatApi.class);
        ApiHandlerTask<ResponseBody> task = new ApiHandlerTask<>(chatApi.getAllChats(
                filter,
                sort,
                (isDateReverse || isNameReverse ? "reverse" : null)), LOAD_CHATS_TASK);
        mStorage.addApiHandlerTask(task, this);
    }

    private void loadFilterDocuments() {
//        final DocumentRootFilterApi documentRootFilterApi = mStorage.getRetrofit().create(DocumentRootFilterApi.class);
//        ApiHandlerTask<ResponseBody> task = new ApiHandlerTask<>(documentRootFilterApi.getAllFilterDocuments(
//                filter,
//                sort,
//                (isDateReverse || isNameReverse ? "reverse" : null)), LOAD_FILTERED_DOCS_TASK);
//        mStorage.addApiHandlerTask(task, this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FolderViewFragment();
        transaction.replace(R.id.fragment, fragment, getString(R.string.chat_list));
        transaction.commit();
    }

    @Override
    public void onDataLoaded(int type, com.vkdisk.konstantin.vkdisk_mobile.pipline.Response response) {
        switch (type) {
            case LOAD_CHATS_TASK:
                try {
                    com.vkdisk.konstantin.vkdisk_mobile.pipline.Response<ResponseBody> castedResponse =
                            (com.vkdisk.konstantin.vkdisk_mobile.pipline.Response<ResponseBody>) response;
                    Bundle bundle = new Bundle();
                    // JSONObject надо пихать в базу данных и выгружать из нее во фрагменте
                    bundle.putString("data", String.valueOf(new JSONObject(castedResponse.content.string())));
                    FragmentManager fm = getSupportFragmentManager();
                    // На самом деле я хз, на сколько это правильно так делать.
                    // Но так фрагменты не накладываются друг на драга)
                    fm.popBackStack();
                    folderList = new FolderListFragment();
                    folderList.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment, folderList, getString(R.string.chat_list)).commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case LOAD_FILTERED_DOCS_TASK:
                try {
                    com.vkdisk.konstantin.vkdisk_mobile.pipline.Response<ResponseBody> castedResponse =
                            (com.vkdisk.konstantin.vkdisk_mobile.pipline.Response<ResponseBody>) response;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", String.valueOf(new JSONObject(castedResponse.content.string())));
                    FragmentManager fm = getSupportFragmentManager();
                    // На самом деле я хз, на сколько это правильно так делать.
                    // Но так фрагменты не накладываются друг на драга)
                    fm.popBackStack();
                    folderList = new DocumentLisFragment();
                    folderList.setArguments(bundle);
                    fm.beginTransaction().replace(R.id.fragment, folderList, getString(R.string.document_list)).commit();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onDataLoadFailed() {

    }
}
