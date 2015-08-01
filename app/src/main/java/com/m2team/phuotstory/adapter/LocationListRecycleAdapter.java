package com.m2team.phuotstory.adapter;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.m2team.phuotstory.R;
import com.m2team.phuotstory.model.MyLocation;
import com.m2team.phuotstory.model.Story;

import net.qiujuer.genius.widget.GeniusTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hoang Minh on 7/27/2015.
 */
public class LocationListRecycleAdapter extends BaseAdapter {
    ArrayList<MyLocation> mLocations;
    Context mContext;

    public LocationListRecycleAdapter(Context context, ArrayList<MyLocation> locations) {
        mLocations = locations;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mLocations.size();
    }

    @Override
    public MyLocation getItem(int position) {
        return mLocations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.layout_each_location, parent, false);
            vh = new ViewHolder();
            vh.tv_title = (GeniusTextView) convertView.findViewById(R.id.tv_location);
            vh.btn_drag = (ImageButton) convertView.findViewById(R.id.ic_drag);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.tv_title.setText(getItem(position).getAddress());
        vh.btn_drag.setImageResource(R.drawable.ic_delete_black_48dp);
        vh.btn_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocations.remove(getItem(position));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    static class ViewHolder  {
        GeniusTextView tv_title;
        ImageButton btn_drag;

    }
}


