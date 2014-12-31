package com.example.android.customnotifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.customnotifications.event.NotificationEvent;

import de.greenrobot.event.EventBus;

/**
 * Represents the app widget provider.
 */
public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    public static final String TAG = AppWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i(TAG, intent.getAction());

        // determine the action taken by the user, then create the event to post
        NotificationEvent event = null;
        if (intent.getAction().contentEquals(NotificationEvent.Type.UpCount.getName())) {
            event = new NotificationEvent(NotificationEvent.Type.UpCount);
        } else if (intent.getAction().contentEquals(NotificationEvent.Type.DownCount.getName())) {
            event = new NotificationEvent(NotificationEvent.Type.DownCount);
        }

        if (event != null) {
            EventBus.getDefault().post(event);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
