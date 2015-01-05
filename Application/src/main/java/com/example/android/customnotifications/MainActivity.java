/*
 * Copyright (C) 2013 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.customnotifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.customnotifications.event.NotificationEvent;

import java.text.DateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int SECS = 1000;
    public static final int MINS = 60 * SECS;
    private CountDownTimer mDownTimer;
    private int mCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        Log.i(TAG, "Created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.w(TAG, "Destroyed");
    }

    /**
     * This sample demonstrates notifications with custom content views with buttons.
     *
     * <p>On API level 16 and above a big content view is also defined that is used for the
     * 'expanded' notification. The notification is created by the NotificationCompat.Builder.
     * The expanded content view is set directly on the {@link android.app.Notification} once it has been build.
     * (See {@link android.app.Notification#bigContentView}.) </p>
     *
     * <p>The content views are inflated as {@link android.widget.RemoteViews} directly from their XML layout
     * definitions using {@link android.widget.RemoteViews#RemoteViews(String, int)}.</p>
     */
    private void postNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //Create Intent to launch this Activity again if the notification is clicked.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Sets the ticker text
        builder.setTicker(getResources().getString(R.string.custom_notification));
        builder.setSmallIcon(R.drawable.ic_stat_custom);
        // Cancel the notification when clicked
        builder.setAutoCancel(false);

        // Build the notification
        Notification notification = builder.build();

        // Inflate the notification layout as RemoteViews
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);

        // Set text on a TextView in the RemoteViews
        final String time = DateFormat.getTimeInstance().format(new Date());
        final String msg = getResources().getString(R.string.collapsed, time);
        final String text = String.format("%s - %d", msg, mCount);
        contentView.setTextViewText(R.id.textView, text);

        contentView.setOnClickPendingIntent(R.id.button,
                getPendingIntentAction(NotificationEvent.Type.UpCount.getName()));

        /* Workaround: Need to set the content view here directly on the notification.
         * NotificationCompatBuilder contains a bug that prevents this from working on platform
         * versions HoneyComb.
         * See https://code.google.com/p/android/issues/detail?id=30495
         */
        notification.contentView = contentView;

        // Add a big content view to the notification if supported.
        // Support for expanded notifications was added in API level 16.
        // (The normal contentView is shown when the notification is collapsed, when expanded the
        // big content view set here is displayed.)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Inflate and set the layout for the expanded notification view
            RemoteViews expandedView =
                    new RemoteViews(getPackageName(), R.layout.notification_expanded);
            notification.bigContentView = expandedView;
            contentView = expandedView;
        }

        contentView.setOnClickPendingIntent(R.id.button,
                getPendingIntentAction(NotificationEvent.Type.DownCount.getName()));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

    private PendingIntent getPendingIntentAction(String action) {
        Intent clickIntent = new Intent(this, AppWidgetProvider.class);
        clickIntent.setAction(action);
        return PendingIntent.getBroadcast(this, 0, clickIntent, 0);
    }

    /**
     * Create and show a notification with a custom layout.
     * This callback is defined through the 'onClick' attribute of the
     * 'Show Notification' button in the XML layout.
     *
     * @param view View
     */
    public void showNotificationClicked(View view) {
        postNotification();

        if (mDownTimer != null) {
            mDownTimer.cancel();
        }

        // change the notification every N ms
        mDownTimer = new CountDownTimer(5 * MINS, 2 * SECS) {
            @Override
            public void onTick(long l) {
                postNotification();
            }

            @Override
            public void onFinish() {}
        };
        mDownTimer.start();
    }

    public void showDetailActivityClicked(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(NotificationEvent event) {
        switch (event.getEventType()) {
            case UpCount:
                ++mCount;
                break;

            case DownCount:
                --mCount;
                break;
        }

        postNotification();
    }
}
