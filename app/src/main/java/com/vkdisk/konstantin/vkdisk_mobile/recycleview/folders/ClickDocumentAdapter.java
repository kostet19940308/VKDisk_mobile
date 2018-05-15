package com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemViewHolder;

import java.util.List;

/**
 * Created by nagai on 15.05.2018.
 */

public class ClickDocumentAdapter extends DocumentItemRecyclerAdapter implements View.OnLongClickListener {

    private final OnItemLongClickListener onLongClickListener;

    public ClickDocumentAdapter(LayoutInflater inflater, List<Document> data, ClickDocumentAdapter.OnItemLongClickListener onLongClickListener) {
        super(inflater, data);
        this.onLongClickListener = onLongClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    @Override
    public boolean onLongClick(View view) {
        Integer position = (Integer)view.getTag();
        onLongClickListener.onItemLongClick(view, position);
        return true;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.itemView.setOnLongClickListener(this);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setTag(position);
    }
}
