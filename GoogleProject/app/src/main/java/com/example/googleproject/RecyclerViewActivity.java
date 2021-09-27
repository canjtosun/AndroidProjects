package com.example.googleproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener,
        Serializable{

    private UserViewModel userViewModel;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private static final String INFOURL = "https://jsonplaceholder.typicode.com/users";
    private static final String PICSURL = "https://robohash.org/";
    private static final String ALREADYCALLED = "isAlreadyCalled";
    public static final int ADDUSERREQUEST = 1;
    public static final int UPDATEUSERREQUEST = 2;
    private static final String TAG = "RecyclerViewActivity";
    OkHttpClient client;
    public static List<User> jsonArrayList; //one time list
    Gson gson;
    RecyclerView recyclerView;
    UserAdapter adapter;
    User[] users;
    AlarmManager alarmManager;
    Intent serviceIntent;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("Your List");
        Log.d(TAG, "onCreate: ");

        /*
        initializations
        */
        jsonArrayList = new ArrayList<>();
        client = new OkHttpClient();
        gson = new Gson();
        adapter = new UserAdapter(jsonArrayList);
        recyclerView = findViewById(R.id.recViewUser);
        serviceIntent = new Intent(this, NotificationService.class);

        //stop Service
        stopService(serviceIntent);


        /*
        Alarm Manager for every minute
        */
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                60*1000, pendingIntent);

        /*
        Airplane Mode Receiver
        */
        AirplaneModeReceiver receiver = new AirplaneModeReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(receiver,intentFilter);

        /*
        Class Holder
        */
        classHolder();



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
                jsonArrayList.clear();
                jsonArrayList.addAll(userArrayList); //jsonArrayList initialization for maps
                Log.d(TAG, "onChanged: " + jsonArrayList.size());
            }
        });


        /*
        set click listeners on the activity
        Regular Button -> LOG OUT
        Menu Button -> Delete All Users, Show Users On Map
        Floating Button -> Add User
        Item On Click -> Update User
        */
        findViewById(R.id.button_add_user).setOnClickListener(this);
        adapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                Intent intent = new Intent(RecyclerViewActivity.this, AddUserActivity.class);
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_ID, user.getId());
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_NAME, user.getName());
                intent.putExtra(AddUserActivity.ADD_IR_UPDATE_USER_EMAIL, user.getEmail());
                intent.putExtra(AddUserActivity.ADD_OR_UPDATE_USER_PROF_PIC, user.getProfilePic());
                intent.putExtra(AddUserActivity.USER_LAT, user.getAddress().getGeo().getLat());
                intent.putExtra(AddUserActivity.USER_LNG, user.getAddress().getGeo().getLng());
                startActivityForResult(intent, UPDATEUSERREQUEST);

            }
        });


        /*
        sharedPreferences is a flag.
        If we already initialize the json to the recyclerview,
        it is preventing us loading over and over again.
        so after that, we only use database values.
        */
        sharedPreferences = getSharedPreferences(ALREADYCALLED, Context.MODE_PRIVATE);
        if (sharedPreferences.getString("callOnce", null) == null) {
            run();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("callOnce", "call_once");
            edit.apply();
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
                Toast.makeText(RecyclerViewActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        /*
        LifecycleObserver for when app goes background
        */
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LcoNotification(this));

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
        jsonArrayList.clear();
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

    //sign out button on menu inflater. top of the page.
    private void signOutAndGoBack() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); //finish the activity, so doesn't go stack

    }

    //creating intent to the save/update person information
    public void goSaveUser() {
        Intent intent = new Intent(RecyclerViewActivity.this, AddUserActivity.class);
        startActivityForResult(intent,ADDUSERREQUEST);
    }

    //creating intent to go to maps activity
    public void goMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("jsonList", (Serializable) jsonArrayList);
//        intent.putExtra("bundle", args);
        saveDataIntoSharedPreferences();
        startActivity(intent);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void saveUsersToTxtFile(){
//        Intent intent = new Intent(this, SaveReadFileActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("jsonList", (Serializable) jsonArrayList);
//        intent.putExtra("bundle", args);
//        startActivity(intent);
//        finish();
//    }

    //creating intent for file save-read and image manipulation
    public void saveUsersToTxtFile(){
        Intent intent = new Intent(this, SaveReadFileActivity.class);
        saveDataIntoSharedPreferences();
        startActivity(intent);
    }

    //inflating the menu. top of the page
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
                Toast.makeText(RecyclerViewActivity.this, "All Users Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.show_users_on_map:
                goMapsActivity();
                Toast.makeText(RecyclerViewActivity.this, "Users are showing on Map", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.save_users_to_txt_file:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    saveUsersToTxtFile();
                }
                return true;
            case R.id.log_out_icon:
                signOutAndGoBack();
                Toast.makeText(RecyclerViewActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //saving data to the json style into sharedPreferences
    // to bring it on other activities when needed
    public void saveDataIntoSharedPreferences(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(jsonArrayList);
        editor.putString("jsonList", json);
        editor.apply();
    }

    //classHolder method saves the class name  to the sharedPreferences
    public void classHolder(){
        SharedPreferences sharedPreferences = getSharedPreferences("CLASS_KEY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastClass", getClass().toString());
        editor.apply();
    }

    /*
    Regular button and floating button listener
    */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add_user:
                goSaveUser();
                break;
        }
    }
}


