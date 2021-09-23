package com.example.googleproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlarmService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Toast Clicked, and I am here!", Toast.LENGTH_SHORT).show();
//        Uri alarmSound =
//                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
//        MediaPlayer mp = MediaPlayer.create(context, alarmSound);
//        mp.start();

    }
}

class AirplaneModeReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Airplane Mode Changed", Toast.LENGTH_SHORT).show();
    }
}

