package com.example.googleproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_read_file);
        setTitle("File and Image Activity");
        Log.d(TAG, "onCreate: ");

        saveFile = findViewById(R.id.save_to_file);
        readFile = findViewById(R.id.read_into_textview);
        showFile = findViewById(R.id.show_into_textview);
        showFile.setMovementMethod(new ScrollingMovementMethod());
        photoFromFile = findViewById(R.id.photo_from_file);
        myLayout = findViewById(R.id.save_read_layout);

        //classHolder method saves the class name  to the sharedPreferences
        classHolder();


        userList = new ArrayList<>();

        //bringing everything from the json list to assign userList(ArrayList)
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString("jsonList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        userList = gson.fromJson(json, type);
        Log.d(TAG, "onCreate: " + userList.size());


        //clickListener for save button
        saveFile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                saveFile();
            }
        });

        //clickListener for Read button
        readFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                readFile();
            }
        });

        //clickListener for imageView
        photoFromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringImageFromStorage();
                requestStoragePermission();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        userList.clear();
        startActivity(intent);
        finish();
    }

    /*
    According to the documentation
    If your application already requests write access, it will automatically get read access as well.
    Only asking a permission for writing to the external storage
    */
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: ");
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                saveFile();
            } else {
                Toast.makeText(this, "Permission Denied. Please Consider Accepting", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    //Saving file method
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
    }


    //reading file method
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
                int i;
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

    }

    //brining image from sdcard
    public void bringImageFromStorage() {
        Log.d(TAG, "bringImageFromStorage: ");
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        Intent chooser = Intent.createChooser(i, "Select Image");
        startActivityForResult(chooser, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                photoFromFile.setImageURI(imageUri);
            } else {
                photoFromFile.setImageResource(R.drawable.ic_image_search);
            }
        }
    }

    //classHolder method saves the class name  to the sharedPreferences
    public void classHolder(){
        SharedPreferences sharedPreferences = getSharedPreferences("CLASS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastClass", getClass().toString());
        editor.apply();
    }
}