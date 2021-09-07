package com.example.finalproject;


import static com.example.finalproject.RecyclerViewActivity.JSON_SAVE_RETRIEVE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class IndividualUserDetails extends Activity implements View.OnClickListener {

    private static final String TAG = "IndividualUserDetails";
    private static final int INDIVIDUAL_USER_NOTIFICATION_ID = 0;
    private static final String INDIVIDUAL_USER_NOTIFICATION_CHANNEL_ID = "channel0";
    private ImageView profPic;
    private EditText firstAndLastName, email;
    private Button goBackButton;
    public Intent intent;
    private ArrayList<User> userArrayList;
    String profPicValue, firstAndLastNameValue, emailValue;
    NotificationManagerCompat notificationManager;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individiual_user_details);
        userArrayList = new ArrayList<>();
        notificationManager = NotificationManagerCompat.from(this);

        Log.d(TAG, "onCreate: ");

        if(savedInstanceState != null){
            Picasso.get().load(savedInstanceState.getString("picKey"));
            firstAndLastName.setText(savedInstanceState.getString("nameAndLastNameKey"));
            email.setText(savedInstanceState.getString("emailKey"));

        }
        else{
            profPic = findViewById(R.id.profile_pic_view);
            firstAndLastName = findViewById(R.id.first_last_name);
            email = findViewById(R.id.email);
            goBackButton = findViewById(R.id.go_back_button);

            //get the values from last intent
            profPicValue = getIntent().getStringExtra("profilePic");
            firstAndLastNameValue = getIntent().getStringExtra("firstAndLastName");
            emailValue = getIntent().getStringExtra("email");

            //assign values
            Picasso.get().load(profPicValue).into(profPic);
            firstAndLastName.setText(firstAndLastNameValue);
            email.setText(emailValue);
        }

        //click listeners
        goBackButton.setOnClickListener(this);
        profPic.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Notification control between activities
        RecyclerViewActivity.isActivityCalled = false;
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saving it when user leaves the app
        saveIt();
        //calling notification if user doesn't moving between activities
        if(!RecyclerViewActivity.isActivityCalled) {
            individualUserNotification();
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
    public void saveIt(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        //pull all info
        String json = sharedPreferences.getString(JSON_SAVE_RETRIEVE, "");
        Type type = new TypeToken<List<User>>() {}.getType();
        userArrayList = gson.fromJson(json, type);

        //find the user that u just clicked and changed the values
        for(User x: userArrayList) {
            if (x.getName().equals(firstAndLastNameValue)) {
                x.setName(firstAndLastName.getText().toString());
                x.setEmail(email.getText().toString());
            }
        }
        //save it back
        sharedPreferences.edit().putString(JSON_SAVE_RETRIEVE, gson.toJson(userArrayList)).apply();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera(){
        RecyclerViewActivity.isActivityCalled = true;
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RecyclerViewActivity.isActivityCalled = true;
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
            else
            {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profPic.setImageBitmap(photo);
            //save the data and send it to arraylist
        }
    }

    //sending notification uniquely for each activity
    public void individualUserNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel0 = new NotificationChannel(INDIVIDUAL_USER_NOTIFICATION_CHANNEL_ID, "channel0", NotificationManager.IMPORTANCE_HIGH);
            channel0.setDescription("This is IndividualUserDetails channel0");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel0);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, getIntent() , 0);

        Notification notification = new NotificationCompat.Builder(this, INDIVIDUAL_USER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("User Details")
                .setContentText("Don't Forget About Me!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(INDIVIDUAL_USER_NOTIFICATION_ID, notification);
    }

    //onSaveInstanceState for if the page pushes back or if phone rotate
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nameAndLastNameKey", firstAndLastName.getText().toString() );
        outState.putString("emailKey", email.getText().toString());
        outState.putString("picKey", profPic.getTransitionName());
        Log.d(TAG, "onSaveInstanceState: " + firstAndLastName.getText() + "->" + email.getText());
    }

    //bring back the recent information saved by onSaveInstanceState
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + firstAndLastName.getText() + "->" + email.getText());
        firstAndLastName.setText(firstAndLastNameValue);
        email.setText(emailValue);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_back_button:
                goBackToRecyclerView();
                break;
            case R.id.profile_pic_view:
                openCamera();
                break;
        }
    }
}