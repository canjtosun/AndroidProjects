package com.example.googleproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AddUserActivity extends AppCompatActivity {
    public static final String ADD_OR_UPDATE_USER_ID = "AddOrEditUserId";
    public static final String ADD_OR_UPDATE_USER_NAME = "AddOrEditUserName";
    public static final String ADD_IR_UPDATE_USER_EMAIL = "AddUserEmail";
    public static final String ADD_OR_UPDATE_USER_PROF_PIC = "AddUserProfPic";
    public static final String USER_LAT = "userLat";
    public static final String USER_LNG = "userLng";
    public static final String TAG = "AddUserActivity";

    private EditText editTextName, editTextEmail;
    private TextView latView, lngView;
    private ImageView editImageView;
    static final int CAPTURE_IMAGE_REQUEST = 1000;
    String mCurrentPhotoPath;
    File photoFile = null;
    Uri photoUri = null;
    String nameValue, emailValue, profPicValue, latValue, lngValue;
    public static boolean isStartingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Log.d(TAG, "onCreate: ");

        editTextName = findViewById(R.id.first_last_name);
        editTextEmail = findViewById(R.id.email);
        editImageView = findViewById(R.id.profile_pic_view);
        latView = findViewById(R.id.lat_view);
        lngView = findViewById(R.id.lng_view);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Floating Button ClickListener
        editImageView.setOnClickListener(view -> openDialog());
        findViewById(R.id.button_send_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailToUser();
            }
        });


        Intent intent = getIntent();

        if(intent.hasExtra(ADD_OR_UPDATE_USER_ID)){
            setTitle("Update User");
            editTextName.setText(intent.getStringExtra(ADD_OR_UPDATE_USER_NAME));
            editTextEmail.setText(intent.getStringExtra(ADD_IR_UPDATE_USER_EMAIL));
            Picasso.get().load(intent.getStringExtra(ADD_OR_UPDATE_USER_PROF_PIC)).transform(new CropCircleTransformation())
                    .resize(400, 400).into(editImageView);
            latView.setText(intent.getStringExtra(USER_LAT));
            lngView.setText(intent.getStringExtra(USER_LNG));

        }
        else{
            setTitle("Add User");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        isStartingActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + isStartingActivity);
        Intent intent = new Intent(this, ExampleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !isStartingActivity ) {
            Log.d(TAG, "onPause: in if loop");
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Please click save or cancel", Toast.LENGTH_SHORT).show();
    }

    private void saveUser() {
        nameValue = editTextName.getText().toString();
        emailValue = editTextEmail.getText().toString();
        latValue = latView.getText().toString();
        lngValue = lngView.getText().toString();


        //handling update and edit photo
        //if user have some kinda picture at the beginning and did not take photo
        if(getIntent().getStringExtra(ADD_OR_UPDATE_USER_PROF_PIC) != null && photoFile == null){
            profPicValue = getIntent().getStringExtra(ADD_OR_UPDATE_USER_PROF_PIC);
        }
        //if new user added without picture
        else if(photoFile == null){
            Log.d(TAG, "saveUser: photoFile is NULL");
            profPicValue = "https://robohash.org/canx?set=set5";
        }
        //if user added with picture or updated the picture
        else{
            Log.d(TAG, "saveUser: THERE IS A PICTURE PATH");
            profPicValue = "file://" + photoFile.getAbsolutePath();
        }


        if (nameValue.trim().isEmpty() || emailValue.trim().isEmpty()) {
            Toast.makeText(this, "Please fill the fields", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent data = new Intent();
        data.putExtra(ADD_OR_UPDATE_USER_NAME, nameValue);
        data.putExtra(ADD_IR_UPDATE_USER_EMAIL, emailValue);
        data.putExtra(ADD_OR_UPDATE_USER_PROF_PIC, profPicValue);

        
        int id = getIntent().getIntExtra(ADD_OR_UPDATE_USER_ID, -1);
        if(id != -1){
            data.putExtra(ADD_OR_UPDATE_USER_ID, id);
            data.putExtra(USER_LAT, latValue);
            data.putExtra(USER_LNG, lngValue);
        }

        isStartingActivity = true;
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_user:
                saveUser();
                return true;
            default:
                isStartingActivity=true;
                return super.onOptionsItemSelected(item);
        }
    }






    //Camera and Storage Implementations

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


    @SuppressLint("QueryPermissionsNeeded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            isStartingActivity = true;
            Log.d(TAG, "openCamera: after request open camera");
        } else {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                photoUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                isStartingActivity = true;
                Log.d(TAG, "openCamera: last thing here activity should be true ");
            } catch (Exception ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
        }
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
            editImageView.setImageBitmap(myBitmap);
            profPicValue = "file://" + photoFile.getAbsolutePath();
        }
        else {
            Log.d("AddUserActivity", "onActivityResult: Request cancelled or something went wrong. Saving back original picture");
        }
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to change profile picture?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int which) {
                openCamera();
            }
        });
        //do nothing if user clicks "No"
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void sendEmailToUser(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, "+ editTextName.getText().toString());
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{editTextEmail.getText().toString()});
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Test");
        Intent chooser = Intent.createChooser(intent, "Send Email");
        isStartingActivity = true;
        startActivity(chooser);
    }

}