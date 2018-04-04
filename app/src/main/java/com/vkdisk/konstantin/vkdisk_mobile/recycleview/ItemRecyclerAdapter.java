/**
 * Copyright Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.R;

import java.lang.ref.WeakReference;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
	public static final String[] sItems = {"lorem", "ipsum", "dolor", "sit", "amet", "consectetuer", "adipiscing", "elit",
			"morbi", "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel", "erat", "placerat", "ante",
			"porttitor", "sodales", "pellentesque", "augue", "purus"};

	private final WeakReference<LayoutInflater> mInflater;

	public ItemRecyclerAdapter(LayoutInflater inflater) {
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

	@Override
	public void onBindViewHolder(ItemViewHolder holder, int position) {
		holder.setText(sItems[position]);
		holder.setTitle(sItems[position].substring(0,1).toUpperCase());
	}

	@Override
	public int getItemCount() {
		return sItems.length;
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}
}
