package com.m2team.phuotstory.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hoang Minh on 7/28/2015.
 */
public class FetchAddressService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchAddressService() {
        super("FetchAddressService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        // Get the location passed to this service through an extra.
        LatLng location = intent.getParcelableExtra(
                Constant.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(Constant.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(Constant.TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(Constant.TAG, errorMessage + ". " +
                    "Latitude = " + location.latitude +
                    ", Longitude = " +
                    location.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(Constant.TAG, errorMessage);
            }
            deliverResultToReceiver(Constant.FAILURE_RESULT, errorMessage, location);
        } else {
            Address address = addresses.get(0);
            String result = Common.getAddress(address);
            Log.i(Constant.TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constant.SUCCESS_RESULT, result, location);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, LatLng latLng) {
        if (mReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.RESULT_DATA_KEY, message);
            bundle.putDoubleArray(Constant.RESULT_DATA_LOCATION, new double[]{latLng.latitude, latLng.longitude});
            mReceiver.send(resultCode, bundle);
        }
    }

}
