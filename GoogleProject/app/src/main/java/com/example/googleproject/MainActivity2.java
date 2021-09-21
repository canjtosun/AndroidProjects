package com.example.googleproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener, Serializable {

    private UserViewModel userViewModel;
    private Button signOutButton;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private static final String INFOURL = "https://jsonplaceholder.typicode.com/users";
    private static final String PICSURL = "https://robohash.org/";
    public static final int ADDUSERREQUEST = 1;
    public static final int UPDATEUSERREQUEST = 2;
    private static final String TAG = "MainActivity2";
    public static boolean isAlreadyCalled = false;
    public static boolean isStartingActivity = false;
    OkHttpClient client;
    public static List<User> jsonArrayList; //one time list
    Gson gson;
    RecyclerView recyclerView;
    UserAdapter adapter;
    User[] users;
    AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(TAG, "onCreate: ");

        /*
        initializations
        */
        jsonArrayList = new ArrayList<>();
        client = new OkHttpClient();
        gson = new Gson();
        adapter = new UserAdapter(jsonArrayList);
        signOutButton = findViewById(R.id.sign_in_button);
        recyclerView = findViewById(R.id.recViewUser);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+10,
                60*1000, pendingIntent);



        /*
        recyclerview setters
        */
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        /*
        user view model updates every time changes happens
        it is also initializing all the user to the temporary arraylist
        and I can send it to map view
        */

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> userArrayList) {
                adapter.setUsers(userArrayList);
                jsonArrayList.addAll(userArrayList); //jsonArrayList initialization for maps
            }
        });


        /*
        set click listeners on the activity
        Regular Button -> LOG OUT
        Menu Button -> Delete All Users, Show Users On Map
        Floating Button -> Add User
        Item On Click -> Update User
        */
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.button_add_user).setOnClickListener(this);
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(MainActivity2.this, AddUserActivity.class);
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_ID, user.getId());
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_NAME, user.getName());
                intent.putExtra(AddUserActivity.ADD_IR_UPDATE_USER_EMAIL, user.getEmail());
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_PROF_PIC, user.getProfilePic());
                intent.putExtra(AddUserActivity.USER_LAT, user.getAddress().getGeo().getLat());
                intent.putExtra(AddUserActivity.USER_LNG, user.getAddress().getGeo().getLng());
                isStartingActivity = true;
                startActivityForResult(intent, UPDATEUSERREQUEST);

            }
        });


        /*
        isAlreadyCalled is a flag.
        If we already initialize the json to the recyclerview,
        it is preventing us loading over and over again.
        so after that, we only use database values.
        */
        if (!isAlreadyCalled) {
            run();
            isAlreadyCalled = true;
        }

        /*
        It is a fancy swiping implementation
        when user swipe left or right,
        user can delete the person from database
        */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT
                | ItemTouchHelper.RIGHT) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                userViewModel.delete(adapter.getUserAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity2.this, "User Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

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
        Intent intent = new Intent(MainActivity2.this, ExampleService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && !isStartingActivity ) {
            startForegroundService(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        //stopService(new Intent(this,ExampleService.class)).;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //back button control
        Toast.makeText(this, "Please click LOG OUT to go Main Page", Toast.LENGTH_SHORT)
                .show();
    }

    public void run() {
        Request request = new Request.Builder()
                .url(INFOURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    users = gson.fromJson(response.body().string(), User[].class);
                    runOnUiThread(() -> newView(users));
                }
            }
        });
    }

    //get info from User[] array and save it in a dynamic array
    @SuppressLint("NotifyDataSetChanged")
    public void newView(User[] users) {
        int i = 0;
        for (User u : users) {
            u.setProfilePic(PICSURL + i);
            jsonArrayList.add(u);
            userViewModel.insert(u);
            i++;
        }

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //location = 40.97288637661819, 28.736715123489
        User googleUser = new User(googleSignInAccount.getDisplayName(),
                googleSignInAccount.getEmail(), String.valueOf(googleSignInAccount.getPhotoUrl()));
        googleUser.setId(-99);
        googleUser.setAddress(new UserAddress(new Geo("40.9728","28.7367")));
        userViewModel.insert(googleUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDUSERREQUEST && resultCode == RESULT_OK) {
            String name = data.getStringExtra(AddUserActivity.ADD_OR_UPDATE_USER_NAME);
            String email = data.getStringExtra(AddUserActivity.ADD_IR_UPDATE_USER_EMAIL);
            String profPic = data.getStringExtra(AddUserActivity.ADD_OR_UPDATE_USER_PROF_PIC);

            User newUser = new User(name, email, profPic);
            userViewModel.insert(newUser);
            Toast.makeText(this, "User Saved Successfully", Toast.LENGTH_SHORT).show();

        }
        else if (requestCode == UPDATEUSERREQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddUserActivity.ADD_OR_UPDATE_USER_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "User did not updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = data.getStringExtra(AddUserActivity.ADD_OR_UPDATE_USER_NAME);
            String email = data.getStringExtra(AddUserActivity.ADD_IR_UPDATE_USER_EMAIL);
            String profPic = data.getStringExtra(AddUserActivity.ADD_OR_UPDATE_USER_PROF_PIC);
            String lat = data.getStringExtra(AddUserActivity.USER_LAT);
            String lng = data.getStringExtra(AddUserActivity.USER_LNG);
            User newUser = new User(name, email, profPic);
            newUser.setId(id);
            newUser.setAddress(new UserAddress(new Geo(lat,lng)));
            userViewModel.update(newUser);
            Toast.makeText(this, "User Updated Successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "User not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOutAndGoBack() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        isStartingActivity = true;
        startActivity(intent);
        finish(); //finish the activity, so doesn't go stack

    }

    public void goSaveUser() {
        Intent intent = new Intent(MainActivity2.this, AddUserActivity.class);
        isStartingActivity = true;
        startActivityForResult(intent,ADDUSERREQUEST);
    }

    public void goMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        isStartingActivity = true;
        Bundle args = new Bundle();
        args.putSerializable("jsonList", (Serializable) jsonArrayList);
        intent.putExtra("bundle", args);
        startActivity(intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveUsersToTxtFile(){
        Intent intent = new Intent(this, SaveReadFileActivity.class);
        isStartingActivity = true;
        Bundle args = new Bundle();
        args.putSerializable("jsonList", (Serializable) jsonArrayList);
        intent.putExtra("bundle", args);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /*
    Menu items click listener
    Top right three dot menu
    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_users:
                userViewModel.deleteAllUsers();
                Toast.makeText(MainActivity2.this, "All Users Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.show_users_on_map:
                goMapsActivity();
                Toast.makeText(MainActivity2.this, "Users are showing on Map", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.save_users_to_txt_file:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    saveUsersToTxtFile();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    Regular button and floating button listener
    */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                signOutAndGoBack();
                break;
            case R.id.button_add_user:
                goSaveUser();
                break;
        }
    }
}


