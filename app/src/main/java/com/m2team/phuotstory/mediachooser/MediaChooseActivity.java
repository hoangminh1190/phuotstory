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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.activity.WriteActivity;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaChooseActivity extends AppCompatActivity {

    GridView gridView;
    MediaGridViewAdapter adapter;
    FloatingActionButton floatingDelete;
    ArrayList<String> pathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
toolbar.setLogo(R.drawable.ic_add_white_48dp);
        toolbar.setNavigationIcon(R.drawable.ic_add_white_48dp);
        toolbar.setNavigationContentDescription(R.string.add);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaChooseActivity.this, BucketHomeFragmentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        floatingDelete = (FloatingActionButton) findViewById(R.id.floating_delete);
        floatingDelete.setVisibility(View.INVISIBLE);
        floatingDelete.setColorNormal(Color.WHITE);
        floatingDelete.setIcon(R.drawable.ic_delete_black_24dp);
        floatingDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> deleteList = adapter.deleteList;
                if (deleteList != null && deleteList.size() > 0) {
                    for (String path : deleteList) {
                        pathList.remove(path);
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MediaChooseActivity.this, getString(R.string.removed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MediaChooseActivity.this, getString(R.string.choose_to_delete), Toast.LENGTH_SHORT).show();
                }
                floatingDelete.setVisibility(View.INVISIBLE);
                TranslateAnimation animation = new TranslateAnimation(0, 100, 0, 0);
                animation.setDuration(100);
                floatingDelete.setAnimation(animation);
                adapter.clearCheckedIcon();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String uris = Utils.getPrefString(this, Constant.PREF_PHOTO_LIST_URI);
            if (!TextUtils.isEmpty(uris)) {
                String[] split = uris.split(",");
                if (split.length > 0)
                    pathList = new ArrayList<>(Arrays.asList(split));
            } else pathList = new ArrayList<>();
            ArrayList<String> extra = intent.getStringArrayListExtra(Constant.ARRAY_PHOTO_URI);
            if (extra != null && extra.size() > 0) {
                for (String path : extra) {
                    if (!pathList.contains(path)) {
                        pathList.add(path);
                    }
                }
                String join = TextUtils.join(",", pathList);
                Utils.putPrefValue(this, Constant.PREF_PHOTO_LIST_URI, join);
                setAdapter();
            }
        }

    }


    private void setAdapter() {
        if (adapter == null) {
            adapter = new MediaGridViewAdapter(MediaChooseActivity.this, 0, pathList, floatingDelete, gridView, false);
            gridView.setAdapter(adapter);
        } else {
            adapter.addAll(pathList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mediachooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_photo_done) {
            Intent intent = new Intent(this, WriteActivity.class);
            intent.putExtra(Constant.ACTION_TYPE , Constant.TYPE_PHOTO);
            intent.putExtra(Constant.ARRAY_PHOTO_URI, pathList);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
