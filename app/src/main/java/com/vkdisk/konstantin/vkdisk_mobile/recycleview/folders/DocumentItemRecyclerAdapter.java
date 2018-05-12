package com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

public class DocumentItemRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private final WeakReference<LayoutInflater> mInflater;
    private List<Document> mData;

    public DocumentItemRecyclerAdapter(LayoutInflater inflater, List<Document> data) {
        mInflater = new WeakReference<>(inflater);
        mData = data;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = mInflater.get();
        if (inflater != null) {
            return new ItemViewHolder(inflater.inflate(R.layout.list_of_documents, parent, false));
        }
        else {
            throw new RuntimeException("Oooops, looks like activity is dead");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Document folder = mData.get(position);
        holder.setText(folder.getTitle());
        holder.setIcon("folder");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setNewData(List<Document> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
