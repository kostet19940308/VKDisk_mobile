package com.vkdisk.konstantin.vkdisk_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;

public class ListActivity extends AppCompatActivity implements
        ClickRecyclerAdapter.OnItemClickListener{

    private RecyclerView mRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);

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
