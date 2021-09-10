package com.example.finalproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "RecyclerViewActivity";
    protected static final String JSON_SAVE_RETRIEVE = "jsonX";
    private static final int RECYCLER_VIEW_NOTIFICATION_ID = 1;
    private static final String RECYCLER_VIEW_NOTIFICATION_CHANNEL_ID = "channel1";

    private NotificationManagerCompat notificationManager;

    private static final String INFOURL = "https://jsonplaceholder.typicode.com/users";
    private static final String PICSURL = "https://robohash.org/";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    private static ArrayList<User> userArrayList;
    public static boolean isActivityCalled = false;
    public static boolean isAlreadyCalled = false;

    private OkHttpClient client;
    private Gson gson;
    User[] users;


    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        Log.d(TAG, "onCreate: ");


        notificationManager = NotificationManagerCompat.from(this);

        recyclerView = findViewById(R.id.recViewUser);
        signOutButton = findViewById(R.id.sign_in_button);

        userArrayList = new ArrayList<>();
        gson = new Gson();
        client = new OkHttpClient();

        //pulling the google information but not adding
        //this info sometimes not pulling correctly, so it cause error

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        String googleName = googleSignInAccount.getDisplayName();
        String googleEmail = googleSignInAccount.getEmail();
        String googleProfPic = String.valueOf(googleSignInAccount.getPhotoUrl());
        User googleUser = new User(googleName, googleEmail, googleProfPic);


        Log.d(TAG, "onCreate: is Activity called" + isAlreadyCalled);
        if (!isAlreadyCalled) {
            try {
                run();
                userArrayList.add(googleUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAlreadyCalled = true;
        } else {
            retrieveData();
            Log.d(TAG, "onCreate: DATA RETRIEVED BCUZ YOU ARE COMING FROM OTHER ACTIVITY");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, userArrayList);
        recyclerView.setAdapter(userAdapter);

        //click listener for sign out
        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityCalled = false;
        Log.d(TAG, "onResume: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        //Notification control between activities
        if (!isActivityCalled) {
            recyclerViewNotification();
        }
        Log.d(TAG, "onPause: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //back button control
        Toast.makeText(this, "Please click LOG OUT to go Main Page", Toast.LENGTH_SHORT)
                .show();

    }

    //sign out and go back to log in page
    private void signOutAndGoBack() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        isActivityCalled = true;
        isAlreadyCalled = false; // avoid retrieve memory data. if you log out, data will reset
        startActivity(intent);

    }

    //saving data with SharedPreferences to json
    public void saveData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userArrayList);
        editor.putString(JSON_SAVE_RETRIEVE, json);
        editor.apply();
    }

    //retrieving data from json
    public void retrieveData() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString(JSON_SAVE_RETRIEVE, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        userArrayList = gson.fromJson(json, type);

    }

    //sending notification uniquely for each activity
    public void recyclerViewNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(RECYCLER_VIEW_NOTIFICATION_CHANNEL_ID, "channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is IndividualUserDetails channel1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent(), PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, RECYCLER_VIEW_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Information Page")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();


        notificationManager.notify(RECYCLER_VIEW_NOTIFICATION_ID, notification);
    }

    //pulling info from json
    //get info, put in User[] array
    //and then call newView method
    public void run() {
        Request request = new Request.Builder()
                .url(INFOURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    users = gson.fromJson(response.body().string(), User[].class);
                    runOnUiThread(() -> newView(users));
                }
            }
        });
    }

    //get info from User[] array and save it in a dynamic array
    @SuppressLint("NotifyDataSetChanged")
    public void newView(User[] users) {
        int i = 0;
        for (User u : users) {
            u.setProfilePic(PICSURL + i);
            userArrayList.add(u);
            i++;
        }
        userAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                signOutAndGoBack();
                break;
        }
    }
}