package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.EditText;
import android.widget.TextView;

import com.vkdisk.konstantin.vkdisk_mobile.fragments.ChatViewFragment;
import com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderViewFragment;

import java.util.Objects;

public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public final String LOG_TAG = this.getClass().getSimpleName();
    public static final String FOLDER_ID_BUNDLE_KEY = "folder_id";

    FolderViewFragment folderList;
    ChatViewFragment chatList;
    String fragmentName;
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
    MenuItem cross;
    MenuItem checked;
    MenuItem trash;
    MenuItem addItem;
    String sort;
    String filter;
    private Storage mStorage;
    private int checkCount;

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
//                loadData();
                changeData();
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
//                loadData();
                changeData();
            }
        });
        sortNameItem.setActionView(titleDate);
    }

    private void setAdd() {
        final EditText editText = (EditText) addItem.getActionView();
        editText.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setFilter() {
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortItem.setVisible(false);
                addItem.setVisible(false);
                toggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortItem.setVisible(true);
                        addItem.setVisible(true);
                        searchView.setIconified(true);
                        searchView.setIconified(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        toggle.setDrawerIndicatorEnabled(true);
                    }
                });
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG, query);
                filter = query;
//                loadData();
                changeData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sortItem.setVisible(true);
                addItem.setVisible(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.setDrawerIndicatorEnabled(true);
                return false;
            }
        });
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
        cross = menu.findItem(R.id.cross);
        checked = menu.findItem(R.id.checked);
        trash = menu.findItem(R.id.trash);
        addItem = menu.findItem(R.id.action_add);

        setSort();
        setFilter();


        sortNameItem.setVisible(false);
        sortDateItem.setVisible(false);
        sortDateArrowItem.setVisible(false);
        sortNameArrowItem.setVisible(false);
        cross.setVisible(false);
        checked.setVisible(false);
        trash.setVisible(false);
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
        } else if (item.equals(trash) && Objects.equals(fragmentName, getString(R.string.document_list))) {
            folderList.deleteFiles();
            setCheckCount(0);
        } else if (item.equals(cross) && Objects.equals(fragmentName, getString(R.string.document_list))) {
            folderList.setUnChecked();
            setCheckCount(0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortToolbar(boolean isSort) {
        searchItem.setVisible(!isSort);
        addItem.setVisible(!isSort);
        sortNameItem.setVisible(isSort);
        sortDateItem.setVisible(isSort);
        sortNameArrowItem.setVisible(isSort);
        sortDateArrowItem.setVisible(isSort);
        getSupportActionBar().setDisplayShowTitleEnabled(!isSort);
    }

    private void loadData() {
        if (Objects.equals(fragmentName, getString(R.string.document_list))) {
            // в scheduler
            loadFilterDocuments();
        } else if (Objects.equals(fragmentName, getString(R.string.chat_list))) {
            loadChats();
        }
    }

    private void changeData() {
        if (Objects.equals(fragmentName, getString(R.string.document_list))) {
            folderList.sendRequestForData();
        } else if (Objects.equals(fragmentName, getString(R.string.chat_list))) {
            chatList.sendRequestForData();
        }
    }

    private void loadChats() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        chatList = new ChatViewFragment();
        transaction.replace(R.id.fragment, chatList, getString(R.string.chat_list));
        transaction.commit();
        fragmentName = getString(R.string.chat_list);
    }

    private void loadFilterDocuments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        folderList = new FolderViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FOLDER_ID_BUNDLE_KEY, 0);
        folderList.setArguments(bundle);
        transaction.replace(R.id.fragment, folderList, getString(R.string.document_list));
        transaction.commit();
        fragmentName = getString(R.string.document_list);
    }

    public String getSort() {
        return sort;
    }

    public String getFilter() {
        return filter;
    }

    public String getReverse() {
        return isDateReverse || isNameReverse ? "reverse" : null;
    }

    public void setFolderList(FolderViewFragment folderList) {
        this.folderList = folderList;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public void setCheckCount(int checkCount) {
        this.checkCount = checkCount;
        sortNameItem.setVisible(false);
        sortDateItem.setVisible(false);
        sortDateArrowItem.setVisible(false);
        sortNameArrowItem.setVisible(false);
        searchItem.setVisible(this.checkCount == 0);
        sortItem.setVisible(this.checkCount == 0);
        addItem.setVisible(this.checkCount == 0);
        cross.setVisible(this.checkCount != 0);
        checked.setVisible(this.checkCount != 0);
        trash.setVisible(this.checkCount != 0);
        checked.setTitle(String.format("CHECKED %s FILES", this.checkCount));
        getSupportActionBar().setDisplayShowTitleEnabled(this.checkCount == 0);
    }
}
