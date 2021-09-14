package com.example.finalproject;


import static com.example.finalproject.RecyclerViewActivity.JSON_SAVE_RETRIEVE;
import static com.example.finalproject.RecyclerViewActivity.isActivityCalled;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class IndividualUserDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "IndividualUserDetails";

    private ImageView profPic;
    private EditText firstAndLastName, email;
    private Button goBackButton;
    public Intent intent;
    private ArrayList<User> userArrayList;
    String profPicValue, firstAndLastNameValue, emailValue;
    NotificationManagerCompat notificationManager;
    NotificationClass notificationClass;
    Context context = this;

    static final int CAPTURE_IMAGE_REQUEST = 1000;

    String mCurrentPhotoPath;
    File photoFile = null;
    Uri photoUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individiual_user_details);
        userArrayList = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(this);
        notificationClass = new NotificationClass(getApplicationContext());

        Log.d(TAG, "onCreate: ");

        profPic = findViewById(R.id.profile_pic_view);
        firstAndLastName = findViewById(R.id.first_last_name);
        email = findViewById(R.id.email);
        goBackButton = findViewById(R.id.go_back_button);

        //get the values from last intent
        profPicValue = getIntent().getStringExtra("profilePic");
        firstAndLastNameValue = getIntent().getStringExtra("firstAndLastName");
        emailValue = getIntent().getStringExtra("email");

        //assign values
        Picasso.get().load(profPicValue).transform(new CropCircleTransformation()).resize(400,400).into(profPic);
        firstAndLastName.setText(firstAndLastNameValue);
        email.setText(emailValue);

        //click listeners
        goBackButton.setOnClickListener(this);
        profPic.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Notification control between activities
        RecyclerViewActivity.isActivityCalled = false;
        notificationManager.cancelAll(); //cancel all background notification when user is back
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saving it when user leaves the app
        saveIt();
        //calling notification if user doesn't moving between activities
        if (!RecyclerViewActivity.isActivityCalled) {
            notificationClass.createNotificationChannel();
        }
        Log.d(TAG, "onPause: Is Activity Called:" + isActivityCalled);
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
        Toast.makeText(this, "Back Button Pressed", Toast.LENGTH_SHORT)
                .show();
        goBackToRecyclerView();

    }

    //go back button calls the method
    //it creates a unique request code, so the Recycler Activity will know
    //where user comes from and retrieve data other than pull from json
    public void goBackToRecyclerView() {
        intent = new Intent(this, RecyclerViewActivity.class);
        RecyclerViewActivity.isActivityCalled = true;
        startActivity(intent);
    }

    //saving data.
    // if data changes by user, finding the right data, changing it
    //and pushing back to json style in memory with SharedPreferences
    public void saveIt() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();

        //pull all info
        String json = sharedPreferences.getString(JSON_SAVE_RETRIEVE, "");
        Type type = new TypeToken<List<User>>() {
        }.getType();
        userArrayList = gson.fromJson(json, type);

        //find the user that u just clicked and changed the values
        for (User x : userArrayList) {
            if (x.getName().equals(firstAndLastNameValue)) {
                x.setName(firstAndLastName.getText().toString());
                x.setEmail(email.getText().toString());
                x.setProfilePic(profPicValue);
            }
        }
        //save it back
        sharedPreferences.edit().putString(JSON_SAVE_RETRIEVE, gson.toJson(userArrayList)).apply();
    }

    //create unique empty image file
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /*
    Pull saved files
    Open Camera
    create file and initialize the file
    get Uri to the specific path
    capture image
    assign to person profile pic
    save back to shared preferences
     */
    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            RecyclerViewActivity.isActivityCalled = true;
        } else {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                photoUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                RecyclerViewActivity.isActivityCalled = true;
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } catch (Exception ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
        }
        RecyclerViewActivity.isActivityCalled = true;
    }

    //camera and external data permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    openCamera();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 400, 400, false);
            profPic.setImageBitmap(myBitmap);
            for (User x : userArrayList) {
                if (x.getName().equals(firstAndLastNameValue)) {
                    profPicValue = "file://" + photoFile.getAbsolutePath();
                }
            }
        } else {
            Log.d(TAG, "onActivityResult: Request cancelled or something went wrong. Saving back original picture");
        }
        saveIt();
    }


    //onSaveInstanceState for if the page pushes back or if phone rotate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("firstAndLastName", firstAndLastName.getText().toString());
        outState.putString("email", email.getText().toString());
        outState.putString("profilePic", profPicValue);
        Log.d(TAG, "onSaveInstanceState: " + firstAndLastName.getText() + "->" + email.getText() + "->" + profPicValue);
    }

    //bring back the recent information saved by onSaveInstanceState
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        firstAndLastNameValue = savedInstanceState.getString("firstAndLastName");
        emailValue = savedInstanceState.getString("email");
        profPicValue = savedInstanceState.getString("profilePic");

    }

    public void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to change profile picture?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int which) {
                openCamera();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back_button:
                goBackToRecyclerView();
                break;
            case R.id.profile_pic_view:
                openDialog();
                break;
        }
    }
}