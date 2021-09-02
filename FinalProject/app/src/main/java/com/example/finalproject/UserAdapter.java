package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context context;
    private ArrayList<User> userArrayList;

    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.generalFirstName.setText(user.getName());
        holder.generalProfilePic.setTransitionName(user.getProfilePic());
        Picasso.get().load(user.getProfilePic()).into(holder.generalProfilePic);
        holder.generalEmail.setText(user.getEmail());
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final private ImageView generalProfilePic;
        final private TextView generalFirstName, generalEmail;
        Intent intent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            generalProfilePic = itemView.findViewById(R.id.general_profile_pic);
            generalFirstName = itemView.findViewById(R.id.general_name);
            generalEmail = itemView.findViewById(R.id.general_email);

            itemView.setOnClickListener(this);

        }

        public void ShowAllInformation(View view){
            intent = new Intent(view.getContext(), IndividualUserDetails.class);

            String profPicUrl = generalProfilePic.getTransitionName();
            String firstAndLastName = generalFirstName.getText().toString();
            String email = generalEmail.getText().toString();

            intent.putExtra("profilePic", profPicUrl);
            intent.putExtra("firstAndLastName", firstAndLastName);
            intent.putExtra("email", email);

            RecyclerViewActivity.isActivityCalled = true;
            view.getContext().startActivity(intent);

        }

        @Override
        public void onClick(View view) {
            ShowAllInformation(view);

        }


    }
}
