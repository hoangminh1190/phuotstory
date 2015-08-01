package com.m2team.phuotstory.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.m2team.phuotstory.R;
import com.m2team.phuotstory.adapter.OnItemDialogListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hoang Minh on 6/15/2015.
 */
public class Utils {
    public static final String ROBOTO_REGULAR = "RobotoCondensed-Regular.ttf";
    public static final String ROBOTO_LIGHT = "RobotoCondensed-Light.ttf";
    public static final String ROBOTO_BOLD = "RobotoCondensed-Bold.ttf";
    public static final String ROBOTO_ITALIC = "RobotoCondensed-Italic.ttf";
    private static final String FONT_PATH = "fonts/";

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        final String column = "_data";
        final String[] projection = {
                column
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            final int column_index = cursor.getColumnIndexOrThrow(column);
            String s = cursor.getString(column_index);
            cursor.close();
            return s;
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getPrefString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(key, "");
    }

    public static int getPrefInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getInt(key, 0);
    }

    public static long getPrefLong(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getLong(key, 0);
    }

    public static boolean getPrefBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void putPrefValue(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer)
            editor.putInt(key, Integer.parseInt(value.toString()));
        else if (value instanceof String)
            editor.putString(key, value.toString());
        else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }

    public static void clearStringSet(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }

    public static float dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static File createImageFile(String fileName) throws IOException {
        // Create an image file name
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + Constant.FOLDER_NAME + "/");
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) return null;
        }
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            boolean result;
            File f = new File(dir, fileName + ".jpg");
            result = f.createNewFile();
            if (result) return f;
            else return File.createTempFile(fileName, ".jpg", dir);
        }
        return null;
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClip = ClipData.newPlainText("", text);
        clipboardManager.setPrimaryClip(primaryClip);

    }

    public static String getFromClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
            if (itemAt != null) {
                return itemAt.getText().toString();
            }
        }
        return null;
    }


    private static void setFont(final Context context, final View v, String font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setFont(context, child, font);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_PATH + font));
            }
        } catch (Exception e) {
        }
    }

    public static void setColorDialog(Dialog dialog, String title, int colorId) {
        SpannableString str = new SpannableString(title);
        str.setSpan(new ForegroundColorSpan(colorId), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialog.setTitle(str);
        Resources resources = dialog.getContext().getResources();
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.getWindow().getDecorView().findViewById(titleDividerId);
        if (titleDivider != null) {
            //titleDivider.setBackgroundColor(colorId);
            titleDivider.setVisibility(View.GONE);
        }
    }

    public static void setColorAlertDialogTitle(AlertDialog dialog, int color) {
        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        if (dividerId != 0) {
            View divider = dialog.findViewById(dividerId);
            //divider.setBackgroundColor(color);
            divider.setVisibility(View.GONE);
        }

        int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        if (textViewId != 0) {
            TextView tv = (TextView) dialog.findViewById(textViewId);
            tv.setTextColor(color);
        }

        int iconId = dialog.getContext().getResources().getIdentifier("android:id/icon", null, null);
        if (iconId != 0) {
            ImageView icon = (ImageView) dialog.findViewById(iconId);
            icon.setColorFilter(color);
        }
    }

    // used for store arrayList in json format
    public static void putSharedPrefStringSetValue(Context context, String key, String value) {
        ArrayList<String> values = getSharedPrefStringSetValue(context, key);
        if (values.contains(value.toLowerCase()) || values.contains(value.toUpperCase()))
            values.remove(value);
        values.add(0, value);
        saveToJson(context, key, values);
    }

    public static void saveToJson(Context context, String key, ArrayList<String> values) {
        SharedPreferences settings = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(values);
        editor.putString(key, jsonFavorites);
        editor.apply();
    }

    public static ArrayList<String> getSharedPrefStringSetValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String jsonFavorites = settings.getString(key, "");
        if (TextUtils.isEmpty(jsonFavorites)) return new ArrayList<>();
        Gson gson = new Gson();
        return gson.fromJson(jsonFavorites, ArrayList.class);
    }

    public static void showSingleChoiceDialog(Context context, ArrayList<String> items, String title, final OnItemDialogListener listener) {
        String[] strings = new String[items.size()];
        items.toArray(strings);
        new MaterialDialog.Builder(context).items(strings).theme(Theme.LIGHT)
                .contentColor(Color.WHITE).itemColorRes(R.color.black_semi_transparent).widgetColorRes(R.color.md_green_400)
                .titleColorRes(R.color.black_semi_transparent)
                .title(title).itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        listener.selectedItem(which);
                        return true;
                    }
                }).show();
    }
}
