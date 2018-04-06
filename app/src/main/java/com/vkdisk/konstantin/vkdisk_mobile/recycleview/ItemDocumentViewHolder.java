package com.vkdisk.konstantin.vkdisk_mobile.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nagai on 06.04.2018.
 */

public class ItemDocumentViewHolder extends RecyclerView.ViewHolder{
    public void setTitle(String title) {
        mTitle.setText(title);
    }
    public void setText(String text) {
        mText.setText(text);
    }

    private TextView mTitle;
    private TextView mText;

    public ItemDocumentViewHolder(View itemView) {
        super(itemView);
    }
}
