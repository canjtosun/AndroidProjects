package com.example.androidpermissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_CONTACTS = 100;
    private static final int PERMISSION_LOCATION = 101;
    private static final int PERMISSION_STORAGE = 102;
    private static final int PERMISSION_CALENDAR = 103;
    private static final int PERMISSION_CAMERA = 104;

    private Button request_contacts;
    private Button request_location;
    private Button request_storage;
    private Button request_calendar;
    private Button request_camera;
    private View myLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        request_contacts = findViewById(R.id.button_request_contacts);
        request_location = findViewById(R.id.button_request_location);
        request_storage = findViewById((R.id.button_request_storage));
        request_calendar = findViewById(R.id.button_request_calendar);
        request_camera = findViewById(R.id.button_request_camera);
        myLayout = findViewById(R.id.main_layout);


        request_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doContacts();
            }
        });

        request_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLocation();
            }
        });

        request_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStorage();
            }
        });

        request_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCalendar();
            }
        });
        request_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCamera();
            }
        });


    }

    private void requestContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(myLayout, "Contacts Access Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACTS);
                        }
                    }).show();
        } else {
            Snackbar.make(myLayout, "Contacts Access Unavailable", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACTS);

        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(myLayout, "Location Access Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION);
                        }
                    }).show();
        } else {
            Snackbar.make(myLayout, "Location Access Unavailable", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION);

        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(myLayout, "Storage Access Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                        }
                    }).show();
        } else {
            Snackbar.make(myLayout, "Storage Access Unavailable", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        }
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)) {
            Snackbar.make(myLayout, "Calendar Access Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_CALENDAR}, PERMISSION_CALENDAR);
                        }
                    }).show();
        } else {
            Snackbar.make(myLayout, "Calendar Access Unavailable", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CALENDAR}, PERMISSION_CALENDAR);
        }

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(myLayout, "Camera Access Required", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                        }
                    }).show();
        } else {
            Snackbar.make(myLayout, "Camera Access Unavailable", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        }

    }

    public void doStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivity(intent);
        } else {
            requestStoragePermission();
        }
    }

    public void doLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //do something
        } else {
            requestLocationPermission();
        }
    }

    public void doContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {
            //do something
        } else {
            requestContactsPermission();
        }
    }

    public void doCalendar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) ==
                PackageManager.PERMISSION_GRANTED) {
            //do something
        } else {
            requestCalendarPermission();
        }
    }
    public void doCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, CameraPreviewActivity.class);
            startActivity(intent);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Main Activity", "onRequestPermissionsResult: requestCode" + requestCode);
        if (requestCode == PERMISSION_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Contact Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("calling", "onRequestPermissionsResult: doContacts()");
                doContacts();
            } else {
                Toast.makeText(MainActivity.this, "Contact Permission Denied", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("calling", "onRequestPermissionsResult: doLocation()");
                doLocation();
            } else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("calling", "onRequestPermissionsResult: doStorage()");
                doStorage();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PERMISSION_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Calendar Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("calling", "onRequestPermissionsResult: doCalendar()");
                doCalendar();
            } else {
                Toast.makeText(MainActivity.this, "Calendar Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                Log.d("calling", "onRequestPermissionsResult: doCamera()");
                doCamera();
            } else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }


}