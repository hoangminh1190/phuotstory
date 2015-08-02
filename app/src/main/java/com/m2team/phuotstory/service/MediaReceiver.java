package com.m2team.phuotstory.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.m2team.phuotstory.common.Constant;
import com.m2team.phuotstory.mediachooser.MediaChooseActivity;

public class MediaReceiver extends BroadcastReceiver {
    public MediaReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MediaChooseActivity.class);
        i.putExtra(Constant.ARRAY_PHOTO_URI, intent.getStringArrayListExtra("list"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
