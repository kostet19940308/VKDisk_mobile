package com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders;

import android.graphics.Color;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
        holder.setIcon("document");
        if (folder.getIsChecked()) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setNewData(List<Document> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteCheckedFiles() {
        mData = mData.stream().filter(s -> !s.getIsChecked()).collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public void setUnChecked() {
        for (Document document:mData) {
            if (document.getIsChecked()) {
                document.setChecked();
            }
        }
        notifyDataSetChanged();
    }

    public void setChecked(int position) {
        mData.get(position).setChecked();
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getCheckedCount() {
        return (int) mData.stream().filter(Document::getIsChecked).count();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
