package com.m2team.phuotstory.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.common.Applog;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.mediachooser.MediaGridViewAdapter;
import com.m2team.phuotstory.model.MyLocation;
import com.m2team.phuotstory.model.Story;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;

import net.qiujuer.genius.widget.GeniusEditText;
import net.qiujuer.genius.widget.GeniusTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WriteActivity extends AppCompatActivity {
    @Bind(R.id.float_feeling)
    FloatingActionButton float_feeling;
    @Bind(R.id.float_friends)
    FloatingActionButton float_friends;
    @Bind(R.id.float_photo)
    FloatingActionButton float_photo;
    @Bind(R.id.float_travel_time)
    FloatingActionButton float_travel;
    @Bind(R.id.tv_travel_time)
    GeniusTextView tv_travel_time;
    @Bind(R.id.tv_feeling)
    GeniusTextView tv_feeling;
    @Bind(R.id.tv_friends)
    GeniusTextView tv_friends;
    @Bind(R.id.edt_title)
    GeniusEditText edt_title;
    @Bind(R.id.fam)
    FloatingActionsMenu floatingActionsMenu;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    Story story;
    long storyId;
    GeniusEditText edt_feeling;
    ArrayList<String> photoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        tv_travel_time.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        story = new Story();
        edt_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                story.setTitle(s.toString().trim());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Applog.d("onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        int type = intent.getIntExtra(Constant.ACTION_TYPE, 0);
        if (type == Constant.TYPE_LOCATIONS)
            processLocations(intent);
        else if (type == Constant.TYPE_PHOTO)
            processPhotoURI(intent);
    }

    private void processLocations(Intent data) {
        Applog.d("pr locations");
        ArrayList<MyLocation> locations = data.getParcelableArrayListExtra(Constant.ARRAY_LOCATION);
        int distance = data.getIntExtra(Constant.TOTAL_DISTANCE, 0);
        String imgPath = data.getStringExtra(Constant.IMAGE_BITMAP_MAP);
        if (locations != null) {
            ArrayList<String> shortAddress = Common.getShortAddress(locations);
            story.setShortTitleLocation(TextUtils.join(Constant.COMMA + " ", shortAddress));
            story.setFullLocationList(Common.objToString(locations));
            story.setDistance(distance);
            story.setPreviewImageUri(imgPath);
        }
    }

    private void processPhotoURI(Intent intent) {
        Applog.d("pr processPhotoURI");
        photoPaths = intent.getStringArrayListExtra(Constant.ARRAY_PHOTO_URI);
        if (photoPaths != null && photoPaths.size() > 0) {
            gridView.setVisibility(View.VISIBLE);
            showPhotoFragment(photoPaths);
            story.setPhotoUri(TextUtils.join(",", photoPaths));
        } else {
            gridView.setVisibility(View.GONE);
        }
    }

    MediaGridViewAdapter adapter;

    private void showPhotoFragment(ArrayList<String> photoPaths) {
        if (adapter == null) {
            adapter = new MediaGridViewAdapter(this, 0, photoPaths, null, gridView, true);
            gridView.setAdapter(adapter);
        } else {
            adapter.addAll(photoPaths);
            adapter.notifyDataSetChanged();
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
            Applog.d("save");
            story.setCreatedTime(System.currentTimeMillis());
            long newId = Common.insertStory(this, story);
            if (newId > 0)
                setResult(RESULT_OK);
            else setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.end_point)
    public void chooseLocation() {
        Intent i = new Intent(this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @OnClick(R.id.float_travel_time)
    public void addTravelTime() {
        DatePickerDialog.Builder builder = new DatePickerDialog.Builder() {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                SimpleDateFormat dateFormat;
                final String format = Settings.System.getString(getContentResolver(), Settings.System.DATE_FORMAT);
                if (TextUtils.isEmpty(format)) {
                    dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                } else {
                    dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                }
                tv_travel_time.setVisibility(View.VISIBLE);
                tv_travel_time.setText(dateFormat.format(dialog.getDate()));
                if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
                story.setTravelTime(dialog.getDate());
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                tv_travel_time.setVisibility(View.GONE);
                tv_travel_time.setText("");
                story.setTravelTime(null);
                if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
                super.onNegativeActionClicked(fragment);
            }

            @Override
            public void onNeutralActionClicked(DialogFragment fragment) {
                if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
                super.onNeutralActionClicked(fragment);
            }
        }.dateRange(0, System.currentTimeMillis());

        builder.title(getString(R.string.travel_time)).neutralAction(getString(R.string.cancel))
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.delete));

        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), "TravelTimeDialog");
    }

    @OnClick(R.id.float_feeling)
    public void addFeeling() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.what_feeling)
                .customView(R.layout.layout_feeling, true)
                .positiveText(R.string.ok)
                .negativeText(android.R.string.cancel)
                .theme(Theme.LIGHT)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        tv_feeling.setVisibility(View.VISIBLE);
                        tv_feeling.setText(edt_feeling.getText());
                        story.setFeeling(edt_feeling.getText().toString().trim());
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
            }
        });
        View customView = dialog.getCustomView();
        if (customView != null) {
            edt_feeling = (GeniusEditText) customView.findViewById(R.id.edt_feeling);
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.showSoftInput(edt_feeling, 0);
            if (!TextUtils.isEmpty(tv_feeling.getText())) {
                edt_feeling.setText(tv_feeling.getText());
            }
        }
        dialog.show();

    }

    @OnClick(R.id.float_photo)
    public void addPhoto() {
        if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
        MediaChooser.setSelectionLimit(20);
        Intent intent = new Intent(this, BucketHomeFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


}
