package com.m2team.phuotstory.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m2team.phuotstory.R;
import com.m2team.phuotstory.common.Applog;
import com.m2team.phuotstory.model.Story;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hoang Minh on 7/27/2015.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    List<Story> mStories;
    Context mContext;

    public RecycleAdapter(Context context, List<Story> stories) {
        mStories = stories;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_cardview_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Story story = mStories.get(i);
        if (story != null) {
            viewHolder.tv_title.setText(story.getTitle());
            if (!TextUtils.isEmpty(story.getShortTitleLocation()))
                viewHolder.tv_road_name.setText(story.getShortTitleLocation());
            if (story.getTravelTime() != null) {
                /*SimpleDateFormat dateFormat;
                final String format = Settings.System.getString(mContext.getContentResolver(), Settings.System.DATE_FORMAT);
                if (TextUtils.isEmpty(format)) {
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                } else {
                    dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                }*/
                viewHolder.tv_travel_time.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(story.getTravelTime()));
            }
            if (story.getDistance() != null)
                viewHolder.tv_distance.setText(story.getDistance() + " km");
            if (!TextUtils.isEmpty(story.getFeeling()))
                viewHolder.tv_feeling.setText(story.getFeeling());
            if (!TextUtils.isEmpty(story.getPreviewImageUri())) {
                File f = new File(story.getPreviewImageUri());
                if (f.exists())
                    Picasso.with(mContext).load(f)
                            .into(viewHolder.img_preview)
                            ;
                else
                    Picasso.with(mContext).load(story.getPreviewImageUri())
                            .into(viewHolder.img_preview);
            } else
                viewHolder.img_preview.setBackgroundResource(R.drawable.header);
        }
    }

    public void updateDataChanged(List<Story> stories) {
        mStories =stories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        TextView tv_title;
        @Bind(R.id.travel_time)
        TextView tv_travel_time;
        @Bind(R.id.road_name)
        TextView tv_road_name;
        @Bind(R.id.tv_feeling)
        TextView tv_feeling;
        @Bind(R.id.distance)
        TextView tv_distance;
        @Bind(R.id.img_preview)
        ImageView img_preview;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}


