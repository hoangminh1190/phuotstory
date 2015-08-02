/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package com.m2team.phuotstory.mediachooser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.learnncode.mediachooser.fragment.VideoFragment;
import com.m2team.phuotstory.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaGridViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mediaFilePathList;
    LayoutInflater viewInflater;
    FloatingActionButton floating_delete;
    public ArrayList<String> deleteList = new ArrayList<>();
    private GridView gridView;
    private boolean mIsSmallGridview;


    public MediaGridViewAdapter(Context context, int resource, List<String> filePathList,
                                FloatingActionButton float_delete, GridView gView,
                                boolean isSmallGridView) {
        super(context, resource, filePathList);
        mediaFilePathList = filePathList;
        mContext = context;
        viewInflater = LayoutInflater.from(mContext);
        if (!isSmallGridView) {
            floating_delete = float_delete;
            floating_delete.setVisibility(View.INVISIBLE);
        }
        gridView = gView;
        mIsSmallGridview = isSmallGridView;
    }

    public int getCount() {
        return mediaFilePathList.size();
    }

    @Override
    public String getItem(int position) {
        return mediaFilePathList.get(position);
    }


    public void addAll(List<String> mediaFile) {
        if (mediaFile != null) {
            int count = mediaFile.size();
            for (int i = 0; i < count; i++) {
                if (mediaFilePathList.contains(mediaFile.get(i))) {

                } else {
                    mediaFilePathList.add(mediaFile.get(i));
                }
            }
        }
    }

    public List<String> getMediaFilePathList() {
        return mediaFilePathList;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            convertView = viewInflater.inflate(R.layout.view_grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromGridItemRowView);
            holder.icChecked = (ImageView) convertView.findViewById(R.id.ic_check);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icChecked.setVisibility(View.INVISIBLE);

        if (!mIsSmallGridview) {
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.icChecked.setVisibility(View.VISIBLE);
                    if (floating_delete.getVisibility() == View.INVISIBLE) {
                        floating_delete.setVisibility(View.VISIBLE);
                        TranslateAnimation animation = new TranslateAnimation(100, 0, 0, 0);
                        animation.setDuration(100);
                        floating_delete.setAnimation(animation);
                    }
                    if (!deleteList.contains(mediaFilePathList.get(position)))
                        deleteList.add(mediaFilePathList.get(position));
                    return true;
                }
            });
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearCheckedIcon();
                    if (floating_delete.getVisibility() == View.VISIBLE) {
                        floating_delete.setVisibility(View.INVISIBLE);
                        TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 0);
                        animation.setDuration(100);
                        floating_delete.setAnimation(animation);
                    }
                    deleteList.clear();
                }
            });
        }
        File mediaFile = new File(mediaFilePathList.get(position));

        if (mediaFile.exists()) {
            if (mediaFile.getPath().contains("mp4") || mediaFile.getPath().contains("wmv") ||
                    mediaFile.getPath().contains("avi") || mediaFile.getPath().contains("3gp")) {
                holder.imageView.setBackgroundResource(R.drawable.ic_video);

            } else {
                if (!mIsSmallGridview)
                Picasso.with(mContext).load(new File(mediaFile.getAbsolutePath()))
                        .resizeDimen(R.dimen.photo_width, R.dimen.photo_height)
                        .into(holder.imageView)
                ;
                else
                    Picasso.with(mContext).load(new File(mediaFile.getAbsolutePath()))
                            .resizeDimen(R.dimen.small_photo_dimen, R.dimen.small_photo_dimen)
                            .into(holder.imageView)
                            ;
            }

            holder.nameTextView.setText(mediaFile.getName());

        }

        return convertView;
    }


    public void clearCheckedIcon() {
        for (int i = 0 ; i < gridView.getChildCount(); i++) {
            View childAt = gridView.getChildAt(i);
            View viewById = childAt.findViewById(R.id.ic_check);
            if (viewById != null) {
                viewById.setVisibility(View.INVISIBLE);
            }
        }
    }

    class ViewHolder {
        ImageView imageView;
        ImageView icChecked;
        TextView nameTextView;
    }

}