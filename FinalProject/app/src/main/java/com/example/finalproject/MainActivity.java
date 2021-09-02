package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


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
import android.widget.AbsListView;
import android.widget.ImageView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;



import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "MainActivity";
    private static final String GOOGLE_LOGO_URI = "https://storage.googleapis.com/gd-wagtail-prod-assets/original_images/evolving_google_identity_2x1.jpg";
    private static final int SIGN_IN_ID = 9001;
    private static final String INFOURL = "http://jsonplaceholder.typicode.com/users";
    private static final String PICSURL = "https://robohash.org/";
    private static final String CHANNEL_1_ID = "channel1";


    protected static final String INFOKEY = "infoKey";
    protected static final String JSON_PULL_PUSH = "jsonPullPush";
    protected static final String REQUEST_CODE = "requestCode";
    protected static final int REQUEST_CODE_VALUE = 90;


    private OkHttpClient client;
    private ArrayList<User> userArrayList;
    private Gson gson;
    User[] users;


    private ImageView googleLogoImageView;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userArrayList = new ArrayList<>();
        gson = new Gson();
        client = new OkHttpClient();


        //buttons for sign in and out
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);


        //get the google logo from website and set into image view
        googleLogoImageView = findViewById(R.id.google_logo_image_view);
        Picasso.get().load(GOOGLE_LOGO_URI).into(googleLogoImageView);

        //build the google sign in options and assign to sign in client
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //create sign in button and set size and color
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);


//        //add yourself very first before update UI, otherwise you will get duplicate value of yourself
//        if(googleSignInAccount != null){
//            users = new User(googleSignInAccount.getDisplayName().toString(), googleSignInAccount.getEmail().toString(), googleSignInAccount.getPhotoUrl().toString());
//            userArrayList.add(0,me);
//        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount != null)
            retrieveData();
        updateUI(googleSignInAccount);
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void handleSignInResult(Task<GoogleSignInAccount> task){
        try{
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
            updateUI(googleSignInAccount);

        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult: failed..Code: " + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_ID){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_ID);
    }

    public void updateUI(@Nullable GoogleSignInAccount googleSignInAccount){

        if(googleSignInAccount != null){

            Intent intent = new Intent(this, RecyclerViewActivity.class);
            intent.putExtra(REQUEST_CODE, REQUEST_CODE_VALUE);
            intent.putExtra(JSON_PULL_PUSH, userArrayList);
            startActivityForResult(intent, REQUEST_CODE_VALUE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    public void pullTheInformation(String infoUrl) throws IOException{

        Request request = new Request.Builder()
                .url(infoUrl)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: you are in on failure. Something is very wrong: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                users = gson.fromJson(responseBody.string(),User[].class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newView(users);
                    }
                });
            }
        });

    }

    public void newView(User[] users){
        int i = 0;
        for(User u: users){
            u.setProfilePic(PICSURL + i);
            userArrayList.add(u);
            i++;
        }
    }


    public void saveData(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userArrayList);
        editor.putString(JSON_PULL_PUSH, json);
        editor.commit();

    }

    public void retrieveData(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString(JSON_PULL_PUSH, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {}.getType();
        userArrayList = gson.fromJson(json, type);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();

                try {
                    pullTheInformation(INFOURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}