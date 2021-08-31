package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.security.acl.NotOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
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
    protected static final String INFOKEY = "infoKey";
    protected static final String JSON_PULL_PUSH = "jsonPullPush";
    private OkHttpClient client;
    private ArrayList<User> usersList;
    private Gson gson;
    User[] users;


    private ImageView googleLogoImageView;
    private GoogleSignInClient googleSignInClient;
    //private NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersList = new ArrayList<>();
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
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
        Log.d(TAG, "onPause: ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
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
        Log.d(TAG, "signIn: clicked");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_ID);

    }

    public void updateUI(@Nullable GoogleSignInAccount googleSignInAccount){
        //if already signed in
        if(googleSignInAccount != null){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, RecyclerViewActivity.class);
            intent.putExtra(JSON_PULL_PUSH, usersList);
            startActivity(intent);

        }
        else{
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    public void pullTheInformation(String infoUrl, String picsurl) throws IOException{

        Request request = new Request.Builder()
                .url(infoUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                users = gson.fromJson(responseBody.string(),User[].class);
                runOnUiThread( () -> newView(users));
                Log.d(TAG, "onResponse: information pulled");
            }
        });

    }

    public void newView(User[] users){
        usersList.clear();
        int i = 0;
        for(User u: users){
            u.setProfilePic(PICSURL + i);
            usersList.add(u);
            i++;
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                try {
                    pullTheInformation(INFOURL, PICSURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}