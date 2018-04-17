package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by nagai on 06.04.2018.
 */

public class ClickDocumentRecycleAdapter extends ItemDocumentRecycleAdapter implements View.OnClickListener, View.OnLongClickListener{

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position) throws JSONException;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position) throws JSONException;
    }

    private final OnItemClickListener mClickListener;
    private final OnItemLongClickListener mLongClickListener;

    public ClickDocumentRecycleAdapter(LayoutInflater inflater, ClickDocumentRecycleAdapter.OnItemClickListener listener, ClickDocumentRecycleAdapter.OnItemLongClickListener longListener, JSONArray array) throws JSONException {
        super(inflater);
        Log.d(LOG_TAG, String.valueOf(array));
        mClickListener = listener;
        mLongClickListener = longListener;
        jsonArray = array;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);

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
        try {
            mClickListener.onItemClick(v, position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Integer position = (Integer)view.getTag();
        try {
            mLongClickListener.onItemLongClick(view, position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
