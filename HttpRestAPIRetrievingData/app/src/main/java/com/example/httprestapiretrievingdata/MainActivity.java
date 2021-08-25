package com.example.httprestapiretrievingdata;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MainActivity extends Activity {

    private static final String TARGETURL = "http://jsonplaceholder.typicode.com/users";
    private static final String INFOKEY = "infoKey";
    private TextView informationView;
    private Button requestInformationButton;
    private OkHttpClient client;
    private ArrayList<String> usersList;
    User[] users;
    private Gson gson;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usersList = new ArrayList<>();
        gson = new Gson();
        client = new OkHttpClient();
        informationView = findViewById(R.id.information_view);
        requestInformationButton = findViewById(R.id.request_information_button);


        requestInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pullIt(TARGETURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(INFOKEY, "");
        informationView.setText(temp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // write all the data entered by the user in SharedPreference and apply
        editor.putString(INFOKEY, informationView.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        preferences.edit().remove(INFOKEY).apply();
        Toast.makeText(MainActivity.this, "Terminated", Toast.LENGTH_SHORT)
                .show();
    }

    public void pullIt(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                users = gson.fromJson(responseBody.string(), User[].class);
                runOnUiThread( () -> newView(users));
            }
        });
    }

    public void newView(User[] users){
        usersList.clear();
        for(User u: users){
            String info = "-ID: " + u.getId() + "\n" +
                    "Name: " + u.getName() + "\n" +
                    "Email: " + u.getEmail() + "\n" +
                    "Street: " + u.getAddress().getStreet() + "\n" +
                    "City: " + u.getAddress().getCity() + "\n" +
                    "WebSite: " + u.getWebsite() + "\n\n";

            usersList.add(info);
        }
        informationView.setText(" " + usersList.toString().replaceAll("\\[|\\]|\\,", ""));
    }

}