package com.example.finalproject;

import static com.example.finalproject.MainActivity.INFOKEY;
import static com.example.finalproject.MainActivity.JSON_PULL_PUSH;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RecyclerViewActivity";
    public static final String CHANNEL_1_ID = "channel1";
    private NotificationManagerCompat notificationManager;


    private RecyclerView recyclerView;
    private ArrayList<User> userArrayList;
    UserAdapter userAdapter;


    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = findViewById(R.id.recViewUser);
        signOutButton = findViewById(R.id.sign_in_button);

        //retrieve data with using serializable from json
        userArrayList = (ArrayList<User>) getIntent().getSerializableExtra(JSON_PULL_PUSH);

        //retrieve data when you close the app
        retrieveData();



        //adapter
        userAdapter = new UserAdapter(this, userArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);


        findViewById(R.id.sign_out_button).setOnClickListener(this);

        notificationManager = NotificationManagerCompat.from(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //retrieve data when you close the app
        retrieveData();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        saveData();
        sendOnChannel1();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

    }

    private void signOutAndGoBack() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonText = gson.toJson(userArrayList);
        editor.putString("aa", jsonText);
        editor.apply();

    }

    public void retrieveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("Mypref", MODE_PRIVATE);

        String jsonText = sharedPreferences.getString("aa","");
        userArrayList =
                new Gson().fromJson(jsonText, new TypeToken<ArrayList<User>>() {
                }.getType());
    }

    public void sendOnChannel1(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("this is channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        //turn back to app
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Final Project")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_out_button:
                signOutAndGoBack();
                break;
        }
    }
}