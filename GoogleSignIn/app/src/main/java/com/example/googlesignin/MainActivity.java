package com.example.googlesignin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final int SIGNINID = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView firstNameView, lastNameView, emailView, personIdView;
    private ImageView personPhotoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        mStatusTextView = findViewById(R.id.status);
        personPhotoView = findViewById(R.id.person_photo);
        firstNameView = findViewById(R.id.first_name);
        lastNameView = findViewById(R.id.last_name);
        emailView = findViewById(R.id.email_full);
        personIdView = findViewById(R.id.person_id);

        //buttons
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(googleSignInAccount);
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
        if(requestCode == SIGNINID){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGNINID);
    }

    public void signOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this,(task) -> {
                    updateUI(null);
                });
    }

    public void updateUI(@Nullable GoogleSignInAccount googleSignInAccount){
        if(googleSignInAccount != null){
            mStatusTextView.setText(googleSignInAccount.getDisplayName());
            firstNameView.setText(googleSignInAccount.getGivenName());
            lastNameView.setText(googleSignInAccount.getFamilyName());
            emailView.setText(googleSignInAccount.getEmail());
            personIdView.setText(googleSignInAccount.getId());

            Picasso.with(this).load(googleSignInAccount.getPhotoUrl()).into(personPhotoView);


            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            personPhotoView.setVisibility(View.VISIBLE);
        }
        else{
            mStatusTextView.setText("Signed Out");
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            personPhotoView.setVisibility(View.GONE);
            firstNameView.setText("");
            lastNameView.setText("");
            emailView.setText("");
            personIdView.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }
}