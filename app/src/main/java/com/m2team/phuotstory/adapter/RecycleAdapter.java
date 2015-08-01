package com.m2team.phuotstory.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m2team.phuotstory.R;
import com.m2team.phuotstory.model.Story;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hoang Minh on 7/27/2015.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    ArrayList<Story> mStories;
    Context mContext;

    public RecycleAdapter(Context context, ArrayList<Story> stories) {
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
            //ViewHolder.tv_road_name.setText(story.get());
            //ViewHolder.tv_travel_time.setText(story.get());
        }
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


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}


