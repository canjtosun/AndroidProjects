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
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<LatLng> userLocationList;
    List<User> users;
    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private static final String TAG = "MapsActivity";

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate: ");
        users = new ArrayList<>();
        userLocationList = new ArrayList<>();


        /*
        bring the saved information from recyclerView
        assign to the user arraylist and iterate to list to assign
        all the values to the specific person's location
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = sharedPrefs.getString("jsonList", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {
        }.getType();
        users = gson.fromJson(json, type);
        for (User u : users) {
            double lat = Double.parseDouble(u.getAddress().getGeo().getLat());
            double lng = Double.parseDouble(u.getAddress().getGeo().getLng());
            userLocationList.add(new LatLng(lat, lng));
        }

        //classHolder method saves the class name  to the sharedPreferences
        classHolder();


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
        finish();
    }


    //enable the location with permission
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


    //iterate the userLocationList and add marker to the map each one of them
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

    //classHolder method saves the class name  to the sharedPreferences
    public void classHolder(){
        SharedPreferences sharedPreferences = getSharedPreferences("CLASS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastClass", getClass().toString());
        editor.apply();
    }
}