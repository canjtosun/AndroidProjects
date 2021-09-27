package com.example.googleproject;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    GoogleSignInAccount googleSignInAccount;
    Class c;

    public static final String TAG = "NotificationService";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //create a channel
        createNotificationChannel();

        /*
        bringing information from other activities
        below code determies the last activity the user left off
         */
        SharedPreferences sharedPreferences = getSharedPreferences("CLASS_KEY", Context.MODE_PRIVATE);
        String prevClass = sharedPreferences.getString("lastClass", getClass().toString());
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (prevClass.equals(SaveReadFileActivity.class.toString())) {
            c = SaveReadFileActivity.class;
            Log.d(TAG, "onStartCommand: Save Read Activity");
        } else if (prevClass.equals(MapsActivity.class.toString())) {
            c = MapsActivity.class;
            Log.d(TAG, "onStartCommand: Maps Activity");
        } else if (prevClass.equals(MainActivity.class.toString()) && googleSignInAccount == null) {
            c = MainActivity.class;
            Log.d(TAG, "onStartCommand: Sing Activity");
        } else {
            c = RecyclerViewActivity.class;
            Log.d(TAG, "onStartCommand: None of them");
        }


        //creating target intent and pending intent for foreground service
        Intent targetIntent = new Intent(this, c);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent broadcastIntent = new Intent(this, AlarmService.class);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("Notification")
                .setContentText("Don't Forget Me!")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(getResources().getColor(R.color.bgColor))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColorized(true)
                .addAction(R.drawable.ic_close, "Toast", actionIntent)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ChannelId1",
                    "ForeGround Notification", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
