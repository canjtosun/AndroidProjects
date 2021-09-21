package com.example.googleproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private static final String TAG = "MainActivity";
    private static final String GOOGLE_LOGO_URI = "https://i.ibb.co/fkHtYJw/580b57fcd9996e24bc43c51f.png";
    private static final int SIGN_IN_ID = 9001;


    private ImageView googleLogoImageView;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //buttons for sign in and out
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);


        //get the google logo from website and set into image view
        googleLogoImageView = findViewById(R.id.google_logo_image_view);
        Picasso.get().load(GOOGLE_LOGO_URI).into(googleLogoImageView);

        //build the google sign in options and assign to sign in client
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
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
        Toast.makeText(this, "You are in LogIn Page", Toast.LENGTH_SHORT)
                .show();
    }

    //handle sign in result, if user cannot sign in, log a failed code and update UI
    public void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
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
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN_ID) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_ID);
    }

    //updating UI according to the google sign in account
    public void updateUI(@Nullable GoogleSignInAccount googleSignInAccount) {

        if (googleSignInAccount != null) {

            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
}