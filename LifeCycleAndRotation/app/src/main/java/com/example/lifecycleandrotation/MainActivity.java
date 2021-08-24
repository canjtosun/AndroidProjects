package com.example.lifecycleandrotation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final static private String TAG = "Main Activity";
    final static private String FIRSTNAMEKEY = "firstNameKey";
    final static private String LASTNAMEKEY = "lastNameKey";
    private EditText firstnameView;
    private EditText lastNameView;

    String firstName, lastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        firstnameView = findViewById(R.id.firstName);
        lastNameView = findViewById(R.id.lastName);

        //if termination happens, this will retrieve the data
        //with overridden onSaveInstanceState function
        if(savedInstanceState != null){
            firstName = savedInstanceState.getString(FIRSTNAMEKEY);
            lastName = savedInstanceState.getString(LASTNAMEKEY);
            Log.d(TAG, "onCreate, saved Instance State Check " + firstName + " " + lastName);
        }
        else{
            Log.d(TAG, "onCreate, saved Instance State Check ELSE" + firstName + " " + lastName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        firstName = sharedPreferences.getString(FIRSTNAMEKEY, "");
        lastName = sharedPreferences.getString(LASTNAMEKEY, "");
        firstnameView.setText(firstName);
        lastNameView.setText(lastName);
        Log.d(TAG, "onResume: Retrieved Saved Information " + firstName + " " + lastName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // write all the data entered by the user in SharedPreference and apply
        editor.putString(FIRSTNAMEKEY, firstnameView.getText().toString());
        editor.putString(LASTNAMEKEY, lastNameView.getText().toString());
        editor.apply();
        Log.d(TAG, "onPause: Information Saved " + firstName + " " + lastName);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: App is on the background, still in memory");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
//        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().apply();
//        Log.d(TAG, "onDestroy: App is not on the background, completely terminated. Data Cleared ");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FIRSTNAMEKEY, firstnameView.getText().toString());
        outState.putString(LASTNAMEKEY, lastNameView.getText().toString());
    }

}