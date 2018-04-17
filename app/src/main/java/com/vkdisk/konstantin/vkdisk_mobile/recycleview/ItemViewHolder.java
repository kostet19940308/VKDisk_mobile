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
import org.apache.commons.io.FilenameUtils;

import com.vkdisk.konstantin.vkdisk_mobile.R;

import java.util.Objects;

public class ItemViewHolder extends RecyclerView.ViewHolder {
	String title;
	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setText(String text) {
		this.title = text;
		mText.setText(text);
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void setIcon(String type) {
		if (Objects.equals(type, "folder")) {
			mIcon.setImageResource(R.mipmap.folder);
		} else if (Objects.equals(type, "document")) {
			String ext = FilenameUtils.getExtension(title);
			switch (ext) {
				case "doc":
					mIcon.setImageResource(R.mipmap.docx);
					break;
				case "docx":
					mIcon.setImageResource(R.mipmap.docx);
					break;
				case "xls":
					mIcon.setImageResource(R.mipmap.xls);
					break;
				case "pdf":
					mIcon.setImageResource(R.mipmap.pdf);
					break;
				case "png":
					mIcon.setImageResource(R.mipmap.png);
					break;
				case "sql":
					mIcon.setImageResource(R.mipmap.sql);
					break;
				case "c":
					mIcon.setImageResource(R.mipmap.c);
					break;
				case "djvu":
					mIcon.setImageResource(R.mipmap.djvu);
					break;
				case "zip":
					mIcon.setImageResource(R.mipmap.zip);
					break;
				case "gif":
					mIcon.setImageResource(R.mipmap.gif);
					break;
				case "ppt":
					mIcon.setImageResource(R.mipmap.pptx);
					break;
				case "pptx":
					mIcon.setImageResource(R.mipmap.pptx);
					break;
				case "tex":
					mIcon.setImageResource(R.mipmap.tex);
					break;
				case "jpg":
					mIcon.setImageResource(R.mipmap.jpg);
					break;
				default:
					mIcon.setImageResource(R.mipmap.document);
			}
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
