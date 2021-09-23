package com.example.googleproject;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.io.Serializable;


//User class with constructor and with getters / setters
@Entity(tableName = "user_table")
public class User implements Serializable {

    private String email;
    private String name;
    private String profilePic;

    //need type converters to read inner json objects
    @TypeConverters(Converters.class)
    public UserAddress address;

    @PrimaryKey(autoGenerate = true)
    private int id;

    //constructor
    public User(String name, String email, String profilePic) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        id = 0;
        setAddress(new UserAddress(new Geo("0", "0")));
    }

    //getter - setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Full Name: " + name +
                "\nEmail: " + email +
                "\nLocation: " + address.getGeo().toString() + "\n";
    }
}

//address class -> in Json its 1st inner object
class UserAddress implements Serializable{

    public Geo geo;

    public UserAddress(Geo geo) {
        this.geo = geo;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

}

//Geo Class ->in json, its 2nd inner object
class Geo implements Serializable {
    String lat, lng;

    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Lat: " + lat + " Lng: " + lng;
    }
}