package com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemViewHolder;

import org.json.JSONException;

import java.util.List;

/**
 * Created by nagai on 13.05.2018.
 */

public class ClickFolderAdapter extends FolderItemRecyclerAdapter implements View.OnClickListener{


    public ClickFolderAdapter(LayoutInflater inflater, List<Folder> data, OnItemClickListener mClickListener) {
        super(inflater, data);
        this.mClickListener = mClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private final OnItemClickListener mClickListener;


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.itemView.setOnClickListener(this);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer)v.getTag();
        mClickListener.onItemClick(v, position);
    }
}
