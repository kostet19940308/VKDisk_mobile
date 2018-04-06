/**
 * Copyright Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;

public class ClickRecyclerAdapter extends ItemRecyclerAdapter implements View.OnClickListener {
	public interface OnItemClickListener {
		void onItemClick(View view, int position) throws JSONException;
	}

	private final OnItemClickListener mClickListener;

	public ClickRecyclerAdapter(LayoutInflater inflater, OnItemClickListener listener, JSONArray array) throws JSONException {
		super(inflater);
		Log.d(LOG_TAG, String.valueOf(array));
		mClickListener = listener;
		jsonArray = array;
	}

	@Override
	public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ItemViewHolder holder = super.onCreateViewHolder(parent, viewType);
		holder.itemView.setOnClickListener(this);

		return holder;
	}

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
}
