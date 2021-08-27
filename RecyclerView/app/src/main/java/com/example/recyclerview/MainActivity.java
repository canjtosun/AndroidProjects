package com.example.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Country> countryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recViewCountry);
        countryArrayList = new ArrayList<>();
        countryArrayList.add(new Country("Turkey","Ankara", 90, R.drawable.flag_turkey,  "https://en.wikipedia.org/wiki/Turkey", R.string.turkey_sports));
        countryArrayList.add(new Country("United States of America","Washington DC", 1, R.drawable.flag_us, "https://en.wikipedia.org/wiki/United_States", R.string.us_sports));
        countryArrayList.add(new Country("Italy", "Rome", 39, R.drawable.flag_italy, "https://en.wikipedia.org/wiki/Italy", R.string.italy_sports));
        countryArrayList.add(new Country("Spain", "Madrid", 34, R.drawable.flag_spain, "https://en.wikipedia.org/wiki/Spain", R.string.spain_sports));
        countryArrayList.add(new Country("Portugal", "Lisbon", 351, R.drawable.flag_portugal, "https://en.wikipedia.org/wiki/Portugal", R.string.portugal_sports));

        CountryAdapter countryAdapter = new CountryAdapter(this, countryArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(countryAdapter);

    }
}