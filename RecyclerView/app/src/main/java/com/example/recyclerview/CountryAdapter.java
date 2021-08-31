package com.example.recyclerview;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

    Context context;
    final private ArrayList<Country> countryArrayList;
    private static final String MESSAGE = "message";
    private static final String WEBSITE = "website";
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final int NOTIFICATIONID = 1;

    public CountryAdapter(Context context, ArrayList<Country> countryArrayList) {
        this.context = context;
        this.countryArrayList = countryArrayList;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.ViewHolder holder, int position) {
        Country country = countryArrayList.get(position);
        holder.imageView1flag.setImageResource(country.getImage());
        holder.country1name.setText(country.getCountryName());
        holder.country1capital.setText(country.getCapital());
        holder.country1phoneCode.setText("+" + country.getPhoneCode());
        holder.country1website.setText(country.getWebsite());
        holder.country1info.setText(country.getMoreInfo());
    }

    @Override
    public int getItemCount() {
        return countryArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final private ImageView imageView1flag;
        final private TextView country1name, country1capital, country1phoneCode, country1website, country1info;
        Intent intent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1flag = itemView.findViewById(R.id.imageView1_flag);
            country1name = itemView.findViewById(R.id.country1_name);
            country1capital = itemView.findViewById(R.id.country1_capital);
            country1phoneCode = itemView.findViewById(R.id.country1_phone_code);
            country1website = itemView.findViewById(R.id.country1_website);
            country1info = itemView.findViewById(R.id.country1_information);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            goNextActivity(view);
            addNotification(view);
        }


        public void goNextActivity(View view){
            intent = new Intent(view.getContext(), CountryInformation.class);
            String sendMessage = "Country Name: " + country1name.getText().toString() + "\n\n" +
                    "Capital City: " + country1capital.getText().toString() + "\n\n" +
                    "Phone Code: " + country1phoneCode.getText().toString() + "\n\n" +
                    "Website: " + country1website.getText().toString() + "\n\n" +
                    "Sports Info: " + country1info.getText().toString();
            intent.putExtra(MESSAGE, sendMessage);
            intent.putExtra(WEBSITE, country1website.getText().toString());
            Toast.makeText(view.getContext(),  "CLICK THE PAGE FOR MORE INFORMATION!", Toast.LENGTH_LONG)
                    .show();
            view.getContext().startActivity(intent);

        }

        private void addNotification(View view){
//            Intent notificationIntent = new Intent(view.getContext(), CountryInformation.class);
//            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), 0, notificationIntent, 0);
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext(), CHANNEL_ID)
//                    .setSmallIcon(R.drawable.flag_us)
//                    .setContentTitle("My notification")
//                    .setContentText("Hey WE missed you")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//
//
//            // Add as notification
//            NotificationManagerCompat manager = NotificationManagerCompat.from(view.getContext());
//            manager.notify(NOTIFICATIONID, builder.build());

        }

    }


}

