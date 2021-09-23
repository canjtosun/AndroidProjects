package com.example.googleproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SaveReadFileActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    List<User> userList;
    Button saveFile, readFile;
    TextView showFile;
    ImageView photoFromFile;
    View myLayout;
    public static final String TAG = "SaveReadFileActivity";
    public static final String fileName = "myList.txt";
    private static final int PERMISSION_STORAGE = 102;
    public static final int PICK_IMAGE = 103;
    public static boolean isStartingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_read_file);
        Log.d(TAG, "onCreate: ");

        saveFile = findViewById(R.id.save_to_file);
        readFile = findViewById(R.id.read_into_textview);
        showFile = findViewById(R.id.show_into_textview);
        showFile.setMovementMethod(new ScrollingMovementMethod());
        photoFromFile = findViewById(R.id.photo_from_file);
        myLayout = findViewById(R.id.save_read_layout);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        userList = (List<User>) args.getSerializable("jsonList");
        Log.d(TAG, "onCreate: " + userList.size());


        saveFile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                saveFile();
            }
        });

        readFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                readFile();
            }
        });

        photoFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringImageFromStorage();
                requestStoragePermission();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + isStartingActivity);
        isStartingActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + isStartingActivity);
        Intent intent = new Intent(SaveReadFileActivity.this, ExampleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !isStartingActivity) {
            startService(intent);
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        userList.clear();
        startActivity(intent);
        isStartingActivity = true;
        finish();
    }

    public void requestStoragePermission() {
        Log.d(TAG, "requestStoragePermission: ");
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission Denied. Please Consider Accepting", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveFile() {
        Log.d(TAG, "saveFile: ");
        String listContent = userList.stream().map(User::toString)
                .collect(Collectors.joining("\n"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !listContent.isEmpty()) {
                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents");
                File myFile = new File(path, fileName);

                if (!myFile.exists()) {
                    try {
                        myFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream fstream;
                try {
                    fstream = new FileOutputStream(myFile);
                    fstream.write(listContent.getBytes());
                    fstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        isStartingActivity = true;
    }


    public void readFile() {
        Log.d(TAG, "readFile: ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents");
                File myFile = new File(path, fileName);
                FileInputStream fis;
                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer sb = new StringBuffer();
                int i = 0;
                while (true) {
                    try {
                        if ((i = fis.read()) == -1)
                            break;
                    } catch (IOException e) {
                        return;
                    }
                    sb.append((char) i);
                }
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showFile.setText(sb);
                Toast.makeText(SaveReadFileActivity.this, "Reading Completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something wrong with the SD CARD", Toast.LENGTH_SHORT).show();
            }
        }
        isStartingActivity = true;

    }

    public void bringImageFromStorage() {
        Log.d(TAG, "bringImageFromStorage: ");
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        isStartingActivity = true;
        startActivityForResult(i, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photoFromFile.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            if (BitmapFactory.decodeFile(picturePath) == null) {
                photoFromFile.setImageResource(R.drawable.ic_image_search);
            }
        }
    }
}