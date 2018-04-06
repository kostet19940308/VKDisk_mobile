/**
 * Copyright Mail.ru Games (c) 2015
 * Created by y.bereza.
 */
package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vkdisk.konstantin.vkdisk_mobile.R;

import java.util.Objects;

public class ItemViewHolder extends RecyclerView.ViewHolder {
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setText(String text) {
		mText.setText(text);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void setIcon(String type) {
		if (Objects.equals(type, "folder")) {
			mIcon.setImageResource(R.mipmap.folder);
		} else if (Objects.equals(type, "document")) {
		    mIcon.setImageResource(R.mipmap.document);
        }
	}

	private ImageView mIcon;
	private TextView mTitle;
	private TextView mText;

	public ItemViewHolder(View itemView) {
		super(itemView);

		mTitle = (TextView)itemView.findViewById(R.id.title);
		mText = (TextView)itemView.findViewById(R.id.text);
		mIcon = (ImageView)itemView.findViewById(R.id.icon);
	}
}
