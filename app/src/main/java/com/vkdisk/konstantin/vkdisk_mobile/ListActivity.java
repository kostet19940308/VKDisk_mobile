package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;

public class ListActivity extends AppCompatActivity implements
        ClickRecyclerAdapter.OnItemClickListener{

    public final String LOG_TAG = this.getClass().getSimpleName();
    private RecyclerView mRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        SharedPreferences pref = getSharedPreferences(getString(R.string.cookie_store), MODE_PRIVATE);
        Log.d(LOG_TAG, pref.getString(getString(R.string.cookie), ""));

        mRecyclerView.setAdapter(new ClickRecyclerAdapter(getLayoutInflater(), this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setHasFixedSize(true);
        setContentView(mRecyclerView);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, ItemRecyclerAdapter.sItems[position], Toast.LENGTH_SHORT).show();
    }
}
