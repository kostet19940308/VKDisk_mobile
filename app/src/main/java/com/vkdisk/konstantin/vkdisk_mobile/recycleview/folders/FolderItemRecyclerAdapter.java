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
import java.util.List;

public class FolderItemRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private final WeakReference<LayoutInflater> mInflater;
    private List<Folder> mData;

    public FolderItemRecyclerAdapter(LayoutInflater inflater, List<Folder> data) {
        mInflater = new WeakReference<>(inflater);
        mData = data;
    }

    @Override
    public int getItemViewType(int position) {
        Folder folder = mData.get(position);
        return folder.getIntType();
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
        Folder folder = mData.get(position);
        holder.setText(folder.getTitle());
        holder.setIcon("folder");
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

    public void setNewData(List<Folder> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public int getFolderId(int position) {
        return mData.get(position).getId();
    }

    public void createFolder(Folder folder) {
        mData.add(folder);
//        notifyItemInserted(mData.size() - 1);
        notifyDataSetChanged();
    }

    public int getId(int position) {
        return mData.get(position).getId();
    }

    public String getTitle(int position) {
        return mData.get(position).getTitle();
    }

    public void setChecked(int position) {
        mData.get(position).setChecked();
        notifyDataSetChanged();
    }

    public int getCheckedCount() {
        int count = 0;
        for (Folder item: mData) {
            if (item.getIsChecked()) {
                ++count;
            }
        }
        return count;
    }

    public void setUnChecked() {
        for (Folder document:mData) {
            if (document.getIsChecked()) {
                document.setChecked();
            }
        }
        notifyDataSetChanged();
    }

    public void updateFolder(Folder content, int updatePosition) {
        mData.set(updatePosition, content);
        notifyItemChanged(updatePosition);
    }

    public void deleteFolder(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }
}

