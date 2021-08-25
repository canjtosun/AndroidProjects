package com.example.httprestapiretrievingimages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TARGETURL= "https://robohash.org/qwerty/?set=set2";
    private ImageView imageView;
    private Button requestImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        requestImageButton = findViewById(R.id.request_image_button);

        requestImageButton.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void pullIt(String url) throws IOException{
        Picasso.get()
                .load(TARGETURL)
                .placeholder(android.R.drawable.picture_frame)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView);

    }
}