package com.jojo.metroapp.utils.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.metroapp.R;

public class HistoryRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView title, datePublished;
    public ImageView imageView;

    HistoryRecyclerViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.frameHistoryListTitle);
        datePublished = itemView.findViewById(R.id.frameHistoryListDatePublished);
        imageView = itemView.findViewById(R.id.frameHistoryListImage);
    }
}
