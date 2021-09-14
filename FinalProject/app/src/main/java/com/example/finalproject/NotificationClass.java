package com.example.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;


import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class NotificationClass {
    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "channel0";
    Context context;
    NotificationManager notificationManager;

    public NotificationClass(Context context) {
        this.context = context;
    }

    //sending notification uniquely for each activity
    protected void createNotificationChannel(Class c, String profPicUrl, String firstAndLastName, String email) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel0", importance);
            channel.setDescription("Notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, c);
        intent.putExtra("profilePic", profPicUrl);
        intent.putExtra("firstAndLastName", firstAndLastName);
        intent.putExtra("email", email);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Notification")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .build();


        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    protected void createNotificationChannel(Class c) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel0", importance);
            channel.setDescription("Notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, c);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Notification")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .build();


        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    public void onDestroyControl(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("isDestroyedCalled", true);
        editor.apply();
    }
}
