package com.android.bhuwan.wishper;

import android.content.Context;
import android.content.Intent;

import com.android.bhuwan.wishper.ui.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by bhuwan on 11/4/2015.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
