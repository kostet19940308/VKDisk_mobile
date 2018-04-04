/**
 * Copyright Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vkdisk.konstantin.vkdisk_mobile.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setText(String text) {
		mText.setText(text);
	}

	//public void setIcon()

	private ImageView mIcon;
	private TextView mTitle;
	private TextView mText;

	public ItemViewHolder(View itemView) {
		super(itemView);

		mTitle = (TextView)itemView.findViewById(R.id.title);
		mText = (TextView)itemView.findViewById(R.id.text);
	}
}
