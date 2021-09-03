package com.example.finalproject;

import static com.example.finalproject.MainActivity.REQUEST_CODE;
import static com.example.finalproject.RecyclerViewActivity.JSON_SAVE_RETRIEVE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class IndividualUserDetails extends Activity implements View.OnClickListener {

    private static final String TAG = "IndividualUserDetails";
    protected static final int INDIVIDUAL_USER_DETAIL_ACTIVITY_VALUE = 70;
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

        Log.d(TAG, "onCreate: beginning onCreate " + profPicValue + " " + firstAndLastNameValue + " " + emailValue);


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

        Log.d(TAG, "onCreate: end of onCreate " + profPicValue + " " + firstAndLastNameValue + " " + emailValue);

        //click listeners
        goBackButton.setOnClickListener(this);
        profPic.setOnClickListener(this);

        

    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerViewActivity.isActivityCalled = false;
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveIt();
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
        Toast.makeText(this, "Back Button Pressed", Toast.LENGTH_SHORT)
                .show();
        goBackToRecyclerView();

    }

    public void goBackToRecyclerView() {
        intent = new Intent(this, RecyclerViewActivity.class);
        intent.putExtra(REQUEST_CODE, INDIVIDUAL_USER_DETAIL_ACTIVITY_VALUE);
        RecyclerViewActivity.isActivityCalled = true;
        startActivityForResult(intent, INDIVIDUAL_USER_DETAIL_ACTIVITY_VALUE);
    }

    public void saveIt(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();

        //pull all info
        String json = sharedPreferences.getString(JSON_SAVE_RETRIEVE, "");
        Type type = new TypeToken<List<User>>() {}.getType();
        userArrayList = gson.fromJson(json, type);

        //find the user that u just clicked and changed the values
        for(User x: userArrayList) {
            Log.d(TAG, "saveIt: " + x.getName());
            if (x.getName().equals(firstAndLastNameValue)) {
                x.setName(firstAndLastName.getText().toString());
                x.setEmail(email.getText().toString());
                //x.profilePic = getIntent().getStringExtra("data"); // find out the switch taken photo to assign here
            }
        }

        //save it back
        sharedPreferences.edit().putString(JSON_SAVE_RETRIEVE, gson.toJson(userArrayList)).apply();


    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void openCamera(){
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//        {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//        }
//        else
//        {
//            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(cameraIntent, CAMERA_REQUEST);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_PERMISSION_CODE)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
//            else
//            {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
//        {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            Log.d(TAG, "onActivityResult: "+photo);
//            data.putExtra("data", photo.toString());
//            profPic.setImageBitmap(photo);
//        }
//    }

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("nameAndLastNameKey", firstAndLastName.getText().toString() );
        outState.putString("emailKey", email.getText().toString());
        Log.d(TAG, "onSaveInstanceState: " + firstAndLastName.getText() + "->" + email.getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        firstAndLastNameValue = savedInstanceState.getString("nameAndLastNameKey");
        emailValue = savedInstanceState.getString("emailKey");
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
                //openCamera();
                break;
        }

    }
}