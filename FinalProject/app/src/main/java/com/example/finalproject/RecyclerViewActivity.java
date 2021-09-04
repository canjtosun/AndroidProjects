package com.example.finalproject;

import static com.example.finalproject.IndividualUserDetails.INDIVIDUAL_USER_DETAIL_ACTIVITY_VALUE;
import static com.example.finalproject.MainActivity.JSON_PULL_PUSH;
import static com.example.finalproject.MainActivity.REQUEST_CODE;
import static com.example.finalproject.MainActivity.REQUEST_CODE_VALUE;


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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RecyclerViewActivity";
    protected static final String JSON_SAVE_RETRIEVE = "jsonX";
    private static final int RECYCLER_VIEW_NOTIFICATION_ID = 1;
    private static final String RECYCLER_VIEW_NOTIFICATION_CHANNEL_ID = "channel1";
    private static final int RECYCLER_ACTIVITY_VALUE = 90;
    private NotificationManagerCompat notificationManager;

    private RecyclerView recyclerView;

    private ArrayList<User> userArrayList;
    public static boolean isActivityCalled = false;


    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        //userArrayList = new ArrayList<>();

        notificationManager = NotificationManagerCompat.from(this);

        recyclerView = findViewById(R.id.recViewUser);
        signOutButton = findViewById(R.id.sign_in_button);

        String googleName = getIntent().getStringExtra("googleName");
        String googleEmail = getIntent().getStringExtra("googleEmail");
        String googleProfPic = getIntent().getStringExtra("googleProfPic");

        //this is the part not to mix up retrieving data and pulling the information and assigning the arraylist
        //if statement calls retrieve data from after coming back form editing user information
        //else part is only calling once, pulling json info with google user info and assign to arraylist
        //without this part, all the information is mixed up and gives error
        Log.d(TAG, "onCreate: " + getIntent().getExtras().getInt(REQUEST_CODE));
        if(getIntent().getExtras().getInt(REQUEST_CODE) == INDIVIDUAL_USER_DETAIL_ACTIVITY_VALUE){
            Log.d(TAG, "onCreate: You are Coming 'back' from Individual User Information page");
            retrieveData();
        }

        else if(getIntent().getExtras().getInt(REQUEST_CODE) == RECYCLER_ACTIVITY_VALUE){
            Log.d(TAG, "onCreate: You are Coming from Main Activity page");
            userArrayList = (ArrayList<User>) getIntent().getSerializableExtra(JSON_PULL_PUSH);
            userArrayList.add(0, new User(googleName, googleEmail, googleProfPic));
            Log.d(TAG, "onCreate: You just initialized the userArrayList");
        }

        //adapter
        UserAdapter userAdapter = new UserAdapter(this, userArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);

        //click listener for sign out
        findViewById(R.id.sign_out_button).setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityCalled = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        //Notification control between activities
        if(!isActivityCalled) {
            recyclerViewNotification();
        }

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
        isActivityCalled = true; //Notification control between activities
        startActivity(intent);

    }

    //saving data with SharedPreferences to json
    public void saveData(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userArrayList);
        editor.putString(JSON_SAVE_RETRIEVE, json);
        editor.apply();
    }

    //retrieving data from json
    public void retrieveData(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString(JSON_SAVE_RETRIEVE, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {}.getType();
        userArrayList = gson.fromJson(json, type);

    }

    //sending notification uniquely for each activity
    public void recyclerViewNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(RECYCLER_VIEW_NOTIFICATION_CHANNEL_ID, "channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is IndividualUserDetails channel1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent(), 0);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_out_button:
                signOutAndGoBack();
                break;
        }
    }
}