package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;

/**
 * Created by nagai on 06.04.2018.
 */

public class ItemDocumentRecycleAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    public static JSONArray jsonArray = new JSONArray();
    public final String LOG_TAG = this.getClass().getSimpleName();

    private final WeakReference<LayoutInflater> mInflater;

    public ItemDocumentRecycleAdapter(LayoutInflater inflater) {
        mInflater = new WeakReference<LayoutInflater>(inflater);
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
        Log.d(LOG_TAG, String.valueOf(jsonArray));
        try {
            holder.setText(jsonArray.getJSONObject(position).getString("title"));
            holder.setIcon("document");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
