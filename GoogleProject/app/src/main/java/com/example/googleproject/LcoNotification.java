package com.example.googleproject;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LcoNotification implements LifecycleObserver {

    Context context;

    public LcoNotification(Context context) {
        this.context = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void goesBack(){
        Intent serviceNotification = new Intent(context, ExampleService.class);
        context.startService(serviceNotification);
    }

}
