package com.example.googleproject;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.List;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private  List<User> userArrayList;
    public  OnItemClickListener listener;

    public UserAdapter(List<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    //cache references to sub views of the View
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    // This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userArrayList.get(position);

        holder.generalFirstName.setText(user.getName());
        holder.generalProfilePic.setTransitionName(user.getProfilePic());
        Picasso.get().load(user.getProfilePic()).transform(new CropCircleTransformation()).into(holder.generalProfilePic);
        holder.generalEmail.setText(user.getEmail());
    }

    //Returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public void setUsers(List<User> userArrayList){
        this.userArrayList = userArrayList;
        notifyDataSetChanged();
    }

    public User getUserAt(int position){
        return userArrayList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        final private ImageView generalProfilePic;
        final private TextView generalFirstName, generalEmail;

        //get items
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            generalProfilePic = itemView.findViewById(R.id.general_profile_pic);
            generalFirstName = itemView.findViewById(R.id.general_name);
            generalEmail = itemView.findViewById(R.id.general_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(userArrayList.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
