package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import okhttp3.OkHttpClient;

public class IndividualUserDetails extends AppCompatActivity implements View.OnClickListener {

    private static final String TARGETURL = "http://jsonplaceholder.typicode.com/users";
    private static final String INFOKEY = "infoKey";
    private ImageView profPic;
    private TextView firstAndLastName, email;
    private Button goBackButton;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individiual_user_details);

        profPic = findViewById(R.id.profile_pic_view);
        firstAndLastName = findViewById(R.id.first_last_name);
        email = findViewById(R.id.email);
        goBackButton = findViewById(R.id.go_back_button);

        String profPicValue = getIntent().getStringExtra("profilePic");
        Picasso.get().load(profPicValue).into(profPic);

        String firstAndLastNameValue = getIntent().getStringExtra("firstAndLastName");
        firstAndLastName.setText(firstAndLastNameValue);

        String emailValue = getIntent().getStringExtra("email");
        email.setText(emailValue);

        goBackButton.setOnClickListener(this);

    }

    public void goBackToRecyclerView() {
        intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_back_button:
                goBackToRecyclerView();
                break;
        }

    }
}