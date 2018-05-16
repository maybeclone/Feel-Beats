package com.silent.feelbeat.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.silent.feelbeat.R;
import com.silent.feelbeat.activities.MainActivity;

/**
 * Created by silent on 5/16/2018.
 */
public class NotificationUtils {


    public static Notification getNotificationForegroundService(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        RemoteViews smallRemoteView = new RemoteViews(context.getPackageName(), R.layout.small_notification_view);
        builder.setCustomBigContentView(smallRemoteView);
        smallRemoteView.setBitmap(R.id.imageView, "setImageBitmap", BitmapFactory.decodeResource(context.getResources(), R.drawable.art_test));
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_ONE_SHOT);

//        builder.setContentIntent(pendingIntent);

        return builder.build();
    }
}
