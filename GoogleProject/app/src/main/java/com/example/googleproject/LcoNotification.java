package com.example.googleproject;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LcoNotification implements LifecycleObserver {

    Context context;

    //LifecycleObserver for sending notification when app goes background
    public LcoNotification(Context context) {
        this.context = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void goesBack(){
        Intent serviceNotification = new Intent(context, NotificationService.class);
        context.startService(serviceNotification);
    }

}
