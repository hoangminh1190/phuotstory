/*
 * Copyright (c) 2013 Akexorcist
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.m2team.phuotstory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.model.MyLocation;

import net.qiujuer.genius.widget.GeniusButton;
import net.qiujuer.genius.widget.GeniusTextView;

import org.w3c.dom.Document;

import java.util.ArrayList;

import app.akexorcist.gdaplibrary.GoogleDirection;
import app.akexorcist.gdaplibrary.GoogleDirection.OnAnimateListener;
import app.akexorcist.gdaplibrary.GoogleDirection.OnDirectionResponseListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DirectionActivity extends FragmentActivity {
    @Bind(R.id.start_address)
    GeniusTextView tv_start_address;
    @Bind(R.id.distance)
    GeniusTextView tv_distance;
    @Bind(R.id.buttonAnimate)
    FloatingActionButton buttonAnimate;
    @Bind(R.id.btn_choose)
    GeniusButton btn_done;
    TextView textProgress;
    GoogleMap mMap;
    GoogleDirection gd;
    int totalDistance;
    ArrayList<MyLocation> locations;

    int index = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        ButterKnife.bind(this);
        final ArrayList<LatLng> latLngs = new ArrayList<>();
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        textProgress = (TextView) findViewById(R.id.textProgress);
        textProgress.setVisibility(View.GONE);

        buttonAnimate.setColorNormalResId(R.color.white);
        buttonAnimate.setIcon(R.drawable.ic_action_direction);
        buttonAnimate.setTitle(getString(R.string.animation));
        buttonAnimate.setSize(FloatingActionButton.SIZE_NORMAL);
        buttonAnimate.setEnabled(false);
        buttonAnimate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gd.animateDirection(mMap, latLngs, GoogleDirection.SPEED_VERY_FAST
                        , true, false, true, true
                        , new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motor2))
                        , false, true, null);
            }
        });
        totalDistance = 0;

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            locations = extras.getParcelableArrayList(Constant.ARRAY_LOCATION);
            if (locations != null && locations.size() > 0) {
                index = 0;
                final int locSize = locations.size();
                gd = new GoogleDirection(this);

                mMap.clear();
                MyLocation sLoc = locations.get(0);
                MyLocation eLoc = locations.get(locations.size() - 1);
                if (eLoc.equals(sLoc)) {
                    eLoc = locations.get(locations.size() - 2);
                }
                mMap.addMarker(new MarkerOptions().position(newLatLong(sLoc))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_start)));

                mMap.addMarker(new MarkerOptions().position(newLatLong(eLoc))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action)));

                MyLocation temp1, temp2;
                for (int i = 0; i < locations.size() - 1; i++) {
                    temp1 = locations.get(i);
                    temp2 = locations.get(i + 1);
                    latLngs.add(newLatLong(temp1));
                    gd.request(newLatLong(temp1), newLatLong(temp2), GoogleDirection.MODE_DRIVING);
                    /*if (i > 0 && i < locations.size() - 1)
                        mMap.addMarker(new MarkerOptions().position(newLatLong(temp1))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_between)));*/
                }
                latLngs.add(newLatLong(eLoc));
                latLngs.add(newLatLong(sLoc));
                //request from last to first location
                gd.request(newLatLong(eLoc), newLatLong(sLoc), GoogleDirection.MODE_DRIVING);

                final ArrayList<String> addressList = new ArrayList<>();

                gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
                    public void onResponse(String status, Document doc, GoogleDirection gd) {
                        index++;
                        String startAddress = gd.getStartAddress(doc);

                        if (!addressList.contains(startAddress))
                            addressList.add(startAddress);
                        totalDistance += gd.getTotalDistanceValue(doc);
                        mMap.addPolyline(gd.getPolyline(doc, 5, R.color.md_blue_600));
                        if (index == locSize) {
                            buttonAnimate.setEnabled(true);
                            tv_start_address.setText(TextUtils.join("->", addressList));
                            tv_distance.setText("Total distance: " + (totalDistance / 1000) + " km");
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (LatLng latLng : latLngs) {
                                builder.include(latLng);
                            }
                            LatLngBounds bounds = builder.build();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                            //clusterMarker(mMap, locations);
                        }
                    }
                });

                gd.setOnAnimateListener(new OnAnimateListener() {
                    public void onStart() {
                        textProgress.setVisibility(View.VISIBLE);
                    }

                    public void onProgress(int progress, int total) {
                        textProgress.setText(progress + " / " + total);
                    }

                    public void onFinish() {
                        buttonAnimate.setEnabled(true);
                        textProgress.setVisibility(View.GONE);
                    }
                });

            } else {
                Toast.makeText(this, getString(R.string.fail_data), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.fail_data), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_choose)
    public void done() {
        Intent intent = new Intent();
        intent.putExtra(Constant.ARRAY_LOCATION, locations);
        setResult(RESULT_OK, intent);
        finish();
    }

    private LatLng newLatLong(MyLocation location) {
        return new LatLng(location.getLat(), location.getLng());
    }

    public void onPause() {
        super.onPause();
        gd.cancelAnimated();
    }

    private void clusterMarker(GoogleMap gMap, ArrayList<MyLocation> locations) {
        ClusterManager<MyLocation> clusterManager = new ClusterManager<MyLocation>(this, gMap);
        clusterManager.addItems(locations);
        clusterManager.cluster();
    }
}
