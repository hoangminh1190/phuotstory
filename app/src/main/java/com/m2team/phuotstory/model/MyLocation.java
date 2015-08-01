package com.m2team.phuotstory.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.m2team.phuotstory.common.Constant;

/**
 * Created by Hoang Minh on 7/30/2015.
 */
public class MyLocation implements Parcelable, ClusterItem {
    String address;
    double lat;
    double lng;

    public MyLocation() {}

    public MyLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public MyLocation(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyLocation) {
            MyLocation o = (MyLocation) obj;
            return (this.lat == o.lat && this.lng == o.lng && this.address.equalsIgnoreCase(o.address));
        }
        return false;
    }

    @Override
    public String toString() {
        return this.lat + Constant.TOKEN_EACH_VALUE + this.lng + Constant.TOKEN_EACH_VALUE + address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public MyLocation(Parcel in) {
        String[] locations = new String[1];
        double[] coors = new double[2];
        in.readStringArray(locations);
        in.readDoubleArray(coors);
        this.address = locations[0];
        this.lat = coors[0];
        this.lng = coors[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.address});
        dest.writeDoubleArray(new double[]{this.lat, this.lng});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public MyLocation createFromParcel(Parcel source) {
            return new MyLocation(source);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };

    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}
