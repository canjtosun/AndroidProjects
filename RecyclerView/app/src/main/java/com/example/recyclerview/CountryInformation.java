package com.example.recyclerview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CountryInformation extends AppCompatActivity {

    private TextView textView;
    String website;
    private static final String MESSAGE = "message";
    private static final String WEBSITE = "website";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_information);

        textView = findViewById(R.id.textView);
        String info = getIntent().getStringExtra(MESSAGE);
        website = getIntent().getStringExtra(WEBSITE);
        textView.setText(info);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickWebsite();
            }
        });
    }

    public void clickWebsite(){
        Toast.makeText(this, "Directing...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(intent);
    }
}
