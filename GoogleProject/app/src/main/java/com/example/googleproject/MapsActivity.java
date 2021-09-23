package com.example.googleproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> userLocationList;
    List<User> users;
    public static boolean isStartingActivity = false;
    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate: ");
        users = new ArrayList<>();
        userLocationList = new ArrayList<>();


        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        users = (List<User>) args.getSerializable("jsonList");
        for (User u : users) {
            double lat = Double.parseDouble(u.getAddress().getGeo().getLat());
            double lng = Double.parseDouble(u.getAddress().getGeo().getLng());
            userLocationList.add(new LatLng(lat, lng));
        }



        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

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
        Log.d(TAG, "onPause: ");
        Intent intent = new Intent(this, ExampleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !isStartingActivity ) {
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
        isStartingActivity = true;
        finish();
    }


    @SuppressLint("MissingPermission")
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation(mMap);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;

        for (int i = 0; i < userLocationList.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(userLocationList.get(i))
                    .title(users.get(i).getName()));
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocationList.get(i), 1));
        }
        enableMyLocation(mMap);

    }
}