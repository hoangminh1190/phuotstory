package com.m2team.phuotstory.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.adapter.LocationListRecycleAdapter;
import com.m2team.phuotstory.adapter.OnItemDialogListener;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.common.Utils;
import com.m2team.phuotstory.model.MyLocation;
import com.m2team.phuotstory.service.FetchAddressService;
import com.mobeta.android.dslv.DragSortListView;

import net.qiujuer.genius.Genius;
import net.qiujuer.genius.widget.GeniusEditText;
import net.qiujuer.genius.widget.GeniusTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnGeocodingListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener, OnLocationUpdatedListener {
    GoogleMap gMap;
    float currentZoom;
    LatLng mCurrentLatLng = null;
    @Bind(R.id.tv_address)
    GeniusTextView tv_address;
    @Bind(R.id.edt_search_address)
    GeniusEditText edt_search_address;
    @Bind(R.id.btn_search)
    ImageButton btn_search;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    DragSortListView dragSortListView;

    ProgressDialog progressDialog;
    SmartLocation.GeocodingControl geocoding;
    ArrayList<MyLocation> locations;
    MyLocation currentMyLocation;
    LocationListRecycleAdapter locationListRecycleAdapter;
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    MyLocation item = locations.get(from);
                    locations.remove(from);
                    locations.add(to, item);
                    locationListRecycleAdapter.notifyDataSetChanged();
                }
            };
    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    locations.remove(which);
                    locationListRecycleAdapter.notifyDataSetChanged();
                }
            };

    private void updateUI(String detailAddress, LatLng latLng) {
        gMap.clear();
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.DEFAULT_ZOOM));
        gMap.addMarker(new MarkerOptions().title(detailAddress).position(latLng).flat(true));
        mCurrentLatLng = latLng;
        currentMyLocation = new MyLocation(latLng.latitude, latLng.longitude, detailAddress);
        displayAddressOutput(detailAddress);
    }

    private void updateAddressAfterRequest(Location location) {
        Log.d(Constant.TAG, "updateAddressAfterRequest: ");
        if (location != null) {
            LatLng latLng = newLatLong(location);
            currentMyLocation = new MyLocation(latLng.latitude, latLng.longitude);
            startFetchAddressService(latLng);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.DEFAULT_ZOOM));
        } else
            Toast.makeText(MapActivity.this, getResources().getString(R.string.fail_req_location), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Genius.initialize(getApplication());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this);
        currentZoom = Constant.DEFAULT_ZOOM;
        locations = new ArrayList<>();

        setSupportActionBar(toolbar);
        toolbar.setNavigationContentDescription(getString(R.string.choose_location_in_trip));
        toolbar.setNavigationIcon(R.drawable.ic_list_white_24dp);
        toolbar.setSubtitle(getString(R.string.choose_location_in_trip));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MapActivity.this);
                dialog.setContentView(R.layout.layout_location_list_dialog);
                dialog.setTitle(R.string.list_location_choose);
                dragSortListView = (DragSortListView) dialog.findViewById(R.id.list_location);
                locationListRecycleAdapter = new LocationListRecycleAdapter(MapActivity.this, locations);
                dragSortListView.setAdapter(locationListRecycleAdapter);
                dragSortListView.setDropListener(onDrop);
                dragSortListView.setRemoveListener(onRemove);
                dragSortListView.setFloatAlpha(0.5f);

                dialog.show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (map != null) {
            gMap = map;
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.setMyLocationEnabled(true);
            map.setOnMapLongClickListener(this);
            map.setOnMyLocationButtonClickListener(this);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    currentZoom = cameraPosition.zoom;
                }
            });
            showLastLocation();
        }
    }

    private void showLastLocation() {
        Location lastLocation = SmartLocation.with(this).location().getLastLocation();
        if (lastLocation != null) {
            updateAddressAfterRequest(lastLocation);
        } else {
            startLocation();
        }
    }

    private void startLocation() {

        LocationGooglePlayServicesProvider provider = new LocationGooglePlayServicesProvider();

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location().provider(provider).start(this);
    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();
    }

    @OnClick(R.id.btn_search)
    public void searchAddress() {
        if (geocoding == null)
            geocoding = SmartLocation.with(this).geocoding();
        if (edt_search_address.getText().length() > 0) {
            showLoadingDialog();
            btn_search.setEnabled(false);
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(edt_search_address.getWindowToken(), 0);
            String keyword = edt_search_address.getText().toString();
            geocoding.add(keyword, 5).start(new OnGeocodingListener() {
                @Override
                public void onLocationResolved(String s, final List<LocationAddress> list) {
                    dismissLoadingDialog();
                    btn_search.setEnabled(true);
                    boolean equals = s.equalsIgnoreCase(edt_search_address.getText().toString());
                    if (equals && list != null && list.size() > 0) {
                        ArrayList<String> items = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            String address = Common.getAddress(list.get(i).getAddress());
                            if (!TextUtils.isEmpty(address))
                                items.add(address);
                        }

                        OnItemDialogListener listener = new OnItemDialogListener() {
                            @Override
                            public void selectedItem(int selectedIndex) {
                                String detailAddress = Common.getAddress(list.get(selectedIndex).getAddress());
                                LatLng latLng = new LatLng(list.get(selectedIndex).getLocation().getLatitude(), list.get(selectedIndex).getLocation().getLongitude());
                                updateUI(detailAddress, latLng);
                            }
                        };
                        if (items.size() > 1)
                            Utils.showSingleChoiceDialog(MapActivity.this, items, getString(R.string.result), listener);
                        else if (items.size() == 1)
                            updateUI(Common.getAddress(list.get(0).getAddress()), newLatLong(list.get(0).getLocation()));
                        else
                            Toast.makeText(MapActivity.this, getResources().getString(R.string.no_address_found), Toast.LENGTH_SHORT).show();
                    } else if (equals && list != null && list.size() == 0) {
                        Toast.makeText(MapActivity.this, getResources().getString(R.string.no_address_found), Toast.LENGTH_SHORT).show();
                    } else if (equals) {
                        Toast.makeText(MapActivity.this, getResources().getString(R.string.fail_to_search), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.input_address), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_choose)
    public void addLocationToList() {
        if (currentMyLocation != null) {
            if (TextUtils.isEmpty(currentMyLocation.getAddress())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        locations.add(currentMyLocation);
                        Toast.makeText(MapActivity.this, getString(R.string.success_choose_location), Toast.LENGTH_SHORT).show();
                        if (locations.size() == 1)
                            toolbar.setSubtitle("Total " + locations.size() + " location");
                        else toolbar.setSubtitle("Total " + locations.size() + " locations");
                        currentMyLocation = null;
                    }
                }, 1000);
            } else {
                locations.add(currentMyLocation);
                Toast.makeText(MapActivity.this, getString(R.string.success_choose_location), Toast.LENGTH_SHORT).show();
                if (locations.size() == 1)
                    toolbar.setSubtitle("Total " + locations.size() + " location");
                else toolbar.setSubtitle("Total " + locations.size() + " locations");
                currentMyLocation = null;
            }

        } else {
            Toast.makeText(this, getString(R.string.fail_to_choose_location), Toast.LENGTH_SHORT).show();
        }
    }


    public void startFetchAddressService(LatLng latlng) {
        //check connection
        if (Common.isOnline(MapActivity.this)) {
            Intent intent = new Intent(this, FetchAddressService.class);
            intent.putExtra(Constant.LOCATION_DATA_EXTRA, latlng);
            intent.putExtra(Constant.RECEIVER, new AddressResultReceiver(null));
            startService(intent);
        } else {
            Toast.makeText(this, getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
            currentMyLocation.setAddress("");
        }
    }

    private LatLng newLatLong(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        currentMyLocation = new MyLocation(latLng.latitude, latLng.longitude);
        mCurrentLatLng = latLng;
        startFetchAddressService(latLng);
        if (gMap != null) {
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
        }
    }

    private void displayAddressOutput(String address) {
        tv_address.setText(address);
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        if (!isFinishing() && !progressDialog.isShowing()) progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        startLocation();
        return false;
    }

    @Override
    public void onLocationUpdated(Location location) {
        if (location != null) {
            currentMyLocation = new MyLocation(location.getLatitude(), location.getLongitude());
            // We are going to get the address for the current position
            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder();
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        displayAddressOutput(builder.toString());
                        currentMyLocation.setAddress(builder.toString());
                    } else {
                        currentMyLocation.setAddress("");
                    }
                }
            });
        } else {
            Toast.makeText(MapActivity.this, getString(R.string.fail_req_location), Toast.LENGTH_SHORT).show();
        }
    }

    //Receive address return from service
    class AddressResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            final String result = resultData.getString(Constant.RESULT_DATA_KEY);
            final double[] arr = resultData.getDoubleArray(Constant.RESULT_DATA_LOCATION);
            // Show a toast message if an address was found.
            if (resultCode == Constant.SUCCESS_RESULT) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean check = false;
                        for (MyLocation location : locations) {
                            if (location.getLat() == arr[0] && location.getLng() == arr[1]) {
                                location.setAddress(result);
                                if (locationListRecycleAdapter != null)
                                    locationListRecycleAdapter.notifyDataSetChanged();
                                check = true;
                                break;
                            }
                        }
                        if (!check)
                            currentMyLocation.setAddress(result);
                        displayAddressOutput(result);
                    }
                });

            } else {
                currentMyLocation.setAddress("");
                Toast.makeText(MapActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (locations != null && locations.size() > 1) {
                Intent intent = new Intent(MapActivity.this, DirectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putParcelableArrayListExtra(Constant.ARRAY_LOCATION, locations);
                startActivity(intent);
            } else if (locations != null && locations.size() == 1) {
                Toast.makeText(MapActivity.this, getString(R.string.choose_two_location), Toast.LENGTH_SHORT).show();
            } else if (locations == null || locations.size() == 0) {
                Toast.makeText(MapActivity.this, getString(R.string.choose_one_location), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}