package com.example.googleproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class SaveReadFileActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    List<User> userList;
    Button saveFile, readFile;
    TextView showFile;
    View myLayout;
    public static final String TAG = "SaveReadFileActivity";
    private static final int PERMISSION_STORAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_read_file);

        saveFile = findViewById(R.id.save_to_file);
        readFile = findViewById(R.id.read_into_textview);
        showFile = findViewById(R.id.show_into_textview);
        showFile.setMovementMethod(new ScrollingMovementMethod());
        myLayout = findViewById(R.id.save_read_layout);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        userList = (List<User>) args.getSerializable("jsonList");

        saveFile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                saveFile();
                Toast.makeText(SaveReadFileActivity.this, "Saving Completed", Toast.LENGTH_SHORT).show();
            }
        });

        readFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                readFile();
                Toast.makeText(SaveReadFileActivity.this, "Reading Completed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainActivity2.class);
        userList.clear();
        startActivity(intent);
    }

    public void requestStoragePermission() {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveFile() {

        String listContent = userList.stream().map(User::toString)
                .collect(Collectors.joining("\n"));
        String fileName = "myList.txt";

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents");
            File myFile = new File(path, fileName);

            if(!myFile.exists()) {
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void readFile(){

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String fileName = "myList.txt";
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents");
            File myFile = new File(path, fileName);
            FileInputStream fstream = null;
            try {
                fstream = new FileInputStream(myFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            StringBuffer sbuffer = new StringBuffer();
            int i = 0;
            while (true) {
                try {
                    if ((i = fstream.read()) == -1) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sbuffer.append((char) i);
                showFile.setText(sbuffer);
            }
            try {
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


}