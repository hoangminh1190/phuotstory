package com.m2team.phuotstory.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
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
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.common.Common;
import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.model.MyLocation;
import com.m2team.phuotstory.model.Story;
import com.m2team.phuotstory.pick.Action;
import com.m2team.phuotstory.pick.CustomGallery;
import com.m2team.phuotstory.pick.GalleryAdapter;
import com.m2team.phuotstory.pick.PickerImageActivity;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;

import net.qiujuer.genius.widget.GeniusEditText;
import net.qiujuer.genius.widget.GeniusTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Bind(R.id.fam)
    FloatingActionsMenu floatingActionsMenu;

    Story story;
    long storyId;
    GeniusEditText edt_feeling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        ButterKnife.bind(this);
        tv_travel_time.setVisibility(View.GONE);
        story = new Story();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.end_point)
    public void chooseLocation() {
        Intent i = new Intent(this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(i, Constant.REQ_CODE_DEST_MAP);
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
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                tv_travel_time.setVisibility(View.GONE);
                tv_travel_time.setText("");
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
        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        View customView = dialog.getCustomView();
        if (customView != null) {
            edt_feeling = (GeniusEditText) customView.findViewById(R.id.edt_feeling);
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.showSoftInput(edt_feeling, InputMethodManager.SHOW_IMPLICIT);
            if (!TextUtils.isEmpty(tv_feeling.getText())) {
                edt_feeling.setText(tv_feeling.getText());
                positiveAction.setEnabled(true);
            } else {
                positiveAction.setEnabled(false); // disabled by default
            }
            edt_feeling.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    positiveAction.setEnabled(s.toString().trim().length() > 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        dialog.show();

    }

    @OnClick(R.id.float_photo)
    public void addPhoto() {
        if (floatingActionsMenu.isExpanded()) floatingActionsMenu.collapseImmediately();
        /*initImageLoader();
        init();
        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(i, Constant.REQ_CODE_PICK_PHOTOS);*/
        startActivityForResult(new Intent(this, PickerImageActivity.class), Constant.REQ_CODE_PICK_PHOTOS);
    }

    GridView gridGallery;
    GalleryAdapter adapter;
    ImageLoader imageLoader;

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void init() {
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(this, imageLoader);
        gridGallery.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQ_CODE_DEST_MAP:
                if (resultCode == RESULT_OK) {
                    ArrayList<MyLocation> locations = data.getParcelableArrayListExtra(Constant.ARRAY_LOCATION);
                    if (locations != null) {
                        ArrayList<String> shortAddress = Common.getShortAddress(locations);
                        story.setShortTitleLocation(TextUtils.join(Constant.COMMA + " ", shortAddress));
                        story.setFullLocationList(Common.objToString(locations));
                    } else {
                        Toast.makeText(this, getString(R.string.fail_data), Toast.LENGTH_SHORT).show();
                    }
                    Log.v(Constant.TAG, "ok");

                } else {
                    Log.v(Constant.TAG, "cancel");
                    Toast.makeText(this, getString(R.string.fail_data), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.REQ_CODE_PICK_PHOTOS:
                if (resultCode == RESULT_OK) {
                        String[] all_path = data.getStringArrayExtra("all_path");

                        ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

                        for (String string : all_path) {
                            CustomGallery item = new CustomGallery();
                            item.sdcardPath = string;

                            dataT.add(item);
                        }

                        adapter.addAll(dataT);

                } else {
                    Log.v(Constant.TAG, "cancel");
                    Toast.makeText(this, getString(R.string.fail_pick_photo), Toast.LENGTH_SHORT).show();
                }
        }
    }
}
