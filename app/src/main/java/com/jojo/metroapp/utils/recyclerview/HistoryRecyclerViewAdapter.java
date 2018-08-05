package com.jojo.metroapp.utils.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jojo.metroapp.R;
import com.jojo.metroapp.activity.DetailFormUserActivity;
import com.jojo.metroapp.model.FormModel;

import java.util.ArrayList;

import static com.jojo.metroapp.config.config.BK_ALASAN_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_DARI_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_PUBLISHED_FORM;
import static com.jojo.metroapp.config.config.BK_DATE_SAMPAI_FORM;
import static com.jojo.metroapp.config.config.BK_IMAGE_URL_FORM;
import static com.jojo.metroapp.config.config.BK_STATUS_FORM;
import static com.jojo.metroapp.config.config.BK_TITLE_FORM;
import static com.jojo.metroapp.utils.utils.setImageWithGlide;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewHolder> {

    private Context context;
    private ArrayList<FormModel> arrayList;

    public HistoryRecyclerViewAdapter(Context context, ArrayList<FormModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.frame_history_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryRecyclerViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getTitleForm());
        holder.datePublished.setText(arrayList.get(position).getDatePublished());
        setImageWithGlide(context, holder.imageView, arrayList.get(position).getImageUrl(), 2);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailFormUserActivity.class)
                .putExtra(BK_IMAGE_URL_FORM, arrayList.get(holder.getAdapterPosition()).getImageUrl())
                .putExtra(BK_TITLE_FORM, arrayList.get(holder.getAdapterPosition()).getTitleForm())
                .putExtra(BK_ALASAN_FORM, arrayList.get(holder.getAdapterPosition()).getAlasan())
                .putExtra(BK_DATE_PUBLISHED_FORM, arrayList.get(holder.getAdapterPosition()).getDatePublished())
                .putExtra(BK_DATE_DARI_FORM, arrayList.get(holder.getAdapterPosition()).getDateDari())
                .putExtra(BK_DATE_SAMPAI_FORM, arrayList.get(holder.getAdapterPosition()).getDateSampai())
                .putExtra(BK_STATUS_FORM, arrayList.get(holder.getAdapterPosition()).getStatus()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
